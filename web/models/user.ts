export default class User {
    id: string
    handle: string
    email: string
    dateCreated: Date
    dateUpdated: Date

    constructor(rawData: any) {
        this.id = rawData.id
        this.handle = rawData.handle
        this.email = rawData.email
        this.dateCreated = new Date(rawData.dateCreated)
        this.dateUpdated = new Date(rawData.dateUpdated)
    }
}
