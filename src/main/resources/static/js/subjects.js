document.addEventListener("DOMContentLoaded", function () {
    // Asegurar que el modal esté completamente oculto al cargar la página
    let modal = document.getElementById("modifyModal");
    if (modal) {
        modal.style.display = "none";
    }
});

function openModifyModal(id, name) {
    let modal = document.getElementById("modifyModal");
    if (modal) {
        modal.style.display = "flex";
        document.getElementById("subjectId").value = id;
        document.getElementById("newName").value = name;
    }
}

function closeModifyModal() {
    let modal = document.getElementById("modifyModal");
    if (modal) {
        modal.style.display = "none";
    }
}

// Cerrar modal si el usuario hace clic fuera de él
window.onclick = function(event) {
    let modal = document.getElementById("modifyModal");
    if (event.target === modal) {
        closeModifyModal();
    }
};

// Funciones para aplicar y eliminar asignaturas
function deleteSubject(id) {
    fetch('/subjects/delete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `id=${id}`
    }).then(() => location.reload());
}

function applySubject(id) {
    fetch('/subjects/apply', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `id=${id}`
    }).then(() => location.reload());
}
