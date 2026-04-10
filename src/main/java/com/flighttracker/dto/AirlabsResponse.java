package com.flighttracker.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AirlabsResponse {
    private List<AirlabsFlight> response;
}

@Data
class AirlabsFlight {
    @JsonProperty("flight_icao")
    private String callsign;
    
    private Double lat;
    private Double lng;
    private Double alt;
    private Double speed;
    private Double dir; // True Track
}