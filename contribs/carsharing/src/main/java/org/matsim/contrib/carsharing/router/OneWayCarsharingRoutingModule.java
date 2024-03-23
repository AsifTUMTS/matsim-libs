package org.matsim.contrib.carsharing.router;


/**
 * @author balacm
 *
 **/


import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.router.RoutingRequest;
import org.matsim.facilities.Facility;

public class OneWayCarsharingRoutingModule implements RoutingModule{
	public OneWayCarsharingRoutingModule() {

	}
	@Override
	public List<? extends PlanElement> calcRoute(RoutingRequest request) {
		final Facility fromFacility = request.getFromFacility();
		final Facility toFacility = request.getToFacility();

		final List<PlanElement> trip = new ArrayList<>();

		final Leg leg1 = PopulationUtils.createLeg("oneway");
		CarsharingRoute csRoute = new CarsharingRoute(fromFacility.getLinkId(), toFacility.getLinkId());
		leg1.setRoute(csRoute);
		leg1.setTravelTime(0.0);
		trip.add( leg1 );


		return trip;
	}

}
