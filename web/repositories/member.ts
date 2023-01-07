import Member from '~~/models/member'

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
        const fetchedRawMembers = await $fetch<any[]>('members', {
            baseURL: this.apiBaseUrl,
            query: {
                id: query.ids ?? undefined,
                user_id: query.userIds ?? undefined,
                room_id: query.roomIds ?? undefined,
                offset: query.offset ?? undefined,
                limit: query.limit ?? undefined,
                sort_criteria: query.sortCriteria ?? undefined,
            },
        })

        const fetchedMembers = fetchedRawMembers.map(fetchedRawMember => new Member(fetchedRawMember))

        return fetchedMembers
    }
}

export default new MemberRepository(
    useRuntimeConfig().public.api.baseUrl,
)
