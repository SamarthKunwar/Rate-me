# RateMe

RateMe is a Spring Boot REST application with a plain HTML/CSS/JavaScript frontend planned for rating POIs such as restaurants, cafes, and pubs around Zweibruecken.

## ER Model

```mermaid
erDiagram
    USER ||--o{ RATING : writes
    POI ||--o{ RATING : receives
    IMAGE ||--o{ RATING : optional_attachment

    USER {
        int id PK
        varchar username UK
        varchar email
        varchar firstname
        varchar lastname
        varchar street
        varchar street_nr
        varchar zip
        varchar city
        varbinary password_hash
        varbinary password_salt
    }

    POI {
        bigint id PK
        enum type
        double lat
        double lon
        varchar name
        varchar amenity
        varchar cuisine
        varchar phone
        varchar opening_hours
        varchar website
        varchar wheelchair
        varchar takeaway
        varchar delivery
        varchar smoking
        varchar outdoor_seating
        varchar reservation
        varchar addr_city
        varchar addr_country
        varchar addr_housenumber
        varchar addr_postcode
        varchar addr_street
        json tags
    }

    RATING {
        int id PK
        int user_id FK
        bigint poi_id FK
        int grade
        varchar txt
        int image_id FK "nullable"
        timestamp created_at
    }

    IMAGE {
        int id PK
        mediumblob img
    }
```

### Relationship Explanation

A `user` can write many ratings. Each rating belongs to exactly one user.

A `poi` can receive many ratings. Each rating belongs to exactly one POI.

A rating can optionally contain an image. This is represented by the nullable foreign key `rating.image_id`.

### Dependencies

| Relationship        | Foreign Key                             | Meaning                                  |
| ------------------- | --------------------------------------- | ---------------------------------------- |
| `user` -> `rating`  | `rating.user_id` references `user.id`   | A rating must belong to an existing user |
| `poi` -> `rating`   | `rating.poi_id` references `poi.id`     | A rating must belong to an existing POI  |
| `image` -> `rating` | `rating.image_id` references `image.id` | A rating may optionally have an image    |

## REST Use Cases And DTO Planning

| Use Case        | Frontend Sends                                                | Backend Returns                                              |
| --------------- | ------------------------------------------------------------- | ------------------------------------------------------------ |
| Register user   | Username, email, firstname, lastname, address data, password  | Session token and user information                           |
| Login user      | Username and password                                         | Session token and user information                           |
| Logout user     | Session token                                                 | Success message                                              |
| Load map        | Session token                                                 | List of POIs with id, name, latitude, longitude, and amenity |
| Select POI      | POI id and session token                                      | POI details and existing ratings                             |
| Create rating   | POI id, grade, comment text, optional image, session token    | Created rating                                               |
| Load my ratings | Session token                                                 | List of ratings created by the logged-in user                |
| Edit rating     | Rating id, grade, comment text, optional image, session token | Updated rating                                               |
| Delete rating   | Rating id and session token                                   | Success message                                              |
| Delete user     | Session token                                                 | Success message, user and related ratings are deleted        |

## DTO Idea

Entities represent the database tables. DTOs represent the data exchanged between frontend and backend.

| DTO | Purpose |
| --- | ------- |
| `UserDtoIn` | Input data needed to create a new user |
| `LoginDtoIn` | Input data needed to log in |
| `LoginDtoOut` | Output data after successful login or registration |
| `UserDtoOut` | Safe user data returned to the frontend |
| `PoiOverviewDtoOut` | Small POI output data for displaying markers on the map |
| `PoiDetailDtoOut` | Detailed POI output data after selecting a marker |
| `RatingDtoIn` | Input data needed to create a rating |
| `RatingDtoOut` | Output rating data shown for a selected POI |
| `RatingUpdateDtoIn` | Input data needed to edit a rating |
| `MyRatingDtoOut` | Output rating data shown in the "My Ratings" tab |

## Backend Architecture

The backend is built as a Spring Boot REST application. The code is separated into layers so that each class has one clear responsibility.

```text
Frontend
   ↓
Controller
   ↓
Service
   ↓
DAO
   ↓
Database
```

