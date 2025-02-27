document.addEventListener("DOMContentLoaded", function () {
    closeModifyModal();
});

function openModifyModal(id, name) {
    document.getElementById("modifyModal").style.display = "flex";
    document.getElementById("subjectId").value = id;
    document.getElementById("newName").value = name;
}

function closeModifyModal() {
    document.getElementById("modifyModal").style.display = "none";
}

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
