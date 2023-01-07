<template>
    <ErrorIndicator v-if="messagesQuery.isFailure || membersQuery.isFailure || usersQuery.isFailure"
        :error="messagesQuery.error ?? membersQuery.error ?? usersQuery.error" />
    <LoadingIndicator v-else-if="messagesQuery.isPending || membersQuery.isPending || usersQuery.isPending" />
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
</template>

<script setup>
    import messages from '~~/repositories/message'
    import members from '~~/repositories/member'
    import users from '~~/repositories/user'

    const { ids, memberIds, roomIds } = defineProps({
        ids: Array,
        memberIds: Array,
        roomIds: Array,
    })

    const messagesQuery = usePromise()
    const membersQuery = usePromise()
    const usersQuery = usePromise()

    onMounted(async () => {
        if ((ids && ! ids.length) || (memberIds && ! memberIds.length) || (roomIds && ! roomIds.length)) {
            messagesQuery.resolve([])
        } else {
            await messagesQuery.use(messages.getAll({
                ids: ids,
                memberIds: memberIds,
                roomIds: roomIds,
            }))
        }

        if (!messagesQuery.data.length) {
            membersQuery.resolve([])
        } else {
            await membersQuery.use(members.getAll({
                ids: messagesQuery.data.map(message => message.memberId)
            }))
        }

        if (!membersQuery.data.length) {
            usersQuery.resolve([])
        } else {
            await usersQuery.use(users.getAll({
                ids: membersQuery.data.map(member => member.userId)
            }))
        }
    })

    const getUserForMessage = message => {
        const member = membersQuery.data.find(member => member.id === message.memberId)
        if (! member) {
            return null
        }

        const user = usersQuery.data.find(user => user.id === member.userId)

        return user
    }
</script>
