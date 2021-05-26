package com.elias.nextTrip.manager;

import com.elias.nextTrip.mapper.Direction;
import com.elias.nextTrip.mapper.Routes;
import com.elias.nextTrip.mapper.Schedule;
import com.elias.nextTrip.mapper.Stops;
import com.elias.nextTrip.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;

@Component
public class ApplicationManager {

  Logger logger = LoggerFactory.getLogger(ApplicationManager.class);

  private String regexPattern = "(\\d{13})([\\+\\-]\\d{4})";

  @Autowired
  private static RestTemplate restTemplate;

  /**
   * @param busRoute
   * @param busStopName
   * @param direction
   * @return
   */
  public String getBusStopEta(String busRoute, String busStopName, String direction) {
    logger.info("ApplicationManager.java --> getEtaToBusStop() :: START");

    List<Stops> listOfBusStops = null;
    String busStopValue = "";
    int directionValue = 0;

    verifyDirection(direction);
    Routes route = getRoutes(busRoute);
    List<Direction> routeDirections = getBusDirections(route.getRoute());

    for (Direction currentDirection : routeDirections) {
      if (currentDirection.getText().toLowerCase().contains(direction.toLowerCase())) {
        listOfBusStops = getBusStops(route.getRoute(), currentDirection.getValue());
        directionValue = currentDirection.getValue();
        break;
      }
    }

    if(listOfBusStops == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Please provide a valid route direction for route " + route.getRoute());
    }

    for (Stops stop : listOfBusStops) {
      if (stop.getText().equalsIgnoreCase(busStopName)) {
        busStopValue = stop.getValue();
        break;
      }
    }

    if(busStopValue.isEmpty()){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Please provide a valid bus stop name for route " + route.getRoute());
    }

    List<Schedule> schedules = getBusDepartureSchedule(route.getRoute(), directionValue, busStopValue);

    String timeToStopInMinutes = getTimeToRoute(schedules);

    logger.info("ApplicationManager.java --> getEtaToBusStop() :: END");
    return timeToStopInMinutes;
  }

  /**
   * @param direction
   */
  // Verifys if teh provided direction is valid
  private void verifyDirection(String direction) {
    logger.info("ApplicationManager.java --> verifyDirection() :: START");

    String localDirection = "";
    switch (direction.toUpperCase()) {
      case "EAST":
        localDirection = Constants.EAST;
        break;
      case "WEST":
        localDirection = Constants.WEST;
        break;
      case "SOUTH":
        localDirection = Constants.SOUTH;
        break;
      case "NORTH":
        localDirection = Constants.NORTH;
        break;
      default:
        logger.error("ApplicationManager.java --> verifyDirection() :: the following direction was not found ");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Please enter a valid direction");
    }

    List<String> listOfAllowedDirections = Arrays.asList(Constants.EAST, Constants.WEST, Constants.SOUTH, Constants.NORTH);
    if (!listOfAllowedDirections.contains(localDirection.toUpperCase())) {
      logger.error("ApplicationManager.java --> verifyDirection() :: END");
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Please enter a valid direction");
    }

    logger.info("ApplicationManager.java --> verifyDirection() :: END");
  }


  /**
   * @param busRoute
   * @return
   */
  // Returns the given route
  public Routes getRoutes(String busRoute) {
    logger.info("ApplicationManager.java --> getRoutes() :: START");

    restTemplate = new RestTemplate();
    List<Routes> listOfRoutes = Arrays.asList(restTemplate.getForObject(Constants.ROUTE_URL, Routes[].class));

    List<Routes> route = listOfRoutes.stream().filter(description -> description.getDescription().equalsIgnoreCase(busRoute)).collect(Collectors.toList());

    if (CollectionUtils.isEmpty(route)) {
      logger.error("ApplicationManager.java --> getRoutes() :: route not found ");
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Please enter a valid route");
    }

    logger.info("ApplicationManager.java --> getRoutes() :: END");
    return route.get(0);
  }

  /**
   * @param routeNumber
   * @return
   */
  // Returns the two directions for the given route (routeNumber)
  public List<Direction> getBusDirections(int routeNumber) {
    logger.info("ApplicationManager.java --> getBusDirections() :: START");

    restTemplate = new RestTemplate();
    List<Direction> busDirections = Arrays.asList(restTemplate.getForObject(Constants.DIRECTION_URL + routeNumber, Direction[].class));

    logger.info("ApplicationManager.java --> getBusDirections() :: END");
    return busDirections;
  }

  /**
   * @param route
   * @param direction
   * @return
   */
  // Returns list of bus stops for the given route and direction
  public List<Stops> getBusStops(int route, int direction) {
    logger.info("ApplicationManager.java --> getBusStops() :: START");
    restTemplate = new RestTemplate();
    List<Stops> busStops = Arrays.asList(restTemplate.getForObject(Constants.BUS_STOPS_URL + route + "/" + direction, Stops[].class));

    if (busStops.isEmpty()) {
      logger.error("ApplicationManager.java --> getBusStops() :: for the given route and direction ");
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There are no bus stops for the given route and direction");
    }

    logger.info("ApplicationManager.java --> getBusStops() :: End");
    return busStops;
  }

  /**
   * @param route
   * @param direction
   * @param stop
   * @return
   */
  // Returns list of bus schedules for the given route direction and stop
  public List<Schedule> getBusDepartureSchedule(int route, int direction, String stop) {

    logger.info("ApplicationManager.java --> getBusDepartureSchedule() :: START");
    restTemplate = new RestTemplate();
    List<Schedule> schedule = Arrays.asList(restTemplate.getForObject(String.format(Constants.BUS_ROUTE_DEPARTURE_SCHEDULE +
      "/%1$s" + "/%2$s" + "/%3$s", route, direction, stop), Schedule[].class));
    if (schedule.isEmpty()) {
      logger.error("ApplicationManager.java --> getBusDepartureSchedule() :: last bus left for the given route, direction and stop ");
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The last bus for the day has already left");
    }

    logger.info("ApplicationManager.java --> getBusDepartureSchedule() :: END");
    return schedule;
  }

  /**
   * @param schedules
   * @return
   */
  // Return the estimated time to the bus stop
  public String getTimeToRoute(List<Schedule> schedules) {
    logger.info("ApplicationManager.java --> getTimeToRoute() :: START");

    String departuretime = schedules.get(0).getDepartureTime();
    Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(departuretime);
    matcher.find();
    String theDepartureTime = matcher.group(1);

    Calendar currentTime = Calendar.getInstance();
    long currentTimeInMillis = currentTime.getTimeInMillis();
    long timeToBusStop = parseLong(theDepartureTime) - currentTimeInMillis;
    long timeToBusStopInMinutes = (timeToBusStop / 1000) / 60;

    logger.info("ApplicationManager.java --> getTimeToRoute() :: END");
    return timeToBusStopInMinutes + " Minutes";
  }
}
