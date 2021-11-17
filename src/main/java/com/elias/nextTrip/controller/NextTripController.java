package com.elias.nextTrip.controller;

import com.elias.nextTrip.manager.ApplicationManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NextTripController {

  Logger logger = LoggerFactory.getLogger(NextTripController.class);

  @Autowired
  ApplicationManager manager;

  @GetMapping("/etaToBusStop")
  public ResponseEntity<String> etaToBusStop(@RequestParam String busRoute, @RequestParam String busStopName, @RequestParam String direction) {
    logger.info("NextTripController.java --> etaToBusStop() :: START");

    if (StringUtils.isBlank(busRoute) || StringUtils.isBlank(busStopName) || StringUtils.isBlank(direction)) {
      logger.error("busRoute, busStopName or direction is not provided in the request");
      return new ResponseEntity<>("Missing busRoute, busStopName or direction", HttpStatus.BAD_REQUEST);
    }

    String etaToBusStop = manager.getBusStopEta(busRoute, busStopName, direction);
    return new ResponseEntity<>(etaToBusStop, HttpStatus.OK);
  }

}
