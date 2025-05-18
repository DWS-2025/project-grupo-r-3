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

    // Variable para almacenar información del usuario actual
    let currentUser = { id: null, isAdmin: false };

    // Cargar información del usuario actual al inicio (sin bloquear la carga de comentarios)
    fetch('/api/users/current')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch current user');
            }
            return response.json();
        })
        .then(user => {
            currentUser = user;
            // Actualizar los comentarios ya cargados para mostrar botones si corresponde
            updateCommentActions();
        })
        .catch(error => {
            console.error("Error loading current user:", error);
            currentUser = { id: null, isAdmin: false };
        });

    function loadMoreComments() {
        if (!postId) return;

        spinner.style.display = "block";
        loadMoreBtn.style.display = "none";

        fetch(`/api/comments?postId=${postId}&page=${currentPage}&size=${batchSize}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Network response was not ok: " + response.statusText);
                }
                return response.json();
            })
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

        // Manejar el ID del usuario desde el campo userId
        const userId = comment.userId || null;
        // Como no tienes username en el comentario, usamos un valor por defecto o lo buscamos si es necesario
        const username = comment.author || 'Unknown'; // Ajusta si tienes un campo diferente para el nombre

        // Determinar si el usuario actual puede editar o borrar este comentario
        const canModify = currentUser && (currentUser.isAdmin || (currentUser.id && userId && currentUser.id === userId));

        commentDiv.innerHTML = `
            <div class="comment-header">
                <p class="comment-meta"><strong>${username}</strong></p>
                <div class="comment-actions" id="actions-${comment.id}">
                    ${canModify ? `
                        <span class="edit-icon" onclick="openEditCommentModal('${comment.id}', '${text}')">\u{1F58A}</span>
                        <span class="delete-icon" onclick="deleteComment('${subjectId}', '${postId}', '${comment.id}')">\u{274C}</span>
                    ` : ''}
                </div>
            </div>
            <div class="comment-text">${text}</div>
            ${imageSrc ? `<img src="${imageSrc}" alt="" class="comment-image">` : ''}
        `;

        // Almacenar userId como data attribute para actualizar acciones más tarde
        commentDiv.dataset.userId = userId || '';

        return commentDiv;
    }

    // Función para actualizar los botones de acciones en comentarios ya renderizados
    function updateCommentActions() {
        const comments = document.querySelectorAll('.comment');
        comments.forEach(commentEl => {
            const commentId = commentEl.id.split('-')[1];
            const actionsDiv = commentEl.querySelector(`#actions-${commentId}`);
            if (!actionsDiv) return;

            const userId = commentEl.dataset.userId ? parseInt(commentEl.dataset.userId) : null;
            const canModify = currentUser && (currentUser.isAdmin || (currentUser.id && userId && currentUser.id === userId));

            if (canModify) {
                const text = commentEl.querySelector('.comment-text').innerHTML.replace(/"/g, '\\"');
                actionsDiv.innerHTML = `
                    <span class="edit-icon" onclick="openEditCommentModal('${commentId}', '${text}')">\u{1F58A}</span>
                    <span class="delete-icon" onclick="deleteComment('${subjectId}', '${postId}', '${commentId}')">\u{274C}</span>
                `;
            } else {
                actionsDiv.innerHTML = '';
            }
        });
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

    // Cargar comentarios inmediatamente
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
