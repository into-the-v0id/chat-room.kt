<template>
    <ErrorIndicator v-if="roomsQuery.isFailure" :error="roomsQuery.error" />
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
    const props = defineProps({
        userIds: Array,
        ids: Array,
        handles: Array,
    })
    let userIds = props.userIds
    let ids = props.ids
    const handles = props.handles

    const membersQuery = usePromise()
    const roomsQuery = usePromise()

    onMounted(async () => {
        if (userIds !== undefined) {
            await membersQuery.use($fetch('members', {
                baseURL: useRuntimeConfig().public.api.baseUrl,
                query: { user_id: userIds },
            }))

            if ((ids && ids.length) || (handles && handles.length)) {
                throw new Error('"userIds" filter cannot be used in conjunction with other filters')
            }

            ids = membersQuery.data.map(member => member.roomId)
            userIds = []
        } else {
            membersQuery.resolve(null)
        }

        if ((ids && ! ids.length) || (handles && ! handles.length)) {
            roomsQuery.resolve([])
        } else {
            await roomsQuery.use($fetch('rooms', {
                baseURL: useRuntimeConfig().public.api.baseUrl,
                query: {
                    id: ids,
                    handle: handles,
                },
            }))
        }
    })
</script>
