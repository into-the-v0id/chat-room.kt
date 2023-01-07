<template>
    <span class="room-handle">
        {{ handle }}
    </span>
</template>

<script lang="ts" setup>
    const { handle } = defineProps<{
        handle: string
    }>()
</script>

<style lang="scss">
    .room-handle {
        &::before {
            content: '#';
        }
    }
</style>
