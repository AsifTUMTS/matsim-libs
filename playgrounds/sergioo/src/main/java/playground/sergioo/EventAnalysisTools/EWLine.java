package playground.sergioo.EventAnalysisTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.ActivityEndEvent;
import org.matsim.core.api.experimental.events.AgentArrivalEvent;
import org.matsim.core.api.experimental.events.AgentDepartureEvent;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.api.experimental.events.handler.ActivityEndEventHandler;
import org.matsim.core.api.experimental.events.handler.AgentArrivalEventHandler;
import org.matsim.core.api.experimental.events.handler.AgentDepartureEventHandler;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.PersonEntersVehicleEvent;
import org.matsim.core.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.PtConstants;
import org.matsim.pt.transitSchedule.TransitScheduleReaderV1;
import org.matsim.pt.transitSchedule.api.Departure;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitRouteStop;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

public class EWLine implements ActivityEndEventHandler, AgentDepartureEventHandler, PersonEntersVehicleEventHandler, AgentArrivalEventHandler {

	//Classes
	private class EWTrip {
		public Id id;
		public double arriveStationTime;
		public double enterVehicleTime;
		public String departureStation;
		public String arrivalStation;
		public double serviceDepartureTime = -1;
		public String activityType;
	}
	
	//Attributes
	public Map<Id, EWTrip> ewAgentsTravelling = new HashMap<Id, EWTrip>();
	public Set<EWTrip> ewAgents = new HashSet<EWTrip>();
	private final TransitSchedule transitSchedule;
	
	//Constructors
	public EWLine(TransitSchedule transitSchedule) {
		this.transitSchedule = transitSchedule;
	}

	//Methods
	@Override
	public void reset(int iteration) {
		
	}
	@Override
	public void handleEvent(ActivityEndEvent event) {
		if(event.getActType().equals(PtConstants.TRANSIT_ACTIVITY_TYPE) || event.getActType().equals("dummy")) {
			EWTrip trip = ewAgentsTravelling.get(event.getPersonId());
			if(trip == null) {
				trip = new EWTrip();
				trip.id = event.getPersonId();
				trip.arriveStationTime = event.getTime();
				trip.activityType = event.getActType();
				ewAgentsTravelling.put(event.getPersonId(), trip);
			}
		}
	}
	@Override
	public void handleEvent(AgentDepartureEvent event) {
		EWTrip trip = ewAgentsTravelling.get(event.getPersonId());
		if(trip!=null && event.getLegMode().equals("pt")) {
			SEARCHING:
			for(TransitRoute route:transitSchedule.getTransitLines().get(new IdImpl("EW")).getRoutes().values())
				for(TransitRouteStop stop:route.getStops())
					if(stop.getStopFacility().getLinkId().equals(event.getLinkId())) {
						trip.departureStation = stop.getStopFacility().getName();
						break SEARCHING;
					}
			if(trip.departureStation==null)
				ewAgentsTravelling.remove(event.getPersonId());
		}
		else if(trip!=null && trip.activityType.equals(PtConstants.TRANSIT_ACTIVITY_TYPE))
			ewAgentsTravelling.remove(event.getPersonId());
	}
	@Override
	public void handleEvent(PersonEntersVehicleEvent event) {
		EWTrip trip = ewAgentsTravelling.get(event.getPersonId());
		if(trip!=null) {
			trip.enterVehicleTime = event.getTime();
			SEARCHING:
			for(TransitRoute route:transitSchedule.getTransitLines().get(new IdImpl("EW")).getRoutes().values())
				for(Departure departure:route.getDepartures().values())
					if(departure.getVehicleId().equals(event.getVehicleId())) {
						trip.serviceDepartureTime = departure.getDepartureTime();
						break SEARCHING;
					}
			if(trip.serviceDepartureTime==-1)
				ewAgentsTravelling.remove(event.getPersonId());
		}
	}
	@Override
	public void handleEvent(AgentArrivalEvent event) {
		EWTrip trip = ewAgentsTravelling.get(event.getPersonId());
		if(trip!=null) {
			if(event.getLegMode().equals("pt")) {
				SEARCHING:
				for(TransitRoute route:transitSchedule.getTransitLines().get(new IdImpl("EW")).getRoutes().values())
					for(TransitRouteStop stop:route.getStops())
						if(stop.getStopFacility().getLinkId().equals(event.getLinkId())) {
							trip.arrivalStation = stop.getStopFacility().getName();
							ewAgents.add(trip);
							break SEARCHING;
						}
				ewAgentsTravelling.remove(event.getPersonId());
			}
		}	
	}

	//Main
	public static void main(String[] args) throws FileNotFoundException {
		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		new MatsimNetworkReader(scenario).parse("./data/MATSim-Sin-2.0/input/network/singapore7.xml");
		scenario.getConfig().scenario().setUseTransit(true);
		new TransitScheduleReaderV1(scenario).parse("./data/EWService/transitScheduleWAM_MRT_EW_Modified.xml");
		EWLine ewLine = new EWLine(scenario.getTransitSchedule());
		EventsManager events = (EventsManager)EventsUtils.createEventsManager();
		events.addHandler(ewLine);
		new MatsimEventsReader(events).readFile("./data/EWService/0.events.xml.gz");
		System.out.println("Events read "+ewLine.ewAgentsTravelling.size()+" "+ewLine.ewAgents.size());
		PrintWriter writer = new PrintWriter(new File("./data/EWService/ewDemand.csv"));
		for(EWTrip trip:ewLine.ewAgentsTravelling.values())
			System.out.print(trip.departureStation+",");
		System.out.println();
		for(EWTrip trip:ewLine.ewAgents)
			writer.println(trip.id+","+trip.arriveStationTime+","+trip.departureStation+","+trip.enterVehicleTime+","+trip.arrivalStation+","+trip.serviceDepartureTime);
		writer.close();
	}

}
