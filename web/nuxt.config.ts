// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    app: {
        head: {
            meta: [
                { name: 'robots', content: 'noindex' },
                { name: 'format-detection', content: 'telephone=no' },
                { name: 'msapplication-tap-highlight', content: 'no' },
            ],
            link: [
                { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' },
            ],
        },
    },
    runtimeConfig: {
        public: {
            api: {
                baseUrl: 'http://localhost:8080',
            }
        },
    },
    experimental: {
        inlineSSRStyles: false,
        payloadExtraction: false,
    }
})
