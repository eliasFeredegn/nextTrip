package com.elias.nextTrip.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Schedule implements Serializable {

  public static final long serialVersionUID = 1L;

  @JsonProperty("Actual")
  private boolean actual;

  @JsonProperty("BlockNumber")
  private long blockNumber;

  @JsonProperty("DepartureText")
  private String departureText;

  @JsonProperty("DepartureTime")
  private String departureTime;

  @JsonProperty("Description")
  private String description;

  @JsonProperty("Gate")
  private String gate;

  @JsonProperty("Route")
  private String route;

  @JsonProperty("RouteDirection")
  private String routeDirection;

  @JsonProperty("Terminal")
  private String terminal;

  @JsonProperty("VehicleHeading")
  private long vehicleHeading;

  @JsonProperty("VehicleLatitude")
  private long vehicleLatitude;

  @JsonProperty("VehicleLongitude")
  private long vehicleLongitude;
}
