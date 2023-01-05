<template>
    <NuxtLayout name="default">
        <h1>{{ error.statusCode }} Error</h1>
    </NuxtLayout>
</template>

<script setup lang="ts">
    const props = defineProps({
        error: Object
    })

    useHead({
        meta: [
            { name: 'robots', content: 'noindex' },
        ],
    })
</script>
