export default class Session {
    id: string
    userId: string
    secret: string
    token: string
    dateValidUntil: Date
    dateCreated: Date

    constructor(rawData: any) {
        this.id = rawData.id
        this.userId = rawData.userId
        this.secret = rawData.secret
        this.token = rawData.token
        this.dateValidUntil = new Date(rawData.dateValidUntil)
        this.dateCreated = new Date(rawData.dateCreated)
    }
}
