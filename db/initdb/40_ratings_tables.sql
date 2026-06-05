CREATE TABLE rating (
id int auto_increment primary key 
, user_id int NOT NULL
, poi_id bigint NOT null
, grade int CHECK (grade >= 0 and grade < 6)
, txt  varchar(2000) not null
, image_id int NULL
, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
,FOREIGN KEY (user_id) REFERENCES user(id)
,FOREIGN KEY (poi_id) REFERENCES poi(id)
,FOREIGN KEY (image_id) REFERENCES image(id)
) CHARACTER SET utf8mb4;
