-- Inserting mock data into Player table
INSERT INTO Player (name, email, token)
VALUES ('John Doe', 'john.doe@example.com', '550e8400-e29b-41d4-a716-446655440000'),
       ('Jane Doe', 'jane.doe@example.com', '6ecd8c99-4036-403d-bf84-cf8400f67836');

-- Inserting mock data into Game table
INSERT INTO Game (name, developer, genres)
VALUES ('Game 1', 'Developer 1', ARRAY [1, 2]),
       ('Game 2', 'Developer 2', ARRAY [2, 3]);

-- Inserting mock data into Session table
-- Assuming that the games were inserted in the same order as above
INSERT INTO Session (date, game, capacity)
VALUES ('2022-01-01 10:00:00', 1, 5),
       ('2022-01-02 10:00:00', 2, 5);

-- Inserting mock data into Session_Player table
-- Assuming that the players and sessions were inserted in the same order as above
INSERT INTO Session_Player (session_id, player_id)
VALUES (1, 1),
       (1, 2),
       (2, 1);