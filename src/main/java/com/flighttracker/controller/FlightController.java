package com.flighttracker.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flighttracker.dto.AviationstackResponse;
import com.flighttracker.dto.FrontendFlightResponse;
import com.flighttracker.dto.OpenSkyResponse;
import com.flighttracker.service.AviationstackService;
import com.flighttracker.service.OpenSkyService;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
	
	@Autowired
	private OpenSkyService openSkyService;
	@Autowired
	private AviationstackService aviationstackService;
	
	@GetMapping("/live")
	public List<FrontendFlightResponse> getLiveFlight(){
		
		OpenSkyResponse openSkyData = openSkyService.getLiveFlights();
		List<FrontendFlightResponse> cleanFlightList = new ArrayList<>();
		
		if(openSkyData == null || openSkyData.getStates() == null) {
			return cleanFlightList;
		}
		
		for(List<Object> flightArray: openSkyData.getStates()) {
			try {
				FrontendFlightResponse cleanFlight = FrontendFlightResponse.builder()
                        .icao24(flightArray.get(0) != null ? flightArray.get(0).toString() : "")
                        .callsign(flightArray.get(1) != null ? flightArray.get(1).toString().trim() : "")
                        .longitude(flightArray.get(5) != null ? Double.parseDouble(flightArray.get(5).toString()) : 0.0)
                        .latitude(flightArray.get(6) != null ? Double.parseDouble(flightArray.get(6).toString()) : 0.0)
                        .altitude(flightArray.get(7) != null ? Double.parseDouble(flightArray.get(7).toString()) : 0.0)
                        .velocity(flightArray.get(9) != null ? Double.parseDouble(flightArray.get(9).toString()) : 0.0)
                        .trueTrack(flightArray.get(10) != null ? Double.parseDouble(flightArray.get(10).toString()) : 0.0)
                        .build();

                cleanFlightList.add(cleanFlight);
			}catch(Exception e) {
				continue;
			}
		}
		return cleanFlightList;
		
	}
	
	@GetMapping("/route/{flightIata}")
    public AviationstackResponse.FlightData getFlightRoute(@PathVariable String flightIata) {
        // This is called ONLY when a user clicks a plane on the frontend
        return aviationstackService.getRouteDetails(flightIata);
    }
	

}
