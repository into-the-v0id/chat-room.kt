<template>
    <h1>Users</h1>

    <ErrorIndicator v-if="usersQuery.isFailiure" :error="usersQuery.error" />
    <LoadingIndicator v-else-if="usersQuery.isPending" />
    <div v-else>
        <ul>
            <li v-for="user in usersQuery.data">
                <NuxtLink :to="{ name: 'users-handle', params: { handle: user.handle } }">
                    @{{ user.handle }}
                </NuxtLink>
            </li>
        </ul>
    </div>
</template>

<script setup>
    const usersQuery = usePromise()

    onBeforeMount(async () => {
        await usersQuery.use($fetch('users', {
            baseURL: useRuntimeConfig().public.api.baseUrl,
        }))
    })
</script>
