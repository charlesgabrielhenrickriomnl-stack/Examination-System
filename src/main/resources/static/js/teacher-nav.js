(function() {
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
                        markActiveNavItem();
                        setupFlashAutoDismiss();
                    });
                } else {
                    markActiveNavItem();
                    setupFlashAutoDismiss();
                }
            })();
