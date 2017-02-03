/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2016 by the members listed in the COPYING,        *
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

package org.matsim.contrib.dvrp.examples.onetaxi;

import java.util.Collection;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.dvrp.data.Fleet;
import org.matsim.contrib.dvrp.passenger.PassengerEngine;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentSource;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.framework.Mobsim;
import org.matsim.core.mobsim.qsim.*;

import com.google.inject.*;


public class OneTaxiQSimProvider
    implements Provider<Mobsim>
{
    private final Scenario scenario;
    private final EventsManager events;
    private final Collection<AbstractQSimPlugin> plugins;

    private final Fleet fleet;


    @Inject
    public OneTaxiQSimProvider(Scenario scenario, EventsManager events,
            Collection<AbstractQSimPlugin> plugins, Fleet fleet)
    {
        this.scenario = scenario;
        this.events = events;
        this.plugins = plugins;
        this.fleet = fleet;
    }


    @Override
    public Mobsim get()
    {
        QSim qSim = QSimUtils.createQSim(scenario, events, plugins);
        
        OneTaxiOptimizer optimizer = new OneTaxiOptimizer(scenario, fleet, qSim.getSimTimer());

        PassengerEngine passengerEngine = new PassengerEngine(RunOneTaxiExample.MODE, events,
                new OneTaxiRequestCreator(), optimizer, scenario.getNetwork());
        qSim.addMobsimEngine(passengerEngine);
        qSim.addDepartureHandler(passengerEngine);

        OneTaxiActionCreator actionCreator = new OneTaxiActionCreator(passengerEngine,
                qSim.getSimTimer());
        qSim.addAgentSource(new VrpAgentSource(actionCreator, fleet, optimizer, qSim));

        return qSim;
    }
}
