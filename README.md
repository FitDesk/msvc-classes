# Microservicio de Gestión de Clases - FitDesk

## 📋 Descripción

Microservicio para la gestión de clases de gimnasio, incluyendo reservas, asistencia, estadísticas y dashboards para trainers y miembros.

## 🚀 Nuevas Funcionalidades Implementadas

### 1. **Vista de Gestión de Clases con Estadísticas**
- Endpoint: `GET /classes/my-classes/stats`
- Rol requerido: `TRAINER` o `ADMIN`
- Retorna lista de clases con:
  - Estudiantes actuales vs capacidad máxima
  - Porcentaje de asistencia promedio
  - Estado de la clase (Activa, Llena, Cancelada)
  - Información del trainer y ubicación

### 2. **Dashboard del Trainer**
- Endpoint: `GET /dashboard/trainer`
- Rol requerido: `TRAINER` o `ADMIN`
- Métricas incluidas:
  - Total de estudiantes únicos
  - Porcentaje de asistencia promedio
  - Clases impartidas este mes
  - Cambio porcentual respecto al mes anterior
  - Tendencia semanal de estudiantes activos/inactivos

### 3. **Detalle de Clase con Estudiantes**
- Endpoint: `GET /classes/{id}/detail`
- Rol requerido: `TRAINER` o `ADMIN`
- Información detallada:
  - Datos completos de la clase
  - Lista de estudiantes inscritos con:
    - Información del miembro (integración con msvc-members)
    - Porcentaje de asistencia individual
    - Total de clases del estudiante
    - Estado de membresía
    - Último acceso

### 4. **Calendario de Clases**
- Endpoint: `GET /classes/calendar?startDate=2025-01-01&endDate=2025-01-31`
- Endpoint: `GET /classes/upcoming` (próximas clases)
- Rol requerido: `USER`, `TRAINER` o `ADMIN`
- Muestra:
  - Clases por rango de fechas
  - Horarios y ubicaciones
  - Capacidad disponible
  - Acción sugerida (Reservar, Lista de espera, Llena)

### 5. **Dashboard del Miembro** (existente, mejorado)
- Endpoint: `GET /dashboard/member`
- Rol requerido: `USER` o `ADMIN`
- Información incluida:
  - Estado actual (en clase o no)
  - Clases restantes del plan
  - Próxima clase programada
  - Días consecutivos de asistencia
  - Actividad semanal
  - Lista de próximas clases

## 📦 Nuevos DTOs Creados

### Gestión de Clases
- `ClassWithStatsResponse`: Clase con estadísticas agregadas
- `ClassDetailResponse`: Detalle completo de clase
- `StudentInClassDTO`: Información de estudiante en clase
- `CalendarClassDTO`: Clase para vista de calendario

### Dashboard y Métricas
- `TrainerDashboardDTO`: Métricas del trainer
- `StudentTrendDTO`: Tendencia de estudiantes por semana
- `MemberDashboardDTO`: Dashboard del miembro (mejorado)
- `WeeklyActivityDTO`: Actividad semanal
- `UpcomingClassDTO`: Próximas clases

### Integración Externa
- `MemberInfoDTO`: Información de miembro desde msvc-members

## 🔧 Componentes Técnicos

### Servicios
- `ClassService`: Extendido con métodos de estadísticas y calendario
- `TrainerDashboardService`: Nuevo servicio para métricas del trainer
- `MemberClientService`: Cliente HTTP para integración con msvc-members
- `DashboardServiceImpl`: Dashboard del miembro (existente)

### Repositorios Extendidos
- `ClassRepository`: Consultas por trainer, fechas, y estadísticas mensuales
- `ClassReservationRepository`: Cálculos de asistencia y conteo de reservas

### Controladores
- `ClassController`: Extendido con endpoints de estadísticas y calendario
- `TrainerDashboardController`: Nuevo controlador para métricas del trainer
- `DashboardController`: Dashboard del miembro (existente)
- `ClassReservationController`: Gestión de reservas (existente)

## 🔐 Roles y Permisos

- **USER**: Puede ver calendario, reservar clases, ver su dashboard
- **TRAINER**: Puede ver sus clases con estadísticas, ver detalles de estudiantes, ver su dashboard
- **ADMIN**: Acceso completo a todas las funcionalidades

