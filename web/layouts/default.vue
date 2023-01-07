<template>
    <slot />
</template>

<script setup lang="ts">
    useHead({
        titleTemplate: (title) => title ? `${title} - Chat Room` : 'Chat Room',
        htmlAttrs: {
          lang: 'en',
        },
    })
</script>
