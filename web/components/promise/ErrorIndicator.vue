<template>
    <div class="error-indicator">
        <a href="#" @click.prevent="showDetails">
            Error
        </a>
    </div>
</template>

<script lang="ts" setup>
    const { error } = defineProps<{
        error: any
    }>()

    function showDetails() {
        alert(convertToString(error))
    }
</script>

<style>
    .error-indicator {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
    }
</style>
