<template>
    <NuxtLink :to="{ name: 'users' }">
        &lt;- Users
    </NuxtLink>

    <h1>User <UserHandle :handle="user.handle" /></h1>

    <h2>Rooms</h2>
    <RoomList :userIds="[user.id]" />
</template>

<script lang="ts" setup>
    import users from '~~/repositories/user'

    const user = await users.getAll({ handles: [useRoute().params.handle as string] })
        .then(users => users.length ? users[0] : null)

    if (! user) {
        throw createError({
            statusCode: 404,
            statusMessage: 'User Not Found',
            fatal: true,
        })
    }
</script>
