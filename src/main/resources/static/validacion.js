
function validaciones() {
    var contrasena = document.getElementById("contrasena").value;
    var confirmarContrasena = document.getElementById("confirmarContrasena").value;
    var telefono = document.getElementById("telefono").value;

    // Validar que las contraseñas coincidan
    if (contrasena !== confirmarContrasena) {
        alert("Las contraseñas no coinciden. Por favor, inténtalo de nuevo.");
        return false;
    }

    // Validar que el teléfono sea un número
    if (isNaN(telefono)) {
        alert("El teléfono debe ser un número. Por favor, inténtalo de nuevo.");
        return false;
    }

    return true;
}