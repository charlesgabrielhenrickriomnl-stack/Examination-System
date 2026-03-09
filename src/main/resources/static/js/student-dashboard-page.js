document.addEventListener('DOMContentLoaded', function () {
        const searchInput = document.getElementById('subjectSearch');
        const subjectCards = Array.from(document.querySelectorAll('.subject-card-col'));
        const loadMoreWrap = document.getElementById('subjectsLoadMoreWrap');
        const loadMoreBtn = document.getElementById('subjectsLoadMoreBtn');

        if (!subjectCards.length) return;

        let visibleLimit = 8;

        function applySubjectVisibility() {
            const query = searchInput ? (searchInput.value || '').toLowerCase().trim() : '';
            let matchedCount = 0;

            subjectCards.forEach(card => {
                const key = card.getAttribute('data-search-key') || '';
                const matches = !query || key.includes(query);

                if (!matches) {
                    card.style.display = 'none';
                    return;
                }

                const shouldShow = query ? true : matchedCount < visibleLimit;
                card.style.display = shouldShow ? '' : 'none';
                matchedCount += 1;
            });

            if (!loadMoreWrap) return;

            if (query) {
                loadMoreWrap.style.display = 'none';
                return;
            }

            const hasMore = matchedCount > visibleLimit;
            loadMoreWrap.style.display = hasMore ? 'flex' : 'none';
            if (loadMoreBtn) {
                loadMoreBtn.disabled = !hasMore;
            }
        }

        if (searchInput) {
            searchInput.addEventListener('input', applySubjectVisibility);
        }

        if (loadMoreBtn) {
            loadMoreBtn.addEventListener('click', function () {
                visibleLimit += 8;
                applySubjectVisibility();
            });
        }

        applySubjectVisibility();
    });
