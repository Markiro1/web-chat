CREATE TABLE IF NOT EXISTS chat_types
(
    id   SERIAL PRIMARY KEY,
    type VARCHAR(255) NOT NULL
);

INSERT INTO chat_types (id, type)
VALUES (1, 'GLOBAL')
ON CONFLICT (id) DO NOTHING;
INSERT INTO chat_types (id, type)
VALUES (2, 'PRIVATE')
ON CONFLICT (id) DO NOTHING;


CREATE TABLE IF NOT EXISTS chats
(
    id           SERIAL PRIMARY KEY,
    chat_type_id BIGINT,
    FOREIGN KEY (chat_type_id) REFERENCES chat_types (id)
);

INSERT INTO chats (id, chat_type_id)
VALUES (nextval('chats_id_seq'), 1)
ON CONFLICT (id) DO NOTHING;

CREATE TABLE IF NOT EXISTS users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS messages
(
    id      SERIAL PRIMARY KEY,
    chat_id BIGINT,
    user_id BIGINT,
    text    TEXT,
    FOREIGN KEY (chat_id) REFERENCES chats (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS chat_users
(
    user_id BIGINT,
    chat_id BIGINT,
    PRIMARY KEY (user_id, chat_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (chat_id) REFERENCES chats (id)
);