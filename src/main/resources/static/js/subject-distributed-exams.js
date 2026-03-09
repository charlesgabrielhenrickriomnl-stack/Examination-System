document.addEventListener('DOMContentLoaded', function () {
        const input = document.getElementById('distributedExamsSearch');
        if (!input) return;

        input.addEventListener('input', function () {
            const query = this.value.toLowerCase().trim();
            document.querySelectorAll('#distributedExamsTable tbody tr').forEach(row => {
                const key = row.getAttribute('data-search-key') || '';
                row.style.display = (!query || key.includes(query)) ? '' : 'none';
            });
        });
    });
