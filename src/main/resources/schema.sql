drop table if exists genre cascade;
drop table if exists rating cascade;
drop table if exists film cascade;
drop table if exists film_genre cascade;
drop table if exists users cascade;
drop table if exists friend_request cascade;
drop table if exists likes cascade;

create table if not exists genre
(
    genre_id
    int
    generated
    by
    default as
    identity
    primary
    key,
    name
    varchar
);

create table if not exists rating
(
    rate_id
    int
    generated
    by
    default as
    identity
    primary
    key,
    name
    varchar,
    description
    varchar
);

create table if not exists film
(
    film_id
    int
    generated
    by
    default as
    identity
    primary
    key,
    title
    varchar,
    description
    varchar,
    release_date
    date,
    duration_minutes
    int,
    rate
    int,
    mpa
    int
    references
    rating
(
    rate_id
)
    );

create table if not exists film_genre
(
    film_id int references film
(
    film_id
),
    genre int references genre
(
    genre_id
)
    );

create table if not exists users
(
    user_id
    int
    generated
    by
    default as
    identity
    primary
    key,
    name
    varchar,
    email
    varchar,
    login
    varchar,
    birthday
    date
);

create table if not exists friend_request
(
    friend_request_id
    int
    generated
    by
    default as
    identity
    primary
    key,
    from_user
    int
    references
    users
(
    user_id
),
    to_user int references users
(
    user_id
),
    is_allowed boolean
    );

create table if not exists likes
(
    film_id int references film
(
    film_id
),
    user_id int references users
(
    user_id
)
    );