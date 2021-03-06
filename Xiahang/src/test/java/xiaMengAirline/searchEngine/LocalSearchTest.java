package xiaMengAirline.searchEngine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import xiaMengAirline.beans.AirPort;
import xiaMengAirline.beans.Aircraft;
import xiaMengAirline.beans.Flight;
import xiaMengAirline.beans.XiaMengAirlineSolution;

public class LocalSearchTest {

	private Flight createFlight(int flightId, String srcPort, String destPort) {
		Flight flight = new Flight();
		flight.setSchdNo(flightId);
		AirPort aAirport = new AirPort();
		AirPort bAirport = new AirPort();
		aAirport.setId(srcPort);
		bAirport.setId(destPort);

		flight.setSourceAirPort(aAirport);
		flight.setDesintationAirport(bAirport);

		return flight;
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testConstructNewSolution() throws CloneNotSupportedException {
		Aircraft air1 = new Aircraft();
		List<Flight> flightChain = new ArrayList<Flight>();
		flightChain.add(createFlight(101, "ORF", "EWR"));
		flightChain.add(createFlight(102, "EWR", "STL"));
		flightChain.add(createFlight(103, "STL", "CLE"));
		flightChain.add(createFlight(104, "CLE", "BDL"));
		flightChain.add(createFlight(105, "BDL", "CLE"));
		air1.setFlightChain(flightChain);
		air1.setId("1");

		Aircraft air2 = new Aircraft();
		List<Flight> flightChain2 = new ArrayList<Flight>();
		flightChain2.add(createFlight(201, "CLE", "ATL"));
		flightChain2.add(createFlight(202, "ATL", "EWR"));
		flightChain2.add(createFlight(203, "EWR", "BWI"));
		flightChain2.add(createFlight(204, "BWI", "CLE"));
		flightChain2.add(createFlight(205, "CLE", "MDW"));
		air2.setFlightChain(flightChain2);
		air2.setId("2");
		
		LocalSearch searchEngine = new LocalSearch();
		
		XiaMengAirlineSolution aSolution = new XiaMengAirlineSolution();
		List<Aircraft> airList = new ArrayList<Aircraft> ();
		aSolution.setSchedule(airList);
		airList.add(air1);
		airList.add(air2);
		
		searchEngine.constructNewSolution(aSolution);
	}

}
