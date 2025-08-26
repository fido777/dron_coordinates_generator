package com.josedrivera.controller

import com.josedrivera.model.Coordinate
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class CoordinateController {

    @MessageMapping("/coordinate")
    fun coordinate(coordinate: Coordinate) {
        println("Received coordinate: $coordinate")
    }

}