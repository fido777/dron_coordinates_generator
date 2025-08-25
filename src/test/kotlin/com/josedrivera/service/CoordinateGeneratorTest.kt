package com.josedrivera.service

import com.josedrivera.config.CityDto
import com.josedrivera.config.SimulatorProperties
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import io.mockk.mockk
import io.mockk.every
import org.junit.jupiter.api.Assertions.*

class CoordinateGeneratorTest {

    private lateinit var coordinateGenerator: CoordinateGenerator
    private lateinit var simulatorProperties: SimulatorProperties

    @BeforeEach
    fun setup() {
        simulatorProperties = mockk(relaxed = true)
        coordinateGenerator = CoordinateGenerator(simulatorProperties)
    }

    @Test
    fun `should generate coordinate when cities are configured`() {
        val cities = listOf(
            CityDto("Medellín", listOf(6.2, 6.3), listOf(-75.6, -75.5)),
            CityDto("Bogotá", listOf(4.5, 4.7), listOf(-74.2, -74.0))
        )
        every { simulatorProperties.cities } returns cities

        val coordinate = coordinateGenerator.generate()

        assertNotNull(coordinate)
        assertTrue(coordinate!!.id.isNotEmpty())
        assertTrue(coordinate.id.startsWith("dron-"))
        assertTrue(coordinate.city in listOf("Medellín", "Bogotá"))
        assertTrue(coordinate.latitude >= 4.5 && coordinate.latitude <= 6.3)
        assertTrue(coordinate.longitude >= -75.6 && coordinate.longitude <= -74.0)
        assertTrue(coordinate.threatLevel in listOf("LOW", "MEDIUM", "HIGH"))
        assertNotNull(coordinate.timestamp)
    }

    @Test
    fun `should generate coordinates within specific city ranges`() {
        val medellinCity = CityDto("Medellín", listOf(6.2, 6.3), listOf(-75.6, -75.5))
        every { simulatorProperties.cities } returns listOf(medellinCity)

        val coordinate = coordinateGenerator.generate()

        assertNotNull(coordinate)
        assertEquals("Medellín", coordinate!!.city)
        assertTrue(coordinate.latitude >= 6.2 && coordinate.latitude <= 6.3)
        assertTrue(coordinate.longitude >= -75.6 && coordinate.longitude <= -75.5)
    }

    @Test
    fun `should return null when no cities are configured`() {
        every { simulatorProperties.cities } returns emptyList()

        val coordinate = coordinateGenerator.generate()

        assertNull(coordinate)
    }

    @Test
    fun `should generate unique IDs for different coordinates`() {
        val cities = listOf(
            CityDto("Medellín", listOf(6.2, 6.3), listOf(-75.6, -75.5))
        )
        every { simulatorProperties.cities } returns cities

        val coordinate1 = coordinateGenerator.generate()
        val coordinate2 = coordinateGenerator.generate()

        assertNotNull(coordinate1)
        assertNotNull(coordinate2)
        assertNotEquals(coordinate1!!.id, coordinate2!!.id)
    }

    @Test
    fun `should generate valid threat levels`() {
        val cities = listOf(
            CityDto("Medellín", listOf(6.2, 6.3), listOf(-75.6, -75.5))
        )
        every { simulatorProperties.cities } returns cities

        val coordinate = coordinateGenerator.generate()

        assertNotNull(coordinate)
        assertTrue(coordinate!!.threatLevel in listOf("LOW", "MEDIUM", "HIGH"))
    }
}