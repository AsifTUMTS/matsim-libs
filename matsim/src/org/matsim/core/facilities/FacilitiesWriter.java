/* *********************************************************************** *
 * project: org.matsim.*
 * FacilitiesWriter.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
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

package org.matsim.core.facilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;

import org.matsim.core.basic.v01.facilities.BasicOpeningTime;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.io.Writer;

public class FacilitiesWriter extends Writer {

	private FacilitiesWriterHandler handler = null;
	private final ActivityFacilitiesImpl facilities;

	//////////////////////////////////////////////////////////////////////
	// constructors
	//////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new FacilitiesWriter to write the specified facilities to the file
	 * specified as output-file in the current configuration.
	 *
	 * @param facilities
	 */
	public FacilitiesWriter(final ActivityFacilitiesImpl facilities) {
		this(facilities, Gbl.getConfig().facilities().getOutputFile());
	}

	/**
	 * Creates a new FacilitiesWriter to write the specified facilities to the specified file.
	 *
	 * @param facilities
	 * @param filename
	 */
	public FacilitiesWriter(final ActivityFacilitiesImpl facilities, final String filename) {
		super();
		this.facilities = facilities;
		this.outfile = filename;
		// always use newest version, currently v1
		this.dtd = "http://www.matsim.org/files/dtd/facilities_v1.dtd";
		this.handler = new FacilitiesWriterHandlerImplV1();
	}

	//////////////////////////////////////////////////////////////////////
	// write methods
	//////////////////////////////////////////////////////////////////////

	@Override
	public final void write() {
		this.writeOpenAndInit();
		for (ActivityFacilityImpl f : this.facilities.getFacilities().values()) {
			this.writeFacility(f);
		}
		this.writeFinish();
	}

	public final void writeOpenAndInit() {
		try {
			this.out = IOUtils.getBufferedWriter(this.outfile);
			writeDtdHeader("facilities");
			this.handler.startFacilities(this.facilities, this.out);
			this.handler.writeSeparator(this.out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final void writeFacility(final ActivityFacilityImpl f) {
		try {
			this.handler.startFacility(f, this.out);
			Iterator<ActivityOption> a_it = f.getActivityOptions().values().iterator();
			while (a_it.hasNext()) {
				ActivityOption a = a_it.next();
				this.handler.startActivity(a, this.out);
				this.handler.startCapacity(a, this.out);
				this.handler.endCapacity(this.out);
				Iterator<SortedSet<BasicOpeningTime>> o_set_it = a.getOpeningTimes().values().iterator();
				while (o_set_it.hasNext()) {
					SortedSet<BasicOpeningTime> o_set = o_set_it.next();
					Iterator<BasicOpeningTime> o_it = o_set.iterator();
					while (o_it.hasNext()) {
						BasicOpeningTime o = o_it.next();
						this.handler.startOpentime(o, this.out);
						this.handler.endOpentime(this.out);
					}
				}
				this.handler.endActivity(this.out);
			}
			this.handler.endFacility(this.out);
			this.handler.writeSeparator(this.out);
			this.out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final void writeFinish() {
		try {
			this.handler.endFacilities(this.out);
			this.out.flush();
			this.out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//////////////////////////////////////////////////////////////////////
	// print methods
	//////////////////////////////////////////////////////////////////////

	@Override
	public final String toString() {
		return super.toString();
	}
}
