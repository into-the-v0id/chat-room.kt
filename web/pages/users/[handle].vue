<template>
    <NuxtLink :to="{ name: 'users' }">
        &lt;- Users
    </NuxtLink>

    <h1>User @{{ user.handle }}</h1>

    <h2>Rooms</h2>
    <RoomList :userIds="[user.id]" />
</template>

<script setup>
    import users from '~~/repositories/user'

    const user = await users.getAll({ handle: useRoute().params.handle })
        .then(users => users.length ? users[0] : null)

    if (! user) {
        throw createError({
            statusCode: 404,
            statusMessage: 'User Not Found',
            fatal: true,
        })
    }
</script>
