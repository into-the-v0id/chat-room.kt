export const usePromise = (promise?: Promise, query?: object) => {
    if (! query) {
        query = ref({
            data: null,
            error: null,
            isPending: true,
            isFailiure: false,
        })

        query.resolve = data => {
            query.value.data = data
            query.value.isPending = false
        }
        query.reject = error => {
            query.value.error = error
            query.value.isFailiure = true
            query.value.isPending = false
        }
        query.use = promise => promise
            .then(data => {
                query.resolve(data)

                return data
            })
            .catch(error => {
                query.reject(error)

                throw error
            })
    }

    if (promise) {
        query.use(promise)
    }

    return query
}
