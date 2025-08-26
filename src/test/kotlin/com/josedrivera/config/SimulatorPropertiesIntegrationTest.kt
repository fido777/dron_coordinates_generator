package com.josedrivera.config

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.beans.factory.annotation.Autowired

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class])
class SimulatorPropertiesIntegrationTest {

    @Autowired
    private lateinit var simulatorProperties: SimulatorProperties

    @Test
    fun `should load interval from application yml`() {
        assertEquals(10000L, simulatorProperties.intervalMs)
    }

    @Test
    fun `should load all 8 Colombian cities from application yml`() {
        assertEquals(8, simulatorProperties.cities.size)
        
        val cityNames = simulatorProperties.cities.map { it.name }
        val expectedCities = listOf(
            "Medellín", "Bogotá", "Cali", "Barranquilla", 
            "Cartagena", "Bucaramanga", "Manizales", "Pereira"
        )
        
        assertTrue(cityNames.containsAll(expectedCities))
    }

    @Test
    fun `should load Medellin coordinates from application yml`() {
        val medellin = simulatorProperties.cities.find { it.name == "Medellín" }
        assertNotNull(medellin)
        
        assertEquals(listOf(6.20, 6.35), medellin!!.latRange)
        assertEquals(listOf(-75.65, -75.50), medellin.lonRange)
    }

    @Test
    fun `should load Bogota coordinates from application yml`() {
        val bogota = simulatorProperties.cities.find { it.name == "Bogotá" }
        assertNotNull(bogota)
        
        assertEquals(listOf(4.50, 4.80), bogota!!.latRange)
        assertEquals(listOf(-74.20, -74.00), bogota.lonRange)
    }

    @Test
    fun `should validate all cities have proper coordinate ranges`() {
        simulatorProperties.cities.forEach { city ->
            // Validate latitude ranges
            assertTrue(city.latRange.size == 2, "City ${city.name} should have exactly 2 latitude values")
            assertTrue(city.latRange[0] < city.latRange[1], "City ${city.name} should have ordered latitude range")
            
            // Validate longitude ranges  
            assertTrue(city.lonRange.size == 2, "City ${city.name} should have exactly 2 longitude values")
            assertTrue(city.lonRange[0] < city.lonRange[1], "City ${city.name} should have ordered longitude range")
            
            // Validate Colombian bounds
            assertTrue(city.latRange[0] >= -4.0 && city.latRange[1] <= 16.0, 
                "City ${city.name} latitude should be within Colombian bounds")
            assertTrue(city.lonRange[0] >= -82.0 && city.lonRange[1] <= -66.0, 
                "City ${city.name} longitude should be within Colombian bounds")
        }
    }

    @Test
    fun `should verify specific cities exist with correct data`() {
        val expectedCitiesData = mapOf(
            "Cali" to Pair(listOf(3.30, 3.50), listOf(-76.60, -76.40)),
            "Barranquilla" to Pair(listOf(10.90, 11.10), listOf(-74.90, -74.70)),
            "Cartagena" to Pair(listOf(10.35, 10.50), listOf(-75.60, -75.40)),
            "Bucaramanga" to Pair(listOf(7.10, 7.20), listOf(-73.15, -73.00)),
            "Manizales" to Pair(listOf(5.02, 5.10), listOf(-75.55, -75.45)),
            "Pereira" to Pair(listOf(4.75, 4.85), listOf(-75.75, -75.60))
        )
        
        expectedCitiesData.forEach { (cityName, expectedData) ->
            val city = simulatorProperties.cities.find { it.name == cityName }
            assertNotNull(city, "City $cityName should exist")
            assertEquals(expectedData.first, city!!.latRange, "Latitude range for $cityName")
            assertEquals(expectedData.second, city.lonRange, "Longitude range for $cityName")
        }
    }
}