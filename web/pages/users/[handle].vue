<template>
    <ErrorIndicator v-if="userQuery.isFailiure" :error="userQuery.error" />
    <LoadingIndicator v-else-if="userQuery.isPending" />
    <div v-else>
        <NuxtLink :to="{ name: 'users' }">
            &lt;- Users
        </NuxtLink>

        <h1>User @{{ userQuery.data.handle }}</h1>

        <h2>Rooms</h2>
        <ErrorIndicator v-if="membersQuery.isFailiure || memberRoomsQuery.isFailiure" :error="membersQuery.error ?? memberRoomsQuery.error" />
        <LoadingIndicator v-else-if="membersQuery.isPending || memberRoomsQuery.isPending" />
        <div v-else>
            <ul>
                <li v-for="room in memberRoomsQuery.data">
                    <NuxtLink :to="{ name: 'rooms-handle', params: { handle: room.handle } }">
                        #{{ room.handle }}
                    </NuxtLink>
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup>
    const userQuery = usePromise()
    const membersQuery = usePromise()
    const memberRoomsQuery = usePromise()

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

        await membersQuery.use($fetch('members', {
            baseURL: useRuntimeConfig().public.api.baseUrl,
            query: { user_id: userQuery.value.data.id },
        }))

        if (membersQuery.value.data.length) {
            await memberRoomsQuery.use($fetch('rooms', {
                baseURL: useRuntimeConfig().public.api.baseUrl,
                query: { id: membersQuery.value.data.map(member => member.roomId) },
            }))
        } else {
            memberRoomsQuery.resolve([])
        }
    })
</script>
