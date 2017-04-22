/* *********************************************************************** *
 * project: org.matsim.*
 * DigicoreVehicleWriter.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
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

package playground.nmviljoen.io;


import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.matsim.core.api.internal.MatsimWriter;
import org.matsim.core.utils.io.MatsimXmlWriter;
import org.matsim.core.utils.misc.Counter;

import edu.uci.ics.jung.graph.DirectedGraph;
import playground.nmviljoen.gridExperiments.GridExperiment;
import playground.nmviljoen.gridExperiments.NmvLink;
import playground.nmviljoen.gridExperiments.NmvNode;

public class MultilayerInstanceWriter extends MatsimXmlWriter implements MatsimWriter{
	private final Logger log = Logger.getLogger(MultilayerInstanceWriter.class);
	private Counter counter = new Counter("  vehicle # ");
	private GridExperiment experiment;

		
	public MultilayerInstanceWriter(GridExperiment experiment){
		super();
		this.experiment = experiment;
	}

	
	@Override
	public void write(final String filename){
		log.info("Writing grid experiment to file: " + filename);
		writeV1(filename);
	}
	
	
	public void writeV1(final String filename){
		String dtd = "http://matsim.org/files/dtd/multilayerNetwork_v1.dtd";
		MultilayerInstanceWriterHandler handler = new MultilayerInstanceWriterHandlerImpl_v1(experiment);
		
		openFile(filename);
		writeXmlHead();
		writeDoctype("multilayerNetwork", dtd);
			
		try {
			handler.startInstance(writer);
			
			/* Physical network */
			DirectedGraph<NmvNode, NmvLink> physicalNetwork = experiment.getPhysicalNetwork();
			handler.startPhysicalNetwork(writer);
			handler.startPhysicalNodes(writer);
			/*TODO Write all physical nodes. */
				Map<Integer, NmvNode> sortedPhysicalNodes = sortNodes(physicalNetwork.getVertices());
				for(Integer i : sortedPhysicalNodes.keySet()){
					handler.startPhysicalNode(writer, sortedPhysicalNodes.get(i));
					handler.endPhysicalNode(writer);
				}
			handler.endPhysicalNodes(writer);
			handler.startPhysicalEdges(writer);
			/*TODO Write all physical edges. */
				Map<Integer, Map<Integer, NmvLink>> sortedPhysicalLinks = sortLinks(physicalNetwork.getEdges());
				for(Integer i : sortedPhysicalLinks.keySet()){
					Map<Integer, NmvLink> thisMap = sortedPhysicalLinks.get(i);
					for(Integer j : thisMap.keySet()){
						NmvLink link = sortedPhysicalLinks.get(i).get(j);
						handler.startPhysicalEdge(writer, link);
						handler.endPhysicalEdge(writer);
					}
				}
			handler.endPhysicalEdges(writer);
			handler.endPhysicalNetwork(writer);
			
			/* Logical network */
			handler.startLogicalNetwork(writer);
			handler.startLogicalNodes(writer);
			/* TODO Write all logical nodes. */
				handler.startLogicalNode(writer);
				handler.endLogicalNode(writer);
			handler.endLogicalNodes(writer);
			handler.startLogicalEdges(writer);
			/* TODO Write all logical egdes. */
				handler.startLogicalEdge(writer);
				handler.endLogicalEdge(writer);
			handler.endLogicalEdges(writer);
			handler.endLogicalNetwork(writer);
			
			/* Association lists */
			handler.startAssociations(writer);
			/* TODO Write all associations. */
				handler.startAssociation(writer);
				handler.endAssociation(writer);
			handler.endAssociations(writer);
			
			/* Shortest path sets. */
			handler.startSets(writer);
			/* TODO Write all sets */
				handler.startSet(writer);
				/* TODO Write all paths */
				handler.startPath(writer);
				handler.endPath(writer);
				handler.endSet(writer);
			handler.endSets(writer);
			
			handler.endInstance(writer);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot write XML for multilayered network.");
		}finally{
			try {
				this.writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot close XML writer.");
			}
		}
//			handler.startVehicles(this.vehicles, this.writer);
//			for(DigicoreVehicle vehicle : this.vehicles.getVehicles().values()){
//				handler.startVehicle(vehicle, this.writer);
//				for(DigicoreChain chain : vehicle.getChains()){
//					handler.startChain(this.writer);
//					List<DigicoreActivity> activities = chain.getAllActivities();
//					for(DigicoreActivity activity : activities){
//						handler.startActivity(activity, this.writer);
//						handler.endActivity(this.writer);
//					}
//					handler.endChain(this.writer);
//				}
//				handler.endVehicle(this.writer);
//				counter.incCounter();
//			}
//			counter.printCounter();
//			handler.endVehicles(this.writer);
	}
	
	private Map<Integer, NmvNode> sortNodes(Collection<NmvNode> nodes){
		Map<Integer, NmvNode> sortedMap = new TreeMap<Integer, NmvNode>();
		Iterator<NmvNode> iterator = nodes.iterator();
		while(iterator.hasNext()){
			NmvNode node = iterator.next();
			int id = Integer.parseInt(node.getId());
			sortedMap.put(id, node);
		}
		return sortedMap;
	}
	
	
	private Map<Integer, Map<Integer, NmvLink>> sortLinks(Collection<NmvLink> links){
		Map<Integer, Map<Integer, NmvLink>> sortedLinks = new TreeMap<>();
		Iterator<NmvLink> iterator = links.iterator();
		while(iterator.hasNext()){
			NmvLink link = iterator.next();
			String[] sa = link.getId().split("_");
			Integer from = Integer.parseInt(sa[0]);
			Integer to = Integer.parseInt(sa[1]);
			if(!sortedLinks.containsKey(from)){
				Map<Integer, NmvLink> newMap = new TreeMap<>();
				sortedLinks.put(from, newMap);
			}
			sortedLinks.get(from).put(to, link);
		}
		return sortedLinks;
	}
	
	
}

