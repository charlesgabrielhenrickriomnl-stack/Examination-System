document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.view-classroom-btn').forEach(function (link) {
        link.addEventListener('click', function (e) {
            e.preventDefault();
            window.location.assign(this.href);
        });
    });

    const subjectSearchInput = document.getElementById('subjectSearchInput');
    const subjectSortSelect = document.getElementById('subjectSortSelect');
    const subjectPageSizeSelect = document.getElementById('subjectPageSizeSelect');
    const subjectGrid = document.getElementById('subjectGrid');
    const subjectPrevBtn = document.getElementById('subjectPrevBtn');
    const subjectNextBtn = document.getElementById('subjectNextBtn');
    const subjectPageLabel = document.getElementById('subjectPageLabel');
    const subjectVisibleCount = document.getElementById('subjectVisibleCount');
    const subjectTotalCount = document.getElementById('subjectTotalCount');
    const subjectPaginationWrap = document.getElementById('subjectPaginationWrap');
    const subjectCards = Array.from(document.querySelectorAll('.subject-card-item'));
    const subjectSearchEmptyState = document.getElementById('subjectSearchEmptyState');
    let currentPage = 1;

    function normalizeValue(value) {
        return String(value || '').toLowerCase();
    }

    function getCardName(card) {
        return normalizeValue(card.getAttribute('data-name'));
    }

    function getCardStudents(card) {
        return Number(card.getAttribute('data-students') || 0);
    }

    function getPageSize() {
        const parsed = Number(subjectPageSizeSelect ? subjectPageSizeSelect.value : 12);
        return Number.isFinite(parsed) && parsed > 0 ? parsed : 12;
    }

    function applySort(cards) {
        const sortMode = subjectSortSelect ? subjectSortSelect.value : 'name-asc';
        const sorted = cards.slice();

        sorted.sort(function (a, b) {
            if (sortMode === 'name-desc') {
                return getCardName(b).localeCompare(getCardName(a));
            }
            if (sortMode === 'students-desc') {
                return getCardStudents(b) - getCardStudents(a) || getCardName(a).localeCompare(getCardName(b));
            }
            if (sortMode === 'students-asc') {
                return getCardStudents(a) - getCardStudents(b) || getCardName(a).localeCompare(getCardName(b));
            }
            return getCardName(a).localeCompare(getCardName(b));
        });

        return sorted;
    }

    function applyFilter() {
        const query = normalizeValue(subjectSearchInput ? subjectSearchInput.value.trim() : '');
        const filtered = subjectCards.filter(function (card) {
            const searchText = normalizeValue(card.getAttribute('data-search'));
            return query === '' || searchText.includes(query);
        });

        return applySort(filtered);
    }

    function updatePaginationUI(filteredCount, totalPages) {
        if (subjectVisibleCount) {
            subjectVisibleCount.textContent = String(filteredCount);
        }

        if (subjectTotalCount) {
            subjectTotalCount.textContent = String(subjectCards.length);
        }

        if (subjectPageLabel) {
            subjectPageLabel.textContent = 'Page ' + currentPage + ' of ' + totalPages;
        }

        if (subjectPrevBtn) {
            subjectPrevBtn.disabled = filteredCount === 0 || currentPage <= 1;
        }

        if (subjectNextBtn) {
            subjectNextBtn.disabled = filteredCount === 0 || currentPage >= totalPages;
        }

        if (subjectPaginationWrap) {
            subjectPaginationWrap.style.display = filteredCount > getPageSize() ? '' : 'none';
        }
    }

    function renderSubjects() {
        const filteredSortedCards = applyFilter();
        const pageSize = getPageSize();
        const totalPages = Math.max(1, Math.ceil(filteredSortedCards.length / pageSize));

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }

        subjectCards.forEach(function (card) {
            card.style.display = 'none';
        });

        if (subjectGrid) {
            filteredSortedCards.forEach(function (card) {
                subjectGrid.appendChild(card);
            });
        }

        const start = (currentPage - 1) * pageSize;
        const pageCards = filteredSortedCards.slice(start, start + pageSize);
        pageCards.forEach(function (card) {
            card.style.display = '';
        });

        if (subjectSearchEmptyState) {
            subjectSearchEmptyState.style.display = filteredSortedCards.length === 0 ? '' : 'none';
        }

        updatePaginationUI(filteredSortedCards.length, totalPages);
    }

    if (subjectCards.length > 0) {
        if (subjectSearchInput) {
            subjectSearchInput.addEventListener('input', function () {
                currentPage = 1;
                renderSubjects();
            });
        }

        if (subjectSortSelect) {
            subjectSortSelect.addEventListener('change', function () {
                currentPage = 1;
                renderSubjects();
            });
        }

        if (subjectPageSizeSelect) {
            subjectPageSizeSelect.addEventListener('change', function () {
                currentPage = 1;
                renderSubjects();
            });
        }

        if (subjectPrevBtn) {
            subjectPrevBtn.addEventListener('click', function () {
                if (currentPage > 1) {
                    currentPage -= 1;
                    renderSubjects();
                }
            });
        }

        if (subjectNextBtn) {
            subjectNextBtn.addEventListener('click', function () {
                currentPage += 1;
                renderSubjects();
            });
        }

        renderSubjects();
    }
});
