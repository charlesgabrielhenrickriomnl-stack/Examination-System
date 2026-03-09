document.addEventListener('DOMContentLoaded', function () {
        const input = document.getElementById('distributionSearch');
        if (!input) return;

        input.addEventListener('input', function () {
            const query = this.value.toLowerCase().trim();
            document.querySelectorAll('#distributionStudentsTable tbody tr').forEach(row => {
                const key = row.getAttribute('data-search-key') || '';
                row.style.display = (!query || key.includes(query)) ? '' : 'none';
            });
        });
    });
