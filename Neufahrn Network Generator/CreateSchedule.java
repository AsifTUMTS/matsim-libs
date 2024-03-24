package org.matsim.pt2matsim.run;

import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.pt2matsim.gtfs.GtfsConverter;
import org.matsim.pt2matsim.run.Gtfs2TransitSchedule;

public class CreateSchedule {

    public static void main(String[] args) {
        Gtfs2TransitSchedule.run("example/Neufahrn/mvv_gtfs.zip", GtfsConverter.DAY_WITH_MOST_SERVICES, TransformationFactory.DHDN_GK4, "example/Neufahrn/neufahrnSchedule.xml", "example/Neufahrn/neufahrnVehicles.xml");
    }
}
