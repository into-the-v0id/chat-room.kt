<template>
    <h1>Login</h1>

    <form @submit.prevent="onSubmit">
        <div>
            <label for="handle-input" style="display: block;">Handle</label>
            <input type="text" id="handle-input" name="handle" autocomplete="username" required
                v-model.trim="handle" ref="handleInput" />

            <label for="password-input" style="display: block;">Password</label>
            <input type="password" id="password-input" name="password" autocomplete="password" required
                v-model.trim="password" />

            <div class="error" v-if="authError">
                {{ authError }}
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
    import auth from '~~/repositories/auth'
    import useSessionStore from '~~/stores/session'

    const handleInput = ref<HTMLInputElement|null>(null)
    const handle = ref('')
    const password = ref('')
    const authError = ref<string | null>(null)

    async function onSubmit() {
        const session = await auth.login({
            handle: handle.value,
            password: password.value,
        })
        if (! session) {
            showAuthError('Invalid credentials')
            return
        }

        useSessionStore().authenticate(session)

        showAuthError(null)
        navigateTo({ name: 'index' })
    }

    function showAuthError(message: string|null) {
        if (! message) {
            authError.value = null
            return
        }

        authError.value = message
        handleInput.value!.focus()
        password.value = ''
    }
</script>
