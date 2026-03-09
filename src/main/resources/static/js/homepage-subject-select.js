document.getElementById('subjectSelect').addEventListener('change', function() {
                                    const newSubjectInput = document.getElementById('newSubjectInput');
                                    const subjectSelect = document.getElementById('subjectSelect');

                                    if (this.value === '__new__') {
                                        newSubjectInput.style.display = 'block';
                                        newSubjectInput.required = true;
                                        subjectSelect.removeAttribute('name');
                                        newSubjectInput.setAttribute('name', 'subject');
                                    } else {
                                        newSubjectInput.style.display = 'none';
                                        newSubjectInput.required = false;
                                        newSubjectInput.removeAttribute('name');
                                        subjectSelect.setAttribute('name', 'subject');
                                    }
                                });
