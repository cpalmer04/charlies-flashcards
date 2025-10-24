const params = new URLSearchParams(window.location.search);
if (params.get("error") === "true") {
    document.getElementById("error-message").textContent = "Invalid username or password";
} else if (params.get("signed-up") === "true") {
    document.getElementById("signed-up-message").textContent = "Account created!";
}

document.getElementById("create-account").onclick = () => {
    window.location.href="signup.html";
}

document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const username = document.querySelector("#username input").value;
    const password = document.querySelector("#password input").value;

    try {
        const res = await axios.post("http://localhost:29001/api/login", {
            username,
            password
        });

        const token = res.data.token;

        sessionStorage.setItem("jwt", token);
        window.location.href = "decks.html";
    } catch (error) {
        document.getElementById("error-message").innerText = "Invalid username or password";
    }
});
