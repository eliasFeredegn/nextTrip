package com.elias.nextTrip.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class NextTripRequestTemplate implements Serializable {

  private static final long serialVersionUID = 1L;

  private Integer caseId;
}
