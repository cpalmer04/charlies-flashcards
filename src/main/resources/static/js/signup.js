const params = new URLSearchParams(window.location.search);
if (params.get("signup-error") === "true") {
    document.getElementById("error-message").textContent = "Sign up error - username taken " +
        "or database is full";
}

document.getElementById("signupForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const username = document.getElementById("entered-username").value;
    const password = document.getElementById("entered-password").value;

    try {
        const res = await axios.post("/api/signup", {
            username,
            password
        });
        window.location.href="login.html?signed-up=true"
    } catch (error) {
        window.location.href="signup.html?signup-error=true"
    }
});

document.getElementById("back").onclick = () => {
    window.location.href="login.html";
}