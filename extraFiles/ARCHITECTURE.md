# 🗺️ System Architecture Diagrams

This document contains **Mermaid** diagrams describing the backend architecture and the real-time WebSocket flow.

---

## 1) Component Overview

```mermaid
flowchart LR
    subgraph Mobile["Android App (Kotlin)"]
      WSClient["STOMP WebSocket Client"]
      MapUI["Map UI (Google Maps)"]
      Notifier["Push Notifications"]
      WSClient --> MapUI
    end

    subgraph Backend["Backend (Spring Boot + Kotlin)"]
      App["Spring Boot Application"]
      subgraph WS["WebSocket Layer"]
        Broker["Spring STOMP Broker (/topic)"]
        Endpoint["/ws Endpoint"]
      end
      subgraph Sim["Simulator"]
        Scheduler["@Scheduled Generator"]
        Generator["Coordinate Generator"]
      end
      API["REST API (/api/...)"]
      Config["SimulatorProperties (YAML)"]
      DB[("Storage (optional)
PostgreSQL")]
      
      Config --> Generator
      Scheduler --> Generator
      Generator --> Broker
      App --> WS
      App --> API
      API --> DB
    end

    WSClient <-->|STOMP /topic/coordinates| Broker
```

---

## 2) WebSocket Publish Flow (Sequence)

```mermaid
sequenceDiagram
    autonumber
    participant S as Scheduler
    participant G as CoordinateGenerator
    participant B as STOMP Broker
    participant C as Mobile Client

    S->>G: trigger() every intervalMs
    G->>G: pickCity(), random(lat, lon)
    G->>B: convertAndSend(/topic/coordinates, Coordinate)
    Note right of B: Broadcast to all subscribers
    B-->>C: Coordinate JSON
    C->>C: Update map UI
```

---

## 3) REST Interactions (High-level)

```mermaid
flowchart TB
  Client["Client (Postman/Frontend)"]
  API["REST API /api/detections"]
  Service["DetectionService"]
  DB[("PostgreSQL (optional)")];

  Client --> API --> Service --> DB
```

---

### Notes
- The **Scheduler** uses `@Scheduled` with `fixedRateString = "\${drone.simulator.interval-ms}"`.
- The **SimulatorProperties** binds to `application.yml` for multiple cities support.
- The **STOMP Broker** broadcasts on `/topic/coordinates`; clients connect via `/ws`.
