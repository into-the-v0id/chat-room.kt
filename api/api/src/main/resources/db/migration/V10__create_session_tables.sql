CREATE TABLE session_events (
    event_id uuid NOT NULL PRIMARY KEY,
    model_id uuid NOT NULL,
    event_type varchar(255) NOT NULL,
    event_data json NOT NULL,
    date_issued timestamp NOT NULL
);

CREATE TABLE session_state (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    token varchar(128) NOT NULL,
    date_valid_until timestamp NOT NULL,
    date_created timestamp NOT NULL
);

CREATE INDEX idx__session_state__id
ON session_state (id);
