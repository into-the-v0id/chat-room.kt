<template>
    <slot />
</template>

<script setup lang="ts">
    const route = useRoute()
    if (route && route.name) {
        // normalize path (remove duplicate/leading/tailing "/")
        let routePath = '/' + route.path
            .split('/')
            .filter(segment => segment !== '')
            .join('/')

        useHead({
            link: [
                { rel: 'canonical', href: routePath },
            ],
        })
    }

    useHead({
        titleTemplate: (title) => title ? `${title} - Chat Room` : 'Chat Room',
    })
</script>
