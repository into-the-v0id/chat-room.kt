import Member from '~~/models/member'
import useSessionStore from '~~/stores/session'

interface MemberQuery {
    ids?: string[]
    userIds?: string[]
    roomIds?: string[]
    offset?: number
    limit?: number
    sortCriteria?: string[]
}

class MemberRepository {
    private apiBaseUrl: string

    constructor(apiBaseUrl: string) {
        this.apiBaseUrl = apiBaseUrl
    }

    async getAll(query: MemberQuery): Promise<Member[]>
    {
        if (
            (query.ids && ! query.ids.length)
            || (query.userIds && ! query.userIds.length)
            || (query.roomIds && ! query.roomIds.length)
        ) {
            return []
        }

        const fetchedRawMembers = await $fetch<any[]>('members', {
            baseURL: this.apiBaseUrl,
            query: {
                id: removeDuplicates(query.ids ?? []),
                user_id: removeDuplicates(query.userIds ?? []),
                room_id: removeDuplicates(query.roomIds ?? []),
                offset: query.offset ?? undefined,
                limit: query.limit ?? undefined,
                sort_criteria: query.sortCriteria ?? undefined,
            },
            headers: {
                Authorization: 'Bearer ' + useSessionStore().session!.token,
            },
        })

        const fetchedMembers = fetchedRawMembers.map(fetchedRawMember => new Member(fetchedRawMember))

        return fetchedMembers
    }
}

export default new MemberRepository(
    useRuntimeConfig().public.api.baseUrl,
)
