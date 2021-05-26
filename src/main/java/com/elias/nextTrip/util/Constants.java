package com.elias.nextTrip.util;

import org.springframework.stereotype.Component;

@Component
public class Constants {

  public static final String ROUTE_URL = "https://svc.metrotransit.org/NexTrip/Routes";

  public static final String DIRECTION_URL = "https://svc.metrotransit.org/NexTrip/Directions/";

  public static final String BUS_STOPS_URL = "https://svc.metrotransit.org/NexTrip/Stops/";

  public static final String BUS_ROUTE_DEPARTURE_SCHEDULE = "https://svc.metrotransit.org/NexTrip/";

  public static final String SOUTH = "SOUTHBOUND";

  public static final String NORTH = "NORTHBOUND";

  public static final String EAST = "EASTBOUND";

  public static final String WEST = "WESTBOUND";


}
