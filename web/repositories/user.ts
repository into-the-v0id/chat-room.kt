import User from '~~/models/user'

interface UserQuery {
    ids?: string[]
    handles?: string[]
    offset?: number
    limit?: number
    sortCriteria?: string[]
}

class UserRepository {
    private apiBaseUrl: string

    constructor(apiBaseUrl: string) {
        this.apiBaseUrl = apiBaseUrl
    }

    async getAll(query: UserQuery): Promise<User[]>
    {
        const fetchedRawUsers = await $fetch<any[]>('users', {
            baseURL: this.apiBaseUrl,
            query: {
                id: query.ids ?? undefined,
                handle: query.handles ?? undefined,
                offset: query.offset ?? undefined,
                limit: query.limit ?? undefined,
                sort_criteria: query.sortCriteria ?? undefined,
            },
        })

        const fetchedUsers = fetchedRawUsers.map(fetchedRawUser => new User(fetchedRawUser))

        return fetchedUsers
    }
}

export default new UserRepository(
    useRuntimeConfig().public.api.baseUrl,
)
