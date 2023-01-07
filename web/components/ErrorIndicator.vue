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

    const showDetails = () => {
        alert(errorToString(error))
    }

    const errorToString = (error: any): string => {
        if (typeof(error) === 'object') {
            if ('toString' in error) {
                return error.toString()
            }

            return JSON.stringify(error)
        }

        return String(error)
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
