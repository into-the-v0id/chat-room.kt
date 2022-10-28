CREATE TABLE message_state (
    id uuid NOT NULL,
    member_id uuid NOT NULL,
    content text NOT NULL,
    date_created timestamp NOT NULL,
    date_updated timestamp NOT NULL
);

CREATE INDEX idx__message_state__id
ON message_state (id);

CREATE INDEX idx__message_state__member_id
ON message_state (member_id);
