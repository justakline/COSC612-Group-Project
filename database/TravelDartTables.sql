CREATE TABLE USERS (
	user_id int NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	username varchar(25) NOT NULL UNIQUE,
	date_of_birth date NOT NULL,
	password varchar(50) NOT NULL
);
CREATE TABLE USER_LOCATIONS (
	location_id int NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	userId int NOT NULL,
	longitude decimal(9,  6),
	latitude decimal(9, 6),
	FOREIGN KEY (userId) REFERENCES USERS(user_id)
);
CREATE TABLE MUSIC_GENRES (
	genre_id int NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	genre varchar(20) NOT NULL UNIQUE
);
CREATE TABLE USER_MUSIC_PREFERENCES (
	music_preference_id int NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	userId int NOT NULL,
	genreId int NOT NULL,
	FOREIGN KEY (userId) REFERENCES USERS(user_id),
	FOREIGN KEY (genreId) REFERENCES MUSIC_GENRES(genre_id)
);
CREATE TABLE FOOD_TYPES (
	food_type_id int NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	f_type varchar(20) NOT NULL UNIQUE
);
CREATE TABLE USER_FOOD_PREFERENCES (
	food_preference_id int NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	userId int NOT NULL,
	f_typeId int NOT NULL,
	FOREIGN KEY (userId) REFERENCES USERS(user_id),
	FOREIGN KEY (f_typeId) REFERENCES FOOD_TYPES(food_type_id)
);
CREATE TABLE USER_REC_SET (
	rec_set_id int NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	userId int NOT NULL,
	date_created date NOT NULL,
	FOREIGN KEY (userId) REFERENCES USERS(user_id)
);
CREATE TABLE FOOD_PLACES(
	place_id int NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	p_name varchar(30) NOT NULL,
	p_address varchar(50),
	p_rating float,
	p_hours varchar(15),
	yelp_id varchar(25) NOT NULL UNIQUE
);
CREATE TABLE MUSIC_EVENTS(
	event_id int NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	e_name varchar(30) NOT NULL,
	e_venue varchar(30),
	e_rating float,
	e_date_time timestamp,
	ebrite_id varchar(25) NOT NULL UNIQUE
);
CREATE TABLE RECOMMENDATIONS (
	recommendation_id int NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	userId int NOT NULL,
	r_name varchar(20),
	r_description varchar(200),
	r_type varchar(20),
	r_location_city varchar(30),
	setId int NOT NULL,
	placeId int,
	eventId int,
	FOREIGN KEY (userId) REFERENCES USERS(user_id),
	FOREIGN KEY (setId) REFERENCES USER_REC_SET(rec_set_id),
	FOREIGN KEY (placeId) REFERENCES FOOD_PLACES(place_id),
	FOREIGN KEY (eventId) REFERENCES MUSIC_EVENTS(event_id)
);