export default class Message {
    id: string
    memberId: string
    content: string
    dateCreated: Date
    dateUpdated: Date

    constructor(rawData: any) {
        this.id = rawData.id
        this.memberId = rawData.memberId
        this.content = rawData.content
        this.dateCreated = new Date(rawData.dateCreated)
        this.dateUpdated = new Date(rawData.dateUpdated)
    }
}
