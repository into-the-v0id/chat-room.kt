<template>
    <NuxtLink :to="{ name: 'rooms' }">
        &lt;- Rooms
    </NuxtLink>

    <h1>Room #{{ room.handle }}</h1>

    <h2>Users</h2>
    <UserList :roomIds="[room.id]" />

    <h2>Messages</h2>
    <MessageList :roomIds="[room.id]" />
</template>

<script setup>
    const room = await $fetch('rooms', {
        baseURL: useRuntimeConfig().public.api.baseUrl,
        query: { handle: useRoute().params.handle },
    })
        .then(rooms => rooms ? rooms[0] : null)

    if (! room) {
        throw createError({
            statusCode: 404,
            statusMessage: 'Room Not Found',
            fatal: true,
        })
    }
</script>
