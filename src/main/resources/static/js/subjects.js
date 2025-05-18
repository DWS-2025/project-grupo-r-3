document.addEventListener("DOMContentLoaded", function () {
    
    let modal = document.getElementById("modifyModal");
    if (modal) {
        modal.style.display = "none";
    }

    
    const csrfToken = document.querySelector('meta[name="csrf-token"]')?.content || '';
    console.log('CSRF Token:', csrfToken); 

    
    const createForm = document.getElementById("createSubjectForm");
    if (createForm) {
        createForm.addEventListener("submit", function (event) {
            event.preventDefault(); 
            const formData = new FormData(createForm);
            fetch('/subjects/create', {
                method: 'POST',
                headers: {
                    'X-CSRF-TOKEN': csrfToken 
                },
                body: formData
            }).then(response => {
                if (response.ok) {
                    console.log("Subject created, redirecting to /subjects");
                    window.location.href = '/subjects'; 
                } else {
                    response.text().then(text => {
                        console.error("Error:", text);
                        alert('Error creating subject: ' + text);
                    });
                }
            }).catch(error => {
                console.error("Fetch error:", error);
                alert('Error creating subject: ' + error.message);
            });
        });
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


window.onclick = function (event) {
    let modal = document.getElementById("modifyModal");
    if (event.target === modal) {
        closeModifyModal();
    }
};


function deleteSubject(id) {
    const csrfToken = document.querySelector('meta[name="csrf-token"]')?.content || '';
    console.log('Deleting subject with CSRF token:', csrfToken);
    fetch('/subjects/delete', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-CSRF-TOKEN': csrfToken 
        },
        body: `id=${id}`
    }).then(response => {
        if (response.ok) {
            window.location.href = '/subjects'; 
        } else {
            return response.text().then(text => {
                alert('Error deleting subject: ' + text);
            });
        }
    }).catch(error => {
        console.error('Error deleting subject:', error);
        alert('Error deleting subject: ' + error.message);
    });
}


function applySubject(id) {
    const csrfToken = document.querySelector('meta[name="csrf-token"]')?.content || '';
    console.log('Applying to subject with CSRF token:', csrfToken);
    fetch('/subjects/apply', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-CSRF-TOKEN': csrfToken 
        },
        body: `id=${id}`
    }).then(response => {
        if (response.ok) {
            window.location.href = '/subjects'; 
        } else {
            return response.text().then(text => {
                alert('Error applying to subject: ' + text);
            });
        }
    }).catch(error => {
        console.error('Error applying to subject:', error);
        alert('Error applying to subject: ' + error.message);
    });
}


function openCreateSubjectModal() {
    document.getElementById("createSubjectModal").style.display = "flex";
}


function closeCreateSubjectModal() {
    document.getElementById("createSubjectModal").style.display = "none";
}