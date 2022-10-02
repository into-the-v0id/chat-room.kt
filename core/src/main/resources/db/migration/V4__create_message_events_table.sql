CREATE TABLE message_events (
    event_id uuid NOT NULL PRIMARY KEY,
    model_id uuid NOT NULL,
    event_type varchar(255) NOT NULL,
    event_data json NOT NULL,
    date_issued timestamp NOT NULL
);
