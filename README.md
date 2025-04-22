# Bootcamp Backend Java - Sprint Nº1 (Equipo 8)

## Proyecto SocialMeli

---

## Dinámica de trabajo

Los requerimientos estuvieron divididos para que todos los integrantes del grupo pudieran realizar actividades tanto **individuales** como **grupales**.  
El objetivo fue fortalecernos y llevar a cabo un buen desarrollo implementando **buenas prácticas**.

Seguimos la guía de **Requerimientos Técnicos Funcionales** otorgada por el facilitador.

---

## Metodología Agile - SCRUM

- **Dailys:** Reuniones diarias de 15 minutos a las 16:00 hs (hora Argentina).
- **Scrum Master:** 
  - Principal: **Ariel Jaime**
  - Suplente: **Manuela Tonelli**

---

## Equipo de desarrollo

- Abril Uberti
- Andrés Camilo Onate Quimbayo
- Juan Sebastian Duran Macias
- Manuela Tonelli
- Ornella Alonso
- Yoana Denise Morello Andreoni

---

## Gestión de proyectos: Trello

Utilizamos **Trello** para planificar, organizar y controlar el proyecto.

Flujo de trabajo:

`BACKLOG` ➔ `TO-DO` ➔ `IN PROGRESS` ➔ `DONE`

---

## Diseño del Proyecto

### Código
El código fue desarrollado en **Inglés**, siguiendo buenas prácticas de nomenclatura.

### Nomenclatura
- **Clases:** `PascalCase`  ➔  `ExampleClass`
- **Atributos:** `lowerCamelCase`  ➔  `exampleAttribute`
- **Métodos:** `lowerCamelCase`  ➔  `exampleMethod`

---

## Estructura del Proyecto

Arquitectura de **N capas**:

```
Controller/
Dto/
Model/
Exception/
Service/ - IService
Repository/ - IRepository
```

**Inyección de dependencias** implementada por constructor.

---

## Diagrama de Clases

(https://drive.google.com/file/d/13Qtv5VHR3Zj4PQSg3ljCSur7UiBeDohV/view)


## Pruebas de APIs

- **Postman:** Colección con todos los endpoints implementados.
- **Swagger:** Documentación interactiva disponible en:

```
http://localhost:8080/swagger-ui.html
```

---

## Cómo ejecutar el proyecto

### Requisitos
- Java 17 (Temurin recomendado)
- Maven

### Comandos
```bash
mvn clean install
mvn spring-boot:run
```

---

## Funcionalidades implementadas

| US ID   | Funcionalidad                                                          | Método | Endpoint                                                    | Responsable    |
|---------|------------------------------------------------------------------------|--------|-------------------------------------------------------------|----------------|
| US0001  | Seguir a un vendedor                                                   | POST   | `/users/{userId}/follow/{userIdToFollow}`                   | Abril          |
| US0002  | Obtener cantidad de seguidores de un vendedor                          | GET    | `/users/{userId}/followers/count`                           | Ornella        |
| US0003  | Listar todos los seguidores de un vendedor                             | GET    | `/users/{userId}/followers/list`                            | Andres         |
| US0004  | Listar todos los vendedores que sigue un usuario                       | GET    | `/users/{userId}/followed/list`                             | Yoana          |
| US0005  | Crear una nueva publicación                                            | POST   | `/products/post`                                            | Abril          |
| US0006  | Listar publicaciones de las últimas 2 semanas de vendedores seguidos   | GET    | `/products/followed/{userId}/list`                          | Ornella        |
| US0007  | Dejar de seguir a un vendedor                                          | PUT    | `/users/{userId}/unfollow/{userIdToUnfollow}`               | Manuela        |
| US0008  | Ordenamiento alfabético ascendente/descendente en followers/followed   | GET    | `/users/{userId}/followers/list?order=name_asc`             | Manuela        |
| US0009  | Ordenamiento por fecha asc/desc en publicaciones                       | GET    | `/products/followed/{userId}/list?order=date_desc`          | Juan           |
| US0010  | Publicar un producto en promoción                                      | POST   | `/products/promo-post`                                      | Andres         |
| US0011  | Obtener cantidad de productos en promoción de un vendedor              | GET    | `/products/promo-post/count?user_id={userId}`               | Yoana          |
| US0012  | Listar productos en promoción de un vendedor (**BONUS**)               | GET    | `/products/promo-post/list?user_id={userId}`                | Juan           |
| US0013  | Valorar el posteo de un vendedor (**BONUS**)                           | POST   | `/products/valoration`                                      | Todos (reunión)|
| US0014  | Listar valoraciones de un posteo por puntuación especifica (**BONUS**) | GET    | `/products/{post_id}/valorations?valoration_number={number}`| Todos (reunión)|
| US0015  | Listar las valoraciones que realizó un usuario (**BONUS**)             | GET    | `products/{user_id}/user/valorations`                       | Todos (reunión)|
| US0016  | Obtener el promedio de valoraciones de un producto (**BONUS**)         | GET    | `products/{product_id}/valorations/average`                 | Todos (reunión)|

