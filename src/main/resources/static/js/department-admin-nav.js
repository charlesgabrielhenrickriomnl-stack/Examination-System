(function() {
                const path = window.location.pathname;
                document.querySelectorAll('#departmentAdminNavMenu .teacher-nav-item[data-path]').forEach(function(link) {
                    const p = link.getAttribute('data-path');
                    if (p && path.startsWith(p)) {
                        link.classList.add('active');
                    }
                });

                if (!document.getElementById('fa6-cdn')) {
                    const fa = document.createElement('link');
                    fa.id = 'fa6-cdn';
                    fa.rel = 'stylesheet';
                    fa.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css';
                    document.head.appendChild(fa);
                }

                const iconMap = {
                    'bi-speedometer2': 'fa-gauge-high',
                    'bi-box-arrow-right': 'fa-right-from-bracket'
                };

                const toFa = function() {
                    document.querySelectorAll('.teacher-layout i.bi').forEach(function(icon) {
                        const classes = Array.from(icon.classList);
                        const biClass = classes.find(c => c.startsWith('bi-'));
                        if (!biClass) return;

                        const mapped = iconMap[biClass] || biClass.replace(/^bi-/, 'fa-');
                        icon.classList.remove('bi');
                        classes.filter(c => c.startsWith('bi-')).forEach(c => icon.classList.remove(c));
                        icon.classList.add('fa-solid', mapped);
                    });
                };

                if (document.readyState === 'loading') {
                    document.addEventListener('DOMContentLoaded', toFa);
                } else {
                    toFa();
                }
            })();
