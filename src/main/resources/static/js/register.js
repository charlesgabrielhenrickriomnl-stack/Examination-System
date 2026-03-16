// Register Page JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('registerForm');
    const email = document.getElementById('email');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const departmentSelect = document.getElementById('departmentName');
    const programSelect = document.getElementById('programName');
    const roleInputs = Array.from(document.querySelectorAll('input[name="role"]'));
    const passwordHelp = document.getElementById('passwordHelp');
    const loginLinks = document.querySelectorAll('a[href="/login"]');
    let exitingToLogin = false;

    loginLinks.forEach(function(link) {
        link.addEventListener('click', function(event) {
            if (event.button !== 0 || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) {
                return;
            }

            const href = link.getAttribute('href');
            if (!href || exitingToLogin) {
                event.preventDefault();
                return;
            }

            exitingToLogin = true;
            event.preventDefault();
            window.location.assign(href);
        });
    });

    function isProgramRequired() {
        const checkedRole = roleInputs.find(input => input.checked);
        const role = checkedRole ? checkedRole.value : '';
        return role === 'TEACHER' || role === 'STUDENT';
    }

    function getProgramsForDepartment(departmentName) {
        const target = (departmentName || '').trim().toLowerCase();
        if (!target) {
            return [];
        }

        const programsMap = window.programsByDepartment || {};
        if (programsMap[departmentName]) {
            return programsMap[departmentName];
        }

        const matchingKey = Object.keys(programsMap).find(key => (key || '').trim().toLowerCase() === target);
        return matchingKey ? (programsMap[matchingKey] || []) : [];
    }

    function syncProgramOptions() {
        if (!departmentSelect || !programSelect) {
            return;
        }

        const selectedDepartment = (departmentSelect.value || '').trim();
        const programs = getProgramsForDepartment(selectedDepartment);

        const previousValue = programSelect.value || '';
        programSelect.innerHTML = '';

        const placeholder = document.createElement('option');
        placeholder.value = '';
        placeholder.textContent = programs.length > 0 ? 'Select Program' : 'Select Department first';
        programSelect.appendChild(placeholder);

        programs.forEach(program => {
            const option = document.createElement('option');
            option.value = program;
            option.textContent = program;
            programSelect.appendChild(option);
        });

        if (previousValue && programs.includes(previousValue)) {
            programSelect.value = previousValue;
        } else {
            programSelect.value = '';
        }

        programSelect.required = isProgramRequired();
        programSelect.disabled = programs.length === 0;
    }

    if (departmentSelect) {
        departmentSelect.addEventListener('change', syncProgramOptions);
        syncProgramOptions();
    }

    roleInputs.forEach(roleInput => {
        roleInput.addEventListener('change', syncProgramOptions);
    });
    
    // Password confirmation validation
    confirmPassword.addEventListener('input', function() {
        if (password.value !== confirmPassword.value) {
            passwordHelp.classList.remove('d-none');
            confirmPassword.setCustomValidity('Passwords do not match');
        } else {
            passwordHelp.classList.add('d-none');
            confirmPassword.setCustomValidity('');
        }
    });
    
    password.addEventListener('input', function() {
        if (confirmPassword.value !== '') {
            if (password.value !== confirmPassword.value) {
                passwordHelp.classList.remove('d-none');
                confirmPassword.setCustomValidity('Passwords do not match');
            } else {
                passwordHelp.classList.add('d-none');
                confirmPassword.setCustomValidity('');
            }
        }
    });
    
    // Form validation
    form.addEventListener('submit', function(e) {
        // Check password match
        if (password.value !== confirmPassword.value) {
            e.preventDefault();
            passwordHelp.classList.remove('d-none');
            confirmPassword.focus();
            return false;
        }
        
        // Validate password length
        if (password.value.length < 6) {
            e.preventDefault();
            alert('⚠️ Password must be at least 6 characters long');
            password.focus();
            return false;
        }

        if (departmentSelect && programSelect) {
            const selectedDepartment = (departmentSelect.value || '').trim();
            const programs = getProgramsForDepartment(selectedDepartment);
            if (!selectedDepartment) {
                e.preventDefault();
                alert('Please select your department.');
                departmentSelect.focus();
                return false;
            }

            if (isProgramRequired() && !programSelect.value.trim()) {
                e.preventDefault();
                alert('Please select your program.');
                programSelect.focus();
                return false;
            }

            if (isProgramRequired() && programs.length > 0 && !programs.includes(programSelect.value.trim())) {
                e.preventDefault();
                alert('Please select a valid program for the selected department.');
                programSelect.focus();
                return false;
            }
        }
        
        return true;
    });
    
    // Show password strength indicator
    password.addEventListener('input', function() {
        const strength = calculatePasswordStrength(password.value);
        // You can add a visual indicator here if needed
    });
    
    function calculatePasswordStrength(pwd) {
        let strength = 0;
        if (pwd.length >= 6) strength++;
        if (pwd.length >= 10) strength++;
        if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) strength++;
        if (/\d/.test(pwd)) strength++;
        if (/[^a-zA-Z0-9]/.test(pwd)) strength++;
        return strength;
    }
});
