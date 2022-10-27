CREATE TABLE member_state (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    room_id uuid NOT NULL,
    date_created timestamp NOT NULL,
    date_updated timestamp NOT NULL
);

CREATE INDEX idx__member_state__id
ON member_state (id);

CREATE INDEX idx__member_state__user_id
ON member_state (user_id);

CREATE INDEX idx__member_state__room_id
ON member_state (room_id);
