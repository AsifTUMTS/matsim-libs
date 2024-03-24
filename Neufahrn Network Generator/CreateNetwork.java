package org.matsim.pt2matsim.run;

import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.pt2matsim.run.Osm2MultimodalNetwork;

public class CreateNetwork {

    public static void main(String[] args) {
        Osm2MultimodalNetwork.run("example/Neufahrn/Neufahrn.osm", "example/Neufahrn/NeufahrnMultimodel.xml", TransformationFactory.DHDN_GK4);
    }
}
