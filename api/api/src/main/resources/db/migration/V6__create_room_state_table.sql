CREATE TABLE room_state (
    id uuid NOT NULL,
    handle varchar(255) NOT NULL,
    date_created timestamp NOT NULL,
    date_updated timestamp NOT NULL
);

CREATE INDEX idx__room_state__id
ON room_state (id);
