package com.elias.nextTrip.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Direction implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty("Text")
  private String text;

  @JsonProperty("Value")
  private int value;
}
