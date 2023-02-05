import User from '~~/models/user'
import useSessionStore from '~~/stores/session'

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
        if ((query.ids && ! query.ids.length) || (query.handles && ! query.handles.length)) {
            return []
        }

        const fetchedRawUsers = await $fetch<any[]>('users', {
            baseURL: this.apiBaseUrl,
            query: {
                id: removeDuplicates(query.ids ?? []),
                handle: removeDuplicates(query.handles ?? []),
                offset: query.offset ?? undefined,
                limit: query.limit ?? undefined,
                sort_criteria: query.sortCriteria ?? undefined,
            },
            headers: {
                Authorization: 'Bearer ' + useSessionStore().session!.token,
            },
        })

        const fetchedUsers = fetchedRawUsers.map(fetchedRawUser => new User(fetchedRawUser))

        return fetchedUsers
    }
}

export default new UserRepository(
    useRuntimeConfig().public.api.baseUrl,
)
