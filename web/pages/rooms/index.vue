<template>
    <h1>Rooms</h1>

    <ErrorIndicator v-if="roomsQuery.isFailiure" :error="roomsQuery.error" />
    <LoadingIndicator v-else-if="roomsQuery.isPending" />
    <div v-else>
        <ul>
            <li v-for="room in roomsQuery.data">
                <NuxtLink :to="{ name: 'rooms-handle', params: { handle: room.handle } }">
                    #{{ room.handle }}
                </NuxtLink>
            </li>
        </ul>
    </div>
</template>

<script setup>
    const roomsQuery = usePromise()

    onBeforeMount(async () => {
        await roomsQuery.use($fetch('rooms', {
            baseURL: useRuntimeConfig().public.api.baseUrl,
        }))
    })
</script>
