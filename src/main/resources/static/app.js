const { createApp, ref } = Vue;

createApp({
    setup() {
        const count = ref(0);
        return { count };
    }
}).mount('#app');
