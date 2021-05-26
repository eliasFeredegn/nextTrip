package com.elias.nextTrip.controller;

import com.elias.nextTrip.manager.ApplicationManager;
import com.elias.nextTrip.model.request.NextTripRequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NextTripController {

  Logger logger = LoggerFactory.getLogger(NextTripController.class);

  @Autowired
  ApplicationManager manager;

  @PostMapping("/etaToBusStop")
  public ResponseEntity<String> etaToBusStop(@RequestBody NextTripRequestTemplate request) {
    logger.info("NextTripController.java --> etaToBusStop() :: START");

    if (StringUtils.isBlank(request.getBusRoute()) || StringUtils.isBlank(request.getBusStopName()) || StringUtils.isBlank(request.getDirection())) {
      logger.error("busRoute, busStopName or direction is not provided in the request");
      return new ResponseEntity<>("Missing busRoute, busStopName or direction", HttpStatus.BAD_REQUEST);
    }

    String etaToBusStop = manager.getBusStopEta(request.getBusRoute(), request.getBusStopName(), request.getDirection());
    return new ResponseEntity<>(etaToBusStop, HttpStatus.OK);
  }

}
