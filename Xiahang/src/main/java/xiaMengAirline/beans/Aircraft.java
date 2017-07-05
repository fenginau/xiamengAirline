package xiaMengAirline.beans;

import java.util.ArrayList;
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
		return this.flightChain.get(position);
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
	public void insertFlightChain (Aircraft sourceAircraft, int addFlightStartPosition, int addFlightEndPosition, int position, boolean isBefore) {
		List<Flight> newFlights = new ArrayList<Flight> (); 
		for (int i=addFlightStartPosition;i<addFlightEndPosition;i++) {
			newFlights.add(sourceAircraft.getFlight(i));
		}
		if (isBefore)
			this.flightChain.addAll(position,newFlights );
		else
			this.flightChain.addAll(position+1,newFlights );
	}

	public void removeFlightChain (List<Integer> deleteFlights)  {
		List<Flight> removeList = new ArrayList<Flight> ();
		for (Integer i:deleteFlights) 
			removeList.add(this.flightChain.get(i));

		this.flightChain.removeAll(removeList);
	}
	public void removeFlightChain (int removeStartPosition, int removeEndPosition)  {
		List<Flight> removeList = new ArrayList<Flight> ();
		
		for (int i=removeStartPosition; i < removeEndPosition; i++)
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
	public Aircraft getCancelAircrafted() {
		return cancelAircrafted;
	}
	public void setCancelAircrafted(Aircraft cancelAircrafted) {
		this.cancelAircrafted = cancelAircrafted;
	}
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
	
	

	

}
