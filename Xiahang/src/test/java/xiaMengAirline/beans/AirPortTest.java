package xiaMengAirline.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class AirPortTest {

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testGetMatchedAirports() throws CloneNotSupportedException {
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

		HashMap<Flight, List<MatchedFlight>> matchedFlights = air1.getMatchedFlights(air2);

		System.out.println("testGetMatchedAirports - start");
		for (Map.Entry<Flight, List<MatchedFlight>> entry : matchedFlights.entrySet()) {
			Flight key = entry.getKey();
			List<MatchedFlight> value = entry.getValue();

			System.out.println(key.getSchdNo());
			for (MatchedFlight aConn : value) {
				System.out.println("air1 source:" + air1.getFlight(aConn.getAir1SourceFlight()).getSchdNo());
				System.out.println("air1 dest:" + air1.getFlight(aConn.getAir1DestFlight()).getSchdNo());
				System.out.println("air2 source:" + air2.getFlight(aConn.getAir2SourceFlight()).getSchdNo());
				System.out.println("air2 dest:" + air2.getFlight(aConn.getAir2DestFlight()).getSchdNo());

				Aircraft newAircraft1 = air1.clone();
				Aircraft newAircraft2 = air2.clone();
				Flight air1SourceFlight = newAircraft1.getFlight(aConn.getAir1SourceFlight());
				Flight air1DestFlight = newAircraft1.getFlight(aConn.getAir1DestFlight());
				Flight air2SourceFlight = newAircraft2.getFlight(aConn.getAir2SourceFlight());
				Flight air2DestFlight = newAircraft2.getFlight(aConn.getAir2DestFlight());
				newAircraft1.insertFlightChain(air2, air2.getFlight(aConn.getAir2SourceFlight()), 
						air2.getFlight(aConn.getAir2DestFlight()),
						air1DestFlight, false);
				newAircraft2.insertFlightChain(air1, air1.getFlight(aConn.getAir1SourceFlight()), 
						air1.getFlight(aConn.getAir1DestFlight()),
						air2DestFlight, false);
				newAircraft1.removeFlightChain(air1SourceFlight, 
						air1DestFlight);
				newAircraft2.removeFlightChain(air2SourceFlight,air2DestFlight);
				List<Flight> updateList1 = newAircraft1.getFlightChain();
				System.out.println("After exchange ...");
				for (Flight aF : updateList1) {
					System.out.println("Air 1 " + aF.getSchdNo());
				}
				List<Flight> updateList2 = newAircraft2.getFlightChain();
				for (Flight aF : updateList2) {
					System.out.println("Air 2 " + aF.getSchdNo());
				}

			}
		}

		System.out.println("testGetMatchedAirports - end");
	}

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

	@Test
	public void testGetCircuitAirports() throws CloneNotSupportedException {
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

		HashMap<Flight, List<Flight>> circuitFlightsAir1 = air1.getCircuitFlights();
		HashMap<Flight, List<Flight>> circuitFlightsAir2 = air2.getCircuitFlights();

		for (Map.Entry<Flight, List<Flight>> entry : circuitFlightsAir1.entrySet()) {
			Flight key = entry.getKey();
			List<Flight> value = entry.getValue();

			for (Flight aFlight : value) {
				System.out.println("Air1 " + key.getSchdNo() + " => " + aFlight.getSchdNo());
				Aircraft newAir1 = air1.clone();
				Aircraft cancelledAir = newAir1.getCancelledAircraft();
				Flight sourceFlight = newAir1.getFlight(air1.getFlightChain().indexOf(key));
				Flight destFlight = newAir1.getFlight(air1.getFlightChain().indexOf(aFlight));
				

				cancelledAir.insertFlightChain(air1, key, aFlight,
						cancelledAir.getFlight(cancelledAir.getFlightChain().size() - 1), false);
				newAir1.removeFlightChain(sourceFlight, destFlight);
				
				System.out.println("After exchange ...");
				List<Flight> updateList1 = newAir1.getFlightChain();
				for (Flight aF : updateList1) {
					System.out.println("Air 1 " + aF.getSchdNo());
				}
				List<Flight> updateList2 = cancelledAir.getFlightChain();
				for (Flight aF : updateList2) {
					System.out.println("Air 1 cancelled " + aF.getSchdNo());
				}
			}
		}

		for (Map.Entry<Flight, List<Flight>> entry : circuitFlightsAir2.entrySet()) {
			Flight key = entry.getKey();
			List<Flight> value = entry.getValue();

			for (Flight aFlight : value) {
				System.out.println("Air2 " + key.getSchdNo() + " => " + aFlight.getSchdNo());

				Aircraft newAircraft1 = air1.clone();
				Aircraft newAircraft2 = air2.clone();
				Flight air2SourceFlight = newAircraft2.getFlight(air2.getFlightChain().indexOf(key));
				Flight air2DestFlight = newAircraft2.getFlight(air2.getFlightChain().indexOf(aFlight));
				Flight air1Flight = newAircraft1.getFlight(1);

				newAircraft1.insertFlightChain(air2, key, aFlight,
						air1Flight, true);
				newAircraft2.removeFlightChain(air2SourceFlight, air2DestFlight);
				
				System.out.println("After exchange ...");
				List<Flight> updateList1 = newAircraft1.getFlightChain();
				for (Flight aF : updateList1) {
					System.out.println("Air 1 " + aF.getSchdNo());
				}
				List<Flight> updateList2 = newAircraft2.getFlightChain();
				for (Flight aF : updateList2) {
					System.out.println("Air 2 " + aF.getSchdNo());
				}
			}
		}

		// ArrayList<AirPort> listAirportB = new ArrayList<AirPort> ();
		// AirPort aAir = new AirPort();
		//
		// aAir = new AirPort();
		// aAir.setId("32");
		// listAirportB.add(aAir);
		//
		// aAir = new AirPort();
		// aAir.setId("49");
		// listAirportB.add(aAir);
		//
		// aAir = new AirPort();
		// aAir.setId("16");
		// listAirportB.add(aAir);
		//
		// aAir = new AirPort();
		// aAir.setId("49");
		// listAirportB.add(aAir);
		//
		// aAir = new AirPort();
		// aAir.setId("25");
		// listAirportB.add(aAir);
		//
		// aAir = new AirPort();
		// aAir.setId("66");
		// listAirportB.add(aAir);
		//
		// aAir = new AirPort();
		// aAir.setId("49");
		// listAirportB.add(aAir);
		//
		// HashMap<Integer, List<Integer>> listCircuit =
		// AirPort.getCircuitAirports(listAirportB);
		// for(Map.Entry<Integer, List<Integer>> entry : listCircuit.entrySet())
		// {
		// Integer key = entry.getKey();
		// List<Integer> value = entry.getValue();
		//
		// System.out.println(key + " => " + value);
		// }

	}

}
