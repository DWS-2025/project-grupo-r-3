document.addEventListener("DOMContentLoaded", function () {
    
    const quill = new Quill('#editor-container', {
        modules: {
            toolbar: [
                ['bold', 'italic', 'underline'],
                ['link', 'image'],
                ['clean']
            ]
        },
        theme: 'snow'
    });

    
    const commentsList = document.getElementById("comments-list");
    const loadMoreBtn = document.getElementById("loadMoreBtn");
    const spinner = document.getElementById("spinner");
    const batchSize = 10;
    let currentPage = 0;
    const postId = commentsList.dataset.postId;
    const subjectId = commentsList.dataset.subjectId;

    
    function loadMoreComments() {
        if (!postId) return;

        spinner.style.display = "block";
        loadMoreBtn.style.display = "none";

        fetch(`/api/comments?postId=${postId}&page=${currentPage}&size=${batchSize}`)
            .then(response => response.json())
            .then(data => {
                if (data.content?.length) {
                    data.content.forEach(comment => {
                        commentsList.appendChild(createCommentElement(comment));
                    });
                    currentPage++;
                    loadMoreBtn.style.display = data.content.length === batchSize ? "inline-block" : "none";
                }
                spinner.style.display = "none";
            })
            .catch(error => {
                console.error("Error loading comments:", error);
                spinner.style.display = "none";
            });
    }

    
    function createCommentElement(comment) {
        const commentDiv = document.createElement("div");
        commentDiv.className = "comment";
        commentDiv.id = `comment-${comment.id}`;

        const text = comment.text?.replace(/"/g, '\\"') || '';
        const imageSrc = comment.id ? `/api/comments/${comment.id}/image` : '';

        commentDiv.innerHTML = `
            <div class="comment-header">
                <p class="comment-meta"><strong>Loading...</strong></p>
                <div class="comment-actions">
                    <span class="edit-icon" onclick="openEditCommentModal('${comment.id}', '${text}')">\u{1F58A}</span>
                    <span class="delete-icon" onclick="deleteComment('${subjectId}', '${postId}', '${comment.id}')">\u{274C}</span>
                </div>
            </div>
            <div class="comment-text">${text}</div>
            ${imageSrc ? `<img src="${imageSrc}" alt="" class="comment-image">` : ''}
        `;

        
        fetch('/api/users/current')
            .then(response => response.json())
            .then(user => {
                commentDiv.querySelector('.comment-meta strong').textContent = user.username || 'Unknown';
            })
            .catch(console.error);

        return commentDiv;
    }

    
    let editQuill = null;
    const editModal = document.getElementById("editCommentModal");
    const editCommentId = document.getElementById("editCommentId");

    window.openEditCommentModal = function (id, text) {
        editCommentId.value = id;

        
        if (editQuill) editQuill.destroy();

        
        editQuill = new Quill('#edit-editor-container', {
            modules: {
                toolbar: [
                    ['bold', 'italic', 'underline'],
                    ['link', 'image'],
                    ['clean']
                ]
            },
            theme: 'snow'
        });

        editQuill.root.innerHTML = text;
        editModal.style.display = "flex";
    };

    window.closeEditCommentModal = function () {
        editModal.style.display = "none";
    };

    
    document.querySelector('form').onsubmit = function() {
        document.querySelector('#hidden-comment-input').value = quill.root.innerHTML;
    };

    document.getElementById('editCommentForm').onsubmit = function() {
        document.querySelector('#hidden-edit-comment-input').value = editQuill.root.innerHTML;
    };

    
    loadMoreComments();
    if (loadMoreBtn) {
        loadMoreBtn.addEventListener("click", loadMoreComments);
    }

    
    window.deleteComment = function (subjectId, postId, commentId) {
        fetch(`/subjects/${subjectId}/posts/${postId}/delete-comment`, {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: `commentId=${commentId}`
        }).then(response => {
            if (response.ok) {
                document.getElementById(`comment-${commentId}`)?.remove();
            } else {
                location.reload();
            }
        }).catch(() => location.reload());
    };
});
