# Tasks: WebSocket-Based Random Coordinate Generator System

## Relevant Files

- `src/main/kotlin/com/josedrivera/config/WebSocketConfig.kt` - WebSocket and STOMP configuration with `/ws` endpoint and `/topic` broker
- `src/main/kotlin/com/josedrivera/config/SimulatorProperties.kt` - Configuration properties class for drone simulator settings
- `src/main/kotlin/com/josedrivera/config/CityDto.kt` - Data class for individual city configuration with lat/lon ranges
- `src/main/kotlin/com/josedrivera/model/Coordinate.kt` - Enhanced data class with id, timestamp, and threatLevel fields
- `src/main/kotlin/com/josedrivera/service/CoordinateSender.kt` - Scheduled service for generating and broadcasting coordinates via WebSocket
- `src/main/kotlin/com/josedrivera/controller/DetectionController.kt` - REST API endpoints for detection queries and health checks
- `src/main/kotlin/com/josedrivera/DronCoordinatesGeneratorApplication.kt` - Updated main application class with @EnableScheduling and @EnableConfigurationProperties
- `src/main/resources/application.yml` - YAML configuration with drone.simulator properties for 8 Colombian cities
- `src/test/kotlin/com/josedrivera/config/WebSocketConfigTest.kt` - Unit tests for WebSocket configuration
- `src/test/kotlin/com/josedrivera/service/CoordinateSenderTest.kt` - Unit tests for coordinate generation and broadcasting
- `src/test/kotlin/com/josedrivera/controller/DetectionControllerTest.kt` - Unit tests for REST API endpoints

### Notes

- Unit tests should be placed in the `src/test/kotlin` directory following the same directory structure as the source code
- The project already has Spring Boot WebSocket, Jackson Kotlin, and commons-lang3 dependencies configured
- Configuration will migrate from `application.properties` to `application.yml` format as specified in @IMPLEMENTATION_IDEAS.md
- WebSocket broadcasts to `/topic/coordinates` topic with enhanced coordinate data including id, timestamp, and threatLevel
- The main application class needs @EnableScheduling and @EnableConfigurationProperties annotations
- Uses @Scheduled with fixedRateString pointing to the configuration property for flexible intervals
- Architecture follows the component structure defined in @ARCHITECTURE.md with clear separation between WebSocket Layer, Simulator, and REST API components
- Scheduler triggers CoordinateGenerator → STOMP Broker → Kotlin Multiplatform Client (Ktor Client) flow as per sequence diagram
- SimulatorProperties binds to application.yml for multi-city configuration as shown in the component diagram

## Tasks

- [x] 1.0 Configure WebSocket Infrastructure
    - [x] 1.1 Create WebSocketConfig class with @EnableWebSocketMessageBroker annotation
    - [x] 1.2 Configure message broker with /topic prefix for broadcasting
    - [x] 1.3 Set application destination prefixes to /app for incoming messages
    - [x] 1.4 Register STOMP endpoint at /ws with SockJS support and CORS configuration
    - [x] 1.5 Set allowed origin patterns to "*" for development cross-origin access
    - [x] 1.6 Create WebSocketConfigTest for WebSocket configuration testing

- [x] 2.0 Implement Data Models and Configuration
    - [x] 2.1 Create CityDto data class with name, latRange, and lonRange properties
    - [x] 2.2 Create SimulatorProperties configuration class with @ConfigurationProperties annotation
    - [x] 2.3 Define intervalMs and cities list properties in SimulatorProperties
    - [x] 2.4 Create enhanced Coordinate data class with id, city, latitude, longitude, timestamp, and threatLevel fields
    - [x] 2.5 Add proper JSON serialization annotations for coordinate data transmission
    - [x] 2.6 Create SimulatorPropertiesTest for configuration property binding and validation testing

- [x] 3.0 Create Coordinate Generation Service
    - [x] 3.1 Create CoordinateGenerator and CoordinateBroadcaster service classes with @Service annotation
      - [x] 3.1.1 Create CoordinateGeneratorTest for service unit testing
      - [x] 3.1.2 Create CoordinateBroadcasterTest for service unit testing
    - [x] 3.2 Inject SimpMessagingTemplate and SimulatorProperties dependencies
    - [x] 3.3 Implement @Scheduled method with fixedRateString pointing to configuration property
    - [x] 3.4 Add logic to randomly select city from configured cities list
    - [x] 3.5 Generate random latitude and longitude within selected city's coordinate ranges
    - [x] 3.6 Create Coordinate object with unique ID, timestamp, and random threat level
    - [x] 3.7 Broadcast coordinate to /topic/coordinates using SimpMessagingTemplate
    - [x] 3.8 Add basic logging for generated coordinate events

- [x] 4.0 Implement REST API Endpoints
    - [x] 4.1 Create for REST endpoint testing
    - [x] 4.2 Create DetectionController class with @RestController annotation
    - [x] 4.3 Implement GET /api/detections endpoint for simulated detection list
    - [x] 4.4 Implement GET /api/detections/{id} endpoint for specific detection details
    - [x] 4.5 Implement GET /api/health endpoint for system status checking
    - [x] 4.6 Add proper HTTP status codes and error handling for all endpoints
    - [x] 4.7 Return mock data that aligns with coordinate broadcast format

- [x] 5.0 Configure Application Properties
    - [x] 5.1 Create application.yml file to replace application.properties
    - [x] 5.2 Configure drone.simulator.interval-ms property (default: 3000)
    - [x] 5.3 Add all 8 Colombian cities with their coordinate ranges (Medellín, Bogotá, Cali, Barranquilla, Cartagena, Bucaramanga, Manizales, Pereira)
    - [x] 5.4 Set realistic latitude and longitude boundaries for each city
    - [x] 5.5 Update main application class with @EnableScheduling and @EnableConfigurationProperties annotations

- [ ] 6.0 Add Testing Infrastructure
    - [x] 6.1 Test coordinate generation logic and city selection randomness
    - [x] 6.2 Test configuration property binding and validation of application.yml as a source of information for the CoordinateGenerator via SimulatorProperties
    - [ ] 6.3 Add integration test for WebSocket message broadcasting and ensure it passes
    - [x] 6.4 Verify coordinate data format and JSON serialization