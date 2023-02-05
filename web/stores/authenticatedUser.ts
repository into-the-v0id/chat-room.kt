import { defineStore, StateTree } from 'pinia'
import User from '~~/models/user'

export default defineStore('authenticatedUser', {
    state: () => ({
        user: null as User|null,
        dateAuthenticated: null as Date|null,
    }),

    actions: {
        authenticate(user: User) {
            this.user = user
            this.dateAuthenticated = new Date()
        }
    },
    persist: {
        serializer: {
            serialize: (state: StateTree): string => JSON.stringify(state),
            deserialize: (value: string): StateTree => {
                const rawData = JSON.parse(value)

                return {
                    user: rawData.user ? new User(rawData.user) : null,
                    dateAuthenticated: rawData.dateAuthenticated ? new Date(rawData.dateAuthenticated) : null,
                }
            },
        }
    },
})
