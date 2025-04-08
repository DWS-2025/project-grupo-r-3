document.addEventListener("DOMContentLoaded", function () {
    const commentsList = document.getElementById("comments-list");
    const loadMoreBtn = document.getElementById("loadMoreBtn");
    const spinner = document.getElementById("spinner");
    const batchSize = 10;
    let currentPage = 0;
    const postId = commentsList.dataset.postId;
    const subjectId = commentsList.dataset.subjectId;

    console.log("postId:", postId, "subjectId:", subjectId);

    function loadMoreComments() {
        if (!postId) {
            console.error("postId is not available");
            return;
        }

        spinner.style.display = "block";
        loadMoreBtn.style.display = "none";

        fetch(`/api/comments?postId=${postId}&page=${currentPage}&size=${batchSize}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Network response was not ok: " + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                console.log("Response data:", data);

                if (data.content && data.content.length > 0) {
                    data.content.forEach(comment => {
                        const commentElement = createCommentElement(comment);
                        commentsList.appendChild(commentElement);
                    });

                    currentPage++;

                    if (data.content.length === batchSize) {
                        loadMoreBtn.style.display = "inline-block";
                    } else {
                        loadMoreBtn.style.display = "none";
                    }
                } else {
                    console.log("No more content or empty response");
                    loadMoreBtn.style.display = "none";
                }

                spinner.style.display = "none";
            })
            .catch(error => {
                console.error("Error loading comments:", error);
                spinner.style.display = "none";
                loadMoreBtn.style.display = "inline-block";
            });
    }

    function createCommentElement(comment) {
        const commentDiv = document.createElement("div");
        commentDiv.className = "comment";
        commentDiv.id = `comment-${comment.id}`;

        const text = comment.text ? comment.text.replace(/'/g, "\\'").replace(/"/g, '\\"') : '';
        const username = comment.username || 'Unknown';

        const imageSrc = comment.id ? `/api/comments/${comment.id}/image` : '';

        commentDiv.innerHTML = `
            <div class="comment-header">
                <p class="comment-meta"><strong>${username}</strong></p>
                <div class="comment-actions">
                    <span class="edit-icon" onclick="openEditCommentModal('${comment.id}', '${text}')">🖊️</span>
                    <span class="delete-icon" onclick="deleteComment('${subjectId}', '${postId}', '${comment.id}')">❌</span>
                </div>
            </div>
            <p class="comment-text">${text}</p>
            ${imageSrc ? `<img src="${imageSrc}" alt="" class="comment-image">` : ''}
        `;

        return commentDiv;
    }

    loadMoreComments();

    if (loadMoreBtn) {
        loadMoreBtn.addEventListener("click", loadMoreComments);
    }

    const editModal = document.getElementById("editCommentModal");
    const editCommentId = document.getElementById("editCommentId");
    const editCommentText = document.getElementById("editCommentText");

    window.openEditCommentModal = function (id, text) {
        editCommentId.value = id;
        editCommentText.value = text;
        editModal.style.display = "flex";
    };

    window.closeEditCommentModal = function () {
        editModal.style.display = "none";
    };

    window.deleteComment = function (subjectId, postId, commentId) {
        fetch(`/subjects/${subjectId}/posts/${postId}/delete-comment`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `commentId=${commentId}`,
        })
            .then(response => {
                if (response.ok) {
                    const commentEl = document.getElementById(`comment-${commentId}`);
                    if (commentEl) commentEl.remove();
                } else {
                    console.error("Error deleting comment, reloading page.");
                    location.reload();
                }
            })
            .catch(error => {
                console.error("Error:", error);
                location.reload();
            });
    };

    const commentForm = document.querySelector("form[action$='/comment']");
    if (commentForm) {
        commentForm.addEventListener("submit", function (event) {
            event.preventDefault();
            const formData = new FormData(this);

            fetch(this.action, {
                method: "POST",
                body: formData,
            })
                .then(response => response.text())
                .then(() => {
                    currentPage = 0;
                    commentsList.innerHTML = '';
                    loadMoreComments();

                    
                    const commentInput = commentForm.querySelector("textarea, input[type='text']");
                    const imageInput = commentForm.querySelector("input[type='file']");
                    if (commentInput) commentInput.value = '';
                    if (imageInput) imageInput.value = '';
                })
                .catch(error => {
                    console.error("Error posting comment:", error);
                });
        });
    }
});