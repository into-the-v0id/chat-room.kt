<template>
    <span class="user-handle">
        {{ handle }}
    </span>
</template>

<script lang="ts" setup>
    const { handle } = defineProps<{
        handle: string
    }>()
</script>

<style lang="scss">
    .user-handle {
        &::before {
            content: '@';
        }
    }
</style>
