# Tienda Microservicios

Proyecto de aprendizaje: microservicios con Spring Boot, Arquitectura Hexagonal,
WebFlux, JPA, seguridad JWT, tests, SonarQube y CI/CD con GitHub Actions.

## Microservicios

| Servicio | Tecnología | Responsabilidad |
|---|---|---|
| auth-service | Spring MVC + JPA | Usuarios y emisión de tokens JWT |
| catalogo-service | Spring MVC + JPA | Productos y stock |
| pedidos-service | Spring WebFlux + R2DBC | Pedidos |

## Ramas (Gitflow)

- `main`: producción
- `develop`: integración
- `feature/*`: nuevas funcionalidades (salen de develop y vuelven a develop)
- `release/*`: preparación de versiones
- `hotfix/*`: arreglos urgentes sobre main