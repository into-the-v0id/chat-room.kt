<template>
    <ErrorIndicator v-if="membersQuery.isFailure || roomsQuery.isFailure"
        :error="membersQuery.error ?? roomsQuery.error" />
    <LoadingIndicator v-else-if="membersQuery.isPending || roomsQuery.isPending" />
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

<script lang="ts" setup>
    import members from '~~/repositories/member'
    import rooms from '~~/repositories/room'
    import Member from '~~/models/member'
    import Room from '~~/models/room'

    const props = defineProps<{
        userIds?: string[]
        ids?: string[]
        handles?: string[]
    }>()
    let userIds = props.userIds
    let ids = props.ids
    const handles = props.handles

    const membersQuery = usePromise<Member[]>()
    const roomsQuery = usePromise<Room[]>()

    onMounted(async () => {
        if (userIds !== undefined) {
            await membersQuery.use(members.getAll({ userIds: userIds }))

            if ((ids && ids.length) || (handles && handles.length)) {
                throw new Error('"userIds" filter cannot be used in conjunction with other filters')
            }

            ids = membersQuery.data!.map(member => member.roomId)
            userIds = []
        } else {
            membersQuery.resolve([])
        }

        if ((ids && ! ids.length) || (handles && ! handles.length)) {
            roomsQuery.resolve([])
        } else {
            await roomsQuery.use(rooms.getAll({
                ids: ids,
                handles: handles,
                sortCriteria: ['dateCreatedAsc'],
            }))
        }
    })
</script>
