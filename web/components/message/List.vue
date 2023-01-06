<template>
    <ErrorIndicator v-if="messagesQuery.isFailiure || membersQuery.isFailiure || usersQuery.isFailiure"
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
            await messagesQuery.use($fetch('messages', {
                baseURL: useRuntimeConfig().public.api.baseUrl,
                query: {
                    id: ids,
                    member_id: memberIds,
                    room_id: roomIds,
                },
            }))
        }

        if (! messagesQuery.value.data.length) {
            membersQuery.resolve([])
        } else {
            await membersQuery.use($fetch('members', {
                baseURL: useRuntimeConfig().public.api.baseUrl,
                query: { id: messagesQuery.value.data.map(message => message.memberId) },
            }))
        }

        if (! membersQuery.value.data.length) {
            usersQuery.resolve([])
        } else {
            await usersQuery.use($fetch('users', {
                baseURL: useRuntimeConfig().public.api.baseUrl,
                query: { id: membersQuery.value.data.map(member => member.userId) },
            }))
        }
    })

    const getUserForMessage = message => {
        const member = membersQuery.value.data.find(member => member.id === message.memberId)
        if (! member) {
            return null
        }

        const user = usersQuery.value.data.find(user => user.id === member.userId)

        return user
    }
</script>
