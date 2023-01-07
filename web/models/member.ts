export default class Member {
    id: string
    userId: string
    roomId: string
    dateCreated: Date
    dateUpdated: Date

    constructor(rawData: any) {
        this.id = rawData.id
        this.userId = rawData.userId
        this.roomId = rawData.roomId
        this.dateCreated = new Date(rawData.dateCreated)
        this.dateUpdated = new Date(rawData.dateUpdated)
    }
}
