document.addEventListener("DOMContentLoaded", function () {
    // Ensure modal is hidden on page load
    closeCreatePostModal();
});

function openCreatePostModal() {
    let modal = document.getElementById("createPostModal");
    if (modal) {
        modal.style.display = "flex";
    }
}

function closeCreatePostModal() {
    let modal = document.getElementById("createPostModal");
    if (modal) {
        modal.style.display = "none";
    }
}

// Close modal when clicking outside of it
window.onclick = function(event) {
    let modal = document.getElementById("createPostModal");
    if (event.target === modal) {
        closeCreatePostModal();
    }
};
