<template>
    <ErrorIndicator v-if="roomQuery.isFailiure" :error="roomQuery.error" />
    <LoadingIndicator v-else-if="roomQuery.isPending" />
    <div v-else>
        <NuxtLink :to="{ name: 'rooms' }">
            &lt;- Rooms
        </NuxtLink>

        <h1>Room #{{ roomQuery.data.handle }}</h1>

        <h2>Users</h2>
        <ErrorIndicator v-if="membersQuery.isFailiure || memberUsersQuery.isFailiure" :error="membersQuery.error ?? memberUsersQuery.error" />
        <LoadingIndicator v-else-if="membersQuery.isPending || memberUsersQuery.isPending" />
        <div v-else>
            <ul>
                <li v-for="user in memberUsersQuery.data">
                    <NuxtLink :to="{ name: 'users-handle', params: { handle: user.handle } }">
                        @{{ user.handle }}
                    </NuxtLink>
                </li>
            </ul>
        </div>

        <h2>Messages</h2>
        <ErrorIndicator v-if="messagesQuery.isFailiure" :error="messagesQuery.error" />
        <LoadingIndicator v-else-if="messagesQuery.isPending" />
        <div v-else>
            <ul>
                <li v-for="message in messagesQuery.data">
                    <div style="font-size: 0.75em;">
                        @{{ getUserForMessage(message).handle }}
                    </div>
                    {{ message.content }}
                    <div style="font-size: 0.75em;">
                        {{ message.dateCreated }}
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup>
    const roomQuery = usePromise()
    const membersQuery = usePromise()
    const memberUsersQuery = usePromise()
    const messagesQuery = usePromise()

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

        await membersQuery.use($fetch('members', {
            baseURL: useRuntimeConfig().public.api.baseUrl,
            query: { room_id: roomQuery.value.data.id },
        }))

        if (membersQuery.value.data.length) {
            await memberUsersQuery.use($fetch('users', {
                baseURL: useRuntimeConfig().public.api.baseUrl,
                query: { id: membersQuery.value.data.map(member => member.userId) },
            }))
        } else {
            memberUsersQuery.resolve([])
        }

        await messagesQuery.use($fetch('messages', {
            baseURL: useRuntimeConfig().public.api.baseUrl,
            query: { room_id: roomQuery.value.data.id },
        }))
    })

    const getUserForMessage = message => {
        const member = membersQuery.value.data.find(member => member.id === message.memberId)
        if (! member) {
            return null
        }

        const user = memberUsersQuery.value.data.find(user => user.id === member.userId)

        return user
    }
</script>
