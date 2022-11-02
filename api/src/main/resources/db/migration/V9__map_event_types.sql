UPDATE user_events
SET event_type = 'user:change-email'
WHERE event_type = 'org.chatRoom.core.event.user.ChangeEmail';

UPDATE user_events
SET event_type = 'user:change-handle'
WHERE event_type = 'org.chatRoom.core.event.user.ChangeHandle';

UPDATE user_events
SET event_type = 'user:create'
WHERE event_type = 'org.chatRoom.core.event.user.CreateUser';

UPDATE user_events
SET event_type = 'user:delete'
WHERE event_type = 'org.chatRoom.core.event.user.DeleteUser';

UPDATE room_events
SET event_type = 'room:change-handle'
WHERE event_type = 'org.chatRoom.core.event.room.ChangeHandle';

UPDATE room_events
SET event_type = 'room:create'
WHERE event_type = 'org.chatRoom.core.event.room.CreateRoom';

UPDATE room_events
SET event_type = 'room:delete'
WHERE event_type = 'org.chatRoom.core.event.room.DeleteRoom';

UPDATE message_events
SET event_type = 'message:change-content'
WHERE event_type = 'org.chatRoom.core.event.message.ChangeContent';

UPDATE message_events
SET event_type = 'message:create'
WHERE event_type = 'org.chatRoom.core.event.message.CreateMessage';

UPDATE message_events
SET event_type = 'message:delete'
WHERE event_type = 'org.chatRoom.core.event.message.DeleteMessage';

UPDATE member_events
SET event_type = 'member:create'
WHERE event_type = 'org.chatRoom.core.event.member.CreateMember';

UPDATE member_events
SET event_type = 'member:delete'
WHERE event_type = 'org.chatRoom.core.event.member.DeleteMember';