### Package Structure

| Package      | Responsibility                                                                          |
| ------------ | --------------------------------------------------------------------------------------- |
| `controller` | REST endpoints. Controllers receive HTTP requests and return DTOs as JSON.              |
| `service`    | Application logic. Services decide what the app should do and convert entities to DTOs. |
| `dao`        | Manual database access with JPA `EntityManager`. No Spring Data repositories are used.  |
| `entity`     | Java classes mapped to database tables.                                                 |
| `dto`        | Java records used for REST request and response data.                                   |
| `auth`       | Authentication helper logic, for example password hashing and token handling.           |

### Layer Responsibilities

Controllers should only handle HTTP:

```text
URL + HTTP method + call service + return result
```

Services contain the app rules:

```text
validate input
handle not-found cases
convert entities to DTOs
coordinate DAO classes
```

DAO classes talk to the database:

```text
EntityManager queries
persist
find
update
delete
```

Entities represent database rows:

```text
poi table    -> Poi entity
user table   -> User entity
rating table -> Rating entity
image table  -> Image entity
```

DTOs represent API data:

```text
Poi entity  -> PoiOverviewDtoOut
Poi entity  -> PoiDetailDtoOut
User entity -> UserDtoOut
```

Sensitive fields such as `password_hash` and `password_salt` are never sent to the frontend.

## Backend Class Diagram

