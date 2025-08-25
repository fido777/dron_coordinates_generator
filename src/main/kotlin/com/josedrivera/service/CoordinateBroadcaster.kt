package com.josedrivera.service

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class CoordinateBroadcaster(
    private val generator: CoordinateGenerator,
    private val simpMessagingTemplate: SimpMessagingTemplate
) {
    private val logger = LoggerFactory.getLogger(CoordinateBroadcaster::class.java)

    @Scheduled(fixedRateString = "\${drone.simulator.interval-ms}")
    fun broadcast() {
        val coordinate = generator.generate()
        if (coordinate != null) {
            simpMessagingTemplate.convertAndSend("/topic/coordinates", coordinate)
            logger.info(
                "Broadcast coordinate: (${coordinate.latitude}, ${coordinate.longitude}) in ${coordinate.city} with threat level ${coordinate.threatLevel}",
            )
        } else {
            logger.warn("No coordinate generated (no cities configured)")
        }
    }
}
