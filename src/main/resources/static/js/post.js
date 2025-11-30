document.addEventListener("DOMContentLoaded", function () {

    const quill = new Quill('#editor-container', {
        modules: {
            toolbar: [
                ['bold', 'italic', 'underline'], // Basic text formatting
                [{ 'list': 'ordered' }, { 'list': 'bullet' }], // Lists
                [{ 'header': [1, 2, 3, false] }],         // Headers
                ['clean']                                  // Remove formatting button
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
                    updateCommentActions(); // Asegurar que los botones se actualicen
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
                    ['bold', 'italic', 'underline'], // Basic text formatting
                    [{ 'list': 'ordered' }, { 'list': 'bullet' }], // Lists
                    [{ 'header': [1, 2, 3, false] }],         // Headers
                    ['clean']                                  // Remove formatting button
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

    // Handle form submission for new comment with validation
    const commentForm = document.querySelector('form[action*="comment"]');
    if (commentForm) {
        commentForm.onsubmit = function(e) {
            const commentText = quill.getText().trim(); // Obtener texto plano y eliminar espacios/saltos de línea
            const commentHtml = quill.root.innerHTML;
            
            // Debug logging
            console.log('Comment text:', commentText);
            console.log('Comment HTML:', commentHtml);
            console.log('Text length:', commentText.length);
            console.log('Quill content:', quill.root.innerHTML);
            
            // Check if comment is empty (only whitespace or just newlines)
            if (commentText.length === 0 || commentText === '\n' || !quill.getText().replace(/\s/g, '').length) {
                e.preventDefault(); // Evitar el envío del formulario
                alert('Por favor, escribe un comentario antes de enviarlo.'); // Mensaje al usuario
                return false;
            }
            
            // Prevent default form submission
            e.preventDefault();
            
            // Create FormData and manually set all fields
            const formData = new FormData();
            formData.append('commentText', commentText);
            
            // Add image if present
            const imageInput = document.querySelector('input[name="image"]');
            if (imageInput && imageInput.files.length > 0) {
                formData.append('image', imageInput.files[0]);
            }
            
            // Add CSRF token
            const csrfToken = document.querySelector('input[name="_csrf"]');
            if (csrfToken) {
                formData.append('_csrf', csrfToken.value);
            }
            
            console.log('FormData commentText:', formData.get('commentText'));
            
            // Submit via fetch
            fetch(commentForm.action, {
                method: 'POST',
                body: formData
            }).then(response => {
                if (response.ok) {
                    window.location.reload(); // Reload to show the new comment
                } else {
                    alert('Error al crear el comentario. Por favor intenta de nuevo.');
                }
            }).catch(error => {
                console.error('Error:', error);
                alert('Error al crear el comentario. Por favor intenta de nuevo.');
            });
            
            return false;
        };
    } else {
        console.error('Comment form not found!');
    }

    // Handle form submission for editing comment with validation
    document.getElementById('editCommentForm').onsubmit = function(e) {
        const editCommentText = editQuill.getText().trim(); // Obtener texto plano y eliminar espacios/saltos de línea
        // Check if comment is empty (only whitespace or just newlines)
        if (editCommentText.length === 0 || editCommentText === '\n' || !editQuill.getText().replace(/\s/g, '').length) {
            e.preventDefault(); // Evitar el envío del formulario
            alert('Por favor, escribe un comentario antes de guardarlo.'); // Mensaje al usuario
            return false;
        }
        // Send plain text, not HTML
        document.querySelector('#hidden-edit-comment-input').value = editCommentText;
        return true;
    };

    // Cargar comentarios inmediatamente
    loadMoreComments();

    if (loadMoreBtn) {
        loadMoreBtn.addEventListener("click", loadMoreComments);
    }

    window.deleteComment = function (subjectId, postId, commentId) {
        const csrfToken = document.querySelector('input[name="_csrf"]').value;
        fetch(`/subjects/${subjectId}/posts/${postId}/delete-comment`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "X-CSRF-Token": csrfToken
            },
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
