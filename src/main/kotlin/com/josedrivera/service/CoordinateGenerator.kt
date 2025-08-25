package com.josedrivera.service

import com.josedrivera.config.SimulatorProperties
import com.josedrivera.model.Coordinate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

@Service
class CoordinateGenerator(
    private val simulatorProperties: SimulatorProperties
) {
    private val threatLevels = listOf("LOW", "MEDIUM", "HIGH")

    fun generate(): Coordinate? {
        if (simulatorProperties.cities.isEmpty()) return null

        val selectedCity = simulatorProperties.cities.random()
        val latitude = Random.nextDouble(selectedCity.latRange[0], selectedCity.latRange[1])
        val longitude = Random.nextDouble(selectedCity.lonRange[0], selectedCity.lonRange[1])

        return Coordinate(
            id = "dron-${UUID.randomUUID().toString().take(8)}",
            city = selectedCity.name,
            latitude = latitude,
            longitude = longitude,
            timestamp = LocalDateTime.now(),
            threatLevel = threatLevels.random()
        )
    }
}
