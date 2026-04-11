package com.flighttracker.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanespottersResponse {
	
	private List<Photo> photos;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Photo {
        private Thumbnail thumbnail_large;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Thumbnail {
        private String src;
    }

}
