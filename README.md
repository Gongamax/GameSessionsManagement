# Multiplayer Video Game Sessions Management System

This project is about the analysis, design, and implementation of an information system to manage multiplayer video game
sessions.
The system is developed in Kotlin using the HTTP4K library for handling HTTP requests and
kotlinx.serialization library for body serialization/deserialization.
The data is stored in a Postgres database.

For more information, please check the [Report](docs/Report.md).

## Domain

- **Player**: Characterized by a unique number, a name, and a unique email.
- **Game**: Characterized by a unique number, a unique name, the developer, and a set of genres (e.g., RPG, Adventure,
  Shooter, Turn-Based, Action).
- **Session**: Characterized by a unique number, the number of players, the session date, the game, and the associated
  players.

## Features

- **Player Management**: Create a new player and get the details of a player.
- **Game Management**: Create a game, get the details of a game, and get a list of games based on genres and developer.
- **Session Management**: Create a new session, add a player to the session, get the detailed information of a session,
  and get a list of sessions based on game identifier, session date, state, and player identifier.
- **Paging**: All GET operations that return a sequence support paging.

## Non-functional requirements

- The backend is developed with Kotlin technology.
- HTTP4K library is used to handle/receive HTTP requests.
- kotlinx.serialization library is used for body serialization/deserialization.
- The data is stored in a Postgres database.
- Tests run using data stored in memory, not the database.

## Note

- For all operations that require authentication, a user token must be sent in the Authorization header using a Bearer
  Token.

## Professors

````markdown
    - Paulo Pereira
    - Pedro Pereira
````

## Group

````markdown
    - Daniel Carvalho (49419)
    - Francisco Saraiva (49462)
    - Gonçalo Frutuoso (49495)
````