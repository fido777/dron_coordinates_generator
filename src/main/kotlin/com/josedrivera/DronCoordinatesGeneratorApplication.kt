package com.josedrivera

import com.josedrivera.config.SimulatorProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
@EnableScheduling
@EnableConfigurationProperties(SimulatorProperties::class)
class DronCoordinatesGeneratorApplication

fun main(args: Array<String>) {
    runApplication<DronCoordinatesGeneratorApplication>(*args)
}
