<template>
    <PromiseErrorIndicator v-if="membersQuery.isFailure || usersQuery.isFailure"
        :error="membersQuery.error ?? usersQuery.error" />
    <PromiseLoadingIndicator v-else-if="membersQuery.isPending || usersQuery.isPending" />
    <div v-else>
        <ul>
            <li v-for="user in usersQuery.data" :key="user.id">
                <NuxtLink :to="{ name: 'users-handle', params: { handle: user.handle } }">
                    <UserHandle :handle="user.handle" />
                </NuxtLink>
            </li>
        </ul>
    </div>
</template>

<script lang="ts" setup>
    import members from '~~/repositories/member'
    import users from '~~/repositories/user'
    import Member from '~~/models/member'
    import User from '~~/models/user'

    const props = defineProps<{
        roomIds?: string[]
        ids?: string[]
        handles?: string[]
    }>()
    let roomIds = props.roomIds
    let ids = props.ids
    const handles = props.handles

    const membersQuery = usePromise<Member[]>()
    const usersQuery = usePromise<User[]>()

    onMounted(async () => {
        if (roomIds !== undefined) {
            await membersQuery.use(members.getAll({ roomIds: roomIds }))

            if ((ids && ids.length) || (handles && handles.length)) {
                throw new Error('"roomIds" filter cannot be used in conjunction with other filters')
            }

            ids = membersQuery.data!.map(member => member.userId)
            roomIds = []
        } else {
            membersQuery.resolve([])
        }

        await usersQuery.use(users.getAll({
            ids: ids,
            handles: handles,
            sortCriteria: ['dateCreatedAsc'],
        }))
    })
</script>
