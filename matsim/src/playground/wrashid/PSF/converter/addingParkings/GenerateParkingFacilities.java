package playground.wrashid.PSF.converter.addingParkings;

import org.matsim.api.basic.v01.Id;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.facilities.ActivityFacilitiesImpl;
import org.matsim.core.facilities.ActivityFacilitiesImpl;
import org.matsim.core.facilities.ActivityFacilityImpl;
import org.matsim.core.facilities.FacilitiesWriter;
import org.matsim.core.facilities.Facility;
import org.matsim.core.facilities.algorithms.AbstractFacilityAlgorithm;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.network.NetworkLayer;
import org.matsim.core.population.MatsimPopulationReader;
import org.matsim.core.population.PopulationImpl;
import org.matsim.core.population.PopulationReader;
import org.matsim.core.utils.geometry.CoordImpl;
import org.matsim.facilities.algorithms.FacilitiesWriterAlgorithm;

import playground.wrashid.lib.GeneralLib;

/*
 * For each activity, define a parking and facility (and home...)
 */
public class GenerateParkingFacilities {

	/**
	 * Generate an output facilities file, which contains parking facilities at the same place, as the activities happen.
	 * @param inputPlansFile
	 * @param networkFile
	 * @param outputFacilitiesFile
	 */
	
	public static void generateParkingFacilties(String inputPlansFile,String networkFile, String outputFacilitiesFile){
		Population inPop = (Population) GeneralLib.readPopulation(inputPlansFile, networkFile);

		// generate facilities

		ActivityFacilitiesImpl facilities = new ActivityFacilitiesImpl();

		for (Person person : inPop.getPersons().values()) {

			for (int i = 0; i < person.getSelectedPlan().getPlanElements()
					.size(); i++) {
				if (person.getSelectedPlan().getPlanElements().get(i) instanceof Activity) {
					Activity act = (Activity) person.getSelectedPlan()
							.getPlanElements().get(i);
					Id facilityId=new IdImpl( "facility_" + act.getLinkId().toString());
					
					// add facility only, if it does not already exist
					if (!facilities.getFacilities().containsKey(facilityId)){
						ActivityFacilityImpl facility = facilities.createFacility(facilityId, act.getCoord());
						facility.createActivityOption(act.getType());
						facility.createActivityOption("parkingArrival");
						facility.createActivityOption("parkingDeparture");
					}
					
					//facilities.getFacilities().put(facilityId, facility);
				}
			}
			
		}

		// write facilities out to file
		GeneralLib.writeActivityFacilities(facilities, outputFacilitiesFile);
	}
}
