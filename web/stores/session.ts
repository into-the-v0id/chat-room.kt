import { defineStore, StateTree } from 'pinia'
import Session from '~~/models/session'

export default defineStore('session', {
    state: () => ({
        session: null as Session|null,
    }),
    actions: {
        authenticate(session: Session) {
            this.session = session
        }
    },
    persist: {
        serializer: {
            serialize: (state: StateTree): string => JSON.stringify(state.session),
            deserialize: (value: string): StateTree => {
                const rawData = JSON.parse(value)

                return {
                    session: rawData ? new Session(rawData) : null,
                }
            },
        }
    },
})
