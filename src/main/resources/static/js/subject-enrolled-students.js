document.addEventListener('DOMContentLoaded', function () {
        const input = document.getElementById('enrolledStudentSearch');
        if (!input) return;

        input.addEventListener('input', function () {
            const query = this.value.toLowerCase().trim();
            document.querySelectorAll('#enrolledStudentsTable tbody tr').forEach(row => {
                const key = row.getAttribute('data-search-key') || '';
                row.style.display = (!query || key.includes(query)) ? '' : 'none';
            });
        });
    });
