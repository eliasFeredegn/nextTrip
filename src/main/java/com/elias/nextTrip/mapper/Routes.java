package com.elias.nextTrip.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Routes implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty("Description")
  private String description;

  @JsonProperty("ProviderID")
  private String providerID;

  @JsonProperty("Route")
  private String route;
}
