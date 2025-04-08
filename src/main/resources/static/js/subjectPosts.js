document.addEventListener("DOMContentLoaded", function () {
    
    closeCreatePostModal();
});
function clearFilters() {
    document.getElementById('titleFilter').value = '';
    document.getElementById('descriptionFilter').value = '';
    document.querySelector('.filter-form').submit(); 
}

function openCreatePostModal() {
    const modal = document.getElementById('createPostModal');
    modal.style.display = 'flex';
    
    modal.style.position = 'fixed';
    modal.style.left = '50%';
    modal.style.top = '50%';
    modal.style.transform = 'translate(-50%, -50%)'; 
}

function closeCreatePostModal() {
    document.getElementById('createPostModal').style.display = 'none';
}

window.onclick = function (event) {
    let modal = document.getElementById("createPostModal");
    if (event.target === modal) {
        closeCreatePostModal();
    }
};