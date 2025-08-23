# 🛰️ Backend - Sistema Móvil de Alerta Temprana para Detección de Drones

**Generador de Coordenadas Aleatorias con Spring Boot + WebSocket**

Este proyecto forma parte de un sistema de alerta temprana desarrollado para la Fuerza Aérea Colombiana, cuyo objetivo es simular detecciones de drones no autorizados mediante coordenadas generadas periódicamente y transmitidas en tiempo real a una aplicación móvil.

## 🚀 Descripción

El backend implementado en **Spring Boot (Kotlin)** simula un sistema de detección de drones generando coordenadas geográficas aleatorias dentro de una región determinada (ej. espacio aéreo colombiano). Cada coordenada representa una supuesta detección de dron y es transmitida al cliente a través de un canal **WebSocket en tiempo real**, permitiendo su visualización inmediata.

Este backend también incluye:

- API REST básica para consultas históricas (simuladas)
- Generador configurable de coordenadas simuladas
- Transmisión WebSocket de alertas en tiempo real
- Configuración modular y preparada para despliegue en producción

---

## 🛠️ Tecnologías utilizadas

- 🧠 **Kotlin**
- 🌱 **Spring Boot 3**
- 🔌 **WebSocket (Spring Messaging + STOMP)**
- 🗄️ **PostgreSQL (opcional - persistencia simulada)**
- 🧪 **JUnit + MockK** para pruebas unitarias
- 📦 **Gradle Kotlin DSL**

---

## 📡 Endpoints

### WebSocket (STOMP)

- **URL de conexión:** `ws://localhost:8080/ws`
- **Topic de suscripción:** `/topic/drones`
- **Formato del mensaje:**

```json
{
  "id": "dron-2391",
  "latitude": 6.2674,
  "longitude": -75.5682,
  "timestamp": "2025-08-23T15:12:00Z",
  "threatLevel": "HIGH"  
}
```

## REST API
| Método | Endpoint               | Descripción                                 |
| ------ | ---------------------- | ------------------------------------------- |
| GET    | `/api/detections`      | Obtener lista simulada de detecciones       |
| GET    | `/api/detections/{id}` | Obtener detalle de una detección específica |
| GET    | `/api/health`          | Verificar estado del backend                |



## ⚙️ Configuración
Variables configurables en application.yml

```yaml
drone:
  simulator:
    interval-ms: 3000 # Frecuencia de generación de coordenadas
    city:
      name: "Medellín"
      lat-range: [6.20, 6.35]
      lon-range: [-75.65, -75.50]
```

## ▶️ Cómo ejecutar
### Requisitos previos

- JDK 17+
- PostgreSQL (opcional)
- Gradle (o usar wrapper ./gradlew)

### Pasos

```bash
# 1. Clona el repositorio
git clone https://github.com/tu_usuario/sistema-alerta-drones-backend.git
cd sistema-alerta-drones-backend

# 2. Ejecuta la aplicación
./gradlew bootRun
```

La aplicación estará disponible en `http://localhost:8080`.

---

### 📱 Casos de uso

Este backend está diseñado para integrarse con una aplicación móvil que:

- Se conecta vía WebSocket para recibir alertas de detección
- Muestra en tiempo real los drones sobre un mapa, e.g. Google Maps SDK.
- Notifica al usuario sobre amenazas según nivel de riesgo

## 🧪 Pruebas

Para ejecutar las pruebas:

```bash
./gradlew test
```

Se utilizan pruebas unitarias con MockK y pruebas de integración usando Spring Test.

## 📂 Estructura del proyecto

```bash
src/
├── config/           # Configuraciones de WebSocket y simulador
├── controller/       # Controladores REST
├── model/            # Modelos de datos
├── service/          # Lógica del simulador y alertas
├── websocket/        # Configuración STOMP/WebSocket
└── Application.kt    # Punto de entrada principal
```

## 🔐 Seguridad

Este proyecto no expone datos reales por razones de confidencialidad. Toda la información generada es simulada, y el sistema puede ser adaptado a un entorno productivo real mediante integración con sensores o radares reales.

## 📃 Licencia

Este proyecto ha sido desarrollado con fines académicos e institucionales. Su distribución y uso está sujeta a autorización de la Fuerza Aérea Colombiana.

## 👨‍💻 Autor

**Nombre:** José Daniel Rivera Vergara  
**Rol:** Practicante de Ingeniería de Telecomunicaciones  
**Institución:** Universidad de Antioquia  
**Contacto:** josed.rivera@udea.edu.co  

