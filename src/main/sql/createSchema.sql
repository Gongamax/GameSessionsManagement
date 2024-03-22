drop table if exists Session_Player;
drop table if exists Session;
drop table if exists Game;
drop table if exists Player;

create table Player
(
    id    serial unique primary key,
    name  varchar(255) unique not null,
    email varchar(255) not null,
    token uuid         not null
);

create table Game
(
    id        serial unique primary key,
    name      varchar(255) unique not null,
    developer varchar(255) not null,
    genres    integer[]    not null -- guarda o número de ordem dos géneros
);

create table Session
(
    id                serial unique primary key,
    date              timestamp                    not null,
    game              integer references Game (id) not null,
    capacity          integer                      not null
);

-- Many to many relationship between Session and Player
-- Represents the players that are associated to a session
create table Session_Player
(
    session_id integer references Session (id) not null,
    player_id  integer references Player (id)  not null,
    primary key (session_id, player_id)
);