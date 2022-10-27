CREATE TABLE user_state (
    id uuid NOT NULL,
    handle varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    date_created timestamp NOT NULL,
    date_updated timestamp NOT NULL
);

CREATE INDEX idx__user_state__id
ON user_state (id);
