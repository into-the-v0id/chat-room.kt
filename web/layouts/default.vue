<template>
    <div v-if="status.isFailure">
        <h1>Error</h1>
        <pre style="color: grey;">{{ convertToString(status.error) }}</pre>
    </div>
    <slot v-else />
</template>

<script setup lang="ts">
    const status = usePromise<null>()

    onErrorCaptured(error => {
        console.error(error)

        if (status.isFailure) {
            return false
        }

        status.reject(error)

        useHead({
            title: 'Error',
            meta: [
                { name: 'robots', content: 'noindex' },
            ],
        })

        return false
    })

    useHead({
        titleTemplate: (title) => title ? `${title} - Chat Room` : 'Chat Room',
        htmlAttrs: {
            lang: 'en',
        },
    })
</script>
