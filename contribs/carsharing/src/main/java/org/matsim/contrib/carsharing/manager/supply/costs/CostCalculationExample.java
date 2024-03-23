package org.matsim.contrib.carsharing.manager.supply.costs;

import org.matsim.contrib.carsharing.manager.demand.RentalInfo;
/**
 * @author balac
 */
public class CostCalculationExample implements CostCalculation {

	private final static double betaTT = 1.0;
	private final static double betaRentalTIme = 1.0;
	private final static double scaleTOMatchCar = 4.0;

	@Override
	public double getCost(RentalInfo rentalInfo) {

		double rentalTIme = rentalInfo.getEndTime() - rentalInfo.getStartTime();
		double inVehicleTime = rentalInfo.getInVehicleTime();
		double rentalDistance = rentalInfo.getDistance();

		return CostCalculationExample.scaleTOMatchCar *
				(inVehicleTime /3600.0 * 0.3) + (rentalDistance/ 1000.0 * 0.42);
	}

}
