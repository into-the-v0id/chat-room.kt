<template>
    <NuxtLayout name="default">
        <h1>{{ error.statusCode }} Error</h1>
    </NuxtLayout>
</template>

<script lang="ts" setup>
    const { error } = defineProps<{
        error: any
    }>()

    useHead({
        meta: [
            { name: 'robots', content: 'noindex' },
        ],
    })
</script>
