(function() {
                const cleanupStaleModalArtifacts = function() {
                    // If no modal is shown, remove lingering backdrops/body locks that can block clicks.
                    const hasOpenModal = document.querySelector('.modal.show');
                    if (hasOpenModal) {
                        return;
                    }

                    document.querySelectorAll('.modal-backdrop').forEach(function(backdrop) {
                        backdrop.remove();
                    });

                    document.body.classList.remove('modal-open');
                    document.body.style.removeProperty('padding-right');
                };

                const markActiveNavItem = function() {
                    const path = window.location.pathname;

                    document.querySelectorAll('#teacherNavMenu .teacher-nav-item').forEach(function(link) {
                        const raw = link.getAttribute('data-path');
                        if (!raw) {
                            return;
                        }

                        const matches = raw
                            .split('|')
                            .map(function(item) { return item.trim(); })
                            .filter(Boolean)
                            .some(function(prefix) { return path.startsWith(prefix); });

                        if (matches) {
                            link.classList.add('active');
                        }
                    });
                };

                const setupReliableNavClicks = function() {
                    let navigating = false;

                    document.querySelectorAll('#teacherNavMenu .teacher-nav-item[href]').forEach(function(link) {
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

                const setupFlashAutoDismiss = function() {
                    const alerts = document.querySelectorAll(
                        '.teacher-content-wrapper .alert.alert-success, .teacher-content-wrapper .alert.alert-danger'
                    );

                    alerts.forEach(function(alert) {
                        if (alert.getAttribute('data-sticky') === 'true') {
                            return;
                        }

                        window.setTimeout(function() {
                            alert.style.transition = 'opacity 0.2s ease';
                            alert.style.opacity = '0';
                            window.setTimeout(function() {
                                alert.remove();
                            }, 200);
                        }, 1000);
                    });
                };

                if (document.readyState === 'loading') {
                    document.addEventListener('DOMContentLoaded', function() {
                        cleanupStaleModalArtifacts();
                        markActiveNavItem();
                        setupReliableNavClicks();
                        setupFlashAutoDismiss();
                    });
                } else {
                    cleanupStaleModalArtifacts();
                    markActiveNavItem();
                    setupReliableNavClicks();
                    setupFlashAutoDismiss();
                }

                // Handle browser back/forward cache restores where stale overlays can persist.
                window.addEventListener('pageshow', function() {
                    cleanupStaleModalArtifacts();
                });
            })();
