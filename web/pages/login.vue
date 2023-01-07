<template>
    <h1>Login</h1>

    <form @submit.prevent="onSubmit">
        <div>
            <label for="handle-input" style="display: block;">Handle</label>
            <input type="text" id="handle-input" name="handle" autocomplete="username" required
                v-model.trim="handle" ref="handleInput" />
            <div class="error" v-if="handleError">
                {{ handleError }}
            </div>
        </div>

        <div>
            <button type="submit">
                Login
            </button>
        </div>
    </form>
</template>

<script lang="ts" setup>
    import users from '~~/repositories/user'

    const handleInput = ref<HTMLInputElement|null>(null)
    const handle = ref('')
    const handleError = ref<string|null>(null)

    async function onSubmit() {
        const user = await users.getAll({ handles: [handle.value] })
            .then(users => users.length ? users[0] : null)

        if (! user) {
            showHandleError('User not found')
            return
        }

        // TODO: store user

        showHandleError(null)
        navigateTo({ name: 'index' })
    }

    function showHandleError(message: string|null) {
        if (! message) {
            handleError.value = null
            return
        }

        handleError.value = 'User not found'
        handleInput.value!.focus()
    }
</script>
