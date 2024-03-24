package org.matsim.pt2matsim.run;

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.utils.collections.CollectionUtils;
import org.matsim.pt2matsim.config.PublicTransitMappingConfigGroup;
import org.matsim.pt2matsim.run.CreateDefaultPTMapperConfig;
import org.matsim.pt2matsim.run.PublicTransitMapper;

public class MapSchedule2Network {

    public static void main(String[] args) {
        // Create a mapping config:
        CreateDefaultPTMapperConfig.main(new String[]{ "example/Neufahrn/config.xml"});
        // Open the mapping config and set the parameters to the required values
        // (usually done manually by opening the config with a simple editor)
        Config config = ConfigUtils.loadConfig(
                "example/Neufahrn/config.xml",
                PublicTransitMappingConfigGroup.createDefaultConfig());
        PublicTransitMappingConfigGroup ptmConfig = ConfigUtils.addOrGetModule(config, PublicTransitMappingConfigGroup.class);

        ptmConfig.setInputNetworkFile("example/Neufahrn/NeufahrnMultimodel.xml");
        ptmConfig.setOutputNetworkFile("example/Neufahrn/NeufahrnMultimodalMapped.xml.gz");
        ptmConfig.setOutputStreetNetworkFile("example/Neufahrn/Neufahrn_streetnetwork.xml.gz");
        ptmConfig.setInputScheduleFile("example/Neufahrn/neufahrnSchedule.xml");
        ptmConfig.setOutputScheduleFile("example/Neufahrn/NeufahrnScheduleMapped.xml");
        ptmConfig.setScheduleFreespeedModes(CollectionUtils.stringToSet("rail, light_rail"));
        // Save the mapping config
        // (usually done manually)
        new ConfigWriter(config).write("example/Neufahrn/config.xml");
        PublicTransitMapper.run("example/Neufahrn/config.xml");
    }
}
