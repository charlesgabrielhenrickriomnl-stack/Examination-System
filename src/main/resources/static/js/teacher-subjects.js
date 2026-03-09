document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.view-classroom-btn').forEach(function (link) {
        link.addEventListener('click', function (e) {
            e.preventDefault();
            window.location.assign(this.href);
        });
    });

    const subjectSearchInput = document.getElementById('subjectSearchInput');
    const subjectCards = Array.from(document.querySelectorAll('.subject-card-item'));
    const subjectSearchEmptyState = document.getElementById('subjectSearchEmptyState');

    if (subjectSearchInput && subjectCards.length > 0) {
        const runSubjectFilter = function () {
            const query = subjectSearchInput.value.trim().toLowerCase();
            let visibleCount = 0;

            subjectCards.forEach(function (card) {
                const searchText = (card.getAttribute('data-search') || '').toLowerCase();
                const isMatch = query === '' || searchText.includes(query);
                card.style.display = isMatch ? '' : 'none';
                if (isMatch) {
                    visibleCount++;
                }
            });

            if (subjectSearchEmptyState) {
                subjectSearchEmptyState.style.display = visibleCount === 0 ? '' : 'none';
            }
        };

        subjectSearchInput.addEventListener('input', runSubjectFilter);
    }
});
