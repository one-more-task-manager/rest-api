CREATE TABLE users(
    id BIGSERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL CHECK ( LENGTH(username) >= 2 AND LENGTH(username) <= 128 ),
    password TEXT NOT NULL CHECK ( LENGTH(password) >= 6 AND LENGTH(password) <= 255 )
);

CREATE INDEX idx_username ON Users (username);