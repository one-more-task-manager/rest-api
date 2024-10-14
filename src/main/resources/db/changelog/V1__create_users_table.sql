CREATE TABLE users(
    id BIGSERIAL PRIMARY KEY,
    email TEXT UNIQUE NOT NULL CHECK ( LENGTH(email) >= 6 AND LENGTH(email) <= 55 ),
    password TEXT NOT NULL CHECK ( LENGTH(password) >= 6 AND LENGTH(password) <= 255 )
);

CREATE INDEX idx_username ON Users (email);