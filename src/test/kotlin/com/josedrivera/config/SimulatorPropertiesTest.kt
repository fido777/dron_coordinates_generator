package com.josedrivera.config

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource

class SimulatorPropertiesTest {

    @Test
    fun `should bind properties from configuration with default values`() {
        val properties = mapOf<String, Any>()
        val propertySource = MapConfigurationPropertySource(properties)
        val binder = Binder(propertySource)
        
        val simulatorProperties = binder.bind("drone.simulator", SimulatorProperties::class.java)
            .orElse(SimulatorProperties())
        
        assertEquals(3000L, simulatorProperties.intervalMs)
        assertTrue(simulatorProperties.cities.isEmpty())
    }

    @Test
    fun `should bind custom interval from configuration`() {
        val properties = mapOf(
            "drone.simulator.interval-ms" to 5000
        )
        val propertySource = MapConfigurationPropertySource(properties)
        val binder = Binder(propertySource)
        
        val simulatorProperties = binder.bind("drone.simulator", SimulatorProperties::class.java)
            .orElse(SimulatorProperties())
        
        assertEquals(5000L, simulatorProperties.intervalMs)
    }

    @Test
    fun `should bind cities list from configuration`() {
        val properties = mapOf(
            "drone.simulator.cities[0].name" to "Medellín",
            "drone.simulator.cities[0].lat-range[0]" to 6.2,
            "drone.simulator.cities[0].lat-range[1]" to 6.3,
            "drone.simulator.cities[0].lon-range[0]" to -75.6,
            "drone.simulator.cities[0].lon-range[1]" to -75.5,
            "drone.simulator.cities[1].name" to "Bogotá",
            "drone.simulator.cities[1].lat-range[0]" to 4.5,
            "drone.simulator.cities[1].lat-range[1]" to 4.7,
            "drone.simulator.cities[1].lon-range[0]" to -74.2,
            "drone.simulator.cities[1].lon-range[1]" to -74.0
        )
        val propertySource = MapConfigurationPropertySource(properties)
        val binder = Binder(propertySource)
        
        val simulatorProperties = binder.bind("drone.simulator", SimulatorProperties::class.java)
            .orElse(SimulatorProperties())
        
        assertEquals(2, simulatorProperties.cities.size)
        
        val medellin = simulatorProperties.cities[0]
        assertEquals("Medellín", medellin.name)
        assertEquals(listOf(6.2, 6.3), medellin.latRange)
        assertEquals(listOf(-75.6, -75.5), medellin.lonRange)
        
        val bogota = simulatorProperties.cities[1]
        assertEquals("Bogotá", bogota.name)
        assertEquals(listOf(4.5, 4.7), bogota.latRange)
        assertEquals(listOf(-74.2, -74.0), bogota.lonRange)
    }

    @Test
    fun `should validate city coordinate ranges`() {
        val city = CityDto(
            name = "Test City",
            latRange = listOf(6.0, 6.5),
            lonRange = listOf(-75.0, -74.5)
        )
        
        // Validate latitude range
        assertTrue(city.latRange.first() < city.latRange.last(),
            "Latitude range should have first value less than second")
        
        // Validate longitude range
        assertTrue(city.lonRange.first() < city.lonRange.last(),
            "Longitude range should have first value less than second")
        
        // Validate reasonable coordinate ranges for Colombia
        assertTrue(city.latRange.first() >= -4.0 && city.latRange.last() <= 16.0,
            "Latitude should be within reasonable bounds for Colombia")
        assertTrue(city.lonRange.first() >= -82.0 && city.lonRange.last() <= -66.0,
            "Longitude should be within reasonable bounds for Colombia")
    }

    @Test
    fun `should have valid default interval value`() {
        val properties = SimulatorProperties()
        
        assertTrue(properties.intervalMs > 0, "Interval should be positive")
        assertTrue(properties.intervalMs >= 1000, "Interval should be at least 1 second for reasonable performance")
    }
}