```mermaid
classDiagram

class AuthController {
  -AuthService authService
  +register(UserDtoIn request) LoginDtoOut
  +login(LoginDtoIn request) LoginDtoOut
  +logout(String token) void
  +deleteCurrentUser(String token) void
}

class PoiController {
  -PoiService poiService
  +getAllPois() List~PoiOverviewDtoOut~
  +getOnePoi(Long id) PoiDetailDtoOut
}

class RatingController {
  -RatingService ratingService
  +getRatingsForPoi(Long poiId) List~RatingDtoOut~
  +createRating(String token, RatingDtoIn request) RatingDtoOut
  +createRatingWithImage(String token, Long poiId, Integer grade, String text, MultipartFile image) RatingDtoOut
  +getMyRatings(String token) List~MyRatingDtoOut~
  +updateRating(String token, Integer id, RatingDtoIn request) RatingDtoOut
  +updateRatingWithImage(String token, Integer id, Integer grade, String text, MultipartFile image) RatingDtoOut
  +deleteRating(String token, Integer id) void
}

class ImageController {
  -ImageService imageService
  +getImage(Integer id) ResponseEntity~byte[]~
}

class AuthService {
  -UserDao userDao
  -RatingDao ratingDao
  -ImageService imageService
  -PasswordService passwordService
  -AuthTokenManager authTokenManager
  +registerUser(UserDtoIn request) LoginDtoOut
  +loginUser(LoginDtoIn request) LoginDtoOut
  +logoutUser(String token) void
  +deleteCurrentUser(String token) void
}

class PoiService {
  -PoiDao poiDao
  +findAllPois() List~PoiOverviewDtoOut~
  +findPoiById(Long id) PoiDetailDtoOut
}

class RatingService {
  -int MIN_GRADE
  -int MAX_GRADE
  -int MAX_TEXT_LENGTH
  -RatingDao ratingDao
  -PoiDao poiDao
  -ImageService imageService
  -AuthTokenManager authTokenManager
  +getRatingsForPoi(Long poiId) List~RatingDtoOut~
  +createRating(String token, RatingDtoIn request) RatingDtoOut
  +createRatingWithImage(String token, Long poiId, Integer grade, String text, MultipartFile imageFile) RatingDtoOut
  +getMyRatings(String token) List~MyRatingDtoOut~
  +updateRating(String token, Integer ratingId, RatingDtoIn request) RatingDtoOut
  +updateRatingWithImage(String token, Integer ratingId, Integer grade, String text, MultipartFile imageFile) RatingDtoOut
  +deleteRating(String token, Integer ratingId) void
  -getUserFromToken(String token) User
  -validateRatingInput(RatingDtoIn request) void
  -validateRatingInput(Integer grade, String text) void
  -checkRatingOwner(Rating rating, User user) void
  -toRatingDtoOut(Rating rating) RatingDtoOut
  -toMyRatingDtoOut(Rating rating) MyRatingDtoOut
  -getImageId(Rating rating) Integer
}

class ImageService {
  -int MAX_IMAGE_SIZE_BYTES
  -ImageDao imageDao
  +saveImage(byte[] imageBytes) Image
  +saveUploadedImage(MultipartFile file) Image
  +getImageBytes(Integer id) byte[]
  +deleteImage(Image image) void
  -validateImageBytes(byte[] imageBytes) void
}

class UserDao {
  -EntityManager entityManager
  +create(User user) User
  +findById(Integer id) Optional~User~
  +findByUsername(String username) Optional~User~
  +existsByUsername(String username) boolean
  +delete(User user) void
}

class PoiDao {
  -EntityManager entityManager
  +findAll() List~Poi~
  +findById(Long id) Optional~Poi~
}

class RatingDao {
  -EntityManager entityManager
  +create(Rating rating) Rating
  +findById(Integer id) Optional~Rating~
  +findByIdWithDetails(Integer id) Optional~Rating~
  +findByPoiId(Long poiId) List~Rating~
  +findByUserId(Integer userId) List~Rating~
  +update(Rating rating) Rating
  +delete(Rating rating) void
  +deleteByUserId(Integer userId) int
}

class ImageDao {
  -EntityManager entityManager
  +create(Image image) Image
  +findById(Integer id) Optional~Image~
  +delete(Image image) void
}

class AuthTokenManager {
  -Map~String, User~ activeTokens
  +createToken(User user) String
  +removeToken(String token) boolean
  +removeTokensForUser(Integer userId) void
  +isValid(String token) boolean
  +getUser(String token) Optional~User~
  +requireValidToken(String token) void
}

class PasswordService {
  +generateSalt() byte[]
  +hashPassword(String password, byte[] salt) byte[]
  +passwordMatches(String password, byte[] salt, byte[] expectedHash) boolean
}

class User {
  -Integer id
  -String username
  -String email
  -String firstname
  -String lastname
  -String street
  -String streetNr
  -String zip
  -String city
  -byte[] passwordHash
  -byte[] passwordSalt
}

class Poi {
  -Long id
  -String type
  -Double lat
  -Double lon
  -String name
  -String amenity
  -String cuisine
  -String phone
  -String openingHours
  -String website
  -String wheelchair
  -String takeaway
  -String delivery
  -String smoking
  -String outdoorSeating
  -String reservation
  -String addrCity
  -String addrCountry
  -String addrHousenumber
  -String addrPostcode
  -String addrStreet
  -String tags
}

class Rating {
  -Integer id
  -User user
  -Poi poi
  -Integer grade
  -String text
  -Image image
  -LocalDateTime createdAt
  +update(Integer grade, String text, Image image) void
}

class Image {
  -Integer id
  -byte[] img
}

AuthController --> AuthService
PoiController --> PoiService
RatingController --> RatingService
ImageController --> ImageService

AuthService --> UserDao
AuthService --> RatingDao
AuthService --> ImageService
AuthService --> PasswordService
AuthService --> AuthTokenManager

PoiService --> PoiDao

RatingService --> RatingDao
RatingService --> PoiDao
RatingService --> ImageService
RatingService --> AuthTokenManager

ImageService --> ImageDao

UserDao --> User
PoiDao --> Poi
RatingDao --> Rating
ImageDao --> Image

Rating --> User
Rating --> Poi
Rating --> Image
```

## Backend Communication Diagrams

### Create Rating With Image

