<template>
    <NuxtLink :to="{ name: 'users' }">
        &lt;- Users
    </NuxtLink>

    <h1>User @{{ userQuery.data.handle }}</h1>

    <h2>Rooms</h2>
    <RoomList :userIds="[userQuery.data.id]" />
</template>

<script setup>
    const user = await $fetch('users', {
        baseURL: useRuntimeConfig().public.api.baseUrl,
        query: { handle: useRoute().params.handle },
    })
        .then(users => users.length ? users[0] : null)

    if (! user) {
        throw createError({
            statusCode: 404,
            statusMessage: 'User Not Found',
            fatal: true,
        })
    }
</script>
