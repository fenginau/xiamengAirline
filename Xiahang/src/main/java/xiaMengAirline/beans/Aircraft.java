package xiaMengAirline.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Aircraft implements Cloneable{
	private String id;
	private String type;
	private List<Flight> flightChain;
	private boolean isCancel;
	private Aircraft cancelAircrafted = null;
	private long cost;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<Flight> getFlightChain() {
		return flightChain;
	}
	public Flight getFlight(int position) {
		if (position >= 0)
			return this.flightChain.get(position);
		else	
			return null;
				
	}
	public void setFlightChain(List<Flight> flightChain) {
		this.flightChain = flightChain;
	}
	public void insertFlightChain (Aircraft sourceAircraft, List<Integer> addFlights, int position) {
		List<Flight> newFlights = new ArrayList<Flight> (); 
		for (int anAdd:addFlights) {
			newFlights.add(sourceAircraft.getFlight(anAdd));
		}
		this.flightChain.addAll(position,newFlights );
	}
	public void insertFlightChain (Aircraft sourceAircraft, Flight startFlight, Flight endFlight, Flight insertFlight, boolean isBefore) {
		List<Flight> newFlights = new ArrayList<Flight> (); 
		int addFlightStartPosition = sourceAircraft.getFlightChain().indexOf(startFlight);
		int addFlightEndPosition = sourceAircraft.getFlightChain().indexOf(endFlight);
		int insertFlightPosition = this.flightChain.indexOf(insertFlight);
		for (int i=addFlightStartPosition;i<=addFlightEndPosition;i++) {
			newFlights.add(sourceAircraft.getFlight(i));
		}
		if (insertFlight != null) {
			if (isBefore)
				this.flightChain.addAll(insertFlightPosition,newFlights );
			else
				this.flightChain.addAll(insertFlightPosition+1,newFlights );			
		} else 
			this.flightChain.addAll(newFlights );

	}

	public void removeFlightChain (List<Integer> deleteFlights)  {
		List<Flight> removeList = new ArrayList<Flight> ();
		for (Integer i:deleteFlights) 
			removeList.add(this.flightChain.get(i));

		this.flightChain.removeAll(removeList);
	}
	public void removeFlightChain (Flight startFlight, Flight endFlight)  {
		List<Flight> removeList = new ArrayList<Flight> ();
		int removeSFlighttartPosition = this.flightChain.indexOf(startFlight);
		int removeFlightEndPosition = this.flightChain.indexOf(endFlight);
		
		for (int i=removeSFlighttartPosition; i <= removeFlightEndPosition; i++)
			removeList.add(this.flightChain.get(i));
		
		this.flightChain.removeAll(removeList);
	}
	
	public List<AirPort>  getAirports() {
		ArrayList<AirPort> retAirPortList = new ArrayList<AirPort> ();
		for (Flight aFlight : flightChain) {
			retAirPortList.add(aFlight.getSourceAirPort());
		}
		if (!flightChain.isEmpty()) {
			//add last destination
			retAirPortList.add(flightChain.get(flightChain.size()-1).getDesintationAirport());
		}
		return (retAirPortList);
		
	}
	
	public AirPort getAirport (int position, boolean isSource) {
		if (isSource)
			return (flightChain.get(position).getSourceAirPort());
		else
			return (flightChain.get(position).getDesintationAirport());
	}
	public boolean isCancel() {
		return isCancel;
	}
	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}
	
	public Aircraft clone() throws CloneNotSupportedException{
		Aircraft aNew = (Aircraft) super.clone();
		List<Flight> newFlightChain = new ArrayList<Flight> ();
		for (Flight aFlight:flightChain) {
			newFlightChain.add(aFlight.clone());
		}
		aNew.setFlightChain(newFlightChain);
		return (aNew);
	}
	public long getCost() {
		return cost;
	}
	public void setCost(long cost) {
		this.cost = cost;
	}
	
	public boolean validate () {
		if (!isCancel) {
			for (Flight aFligth:flightChain) {
				aFligth.valdiate();
			}
			return true;			
		} else
			return true;

		
	}
	public void adjustment  () {
		if (!isCancel) {
			
		}
		
	}
	public long refreshCost () {
		if (!isCancel) {
			this.cost = 0;
			return cost;
		} else 
			return 0;

	}
//	public Aircraft getCancelAircrafted() {
//		return cancelAircrafted;
//	}
//	public void setCancelAircrafted(Aircraft cancelAircrafted) {
//		this.cancelAircrafted = cancelAircrafted;
//	}
	public Aircraft getCancelledAircraft() {
		Aircraft retCancelled = cancelAircrafted;
		if (retCancelled == null) {
			retCancelled = new Aircraft();
			retCancelled.setCancel(true);
			retCancelled.setCost(0);
			retCancelled.setFlightChain(new ArrayList<Flight> ());
			retCancelled.setId(this.id);
			retCancelled.setType(this.type);
			this.cancelAircrafted = retCancelled;
		}
		return retCancelled;
	}
	
	public void clear() {
		flightChain.clear();
	}
	
	public HashMap<Flight, List<Flight>> getCircuitFlights () {
		HashMap<Flight, List<Flight>> retCircuitList = new HashMap<Flight, List<Flight>> ();
		
		for (Flight aFlight:flightChain) {
			ArrayList<Flight> matchedList = new ArrayList<Flight> ();
			int currentPos = flightChain.indexOf(aFlight);
			String currentSourceAirport = aFlight.getSourceAirPort().getId();
			for (int j=currentPos+1;j < flightChain.size();j++) {
				String nextDestAirport = flightChain.get(j).getDesintationAirport().getId();
				if (currentSourceAirport.equals(nextDestAirport)) {
					matchedList.add(flightChain.get(j));
				}
			}
			
			if (!matchedList.isEmpty()) 
				retCircuitList.put(aFlight, matchedList);
		}
		
		return (retCircuitList);
		
	}
	
	public HashMap<Flight, List<MatchedFlight>> getMatchedFlights (Aircraft air2) {
		HashMap<Flight, List<MatchedFlight>> retMatchedList = new HashMap<Flight, List<MatchedFlight>> ();
		
		for (Flight aFlight:flightChain) {
			String sourceAirPortAir1 = aFlight.getSourceAirPort().getId();
			for (Flight bFlight:air2.getFlightChain()) {
				String sourceAirPortAir2 = bFlight.getSourceAirPort().getId();
				if (sourceAirPortAir1.equals(sourceAirPortAir2)) {
					List<MatchedFlight> matchedList = new ArrayList<MatchedFlight>();
					for (int i=flightChain.indexOf(aFlight);i < flightChain.size();i++) {
						String airPortA = getFlight(i).getDesintationAirport().getId();
						for (int j=air2.getFlightChain().indexOf(bFlight);j < air2.getFlightChain().size();j++) {
							String airPortB = air2.getFlight(j).getDesintationAirport().getId();
							if (airPortA.equals(airPortB)) {
								MatchedFlight aMatched = new MatchedFlight();
								aMatched.setAir1SourceFlight(flightChain.indexOf(aFlight));
								aMatched.setAir1DestFlight(i);
								aMatched.setAir2SourceFlight(air2.getFlightChain().indexOf(bFlight));
								aMatched.setAir2DestFlight(j);
								matchedList.add(aMatched);
							}
						}
					}
					if (!matchedList.isEmpty()) {
						retMatchedList.put(aFlight, matchedList);
					} else {
						//means source airport overlapped but no destination overlapped
						;
					}
				}
			}
		}
		return retMatchedList;
	}
	

	

}
