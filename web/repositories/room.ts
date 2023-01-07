import Room from '~~/models/room'

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
        const fetchedRawRooms = await $fetch<any[]>('rooms', {
            baseURL: this.apiBaseUrl,
            query: {
                id: query.ids ?? undefined,
                handle: query.handles ?? undefined,
                offset: query.offset ?? undefined,
                limit: query.limit ?? undefined,
                sort_criteria: query.sortCriteria ?? undefined,
            },
        })

        const fetchedRooms = fetchedRawRooms.map(fetchedRawRoom => new Room(fetchedRawRoom))

        return fetchedRooms
    }
}

export default new RoomRepository(
    useRuntimeConfig().public.api.baseUrl,
)
