(function() {
    const path = window.location.pathname;
    document.querySelectorAll('#departmentAdminNavMenu .teacher-nav-item[data-path]').forEach(function(link) {
        const p = link.getAttribute('data-path');
        if (!p) {
            return;
        }
        if (path.startsWith(p)) {
            link.classList.add('active');
        }
    });
})();
