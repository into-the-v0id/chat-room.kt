import { Ref } from "nuxt/dist/app/compat/capi"

class PromiseQueryState<T> {
    data: T|null = null
    error: any = null
    isPending = true
    isFailiure = false
}

class PromiseQuery<T> {
    private state: Ref<PromiseQueryState<T>> = ref(new PromiseQueryState<T>()) as Ref<PromiseQueryState<T>>

    get data() {
        return this.state.value.data
    }

    get error() {
        return this.state.value.error
    }

    get isPending() {
        return this.state.value.isPending
    }

    get isFailiure() {
        return this.state.value.isFailiure
    }

    resolve(data: T): void {
        this.state.value.data = data
        this.state.value.error = null
        this.state.value.isPending = false
        this.state.value.isFailiure = false
    }

    reject(error: any): void {
        this.state.value.data = null
        this.state.value.error = error
        this.state.value.isFailiure = true
        this.state.value.isPending = false
    }

    reset(): void {
        this.state.value.data = null
        this.state.value.error = null
        this.state.value.isPending = true
        this.state.value.isFailiure = false
    }

    use(promise: Promise<T>): Promise<T> {
        this.reset()

        return promise
            .then(data => {
                this.resolve(data)

                return data
            })
            .catch(error => {
                this.reject(error)

                throw error
            })
    }
}

export const usePromise = <T> (promise?: Promise<T>, query?: PromiseQuery<T>) => {
    if (! query) {
        query = new PromiseQuery()
    }

    if (promise) {
        query.use(promise)
    }

    return query
}