```mermaid
sequenceDiagram
    actor Frontend
    participant RatingController
    participant RatingService
    participant AuthTokenManager
    participant PoiDao
    participant ImageService
    participant ImageDao
    participant RatingDao
    participant Database

    Frontend->>RatingController: POST /ratings multipart/form-data + Authorization token
    RatingController->>RatingService: createRatingWithImage(token, poiId, grade, text, image)

    RatingService->>AuthTokenManager: getUser(token)
    AuthTokenManager-->>RatingService: Optional<User>

    RatingService->>RatingService: validateRatingInput(grade, text)

    RatingService->>PoiDao: findById(poiId)
    PoiDao->>Database: SELECT poi by id
    Database-->>PoiDao: Poi
    PoiDao-->>RatingService: Optional<Poi>

    RatingService->>ImageService: saveUploadedImage(imageFile)
    ImageService->>ImageService: validateImageBytes(imageBytes)
    ImageService->>ImageDao: create(new Image(imageBytes))
    ImageDao->>Database: INSERT image
    Database-->>ImageDao: saved Image
    ImageDao-->>ImageService: Image
    ImageService-->>RatingService: Image

    RatingService->>RatingDao: create(new Rating(user, poi, grade, text, image))
    RatingDao->>Database: INSERT rating
    Database-->>RatingDao: saved Rating
    RatingDao-->>RatingService: Rating

    RatingService->>RatingService: toRatingDtoOut(savedRating)
    RatingService-->>RatingController: RatingDtoOut
    RatingController-->>Frontend: JSON RatingDtoOut
```

### Delete Current User

```mermaid
sequenceDiagram
    actor Frontend
    participant AuthController
    participant AuthService
    participant AuthTokenManager
    participant RatingDao
    participant ImageService
    participant ImageDao
    participant UserDao
    participant Database

    Frontend->>AuthController: DELETE /auth/me + Authorization token
    AuthController->>AuthService: deleteCurrentUser(token)

    AuthService->>AuthTokenManager: getUser(token)
    AuthTokenManager-->>AuthService: Optional<User>

    AuthService->>RatingDao: findByUserId(user.id)
    RatingDao->>Database: SELECT ratings for user
    Database-->>RatingDao: List<Rating>
    RatingDao-->>AuthService: List<Rating>

    loop for each rating
        AuthService->>RatingDao: delete(rating)
        RatingDao->>Database: DELETE rating

        AuthService->>ImageService: deleteImage(image)
        ImageService->>ImageDao: delete(image)
        ImageDao->>Database: DELETE image
    end

    AuthService->>UserDao: delete(user)
    UserDao->>Database: DELETE user

    AuthService->>AuthTokenManager: removeTokensForUser(user.id)
    AuthService-->>AuthController: void
    AuthController-->>Frontend: 200/204 response
```

## Current Backend Status

### User DAO

`UserDao` is responsible for database access to the `user` table.

Current responsibilities:

```text
create a user
find a user by id
find a user by username
check whether a username already exists
```

Password hashing is not done in `UserDao`, because database access and authentication logic are separate responsibilities.

### Password Handling

`PasswordService` is responsible for password-related security logic.

Current responsibilities:

```text
generate random salt
hash password with salt
compare entered password with stored password hash
```

The application does not store plain text passwords. During registration, the backend creates:

```text
password_salt
password_hash
```

During login, the backend hashes the entered password again with the stored salt and compares the result with the stored hash.

### Token Handling

`AuthTokenManager` stores active login tokens in memory.

Current responsibilities:

```text
create a UUID token
store token -> user
remove a token on logout
check whether a token is valid
find the user for a token
reject invalid tokens with 401 Unauthorized
```

This is a simple custom token mechanism and not JWT.

## Backend Rules From The Lecture Notes

The implementation follows the lecture style:

| Rule                       | Implementation Direction                                                           |
| -------------------------- | ---------------------------------------------------------------------------------- |
| Use REST resources         | Endpoints use nouns such as `/pois` instead of verbs like `/getPois`.              |
| Use HTTP methods correctly | `GET` for reading, `POST` for creating, `PUT` for updating, `DELETE` for deleting. |
| Use DTO records            | REST request and response objects are Java records.                                |
| Use JPA manually           | DAO classes use `EntityManager`.                                                   |
| No Spring Data             | No `JpaRepository` or `CrudRepository`.                                            |
| No Spring Security/JWT     | Authentication uses a custom token mechanism.                                      |
| Store passwords safely     | Passwords are stored as hash + salt, never as plain text.                          |

tests for DAO and controller classes

```

```
