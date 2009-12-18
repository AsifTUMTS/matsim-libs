/* *********************************************************************** *
 * project: org.matsim.*
 * OTFConnectionManager.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.vis.otfvis.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.matsim.vis.otfvis.caching.SceneGraph;
import org.matsim.vis.otfvis.caching.SceneLayer;
import org.matsim.vis.otfvis.interfaces.OTFDataReader;



/**
 * The OTFConnectionManager is the most important class when building an OTFVis instance.
 * It holds pairs of classes. Each of this class-pairs yields as a "From" -> "To" connection between classes.
 * The whole from-to conncetions established in a OTFConnectionManager describe the route all data has to 
 * take from the source (normally a QueueLink/Node, etc.) to the actual display on screen.
 * It is the programmer's responsibility to define a complete chain of responsible objects for all data sent.
 * 
 *  A chain of responsibility normally consists of 
 *  a DataSource (e.g. QueueLink), 
 *  a DataWriter (e.g. OTFDefaultLinkHandler.Writer)
 *  a DataReader (e.g. OTFDefaultLinkHandler)
 *  a Visualizer class (e.g. SimpleStaticNetLayer.QuadDrawer)
 *  and possibly a layer this Drawer belongs to (e.g. SimpleStaticNetLayer)
 *  
 * @author dstrippgen
 *
 */
public class OTFConnectionManager implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6481835753628883014L;
	
	/* transient */ private static final Logger log = Logger.getLogger(OTFConnectionManager.class);
	private boolean isValidated = false;

	public static class Entry implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2260651735789627280L;
		Class from, to;

		public Entry(Class from, Class to) {
			this.from = from;
			this.to = to;
		}
		
		@Override
		public String toString() {
			return "(" + from.toString() + "," + to.toString() + ") ";
		}

		public Class getTo(){
			return this.to;
		}
		
		public Class getFrom(){
			return this.from;
		}
		
		
	}
	
	private final List<Entry> connections = new LinkedList<Entry>();
	
	@Override
	public OTFConnectionManager clone() {
		OTFConnectionManager clone = new OTFConnectionManager();
		Iterator<Entry> iter = connections.iterator();
		while(iter.hasNext()) {
			Entry entry = iter.next();
			clone.add(entry.from, entry.to);
		}
		return clone;
	}
	
	public void validate() {
		isValidated = true;
		
		for (Entry entry  : connections) {
			if(OTFDataWriter.class.isAssignableFrom(entry.from)){
				Collection<Class> readerClasses = this.getToEntries(entry.from);
				int count = readerClasses.size();
				if (count != 1) {
					// there must be exactly ONE Reader class corresponding to every Writer class
					if (count > 1) log.fatal("For Writer class" + entry.from.getCanonicalName() + " there is more than ONE reader class defined");
					else log.fatal("For Writer class" + entry.from.getCanonicalName() + " there is NO reader class defined");
					throw new RuntimeException(); //System.exit(1);
				}
			}
		}

	}

	public void add(Entry entry) {
		remove(entry.from,entry.to);
		connections.add(entry);
	}
	
	public void add (Class from, Class to) {
		add(new Entry(from,to));
	}

	public void remove(Class from, Class to) {
		Iterator<Entry> iter = connections.iterator();
		while(iter.hasNext()) {
			Entry entry = iter.next();
			if ((entry.from == from) && (entry.to == to)) iter.remove();
		}
	}

	public void remove (Class from) {
		Iterator<Entry> iter = connections.iterator();
		while(iter.hasNext()) {
			Entry entry = iter.next();
			if (entry.from == from) iter.remove();
		}
	}

	public List<Entry> getEntries(){
		return this.connections;
	}
	
	public Collection<Class> getToEntries(Class srcClass) {
		if (!isValidated) validate();
		
		List<Class> classList = new LinkedList<Class>();
		for(Entry entry : connections) {
			if (entry.from.equals(srcClass)) classList.add(entry.to);
		}
		return classList;
	}

	public Class getFirstToEntry(Class srcClass) {
		if (!isValidated) validate();
		
		for(Entry entry : connections) {
			if (entry.from.equals(srcClass)) return entry.to;
		}
		return Object.class;
	}

	public Collection<OTFDataReceiver> getReceivers(Class srcClass, SceneGraph graph) {
		Collection<Class> classList = getToEntries(srcClass);
//		log.error("getting receivers for class " + srcClass.getName());
//		for (Class c : classList){
//			log.error("found to entry " + c.getName());
//		}
		List<OTFDataReceiver> receiverList = new LinkedList<OTFDataReceiver>();
		
		for(Class entry : classList) {
			try {
				receiverList.add((OTFDataReceiver)(graph.newInstance(entry)));
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return receiverList;
	}
	private Class handleClassAdoption(Class entryClass, String fileFormat) {
		Class newReader = null;
		
		if (OTFDataReader.class.isAssignableFrom(entryClass)) {
			// entry.to is a OTFDataReader class, check for special version for this fileversion
			String ident = entryClass.getCanonicalName() + fileFormat;
			if (OTFDataReader.previousVersions.containsKey(ident)) {
				// there is a special version, replace the entry to with it!
				newReader = OTFDataReader.previousVersions.get(ident);
			}
		}
		return newReader;
	}
	
	public void adoptFileFormat(String fileFormat) {
		// go through every reader class and look for the appropriate Reader Version for this fileformat
		
		ListIterator<Entry> iter = connections.listIterator();
		while(iter.hasNext()) {
			Entry entry = iter.next();
			// make sure, that the static members have been initialized
			try {
				entry.from.newInstance();
			} catch (InstantiationException e) {
				log.warn("FROM For Entry " + entry.from.getCanonicalName()+ " " + entry.to.getCanonicalName() + " the from  instance could not be generated!");
			} catch (IllegalAccessException e) {
				log.warn("FROM For Entry " + entry.from.getCanonicalName()+ " " + entry.to.getCanonicalName() + " the from instance could not be generated!");
			}
			
			try {
				entry.to.newInstance();
			} catch (InstantiationException e) {
				log.warn("TO For Entry " + entry.from.getCanonicalName()+ " " + entry.to.getCanonicalName() + " the to instance could not be generated");
			} catch (IllegalAccessException e) {
				log.warn("TO For Entry " + entry.from.getCanonicalName()+ " " + entry.to.getCanonicalName() + " the to instance could not be generated");
			}

			
			// check for both classes, if they need to be replaced
			Class newReader = handleClassAdoption(entry.to, fileFormat);
			if (newReader != null) iter.set(new Entry(entry.from, newReader));

			newReader = handleClassAdoption(entry.from, fileFormat);
			if (newReader != null) iter.set(new Entry(newReader, entry.to));
		}
		log.info(OTFDataReader.previousVersions);
		log.info(connections);

	}

	public void fillLayerMap(Map<Class, SceneLayer> layers) {
		Iterator<Entry> iter = connections.iterator();
		while(iter.hasNext()) {
			Entry entry = iter.next();
			if (SceneLayer.class.isAssignableFrom(entry.to))
				try {
					layers.put(entry.from, (SceneLayer)(entry.to.newInstance()));
//					log.info("created layer " + entry.to.getName());
//					log.info("drawer for layer is " + entry.from);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
		}
	}

	public void updateEntries(OTFConnectionManager connect2) {
		Iterator<Entry> iter = connect2.connections.iterator();
		while(iter.hasNext()) {
			Entry entry = iter.next();
			log.info("updating entry: " + entry.from.getCanonicalName() + " to " + entry.to.getName());
			this.add(entry);
		}
	}
}
