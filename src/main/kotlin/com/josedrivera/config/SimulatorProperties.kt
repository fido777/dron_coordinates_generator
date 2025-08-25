package com.josedrivera.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "drone.simulator")
data class SimulatorProperties(
    var intervalMs: Long = 3000,
    var cities: List<CityDto> = emptyList()
)
