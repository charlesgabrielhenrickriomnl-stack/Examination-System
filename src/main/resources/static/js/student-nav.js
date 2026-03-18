(function() {
    const markActiveNavItem = function() {
        const path = window.location.pathname;

        document.querySelectorAll('#studentNavMenu .teacher-nav-item').forEach(function(link) {
            const raw = link.getAttribute('data-path');
            if (!raw) {
                return;
            }

            const matchMode = (link.getAttribute('data-match') || '').toLowerCase();
            const matches = raw
                .split('|')
                .map(function(item) { return item.trim(); })
                .filter(Boolean)
                .some(function(prefix) {
                    if (matchMode === 'exact') {
                        return path === prefix;
                    }
                    return path.startsWith(prefix);
                });

            if (matches) {
                link.classList.add('active');
            }
        });
    };

    const setupReliableNavClicks = function() {
        let navigating = false;

        document.querySelectorAll('#studentNavMenu .teacher-nav-item[href]').forEach(function(link) {
            link.addEventListener('click', function(event) {
                if (event.button !== 0 || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) {
                    return;
                }

                const href = link.getAttribute('href');
                if (!href || href === '#') {
                    return;
                }

                if (navigating) {
                    event.preventDefault();
                    return;
                }

                navigating = true;
                event.preventDefault();
                window.location.assign(href);
            });
        });
    };

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            markActiveNavItem();
            setupReliableNavClicks();
        });
    } else {
        markActiveNavItem();
        setupReliableNavClicks();
    }
})();
