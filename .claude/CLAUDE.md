# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot (Kotlin) application that seeks to simulate a drone detection system for the Colombian Air Force. It generates random geographical coordinates within configurable Colombian cities and transmits them via WebSocket to simulate real-time drone alerts for mobile/web applications.
It is not implemented yet.

## Technology Stack

- **Kotlin** with Spring Boot 3.5.5
- **JDK 17** (required)
- **Spring WebSocket** with STOMP messaging
- **Spring Data JPA** for persistence
- **Jackson Kotlin** for JSON serialization
- **JUnit + MockK** for testing
- **Gradle Kotlin DSL**

## Common Commands

### Build and Run
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Run tests
./gradlew test

# Clean build
./gradlew clean build
```

### Development
- Application runs on `http://localhost:8080`
- WebSocket endpoint: `ws://localhost:8080/ws`
- Main WebSocket topic: `/topic/drones`

## Architecture

### WebSocket Message Format
```json
{
  "id": "dron-2391",
  "latitude": 6.2674,
  "longitude": -75.5682,
  "timestamp": "2025-08-23T15:12:00Z",
  "threatLevel": "HIGH"  
}
```

### REST API Endpoints
- `GET /api/detections` - Get simulated detection list
- `GET /api/detections/{id}` - Get specific detection details
- `GET /api/health` - Check backend status

### Planned Project Structure
- `config/` - WebSocket and simulator configurations
- `controller/` - REST API endpoints
- `model/` - Data models for drone detection
- `service/` - Core simulation and alert logic
- `websocket/` - STOMP/WebSocket configuration

### Colombian Cities Configuration
The system supports 8 Colombian cities with predefined coordinate ranges:
- Medellín, Bogotá, Cali, Barranquilla, Cartagena, Bucaramanga, Manizales, Pereira
- 3-second generation interval (configurable)
- Random city selection for each coordinate generation

## Development Notes

- Uses `@EnableConfigurationProperties` for configuration binding
- JPA entities configured with `allOpen` plugin
- Random coordinate generator selects from configured city ranges
- Academic/military purpose for Colombian Air Force early warning system
- All data is simulated for confidentiality reasons

# AI Dev Tasks

Use these files when the user requests structured feature development using PRDs:
/ai-dev-tasks/create-prd.md
/ai-dev-tasks/generate-tasks.md
/ai-dev-tasks/process-task-list.md
- Don't run tests again, I will run them to avoid wasting tokens
- Before confirming a task about a test, give me the command to run the test and then wait for my confirmation of success to mark the test task as complete.