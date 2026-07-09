# Pamela Alexandra Gómez Tenorio - 00139622

## Indicaciones

Recientemente, se utilizó AI para crear un sistema de gestion de una biblioteca, el cual ha generado varios errores, su trabajo es arreglarlo. Dado el siguiente caso de uso, explique y/o resuelva cada problema según se le pida.

---

## Consideraciones

La libreria crea automaticamente un correo con los nombres de la persona

---

## Problemas

### 1. Filtro por autor y género (10%)

QA ha reportado que el endpoint para obtener los libros puede filtrar por **autor** y por **género**, o por cualquiera de los dos de manera individual.

Actualmente:

- Filtrar únicamente por autor funciona correctamente.
- Filtrar únicamente por género funciona correctamente.
- Filtrar por **autor y género al mismo tiempo** provoca que el servidor falle.

**Instrucción:** Explique la causa del problema y resuélvalo.

El problema es que en BookRepository el método definido para filtrar por autor y genero al mismo tiempo recibía un String genre, cuando genre en realidad es un ENUM. Debido a este cambio también se debe modificar el service que utiliza el método en getAllBooks y utilizar un parseGenre porque aunque el usuario ingresa un string, lo que necesitamos nosotros es un enum, por ello creamos la nueva función que facilita la conversión de un tipo a otro sin tener que pedirle al usuario que ingrese otra cosa. 

---

### 2. Error al volver a prestar un libro (10%)

Un usuario reportó que al pedir prestado el libro **The Selfish Gene**, devolverlo e intentar pedirlo prestado nuevamente, el servidor falla.

**Instrucción:** Explique la causa del problema y resuélvalo.

el problema es que available no retomaba su estado y ahora si regresa a true, lo que permite volver a prestar The Selfish Gene.


---

### 3. Cantidad de libros por género (10%)

Existe un endpoint que devuelve la cantidad de libros disponibles por género. Sin embargo, actualmente dicho endpoint falla.

**Instrucción:** Explique la causa del problema y resuélvalo.

Uno de los problemas es que BookService.getGenresAvailable() está sin implementar y solamente retorna null, por tanto no hay ningún método en todo el proyecto que cuente, solo que los recupere, al agregar una query que suma availableCount por género en BookRepository.java. se resuelve el problema: 

Fallaba porque solo existía GET /books/{id}. Con una query param en BookController.java se puede conseguir la cantidad de libros por genero

---

### 4. Error al consultar un libro por ID (10%)

Un miembro del equipo de frontend reporta que la siguiente llamada falla:

```http
GET /books?id=ed16ed1e-7017-4697-a08a-d28c09a74acf
```

**Instrucción:** Explique la causa del problema.

falla porque solo existía GET /books/{id}, pero con un query param en BookController.java se puede resolver.


---

### 5. Error al crear un libro (10%)

QA ha reportado que el siguiente payload enviado al endpoint `POST /books` provoca un error:

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "genre": "classic",
  "isbn": "978-0132350884",
  "available": true,
  "availableCount": 5
}
```
Provoca un error por varias razones, una es que no aceptaba géneros en minúsculas, por ejemplo para este payload lo que el esperaba era CLASSIC y le llegó classic, no es un código muy flexible. 
Con un dto nos podemos asegurar que el libro se inserte con todos los atributos necesarios


**Instrucción:** Explique la causa del problema.

---

El problema es que 

### 6. Devolución de libros no prestados (20%)

QA ha reportado que un usuario es capaz de devolver libros que nunca ha solicitado en préstamo.

**Instrucción:**

- Confirme si este comportamiento es realmente posible.
- Si es posible, explique la causa y resuelva el problema.
- Si no es posible, explique por qué, haciendo referencia al código correspondiente.

Si es posible devolver libros no prestados, porque en MovementService no validaba el historial previo, es decir, no verifica si existe un préstamo activo, se soluciona al consultar el último movimiento y verificar que tuviera estado BORROWING como para autorizar la devolución del libro.


---