package xiaMengAirline.beans;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class InitSolution {
	private List<Aircraft> aircraftList;
	private HashMap<String, Integer> parkedAircrafts;
	private final int passTime = 50;
	
	public InitSolution(){
		aircraftList = new ArrayList<Aircraft>();
		parkedAircrafts = new HashMap<String, Integer>();
	}
	public List<Aircraft> getInitialSolution(List<Aircraft> acs, List<AirPort> aps) throws Exception{
		List<AirPort> affectedAps = new ArrayList<AirPort>();
		for (AirPort ap : aps){
			if (ap.getCloseSchedule().size() > 0){
				affectedAps.add(ap);
				parkedAircrafts.put(ap.getId(), 0);
			}
		}
		
		List<Aircraft> affectedAircraft = new ArrayList<Aircraft>();
		for (Aircraft ac : acs){
			List<Flight> flights = ac.getFlightChain();
			for (Flight flight : flights){
				if (isFlightAffected(flight, affectedAps)){
					affectedAircraft.add(ac);
				}else{
					aircraftList.add(ac);
				}
			}
		}
		
		for (Aircraft ac : affectedAircraft){
			adjustAircraftFlightChain(ac, affectedAps);
		}
		
		
		return aircraftList;
	}
	
	private void adjustAircraftFlightChain(Aircraft aircraft, List<AirPort> affectedArports) throws CloneNotSupportedException{
		Aircraft aAircraft = aircraft.clone();
		List<Flight> flights = aAircraft.getFlightChain();
		flightLoop:
		for (int i = 0; i < flights.size(); i++){
			Flight aFlight = flights.get(i);
			AirPort ap = isFlightDepartureAffected(aFlight, affectedArports);
			if (ap != null){
				for (AirPortClose apc : ap.getCloseSchedule()){
					if (!apc.isAllowForTakeoff()){
						if (aFlight.getPlannedDepartureTime().compareTo(addHour(apc.getStartTime(), 6)) < 0) {
							Date newDepartTime = apc.getStartTime();
							if (i == 0 || isAllowedInAdv(newDepartTime, flights.get(i - 1).getPlannedArrivalTime())){
								aFlight.setAdjustedDepartureTime(newDepartTime);
								continue flightLoop;
							}
						}else{
							if (i == 0){
								parkedAircrafts.put(ap.getId(), parkedAircrafts.get(ap.getId()) + 1);
								aFlight.setAdjustedDepartureTime(apc.getEndTime());
								long adjustTime = getTimeDifference(aFlight.getPlannedDepartureTime(), apc.getEndTime());
								Date newArriveTime = setAdjustedTime(aFlight.getPlannedArrivalTime(), adjustTime); 
								aFlight.setAdjustedArrivalTime(newArriveTime);
								aAircraft = shiftFlightBehind(aAircraft);
							}else{
								Flight lastFlight = flights.get(i - 1);
								lastFlight.setAdjustedArrivalTime(apc.getEndTime());
								long adjustTime = getTimeDifference(lastFlight.getPlannedArrivalTime(), lastFlight.getAdjustedArrivalTime());
								lastFlight.setAdjustedDepartureTime(setAdjustedTime(lastFlight.getPlannedDepartureTime(), adjustTime));
								aFlight.setAdjustedDepartureTime(setAdjustedTime(apc.getEndTime(), 50));
								adjustTime = getTimeDifference(aFlight.getPlannedArrivalTime(), aFlight.getAdjustedArrivalTime());
								aFlight.setAdjustedArrivalTime(setAdjustedTime(aFlight.getPlannedDepartureTime(), adjustTime));
								aAircraft = shiftFlightBehind(aAircraft);
							}
						}
						
					}else{
						aFlight.setAdjustedArrivalTime(apc.getEndTime());
						long adjustTime = getTimeDifference(aFlight.getPlannedArrivalTime(), aFlight.getAdjustedArrivalTime());
						aFlight.setAdjustedDepartureTime(setAdjustedTime(aFlight.getPlannedDepartureTime(), adjustTime));
						aAircraft = shiftFlightBehind(aAircraft);
					}
					
				}
			}
		}
		aircraftList.add(aAircraft);
	}
	
	private Aircraft shiftFlightBehind(Aircraft aircraft){
		List<Flight> flights = aircraft.getFlightChain();
		boolean detectedDelay = false;
		for (int i = 0 ; i < flights.size(); i++){
			Flight flight = flights.get(i);
			if (detectedDelay){
				Flight lastFlight = flights.get(i-1);
				if (isAffectedByPreviousFlight(flight.getPlannedDepartureTime(), lastFlight.getAdjustedArrivalTime())){
					flight.setAdjustedDepartureTime(setAdjustedTime(lastFlight.getAdjustedArrivalTime(), passTime));
					long adjustTime = getTimeDifference(flight.getPlannedDepartureTime(), flight.getAdjustedDepartureTime());
					Date newArriveTime = setAdjustedTime(flight.getPlannedArrivalTime(), adjustTime);
					flight.setAdjustedArrivalTime(newArriveTime);
				}else{
					detectedDelay = false;
				}
			}else{
				if (flight.getAdjustedDepartureTime() != null && 
						flight.getAdjustedDepartureTime().compareTo(flight.getPlannedDepartureTime()) > 0){
					detectedDelay = true;
				}
			}
		}
		return aircraft;
	}
	
	private boolean isAffectedByPreviousFlight(Date thisDepart, Date lastArrive){
		if (getTimeDifference(lastArrive, thisDepart) < passTime){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean isAllowedInAdv(Date thisDepartTime, Date lastLandTime){
		if ((thisDepartTime.getTime() - lastLandTime.getTime()) / 60000 > passTime){
			return false;
		}else{
			return true;
		}
	}
	
	private Date addHour(Date date, int hour){
		Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.HOUR_OF_DAY, hour); 
		
		return cal.getTime();
	}
	
	private boolean isFlightAffected(Flight flight, List<AirPort> affectedAirport){
		for (AirPort airport : affectedAirport){
			if (airport.getId() == flight.getSourceAirPort().getId() || 
					airport.getId() == flight.getDesintationAirport().getId()){
				for (AirPortClose apc : airport.getCloseSchedule()){
					if ((!apc.isAllowForTakeoff() &&
							(flight.getPlannedDepartureTime().compareTo(apc.getStartTime()) > 0
							|| flight.getPlannedDepartureTime().compareTo(apc.getEndTime()) < 0)) ||
						(!apc.isAllowForLanding() &&
							(flight.getPlannedArrivalTime().compareTo(apc.getStartTime()) > 0
							|| flight.getPlannedArrivalTime().compareTo(apc.getEndTime()) < 0))){
						// flight is affected
						return true;
					}
				}
			}
		}
		return false;

	}
	
	private AirPort isFlightDepartureAffected(Flight flight, List<AirPort> affectedAirport){
		for (AirPort airport : affectedAirport){
			if (airport.getId() == flight.getSourceAirPort().getId() || 
					airport.getId() == flight.getDesintationAirport().getId()){
				for (AirPortClose apc : airport.getCloseSchedule()){
					if (!apc.isAllowForTakeoff() &&
							(flight.getPlannedDepartureTime().compareTo(apc.getStartTime()) > 0
							|| flight.getPlannedDepartureTime().compareTo(apc.getEndTime()) < 0)){
						// flight is affected
						return airport;
					}
				}
			}
		}
		return null;
	}
	
	private boolean isFlightLandingAffected(Flight flight, List<AirPort> affectedAirport){
		for (AirPort airport : affectedAirport){
			if (airport.getId() == flight.getSourceAirPort().getId() || 
					airport.getId() == flight.getDesintationAirport().getId()){
				for (AirPortClose apc : airport.getCloseSchedule()){
					if (!apc.isAllowForLanding() &&
							(flight.getPlannedArrivalTime().compareTo(apc.getStartTime()) > 0
							|| flight.getPlannedArrivalTime().compareTo(apc.getEndTime()) < 0)){
						// flight is affected
						return true;
					}
				}
			}
			
		}
		return false;
	}
	
	private long getTimeDifference(Date startTime, Date endTime){
		return (endTime.getTime() - startTime.getTime()) / (1000*60);
	}
	
	private Date setAdjustedTime(Date origTime, long minute){
		Calendar cal = Calendar.getInstance();
	    cal.setTime(origTime);
	    long t= cal.getTimeInMillis();
	    Date afterAddingMins=new Date(t + (minute * 60000));
		
		return afterAddingMins;
	}
	
	//mock up function
	
	//if airport is not closed due to normal maintain
	private boolean isAirportClose(){
		return false;
	}
	
}
