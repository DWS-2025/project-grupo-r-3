// Open the Edit Comment Modal
function openEditCommentModal(id, text) {
    document.getElementById("editCommentId").value = id;
    document.getElementById("editCommentText").value = text;
    document.getElementById("editCommentModal").style.display = "flex";
}

// Close the Edit Comment Modal
function closeEditCommentModal() {
    document.getElementById("editCommentModal").style.display = "none";
}

// Delete a Comment
function deleteComment(postId, commentId) {
    fetch(`/subjects/${postId}/posts/${commentId}/delete`, {
        method: "POST"
    }).then(() => location.reload());
}
