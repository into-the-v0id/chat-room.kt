import Session from '~~/models/session'

interface LoginPayload {
    userId?: string
    handle?: string
    password: string
}

class AuthRepository {
    private apiBaseUrl: string

    constructor(apiBaseUrl: string) {
        this.apiBaseUrl = apiBaseUrl
    }

    async login(data: LoginPayload): Promise<Session|null>
    {
        if (data.userId === null && data.handle === null) {
            return null
        }

        const rawSession = await $fetch<any[]>('auth/login', {
            method: 'POST',
            baseURL: this.apiBaseUrl,
            body: data,
            headers: {
                Accept: 'application/json',
            },
        }).catch(e => {
            if (e.response.status === 401) {
                return null
            }

            throw e
        })
        if (! rawSession) {
            return null
        }

        return new Session(rawSession)
    }
}

export default new AuthRepository(
    useRuntimeConfig().public.api.baseUrl,
)
