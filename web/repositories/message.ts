import Message from '~~/models/message'
import useSessionStore from '~~/stores/session'

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
        if (
            (query.ids && ! query.ids.length)
            || (query.memberIds && ! query.memberIds.length)
            || (query.roomIds && ! query.roomIds.length)
        ) {
            return []
        }

        const result = await $fetch<any>('messages', {
            baseURL: this.apiBaseUrl,
            query: {
                id: removeDuplicates(query.ids ?? []),
                member_id: removeDuplicates(query.memberIds ?? []),
                room_id: removeDuplicates(query.roomIds ?? []),
                offset: query.offset ?? undefined,
                limit: query.limit ?? undefined,
                sort_criteria: query.sortCriteria ?? undefined,
            },
            headers: {
                Accept: 'application/json',
                Authorization: 'Bearer ' + useSessionStore().session!.token,
            },
        })

        const fetchedMessages = result.data.map((rawMessage: any) => new Message(rawMessage))

        return fetchedMessages
    }
}

export default new MessageRepository(
    useRuntimeConfig().public.api.baseUrl,
)
