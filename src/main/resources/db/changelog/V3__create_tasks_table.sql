CREATE TABLE tasks
(
    id          BIGSERIAL PRIMARY KEY,
    title       TEXT    NOT NULL CHECK ( LENGTH(title) > 0 and LENGTH(title) <= 50 ),
    is_done     boolean NOT NULL,
    todolist_id BIGINT  NOT NULL REFERENCES todolists (id)
)