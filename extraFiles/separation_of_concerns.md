
* **Generating a `Coordinate`** → **domain logic** (your simulator knows how to model drones, coordinates, cities, threat levels).
* **Sending a `Coordinate` over WebSocket/STOMP** → **infrastructure task** (concerns how data leaves your system and reaches clients).

---

## 🧭 How can be separated

### 1. Extract a **domain service** for coordinate generation

```kotlin
import kotlin.uuid.Uuid

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
            id = "dron-${Uuid.random().toString().take(8)}",
            city = selectedCity.name,
            latitude = latitude,
            longitude = longitude,
            timestamp = LocalDateTime.now(),
            threatLevel = threatLevels.random()
        )
    }
}
```

---

### 2. Keep an **infrastructure service** that handles scheduling + sending

```kotlin
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
                "Broadcast coordinate: {} in {} with threat level {}",
                "${coordinate.latitude}, ${coordinate.longitude}",
                coordinate.city,
                coordinate.threatLevel
            )
        } else {
            logger.warn("No coordinate generated (no cities configured)")
        }
    }
}
```

---

## ✅ Benefits of this separation

* **CoordinateGenerator** = pure domain logic.
  👉 You could reuse it elsewhere (tests, REST endpoints, CLI simulator) without dragging in WebSockets.

* **CoordinateBroadcaster** = infrastructure concern (knows how to push messages to WebSocket clients).
  👉 If one day you want to broadcast via Kafka, RabbitMQ, or REST SSE, you only replace this class, not the generator.

* **Clear boundaries**: easy to test each piece in isolation.
