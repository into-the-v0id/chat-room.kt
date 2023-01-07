<template>
    <ErrorIndicator v-if="membersQuery.isFailure || usersQuery.isFailure"
        :error="membersQuery.error ?? usersQuery.error" />
    <LoadingIndicator v-else-if="membersQuery.isPending || usersQuery.isPending" />
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
    const props = defineProps({
        roomIds: Array,
        ids: Array,
        handles: Array,
    })
    let roomIds = props.roomIds
    let ids = props.ids
    const handles = props.handles

    const membersQuery = usePromise()
    const usersQuery = usePromise()

    onMounted(async () => {
        if (roomIds !== undefined) {
            await membersQuery.use($fetch('members', {
                baseURL: useRuntimeConfig().public.api.baseUrl,
                query: { room_id: roomIds },
            }))

            if ((ids && ids.length) || (handles && handles.length)) {
                throw new Error('"roomIds" filter cannot be used in conjunction with other filters')
            }

            ids = membersQuery.data.map(member => member.userId)
            roomIds = []
        } else {
            membersQuery.resolve(null)
        }

        if ((ids && ! ids.length) || (handles && ! handles.length)) {
            usersQuery.resolve([])
        } else {
            await usersQuery.use($fetch('users', {
                baseURL: useRuntimeConfig().public.api.baseUrl,
                query: {
                    id: ids,
                    handle: handles,
                },
            }))
        }
    })
</script>