## 🌐 Integración con Otros Microservicios

### msvc-members
- Obtención de información detallada de miembros
- Estado de membresía
- Último acceso
- URL: `http://msvc-members/members/{id}`

### Configuración necesaria
- Eureka Client configurado para service discovery
- RestTemplate con LoadBalancer para comunicación entre microservicios

## 📊 Endpoints Principales

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| GET | `/classes` | Listar todas las clases | ANY |
| POST | `/classes` | Crear nueva clase | ADMIN/TRAINER |
| GET | `/classes/my-classes/stats` | Clases con estadísticas del trainer | TRAINER/ADMIN |
| GET | `/classes/{id}/detail` | Detalle completo de clase | TRAINER/ADMIN |
| GET | `/classes/calendar` | Calendario de clases | ANY |
| GET | `/classes/upcoming` | Próximas clases | ANY |
| GET | `/dashboard/trainer` | Dashboard del trainer | TRAINER/ADMIN |
| GET | `/dashboard/member` | Dashboard del miembro | USER/ADMIN |
| POST | `/reservations` | Reservar clase | USER/ADMIN |
| GET | `/reservations/my` | Mis reservas | USER/ADMIN |

## 🚦 Cómo Probar

### 1. Obtener clases con estadísticas (como Trainer)
```bash
curl -X GET "http://localhost:8083/classes/my-classes/stats" \
  -H "Authorization: Bearer {token_trainer}"
```

### 2. Ver dashboard del trainer
```bash
curl -X GET "http://localhost:8083/dashboard/trainer" \
  -H "Authorization: Bearer {token_trainer}"
```

### 3. Ver calendario de clases
```bash
curl -X GET "http://localhost:8083/classes/calendar?startDate=2025-01-01&endDate=2025-01-31"
```

### 4. Ver detalle de clase
```bash
curl -X GET "http://localhost:8083/classes/{classId}/detail" \
  -H "Authorization: Bearer {token_trainer}"
```

## 📝 Notas de Implementación

1. **Cálculo de Asistencia**: Se basa en el campo `attended` de `ClassReservation`
2. **Tendencias Semanales**: Analiza las últimas 4 semanas de datos
3. **Estado de Clase**: Se determina por capacidad y estado activo
4. **Integración Resiliente**: Si msvc-members no responde, se usan datos por defecto
5. **Optimización**: Uso de transacciones read-only para consultas

## 🔄 Próximas Mejoras

- [ ] Cache de información de miembros para reducir llamadas HTTP
- [ ] Websockets para actualizaciones en tiempo real del dashboard
- [ ] Exportación de estadísticas a PDF/Excel
- [ ] Notificaciones automáticas cuando una clase está por llenarse
- [ ] Sistema de lista de espera automatizado
- [ ] Análisis predictivo de asistencia

## 🛠️ Stack Tecnológico

- **Framework**: Spring Boot 3.5.5
- **Base de Datos**: PostgreSQL
- **ORM**: Spring Data JPA
- **Mapeo**: MapStruct 1.5.5 ⭐ (usado en todos los DTOs)
- **Service Discovery**: Eureka Client
- **Config**: Spring Cloud Config
- **Seguridad**: Spring Security + OAuth2
- **Documentación**: Swagger/OpenAPI 3
- **Logging**: SLF4J + Logback

### 🔄 MapStruct Integration
Los mappers están completamente implementados:
- `ClassMapper`: Mapeo CRUD básico (4 métodos)
- `ClassStatsMapper`: Mapeo de estadísticas y vistas (4 métodos)
- `ClassReservationMapper`: Mapeo de reservas
- Mapeo Entity ↔ DTO sin código manual
- Ver: `MAPSTRUCT_USAGE.md` para detalles

### 🏗️ Arquitectura de Controladores
Controladores especializados por responsabilidad:
- `AdminClassController`: CRUD (solo ADMIN)
- `ClassViewController`: Vistas y consultas (TRAINER/USER)
- Ver: `ARQUITECTURA_CONTROLADORES.md` para detalles

---

✨ **Desarrollado para FitDesk Gym Management System**
