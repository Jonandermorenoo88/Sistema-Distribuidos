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

## 📡 <span style="color:#8A2BE2;">Endpoints de la API</span>

A continuación se presentan los diferentes endpoints disponibles en la API Flask. Cada uno permite interactuar con los datos de los países registrados en la base de datos.

<div align="center">

| **Método** | **Endpoint**               | **Descripción**                           |
|:----------:|:----------------------------|:------------------------------------------|
| 🟢 **GET**    | `/`                        | Devuelve una presentación y autores de la API |
| 🟢 **GET**    | `/paises`                  | Obtiene la lista completa de países        |
| 🟢 **GET**    | `/paises/<id>`        | Obtiene la información de un país por ID   |
| 🔵 **POST**   | `/paises`                  | Crea un nuevo país                         |
| 🟠 **PUT**    | `/paises/<id>`        | Actualiza la información de un país por ID |
| 🔴 **DELETE** | `/paises/<id>`        | Elimina un país de la base de datos por ID  |
| 🟢 **GET**    | `/paises/download/txt`      | Descarga un archivo TXT con la lista de países |

</div>

## 📦 <span style="color:#FF6347;">Estructura del JSON de Países</span>

Todos los datos que se envían o reciben desde la API relacionados con países siguen la siguiente estructura en formato **JSON**:

```json
{
    "id": 1,
    "nombre": "España",
    "capital": "Madrid",
    "continente": "Europa",
    "poblacion": 47000000
}
```
### 📝 <span style="color:#20B2AA;">Descripción de los campos</span>

- 🆔 **id**: Identificador único del país (**entero**).  
  *No es necesario proporcionarlo al realizar solicitudes **POST** o **PUT** (se genera automáticamente).*
- 🌍 **nombre**: Nombre del país (**texto**).
- 🏛️ **capital**: Capital del país (**texto**).
- 🗺️ **continente**: Continente al que pertenece (**texto**).
- 👥 **poblacion**: Número de habitantes (**entero**).
- 

### ⚠️ <span style="color:#DC143C;">Tratamiento de errores</span>

La API muestra diferentes tipos de mensajes para garantizar una mejor experiencia de usuario y una respuesta clara ante problemas.

A continuación se muestran algunos ejemplos:

<div align="center">
  <img src="./imagenes/DELETE.png" alt="Error 404 - País no encontrado - DELETE" width="800" style="border: 2px solid #ccc; border-radius: 10px;"/>
  <p><em>Figura 1: Error 404 - País no encontrado.</em></p>
</div>

<div align="center">
  <img src="./imagenes/GET_ONE.png" alt="Error 404 - País no encontrado - GET" width="800" style="border: 2px solid #ccc; border-radius: 10px;"/>
  <p><em>Figura 2: Error 404 - País no encontrado.</em></p>
</div>

<div align="center">
  <img src="./imagenes/POST.png" alt="Error 400 - Faltan datos - POST" width="800" style="border: 2px solid #ccc; border-radius: 10px;"/>
  <p><em>Figura 3: Error 400 - Faltan datos.</em></p>
</div>

<div align="center">
  <img src="./imagenes/PUT.png" alt="Error 404 - País no encontrado - PUT" width="800" style="border: 2px solid #ccc; border-radius: 10px;"/>
  <p><em>Figura 4: Error 404 - País no encontrado.</em></p>
</div>

## 📚 <span style="color:#DC143C;">Manuel de Usuario</span>

En esta sección vamos a explicar de manera general como utilizar la aplicación:

<div align="center">
  <img src="./imagenes/PANTALLA_INICIO.png" alt="Inicio" width="800" style="border: 2px solid #ccc; border-radius: 10px;"/>
</div>
 <p><em>Al entrar en la aplicación tenemos la opción de loguearnos o crear un nuevo usuario, cabe resaltar que no hay ningún usuario por defecto creado, por lo que tendras que crear uno en el boton "Crear nuevo usuario".</em></p>

 <div align="center">
  <img src="./imagenes/CREAR_USUARIO.png" alt="Crear usuario" width="800" style="border: 2px solid #ccc; border-radius: 10px;"/>
</div>
 <p><em>Con este formulario podrá crear un nuevo usuario.</em></p>

  <div align="center">
  <img src="./imagenes/TABLA_PRINCIPAL.png" alt="Tabla Principal" width="800" style="border: 2px solid #ccc; border-radius: 10px;"/>
</div>
 <p><em>Al loguearnos, tendremos una tabla con todos los registros de la bbdd, desde allí podremos crear, editar o borrar los registros de la mismas o incluso descargar un .txt con todos los registros</em></p>

### ⚠️ <span style="color:#DC143C;">Tratamiento de errores</span>
<div align="center">
  <img src="./imagenes/ERROR_404.png" alt="Error 404" width="800" style="border: 2px solid #ccc; border-radius: 10px;"/>
</div>
 <p><em>En caso de tener una excepción se mostrará una pantalla de error mostrando el mensaje que devuelva la API; por ejemplo: al intentar editar un pais cuyo id no existe</em></p>

<div align="center">
  <img src="./imagenes/API_NO_LEVANTADA.png" alt="API no levantada" width="800" style="border: 2px solid #ccc; border-radius: 10px;"/>
</div>
 <p><em>esta pantalla no solo se muestra en caso de errores de la API, si no tambien si esta no esta levantada</em></p>

