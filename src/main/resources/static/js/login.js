const params = new URLSearchParams(window.location.search);
if (params.get("error") === "true") {
    document.getElementById("error-message").textContent = "Invalid username or password";
} else if (params.get("signed-up") === "true") {
    document.getElementById("signed-up-message").textContent = "Account created!";
}

document.getElementById("create-account").onclick = () => {
    window.location.href="signup.html";
}