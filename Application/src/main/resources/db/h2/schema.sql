-- H2 (MySQL compatibility mode) schema for the `cloud` profile.
-- Mirrors db/initdb/*.sql, with MySQL-only types mapped to H2 equivalents:
--   ENUM(...)   -> VARCHAR       JSON       -> VARCHAR (stored/served as text)
--   MEDIUMBLOB  -> BLOB          VARBINARY  -> VARBINARY

DROP TABLE IF EXISTS rating;
DROP TABLE IF EXISTS image;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS poi;

CREATE TABLE poi (
    id BIGINT PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    lat DOUBLE,
    lon DOUBLE,
    name VARCHAR(255),
    amenity VARCHAR(100),
    cuisine VARCHAR(255),
    phone VARCHAR(100),
    opening_hours VARCHAR(255),
    website VARCHAR(255),
    wheelchair VARCHAR(50),
    takeaway VARCHAR(50),
    delivery VARCHAR(50),
    smoking VARCHAR(50),
    outdoor_seating VARCHAR(50),
    reservation VARCHAR(100),
    addr_city VARCHAR(100),
    addr_country VARCHAR(10),
    addr_housenumber VARCHAR(20),
    addr_postcode VARCHAR(20),
    addr_street VARCHAR(255),
    tags VARCHAR(10000) NOT NULL
);

CREATE TABLE image (
    id INT AUTO_INCREMENT PRIMARY KEY,
    img BLOB NOT NULL
);

CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL,
    firstname VARCHAR(20) NOT NULL,
    lastname VARCHAR(20) NOT NULL,
    street VARCHAR(30) NOT NULL,
    street_nr VARCHAR(20) NOT NULL,
    zip VARCHAR(20) NOT NULL,
    city VARCHAR(30) NOT NULL,
    password_hash VARBINARY(1000) NOT NULL,
    password_salt VARBINARY(1000) NOT NULL
);

CREATE TABLE rating (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    poi_id BIGINT NOT NULL,
    grade INT CHECK (grade >= 0 AND grade < 6),
    txt VARCHAR(2000) NOT NULL,
    image_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (poi_id) REFERENCES poi(id),
    FOREIGN KEY (image_id) REFERENCES image(id)
);
