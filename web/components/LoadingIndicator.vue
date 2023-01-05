<template>
    <div :class="{ 'loading-indicator': true, 'loading-indicator--hidden': isHidden }">
        <slot>
            Loading ...
        </slot>
    </div>
</template>

<script setup>
    const { delay } = defineProps({
        delay: {
            type: Number,
            default: 500,
        },
    })

    const isHidden = ref(delay !== 0)

    onMounted(() => {
        if (isHidden.value) {
            setTimeout(() => {
                isHidden.value = false
            }, delay)
        }
    })
</script>

<style>
    .loading-indicator {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
    }

    .loading-indicator--hidden {
        visibility: hidden;
    }
</style>
