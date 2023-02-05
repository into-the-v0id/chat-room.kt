import Room from '~~/models/room'
import useSessionStore from '~~/stores/session'

interface RoomQuery {
    ids?: string[]
    handles?: string[]
    offset?: number
    limit?: number
    sortCriteria?: string[]
}

class RoomRepository {
    private apiBaseUrl: string

    constructor(apiBaseUrl: string) {
        this.apiBaseUrl = apiBaseUrl
    }

    async getAll(query: RoomQuery): Promise<Room[]>
    {
        if ((query.ids && ! query.ids.length) || (query.handles && ! query.handles.length)) {
            return []
        }

        const result = await $fetch<any>('rooms', {
            baseURL: this.apiBaseUrl,
            query: {
                id: removeDuplicates(query.ids ?? []),
                handle: removeDuplicates(query.handles ?? []),
                offset: query.offset ?? undefined,
                limit: query.limit ?? undefined,
                sort_criteria: query.sortCriteria ?? undefined,
            },
            headers: {
                Accept: 'application/json',
                Authorization: 'Bearer ' + useSessionStore().session!.token,
            },
        })

        const fetchedRooms = result.data.map((rawRoom: any) => new Room(rawRoom))

        return fetchedRooms
    }
}

export default new RoomRepository(
    useRuntimeConfig().public.api.baseUrl,
)
