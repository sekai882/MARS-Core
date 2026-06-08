<div align="center">
  
# 🪐 MARS-Core: Enterprise Scouting Platform

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white)](https://www.thymeleaf.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white)](https://getbootstrap.com/)
[![Render](https://img.shields.io/badge/Render-%46E3B7.svg?style=for-the-badge&logo=render&logoColor=white)](https://render.com/)
[![Supabase](https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)](https://supabase.com/)

**Analítica de Rendimiento • Algoritmo Moneyball • Proyecciones Deportivas**

[**🚀 VER DEMO EN VIVO (RENDER)**](https://mars-core.onrender.com)

</div>

---

## 📖 Sobre el Proyecto

**MARS-Core** es una plataforma avanzada de *scouting* deportivo construida sobre el concepto de **Moneyball**. A través de la recolección de métricas tácticas y financieras, MARS-Core es capaz de calcular el **Índice de Eficiencia MARS-IEM** para descubrir joyas ocultas en el mercado de fichajes: jugadores cuyo rendimiento deportivo supera ampliamente su valoración y coste financiero.

## ✨ Características Principales

- 📊 **Algoritmo de Scouting Moneyball (IEM):** Evalúa el retorno deportivo en base a estadísticas ponderadas por minuto en relación a su coste financiero.
- 💎 **Tracking de "Mejores Joyas":** Registro automático de los mejores talentos encontrados globalmente por los usuarios (Top 5 histórico).
- 📈 **Proyección de Valor de Mercado:** Modelos matemáticos predictivos a 2, 5 y 10 años que aplican factores de corrección por edad y rendimiento (IEM).
- 🏆 **Matriz de Dominancia Competitiva:** Cruce de KPIs individuales (xG, velocidad punta, duelos defensivos, atajadas) contra el resto de jugadores de su misma posición.
- ⚽ **Sugerencia de Mejor XI:** Armado de plantilla óptima basada en el club, aplicando además bonificaciones de química entre jugadores de la misma nacionalidad y posiciones afines.
- 🛡️ **Seguridad y Roles:** Autenticación por roles para separar paneles analíticos avanzados de vistas de usuario estándar.

## 💻 Stack Tecnológico

- **Backend:** Java 17+, Spring Boot (Web, Data JPA, Security)
- **Base de Datos:** Supabase (PostgreSQL)
- **Frontend:** HTML5, Bootstrap 5, Thymeleaf, CSS3 (Glassmorphism & Dark Theme)
- **Gestión de Dependencias:** Maven
- **Despliegue:** Render

## 🚀 Despliegue en Vivo

El proyecto está actualmente desplegado y funcional en Render. Puedes probar el simulador del dashboard Moneyball accediendo a:
🔗 **[https://mars-core.onrender.com](https://mars-core.onrender.com)**

## 🛠️ Instalación y Configuración Local

Si deseas correr este proyecto en tu entorno local:

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/MARS-Core.git
   cd MARS-Core
   ```

2. **Compilar el proyecto con Maven:**
   ```bash
   ./mvnw clean package -DskipTests
   ```

3. **Ejecutar la aplicación:**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Acceder a la aplicación:**
   Abre tu navegador y dirígete a `http://localhost:8080`
