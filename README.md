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

| Relationship | Foreign Key | Meaning |
|---|---|---|
| `user` → `rating` | `rating.user_id` references `user.id` | A rating must belong to an existing user |
| `poi` → `rating` | `rating.poi_id` references `poi.id` | A rating must belong to an existing POI |
| `image` → `rating` | `rating.image_id` references `image.id` | A rating may optionally have an image |
