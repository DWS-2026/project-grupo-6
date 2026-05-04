# 🚀 Dashboard de Seguimiento: API REST y Seguridad (URJC)
> **Proyecto:** Desarrollo Web Seguro (2025-2026)
> **Calificación Inicial:** 10.0 | **Estado de la Entrega:** 🟡 En Desarrollo
---
## TO DO, los puntos que deberiamos tener, los mas relevantes
Los puntos negros son las cosas que deberian
* Funcionalidad de la api, que esten los metodos get,post,delete y put en las entidades. Eso corresponde dentro de la rubrica a lo de: [ ]
    * listados paginados [ ]
    * detalle individual [ ]
    * creacion entidades [ ]
    * Borrado de entidades [ ]
* Acceso por Rol en la api y web
    * web [X] (en principio ya esta)
    * api [ ] (faltan cositas)
* Texto enriquecido, ya esta, lo unico que diria es una comprobacion con ia con algun ataque asi mas complejo [ ]
* Fichero en disco
    * Implementacion [ ]
    * Seguridad del ataque [ ]

Cosas extra, o mas secundarias, o que si no nos da tiempo, no suspendemos.
* Imagenes en la api []
* Error en la web, en donde al iniciar sesion en https://localhost:8433/ en el header sale el id del usuario pero en otras ubicaciones sale el nombre


---

## 📹 SECCIÓN 1: Funcionalidades del Vídeo (Obligatorio)
*Si no se muestra en el vídeo con su subtítulo correspondiente, la nota es 0 en ese apartado.*

| Funcionalidad | Subtítulo Obligatorio | Opciones de Penalización | Estado |
| :--- | :--- | :--- | :---: |
| **Login** | `"Endpoint de login"` | ✅ Si / ❌ No (-3) | [X] |
| **Registro** | `"Endpoint de registro"` | ✅ Si / ❌ No (-3) | [X] |
| **Listados Paginados** | `"Endpoint listado de X"` | ✅ Si / ⚠️ Faltan datos (-2) / ❌ No (-10) | [ ] |
| **Detalle Individual** | `"Endpoint detalle de X"` | ✅ Si / ❌ No (-2) | [ ] |
| **Creación Entidades** | `"Endpoint creación de X"` | ✅ Si / ⚠️ Faltan cosas (-1 a -3) / ❌ No (-10) | [ ] |
| **Validación Servidor** | `"Validación de campo"` | ✅ Si / ⚠️ Sin mensaje (-1) / ❌ No (-2) | [ ] |
| **Borrado Entidades** | `"Endpoint borrado de X"` | ✅ Si / ⚠️ Faltan cosas (-1 a -3) / ❌ No (-10) | [ ] |
| **Edición Entidades** | `"Endpoint edición de X entidad"` | ✅ Si / ❌ No (-2) | [ ] |
| **Ver Imágenes** | `"Endpoint imagen X entidad"` | ✅ Si / ❌ No (-2) | [ ] |
| **Acceso por Rol** | `"Endpoint control de acceso por rol"` | ✅ Si / ⚠️ Fallos leves (-2) / ❌ No (-5.5) | [ ] |
| **Acceso por Dueño** | `"Endpoint control de acceso por dueño"` | ✅ Si / ❌ No (-5.5) | [ ] |
| **Errores en JSON** | `"Endpoint error"` | ✅ JSON / ❌ Aparece HTML (-2) | [ ] |
| **Interfaz Web** | `"Web"` | ✅ Si / ❌ No (-4) | [ ] |
| **Texto Enriquecido** | `"Texto enriquecido"` | ✅ Si / ❌ No (-5.5) | [ ] |
| **Ficheros en Disco** | `"Fichero en disco"` | ✅ Si / ❌ No (-5.5) | [ ] |

---

## 🛠️ SECCIÓN 2: Calidad de Código y Arquitectura
*Evaluación del código fuente y cumplimiento de estándares técnicos.*

| Aspecto Técnico | Requisito del Proyecto | Penalización si falla | Estado |
| :--- | :--- | :--- | :---: |
| **Vulnerabilidades** | Sin fallos de seguridad vistos en clase | ❌ Si tiene (-5.5) | [ ] |
| **Reutilización** | Servicios compartidos (Rest/Web Controllers) | ❌ No compartidos (-2) | [X] |
| **Diseño REST** | Plurales, Inglés, Métodos y Estados HTTP | ⚠️ Deficiencias (-0.5 a -2) | [ ] |
| **Prefijo API** | Todas las URLs comienzan con `/api/v1/` | ❌ No cumple (-0.5) | [X] |
| **Uso de DTOs** | No usar entidades en entrada/salida | ❌ No usa DTOs (-2) | [X] |
| **Seguridad Pass** | No devolver contraseña en listados | ⚠️ Cifrada (-3) / ❌ En claro (-5) | [ ] |
| **Protección CSRF** | OFF en API REST / ON en Web | ❌ Config. incorrecta (-1) | [X] |
| **Idioma** | Código y comentarios 100% en Inglés | ⚠️ Castellano (-0.5 a -1) | [X] |
| **Estilo** | Formato de código adecuado y limpio | ⚠️ Fallos estilo (-0.5 a -1.5) | [X] |

---

## 📄 SECCIÓN 3: Documentación y Entregables
*Archivos auxiliares necesarios en la raíz del repositorio.*

| Entregable | Requisito Específico | Penalización si falla | Estado |
| :--- | :--- | :--- | :---: |
| **Postman** | Variable `{{baseUrl}}` para local/remoto | ❌ No la usa (-1) | [ ] |
| **OpenAPI Docs** | Carpeta `/api-docs` (.yaml y .html) | ❌ No están (-2) | [ ] |
| **README Links** | Enlaces a OpenAPI y doc visual operativa | ❌ No hay enlaces (-0.5) | [ ] |
| **Diagrama Clases** | Actualizado con RestControllers y Servicios | ❌ No actualizado (-1) | [ ] |
| **Participación** | Detalle de tareas por cada miembro | ❌ No aparece (-3) | [ ] |

---

## 📌 Recordatorios de Última Hora
* **Vídeo:** Probad el control de dueño intentando borrar un objeto de "Usuario B" estando logueados como "Usuario A".
* **Vídeo:** No olvidéis que el archivo `.srt` debe estar perfectamente sincronizado.
* **Código:** Revisad que ningún `@RestController` devuelva entidades JPA directamente.