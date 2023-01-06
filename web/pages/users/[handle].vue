<template>
    <ErrorIndicator v-if="userQuery.isFailiure" :error="userQuery.error" />
    <LoadingIndicator v-else-if="userQuery.isPending" />
    <div v-else>
        <NuxtLink :to="{ name: 'users' }">
            &lt;- Users
        </NuxtLink>

        <h1>User @{{ userQuery.data.handle }}</h1>

        <h2>Rooms</h2>
        <RoomList :userIds="[userQuery.data.id]" />
    </div>
</template>

<script setup>
    const userQuery = usePromise()

    onBeforeMount(async () => {
        const user = await $fetch('users', {
            baseURL: useRuntimeConfig().public.api.baseUrl,
            query: { handle: useRoute().params.handle },
        })
            .then(users => users ? users[0] : null)
            .catch(error => {
                userQuery.reject(error)

                throw error
            })
        if (! user) {
            throw createError({
                statusCode: 404,
                statusMessage: 'User Not Found',
                fatal: true,
            })
        }
        userQuery.resolve(user)
    })
</script>
