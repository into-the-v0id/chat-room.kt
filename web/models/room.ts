export default class Room {
    id: string
    handle: string
    dateCreated: Date
    dateUpdated: Date

    constructor(rawData: any) {
        this.id = rawData.id
        this.handle = rawData.handle
        this.dateCreated = new Date(rawData.dateCreated)
        this.dateUpdated = new Date(rawData.dateUpdated)
    }
}
