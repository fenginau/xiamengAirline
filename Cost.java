package xiaMengAirline.beans;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Map;
import java.util.Map.Entry;

public class Cost {
	private final int flightChangeCost = 5000;
	private final int cancelCost = 10000;
	private final int modelChangeCost = 1000;
	private final int adjustChainFlightCost = 750;
	private final int delayCost = 100;
	private final int inadvanceCost = 150;
	
	public BigDecimal getCost(List<Aircraft> schedule){
		BigDecimal totalCost = new BigDecimal(0);
		for (Aircraft aircraft : schedule){
			totalCost.add(getCost(aircraft));
		}
		return totalCost;
	}
	
	public BigDecimal getCost(Aircraft aircraft){
		BigDecimal totalCost = new BigDecimal(0);
		for (Flight flight : aircraft.getFlightChain()){
			totalCost.add(getCost(flight));
		}
		return totalCost;
	}
	
	public BigDecimal getCost(Flight flight){
		BigDecimal totalCost = new BigDecimal(0);
		if (flight.getIsCancel()){
			totalCost.add(new BigDecimal(cancelCost).multiply(flight.getImpCoe()));
		}else if (flight.getIsFlightAdjust()){
			totalCost.add(new BigDecimal(flightChangeCost));
		}else {
			if (flight.getAdjustedDepartureTime() != null && 
					!flight.getAdjustedDepartureTime().equals(flight.getPlannedDepartureTime())){
				BigDecimal timeDiff = getTimeDifference(flight.getAdjustedDepartureTime(), flight.getPlannedDepartureTime());
				if (timeDiff.compareTo(BigDecimal.ZERO) < 0){
					//delay
					totalCost.add(timeDiff.multiply(new BigDecimal(delayCost)).multiply(flight.getImpCoe()));
				}else{
					totalCost.add(timeDiff.abs().multiply(new BigDecimal(inadvanceCost)).multiply(flight.getImpCoe()));
				}
			}

			if (flight.getModelChanged()){
				totalCost.add(new BigDecimal(modelChangeCost).multiply(flight.getImpCoe()));
			}
			
			if (flight.getIsChain() && !flight.getIsCancel()){
				totalCost.add(new BigDecimal(adjustChainFlightCost).multiply(flight.getImpCoe()));
			}
		}
		return totalCost;
	}
	
	private BigDecimal getTimeDifference(Date startTime, Date endTime){
		return new BigDecimal((endTime.getTime() - startTime.getTime()) / (1000*60*60)).setScale(4, RoundingMode.HALF_UP);
	}
}
