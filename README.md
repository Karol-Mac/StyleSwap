# StyleSwap REST API

StyleSwap is a complex level 4 (on the Fielding scale) REST API for a shopping platform to sell clothes to other users.
It is my solo project, which I am successively developing, fixing bugs and adding more features.
The API is as developer-friendly as possible, thanks to its simple design,
the use of HATEOAS (which makes it much easier to add a frontend)
and clear documentation created in the widely used SwaggerUI.
Users have access to a wide range of functionalities, including converting with sellers (other users),
saving interesting clothes in their warehouse and performing real transactions
(thanks to the connection with Stripe, the payment gateway).

## Main Application functionalities:
- security of ednpoints with JWT tokens
- connected payment gateway (Stripe)
- user management (role system)
- image storage on server
- CRUD of clothes
- CRUD of categories (special administrator rights)
- data validation & exception handling
- management of conversations and messages


### To test the API by yourself, you can:

- [x] check the SwaggerUI documentation [here](https://styleswap-691724339754.us-central1.run.app/swagger-ui/index.html#/)
- [x] download the project from GitHub and run it on your local machine
- [x] download the docker image and run it on your local machine

_If you decide to run the project on your local machine, the Application creates and loads example data automaticly_ [read more about it](#loaded-data)
#### Hope you enjoy ;D

## Using the API
If you running API on your local machine, ramember that it runs on port 8080 by default.
The API validates All data, so you can't send the wrong data to the server.
The API uses JWT tokens to secure endpoints, so you need to log in to get the token.
Two roles are created: ADMIN and USER.
Some endpoints are available only for ADMIN, some only for USER (e.g., owner of exact clothing), and some for both.
There are also endpoints available to everyone (ALL), for example, the login endpoint.

### Loaded data:
Beside roles, the API loads example data to the database:
- Three users:

    | username | email           | password | role  |
    |----------|-----------------|----------|-------|
    | admin    | admin@email.com | password | ADMIN |
    | user1    | user1@email.com | password | USER  |
    | user2    | user2@email.com | password | USER  |
 - Five clothing categories: 

    | ID | name    |
    |----|---------|
    | 1  | T-Shirt |
    | 2  | Jeans   |
    | 3  | Dresses |
    | 4  | Skirt   | 
    | 5  | Blouses | 
 - Six clothes:

| ID | name          | categoryID | owner |
|----|---------------|------------|-------|
| 1  | T-Shirt       | 1          | user1 |
| 2  | Jeans         | 3          | user2 |
| 3  | Dress         | 2          | user1 |
| 4  | Skirt         | 3          | user2 |
| 5  | Blouse        | 2          | user1 |
| 6  | White T-Shirt | 1          | user1 |

 - Every user also has storage:
   - user1 has stored clothes with ID: 2
   - user1 has stored clothes with ID: 3, 6

 - One conversation between user2 and user1 with messages:
    - (user2): "Hello, I would like to buy this T-Shirt but half the price"
    - (user1): "No, I won't sell it for 10$"


## API ENDPOINTS
### Authentication

| REQUEST STATUS | LINKS                | REQUEST CODE  | RETURN TYPE                     | PARAMETERS    | ACCESS |
|----------------|----------------------|---------------|---------------------------------|---------------|--------|
| POST           | `/api/auth/login`    | 200 (OK)      | `ResponseEntityJWTAuthResponse` | `LoginDto`    | ALL    |
| POST           | `/api/auth/signin`   | 200 (OK)      | `ResponseEntityJWTAuthResponse` | `LoginDto`    | ALL    |
| POST           | `/api/auth/register` | 201 (Created) | `ResponseEntity<String>`        | `RegisterDto` | ALL    |
| POST           | `/api/auth/signup`   | 201 (Created) | `ResponseEntity<String>`        | `RegisterDto` | ALL    |

### Category

| REQUEST STATUS | LINKS                          | REQUEST CODE  | RETURN TYPE                         | TYPE                       | ACCESS |
|----------------|--------------------------------|---------------|-------------------------------------|----------------------------|--------|
| GET            | `/api/categories`              | 200 (OK)      | `ResponseEntity<List<CategoryDto>>` | -                          | ALL    |
| GET            | `/api/categories/{categoryId}` | 200 (OK)      | `ResponseEntity<CategoryDto>`       | `long`                     | ALL    |
| POST           | `/api/categories`              | 201 (Created) | `ResponseEntity<CategoryDto>`       | `CategoryEdditDto`         | ADMIN  |
| PUT            | `/api/categories/{categoryId}` | 200 (OK)      | `ResponseEntity<CategoryDto>`       | `long`, `CategoryEdditDto` | ADMIN  |
| DELETE         | `/api/categories/{categoryId}` | 200 (OK)      | `ResponseEntity<String>`            | `long`                     | ADMIN  |

### Clothing

| REQUEST STATUS | LINKS                                       | REQUEST CODE     | RETURN TYPE                           | TYPE                | ACCESS      |
|----------------|---------------------------------------------|------------------|---------------------------------------|---------------------|-------------|
| GET            | `/api/clothes/category/{categoryId}?params` | 200 (OK)         | `ResponseEntity<ClotheModelResponse>` | `long`              | ALL         |
| GET            | `/api/clothes/{id}`                         | 200 (OK)         | `ResponseEntity<ClotheDto>`           | `long`              | ALL         |
| GET            | `/api/clothes/my`                           | 200 (OK)         | `ResponseEntity<ClotheModelResponse>` |                     | USER, ADMIN |
| POST           | `/api/clothes`                              | 201 (Created)    | `ResponseEntity<ClotheDto>`           | `ClotheDto`         | USER, ADMIN |
| PUT            | `/api/clothes/{id}`                         | 200 (OK)         | `ResponseEntity<ClotheDto>`           | `long`, `ClotheDto` | USER, ADMIN |
| DELETE         | `/api/clothes/{id}`                         | 204 (No Content) | `ResponseEntity<Void>`                | `long`              | USER, ADMIN |

### Image
| REQUEST STATUS | LINKS                      | REQUEST CODE     | RETURN TYPE                | TYPE                      | ACCESS      |
|----------------|----------------------------|------------------|----------------------------|---------------------------|-------------|
| GET            | `/api/images/{imageName}`  | 200 (OK)         | `ResponseEntity<Resource>` | `String`                  | ALL         |
| POST           | `/api/clothes/{id}/images` | 200 (Created)    | `ResponseEntity<String>`   | `long`, `[MultipartFile]` | USER, ADMIN |
| DELETE         | `/api/clothes/{id}/images` | 204 (No Content) | `ResponseEntity<Void>`     | `long`, `[String]`        | USER, ADMIN |

### Order

| REQUEST STATUS | LINKS                  | REQUEST CODE  | RETURN TYPE              | TYPE     | ACCESS      |
|----------------|------------------------|---------------|--------------------------|----------|-------------|
| POST           | `/api/orders?clotheId` | 201 (Created) | `ResponseEntity<String>` | `long`,  | USER, ADMIN |
| POST           | `/api/orders/webhook`  | 200 (OK)      | `ResponseEntity<String>` | `String` | ALL         |

### Storage

| REQUEST STATUS | LINKS                     | REQUEST CODE     | RETURN TYPE                       | TYPE   | ACCESS      |
|----------------|---------------------------|------------------|-----------------------------------|--------|-------------|
| GET            | `/api/storage`            | 200 (OK)         | `ResponseEntity<List<ClotheDto>>` |        | USER, ADMIN |
| POST           | `/api/storage/{clotheId}` | 201 (Created)    | `ResponseEntity<Void>`            | `long` | USER, ADMIN |
| DELETE         | `/api/storage/{clotheId}` | 204 (No Content) | `ResponseEntity<Void>`            | `long` | USER, ADMIN |

### Messaging

| REQUEST STATUS | LINKS                                        | REQUEST CODE  | RETURN TYPE                             | TYPE                       | ACCESS |
|----------------|----------------------------------------------|---------------|-----------------------------------------|----------------------------|--------|
| POST           | `/api/messaging/conversations`               | 201 (Created) | `ResponseEntity<Void>`                  | `long`, `String`           | USER   |
| GET            | `/api/messaging/conversations/buying`        | 200 (OK)      | `ResponseEntity<List<ConversationDto>>` | `String`                   | USER   |
| GET            | `/api/messaging/conversations/selling`       | 200 (OK)      | `ResponseEntity<List<ConversationDto>>` | `long`, `String`           | USER   |
| GET            | `/api/messaging/conversations/{id}/messages` | 200 (OK)      | `ResponseEntity<List<MessageDto>>`      | `long`, `String`           | USER   |
| POST           | `/api/messaging/conversations/{id}/messages` | 201 (Created) | `ResponseEntity<Void>`                  | `long`, `String`, `String` | USER   |


# Examples:
TODO: write about example api usages
(with curl examples etc.)