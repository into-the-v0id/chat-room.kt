<template>
    <ErrorIndicator v-if="roomQuery.isFailiure" :error="roomQuery.error" />
    <LoadingIndicator v-else-if="roomQuery.isPending" />
    <div v-else>
        <NuxtLink :to="{ name: 'rooms' }">
            &lt;- Rooms
        </NuxtLink>

        <h1>Room #{{ roomQuery.data.handle }}</h1>

        <h2>Users</h2>
        <UserList :roomIds="[roomQuery.data.id]" />

        <h2>Messages</h2>
        <MessageList :roomIds="[roomQuery.data.id]" />
    </div>
</template>

<script setup>
    const roomQuery = usePromise()

    onBeforeMount(async () => {
        const room = await $fetch('rooms', {
            baseURL: useRuntimeConfig().public.api.baseUrl,
            query: { handle: useRoute().params.handle },
        })
            .then(rooms => rooms ? rooms[0] : null)
            .catch(error => {
                roomQuery.reject(error)

                throw error
            })
        if (! room) {
            throw createError({
                statusCode: 404,
                statusMessage: 'Room Not Found',
                fatal: true,
            })
        }
        roomQuery.resolve(room)
    })
</script>
