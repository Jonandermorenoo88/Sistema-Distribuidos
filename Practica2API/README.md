# EPO2-1C (Sistema con Spring Boot Frontend <img src="./imagenes/logo_spring_boot.svg" alt="Spring Boot Logo" width="30"/> y API en Flask <img src="./imagenes/logo_flask.png" alt="Flask Logo" width="30"/>)

## 📝 <span style="color:#2E8B57;">Descripción</span>

Este proyecto consiste en un sistema dividido en dos componentes principales: un **frontend** desarrollado con <strong>Spring Boot</strong> y una **API** creada con <strong>Flask</strong>.

Para una mejor separación de responsabilidades y lograr una arquitectura más **modular y escalable**, se optó por utilizar **dos bases de datos (BBDD)** distintas:
- **SQLite**: utilizada en la API para almacenar los datos relacionados con los países.
- **H2**: utilizada en el frontend para gestionar los usuarios que acceden a la aplicación.

La temática del sistema gira en torno a uan lista de **países**, permitiendo:
- Ver la lista completa de países registrados en la BBDD.
- Editar o eliminar países existentes.
- Descargar un archivo **.txt** con el listado completo de países que hay en la BBDD.

## 🛠️ <span style="color:#1E90FF;">Tecnologías utilizadas</span>

- ☕ <span style="color:#6A5ACD;">Java 21</span>
- 🌱 <span style="color:#228B22;">Spring Boot</span>
- 🖼️ <span style="color:#FF8C00;">Thymeleaf</span>
- 🎨 <span style="color:#DA70D6;">HTML/CSS</span>
- 🐍 <span style="color:#FFD700;">Python</span>
- ⚗️ <span style="color:#2F4F4F;">Flask</span>
- 🗄️ <span style="color:#B22222;">SQLite</span> (para la BBDD de la API)
- 🧪 <span style="color:#808080;">H2</span> (para la BBDD de usuarios en el frontend)
## 🚀 <span style="color:#FF69B4;">¿Cómo ejecutar la aplicación?</span>

### <img src="./imagenes/logo_spring_boot.svg" alt="Spring Boot Logo" width="30"/> Frontend (Spring Boot): 
Desde este directorio, ejecuta el siguiente comando en la terminal: `java -jar epo2-0.0.1-SNAPSHOT.jar`. La aplicación estará disponible en `http://localhost:8000`.
### <img src="./imagenes/logo_flask.png" alt="Flask Logo" width="30"/> API (Flask):
Accede al directorio /api y ejecuta: `python api.py`. La API se iniciará en `http://localhost:4400`.








