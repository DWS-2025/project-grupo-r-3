// Ensure the Edit Comment Modal is closed on page load
document.addEventListener("DOMContentLoaded", function () {
    closeEditCommentModal();

    // Attach event listener for adding a comment dynamically
    let commentForm = document.getElementById("commentForm");
    if (commentForm) {
        commentForm.addEventListener("submit", function (event) {
            event.preventDefault(); // Prevent full page reload

            let formData = new FormData(this);
            fetch(this.action, {
                method: "POST",
                body: new URLSearchParams(formData),
            })
                .then(response => response.text())
                .then(data => {
                    location.reload(); // Refresh to show the new comment
                })
                .catch(error => console.error("Error posting comment:", error));
        });
    }
});

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

// Function to delete a comment and update UI dynamically
function deleteComment(subjectId, postId, commentId) {
    fetch(`/subjects/${subjectId}/posts/${postId}/delete-comment`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
        },
        body: `commentId=${commentId}`,
    })
        .then(response => {
            if (response.ok) {
                // Remove comment from DOM without refreshing
                let commentElement = document.getElementById(`comment-${commentId}`);
                if (commentElement) {
                    commentElement.remove();
                }
            } else {
                console.error("Error deleting comment, reloading page.");
                location.reload(); // Reload if deletion fails
            }
        })
        .catch(error => {
            console.error("Error:", error);
            location.reload(); // Reload if fetch request fails
        });
}
