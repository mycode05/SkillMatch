// Example for scroll animation
window.addEventListener('scroll', () => {
    const header = document.querySelector('header');
    header.style.backgroundColor = window.scrollY > 50 ? '#732b9d' : 'rgb(129, 51, 162)';
});

// Login form validation
document.querySelector(".login form")?.addEventListener("submit", function (e) {
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    if (!email.includes("@") || !email.includes(".") || email.indexOf("@") > email.lastIndexOf(".")) {
        alert("Please enter a valid email address.");
        e.preventDefault();
        return;
    }

    if (!isValidPassword(password)) {
        alert(
            "Password must meet the following criteria:\n" +
            "- At least 8 characters long\n" +
            "- Include at least one uppercase letter\n" +
            "- Include at least one lowercase letter\n" +
            "- Include at least one number\n" +
            "- Include at least one special character (!@#$%^&*)\n" +
            "- Must not contain spaces"
        );
        e.preventDefault();
    }
});

// Registration form validation
document.querySelector(".register form")?.addEventListener("submit", function (e) {
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirm-password").value;
    const email = document.getElementById("email").value.trim();
    const mobile = document.getElementById("mobile").value.trim();

    if (username.length < 6) {
        alert("Username must be at least 6 characters long.");
        e.preventDefault();
        return;
    }

    if (!isValidPassword(password)) {
        alert(
            "Password must meet the following criteria:\n" +
            "- At least 8 characters long\n" +
            "- Include at least one uppercase letter\n" +
            "- Include at least one lowercase letter\n" +
            "- Include at least one number\n" +
            "- Include at least one special character (!@#$%^&*)\n" +
            "- Must not contain spaces"
        );
        e.preventDefault();
        return;
    }

    if (password !== confirmPassword) {
        alert("Password and Confirm Password must match.");
        e.preventDefault();
        return;
    }

    if (!email.includes("@") || !email.includes(".") || email.indexOf("@") > email.lastIndexOf(".")) {
        alert("Please enter a valid email address.");
        e.preventDefault();
        return;
    }

    if (mobile.length !== 10 || !isValidMobileNumber(mobile)) {
        alert("Mobile number must be 10 digits long and start with a number between 6 and 9.");
        e.preventDefault();
    }
});

// Helper function to validate the password
function isValidPassword(password) {
    const hasUpperCase = [...password].some((char) => char >= 'A' && char <= 'Z');
    const hasLowerCase = [...password].some((char) => char >= 'a' && char <= 'z');
    const hasNumber = [...password].some((char) => char >= '0' && char <= '9');
    const hasSpecialChar = [...password].some((char) => "!@#$%^&*".includes(char));
    const hasNoSpaces = ![...password].some((char) => char === " ");

    return (
        password.length >= 8 &&
        hasUpperCase &&
        hasLowerCase &&
        hasNumber &&
        hasSpecialChar &&
        hasNoSpaces
    );
}

// Mobile number validation helper function
function isValidMobileNumber(mobile) {
    if (isNaN(mobile)) return false;

    const firstDigit = parseInt(mobile.charAt(0));
    return firstDigit >= 6 && firstDigit <= 9;
}
// JavaScript for form validation if necessary
document.querySelector(".reset-password form")?.addEventListener("submit", function (e) {
    const email = document.getElementById("email").value.trim();
    
    if (!email.includes("@") || !email.includes(".") || email.indexOf("@") > email.lastIndexOf(".")) {
        alert("Please enter a valid email address.");
        e.preventDefault();
    }
});
