import Message from '~~/models/message'

interface MessageQuery {
    ids?: string[]
    memberIds?: string[]
    roomIds?: string[]
    offset?: number
    limit?: number
    sortCriteria?: string[]
}

class MessageRepository {
    private apiBaseUrl: string

    constructor(apiBaseUrl: string) {
        this.apiBaseUrl = apiBaseUrl
    }

    async getAll(query: MessageQuery): Promise<Message[]>
    {
        const fetchedRawMessages = await $fetch<any[]>('messages', {
            baseURL: this.apiBaseUrl,
            query: {
                id: removeDuplicates(query.ids ?? []),
                member_id: removeDuplicates(query.memberIds ?? []),
                room_id: removeDuplicates(query.roomIds ?? []),
                offset: query.offset ?? undefined,
                limit: query.limit ?? undefined,
                sort_criteria: query.sortCriteria ?? undefined,
            },
        })

        const fetchedMessages = fetchedRawMessages.map(fetchedRawMessage => new Message(fetchedRawMessage))

        return fetchedMessages
    }
}

export default new MessageRepository(
    useRuntimeConfig().public.api.baseUrl,
)
