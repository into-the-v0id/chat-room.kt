<template>
    <NuxtLink :to="{ name: 'rooms' }">
        &lt;- Rooms
    </NuxtLink>

    <h1>Room <RoomHandle :handle="room.handle" /></h1>

    <h2>Users</h2>
    <UserList :roomIds="[room.id]" />

    <h2>Messages</h2>
    <MessageList :roomIds="[room.id]" />
</template>

<script lang="ts" setup>
    import rooms from '~~/repositories/room'

    const room = await rooms.getAll({ handles: [useRoute().params.handle as string] })
        .then(rooms => rooms.length ? rooms[0] : null)

    if (! room) {
        throw createError({
            statusCode: 404,
            statusMessage: 'Room Not Found',
            fatal: true,
        })
    }
</script>
