document.addEventListener('DOMContentLoaded', function () {
            const successAlert = document.getElementById('loginSuccessAlert');
            if (successAlert) {
                window.setTimeout(function () {
                    if (successAlert.parentNode) {
                        successAlert.remove();
                    }
                }, 1000);
            }

            const errorAlert = document.getElementById('loginErrorAlert');
            if (errorAlert) {
                window.setTimeout(function () {
                    if (errorAlert.parentNode) {
                        errorAlert.remove();
                    }
                }, 500);
            }

            const errorField = document.body.dataset.errorField || '';
            const emailInput = document.getElementById('username');
            const passwordInput = document.getElementById('password');

            if (errorField === 'password' && passwordInput) {
                passwordInput.focus();
                return;
            }

            if (emailInput) {
                emailInput.focus();
            }
        });
