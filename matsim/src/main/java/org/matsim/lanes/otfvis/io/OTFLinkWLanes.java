/* *********************************************************************** *
 * project: org.matsim.*
 * OTFLanesLinkData
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2010 by the members listed in the COPYING,        *
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
package org.matsim.lanes.otfvis.io;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.signalsystems.otfvis.io.OTFSignal;


/**
 * @author dgrether
 *
 */
public class OTFLinkWLanes implements Serializable{

	private Point2D.Double linkStart = null;
	private Point2D.Double linkEnd = null;
	private Point2D.Double normalizedLinkVector;
	private Point2D.Double linkOrthogonalVector;
	private double numberOfLanes = 1.0;
	private int maximalAlignment = 0;
	private Map<String, OTFLane> laneData =  null;
	private String id = null;
	private double linkWidth;
	private Point2D.Double linkStartCenterPoint = null;
	private Point2D.Double linkEndCenterPoint = null;
	private Map<String, OTFSignal> signals = null;
	private ArrayList<Id> toLinkIds;
	private transient List<OTFLinkWLanes> toLinks = null;
	
	public OTFLinkWLanes(String id){
		this.id = id;
	}
	
	public String getLinkId() {
		return this.id;
	}
	
	public void setLinkStart(Point2D.Double v) {
		this.linkStart = v;
	}

	public void setLinkEnd(Point2D.Double v) {
		this.linkEnd = v;
	}

	public void setNormalizedLinkVector(Point2D.Double v) {
		this.normalizedLinkVector = v;
	}
	
	public void setLinkOrthogonalVector(Point2D.Double v){
		this.linkOrthogonalVector = v;
	}
	
	public Point2D.Double getLinkStart() {
		return linkStart;
	}

	
	public Point2D.Double getLinkEnd() {
		return linkEnd;
	}

	
	public Double getNormalizedLinkVector() {
		return normalizedLinkVector;
	}

	
	public Double getLinkOrthogonalVector() {
		return linkOrthogonalVector;
	}

	public void setNumberOfLanes(double nrLanes) {
		this.numberOfLanes = nrLanes;
	}

	public double getNumberOfLanes() {
		return this.numberOfLanes;
	}

	public void setMaximalAlignment(int maxAlign) {
		this.maximalAlignment = maxAlign;
	}

	public int getMaximalAlignment(){
		return this.maximalAlignment;
	}
	
	public void addLaneData(OTFLane laneData){
		if (this.laneData == null){
			this.laneData = new HashMap<String, OTFLane>();
		}
		this.laneData .put(laneData.getId(), laneData);
	}
	
	public Map<String, OTFLane> getLaneData(){
		return this.laneData;
	}

	public void addSignal(OTFSignal signal) {
		if (this.signals == null){
			this.signals = new HashMap<String, OTFSignal>();
		}
		this.signals.put(signal.getId(), signal);
	}
	
	public Map<String, OTFSignal> getSignals(){
		return this.signals;
	}
	
	public void setLinkWidth(double linkWidth) {
		this.linkWidth = linkWidth;
	}
	
	public double getLinkWidth(){
		return this.linkWidth;
	}

	public void setLinkStartCenterPoint(Point2D.Double linkStartCenter) {
		this.linkStartCenterPoint = linkStartCenter;
	}
	
	public Point2D.Double getLinkStartCenterPoint() {
		return this.linkStartCenterPoint;
	}
	
	public void setLinkEndCenterPoint(Point2D.Double linkStartCenter) {
		this.linkEndCenterPoint = linkStartCenter;
	}
	
	public Point2D.Double getLinkEndCenterPoint() {
		return this.linkEndCenterPoint;
	}
	
	public void addToLink(OTFLinkWLanes link){
		if (this.toLinks == null){
			this.toLinks = new ArrayList<OTFLinkWLanes>();
		}
		this.toLinks.add(link);
	}

	public List<OTFLinkWLanes> getToLinks() {
		return this.toLinks ;
	}
	
	public void addToLinkId(Id toLinkId){
		if (this.toLinkIds == null)
			this.toLinkIds = new ArrayList<Id>();
		this.toLinkIds.add(toLinkId);
	}

	public List<Id> getToLinkIds() {
		return toLinkIds ;
	}
	
}
