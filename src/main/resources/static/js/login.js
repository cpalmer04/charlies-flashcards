const { createApp } = Vue;

createApp({
    data() {
        return {
            username: '',
            password: ''
        };
    },
    methods: {
        async loginUser() {
            try {
                const response = await axios.post('http://localhost:29001/api/login', {
                    username: this.username,
                    password: this.password
                });

                console.log('Login successful:', response.data);
                alert('Login successful!');

                sessionStorage.setItem('user', JSON.stringify(response.data));
                window.location.href="decks.html";
            } catch (error) {
                console.error('Login failed:', error);
                alert('Invalid username or password.');
            }
        }
    }
}).mount('#login');
