/* *********************************************************************** *
 * project: org.matsim.*
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
package playground.wrashid.parkingSearch.ppSim.jdepSim.zurich;

import java.util.LinkedList;

import org.matsim.api.core.v01.Id;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;

import playground.wrashid.lib.obj.TwoHashMapsConcatenated;
import playground.wrashid.parkingSearch.ppSim.jdepSim.searchStrategies.RandomGarageParkingSearch;
import playground.wrashid.parkingSearch.ppSim.jdepSim.searchStrategies.RandomStreetParkingSearch;
import playground.wrashid.parkingSearch.ppSim.jdepSim.searchStrategies.analysis.ParkingEventDetails;
import playground.wrashid.parkingSearch.ppSim.jdepSim.searchStrategies.analysis.StrategyScoreStats;
import playground.wrashid.parkingSearch.ppSim.jdepSim.searchStrategies.score.ParkingScoreEvaluator;
import playground.wrashid.parkingSearch.withindayFW.utility.ParkingPersonalBetas;

public class ZHScenarioGlobal {

	public static ParkingScoreEvaluator parkingScoreEvaluator;
	public static final String outputFolder = "C:/data/parkingSearch/psim/zurich/output/run3/";;
	public static StrategyScoreStats strategyScoreStats = new StrategyScoreStats();
	public static int iteration = 0;
	public static int numberOfIterations = 1000;
	public static int writeEachNthIteration = 1000;
	public static int skipOutputInIteration = 0;
	public static LinkedList<ParkingEventDetails> parkingEventDetails;
	
	// personId, legIndex, route
	public static TwoHashMapsConcatenated<Id, Integer, LinkNetworkRouteImpl> initialRoutes;

	public static void reset() {
		parkingEventDetails = new LinkedList<ParkingEventDetails>();
	}

	public static void produceOutputStats() {
		printGeneralParkingStats();
		printFilteredParkingStatsParkingType("stp");
		printFilteredParkingStatsParkingType("gp");
		printFilteredParkingStatsParkingType(RandomStreetParkingSearch.class);
		printFilteredParkingStatsParkingType(RandomGarageParkingSearch.class);
	}
	
	private static void printFilteredParkingStatsParkingType(Class c) {
		double averageParkingDuration = 0;
		double averageSearchDuration = 0;
		double averageWalkDuration = 0;

		System.out.println("stats for parking strategy: " + c.getName());

		int numberOfParkingOperations = 0;
		for (ParkingEventDetails ped : parkingEventDetails) {
			if (ped.parkingStrategy.getClass().isAssignableFrom(c)) {
				averageParkingDuration += ped.parkingActivityAttributes.getParkingDuration();
				averageSearchDuration += ped.parkingActivityAttributes.getParkingSearchDuration();
				averageWalkDuration += ped.parkingActivityAttributes.getToActWalkDuration();
				numberOfParkingOperations++;
			}
		}

		System.out.println("averageParkingDuration: " + averageParkingDuration / numberOfParkingOperations);
		System.out.println("averageSearchDuration: " + averageSearchDuration / numberOfParkingOperations);
		System.out.println("averageWalkDuration: " + averageWalkDuration / numberOfParkingOperations);
		System.out.println("numberOfParkingOperations: " + numberOfParkingOperations);
		System.out.println("========================");
	}

	private static void printFilteredParkingStatsParkingType(String type) {
		double averageParkingDuration = 0;
		double averageSearchDuration = 0;
		double averageWalkDuration = 0;

		System.out.println("stats for parking type: " + type);

		int numberOfParkingOperations = 0;
		for (ParkingEventDetails ped : parkingEventDetails) {
			if (ped.parkingActivityAttributes.getFacilityId().toString().contains(type)) {
				averageParkingDuration += ped.parkingActivityAttributes.getParkingDuration();
				averageSearchDuration += ped.parkingActivityAttributes.getParkingSearchDuration();
				averageWalkDuration += ped.parkingActivityAttributes.getToActWalkDuration();
				numberOfParkingOperations++;
			}
		}

		System.out.println("averageParkingDuration: " + averageParkingDuration / numberOfParkingOperations);
		System.out.println("averageSearchDuration: " + averageSearchDuration / numberOfParkingOperations);
		System.out.println("averageWalkDuration: " + averageWalkDuration / numberOfParkingOperations);
		System.out.println("numberOfParkingOperations: " + numberOfParkingOperations);
		System.out.println("========================");
	}

	private static void printGeneralParkingStats() {
		double averageParkingDuration = 0;
		double averageSearchDuration = 0;
		double averageWalkDuration = 0;

		System.out.println("general parking stats");
		
		for (ParkingEventDetails ped : parkingEventDetails) {
			averageParkingDuration += ped.parkingActivityAttributes.getParkingDuration();
			averageSearchDuration += ped.parkingActivityAttributes.getParkingSearchDuration();
			averageWalkDuration += ped.parkingActivityAttributes.getToActWalkDuration();
		}

		int numberOfParkingOperations = parkingEventDetails.size();
		System.out.println("averageParkingDuration: " + averageParkingDuration / numberOfParkingOperations);
		System.out.println("averageSearchDuration: " + averageSearchDuration / numberOfParkingOperations);
		System.out.println("averageWalkDuration: " + averageWalkDuration / numberOfParkingOperations);
		System.out.println("numberOfParkingOperations: " + numberOfParkingOperations);
		System.out.println("========================");
	}
}
