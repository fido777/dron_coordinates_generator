# 🛠️ Implementation Guide - Drone Detection Backend

This document provides **technical details** of the backend implementation. It complements the main `README.md` (which is focused on project purpose and usage).

---

## ✅ Goal

* Expose a WebSocket STOMP endpoint (`/ws`)
* Broadcast random coordinates of multiple Colombian cities to `/topic/coordinates`

---

## 🔧 Configuration with YAML

The backend supports multiple cities defined in `application.yml`:

```yaml
drone:
  simulator:
    interval-ms: 3000  # Frequency in milliseconds
    cities:
      - name: "Medellín"
        lat-range: [6.20, 6.35]
        lon-range: [-75.65, -75.50]
      - name: "Bogotá"
        lat-range: [4.50, 4.80]
        lon-range: [-74.20, -74.00]
      - name: "Cali"
        lat-range: [3.30, 3.50]
        lon-range: [-76.60, -76.40]
      - name: "Barranquilla"
        lat-range: [10.90, 11.10]
        lon-range: [-74.90, -74.70]
      - name: "Cartagena"
        lat-range: [10.35, 10.50]
        lon-range: [-75.60, -75.40]
      - name: "Bucaramanga"
        lat-range: [7.10, 7.20]
        lon-range: [-73.15, -73.00]
      - name: "Manizales"
        lat-range: [5.02, 5.10]
        lon-range: [-75.55, -75.45]
      - name: "Pereira"
        lat-range: [4.75, 4.85]
        lon-range: [-75.75, -75.60]
```

### Kotlin Configuration Classes

```kotlin
data class CityConfig(
    val name: String,
    val latRange: List<Double>,
    val lonRange: List<Double>
)

@ConfigurationProperties(prefix = "drone.simulator")
data class SimulatorProperties(
    val intervalMs: Long,
    val cities: List<CityConfig>
)
```

These classes connect the YAML configuration with the Kotlin code. The random generator will pick one city at a time and create simulated coordinates within the defined ranges.

---

## 🔧 Spring Boot WebSocket with STOMP (Kotlin)

### 🔹 `WebSocketConfig.kt`

```kotlin
@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic") // for broadcasting
        config.setApplicationDestinationPrefixes("/app") // for sending messages
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS()
    }
}
```

---

### 🔹 `Coordinate.kt`

```kotlin
data class Coordinate(
    val latitude: Double,
    val longitude: Double
)
```

---

### 🔹 `CoordinateSender.kt`

```kotlin
@Service
class CoordinateSender(
    private val messagingTemplate: SimpMessagingTemplate,
    private val properties: SimulatorProperties
) {

    private val random = Random()

    @Scheduled(fixedRateString = "\${drone.simulator.interval-ms}")
    fun sendCoordinate() {
        val city = properties.cities.random()
        val latitude = city.latRange.random()
        val longitude = city.lonRange.random()

        val coord = Coordinate(latitude, longitude)
        messagingTemplate.convertAndSend("/topic/coordinates", coord)
    }
}
```

---

✅ Add `@EnableScheduling` to your main application class:

```kotlin
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(SimulatorProperties::class)
class DroneApplication

fun main(args: Array<String>) {
    runApplication<DroneApplication>(*args)
}
```

## WebSocket Message Format (output)
```json
{
  "id": "dron-2391",
  "latitude": 6.2674,
  "longitude": -75.5682,
  "timestamp": "2025-08-23T15:12:00Z",
  "threatLevel": "HIGH"  
}
```

---

## 📂 Project Structure

```
src/
├── config/           # WebSocket and simulator configurations
├── controller/       # REST controllers
├── model/            # Data models
├── service/          # Simulation logic and WebSocket broadcasting
├── Application.kt    # Main entry point
```

---

## 🧪 Testing

Unit tests are implemented using **JUnit 5** and **MockK** for mocking dependencies.

Integration tests use **Spring Boot Test** with an embedded WebSocket client to verify broadcasting.

---
