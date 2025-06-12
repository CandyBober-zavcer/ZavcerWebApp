function togglePassword(icon, inputId) {
    const input = document.getElementById(inputId);
    const isPassword = input.type === "password";
    input.type = isPassword ? "text" : "password";
    icon.classList.toggle("fa-eye");
    icon.classList.toggle("fa-eye-slash");
}

function validatePasswordStrength(password) {
    const lengthCheck = password.length >= 8;
    const letterCheck = /[A-Za-zА-Яа-яЁё]/.test(password);
    const digitCheck = /\d/.test(password);
    return lengthCheck && letterCheck && digitCheck;
}

function attachFormValidation(formId) {
    const form = document.getElementById(formId);
    if (!form) return;

    form.addEventListener("submit", function (e) {
        const pass = form.querySelector("#password")?.value || "";
        const confirm = form.querySelector("#confirm")?.value || null;

        if (!validatePasswordStrength(pass)) {
            e.preventDefault();
            alert("Пароль должен содержать минимум 8 символов, хотя бы одну букву и одну цифру.");
            return;
        }

        if (confirm !== null && pass !== confirm) {
            e.preventDefault();
            alert("Пароли не совпадают");
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    attachFormValidation("resetForm");
    attachFormValidation("registerForm");

    document.querySelectorAll(".show.fa-eye, .show.fa-eye-slash").forEach(icon => {
        const input = icon.previousElementSibling;
        if (input && input.tagName === "INPUT") {
            icon.addEventListener("click", () => togglePassword(icon, input.id));
        }
    });
});
