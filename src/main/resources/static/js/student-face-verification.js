(function () {
    const root = document.querySelector('.verification-shell');
    if (!root) {
        return;
    }

    const startCameraBtn = document.getElementById('startCameraBtn');
    const captureBtn = document.getElementById('captureBtn');
    const submitBtn = document.getElementById('submitBtn');
    const cameraError = document.getElementById('cameraError');
    const video = document.getElementById('cameraVideo');
    const canvas = document.getElementById('captureCanvas');
    const preview = document.getElementById('capturedPreview');
    const placeholder = document.getElementById('cameraPlaceholder');
    const capturedImageInput = document.getElementById('capturedImageInput');
    const capturedDescriptorInput = document.getElementById('capturedDescriptorInput');
    const faceVerifyForm = document.getElementById('faceVerifyForm');
    const modelUrl = String(root.dataset.modelUrl || '').trim();

    let stream = null;
    let modelsLoaded = false;
    let modelsLoading = null;

    function setError(message) {
        cameraError.textContent = message || '';
    }

    async function ensureModelsLoaded() {
        if (modelsLoaded) {
            return;
        }
        if (!window.faceapi) {
            throw new Error('Face recognition library is not available in this browser session.');
        }
        if (!modelUrl) {
            throw new Error('Face model URL is missing.');
        }

        if (!modelsLoading) {
            modelsLoading = Promise.all([
                faceapi.nets.tinyFaceDetector.loadFromUri(modelUrl),
                faceapi.nets.faceLandmark68Net.loadFromUri(modelUrl),
                faceapi.nets.faceRecognitionNet.loadFromUri(modelUrl)
            ]);
        }

        await modelsLoading;
        modelsLoaded = true;
    }

    async function startCamera() {
        setError('');
        try {
            await ensureModelsLoaded();

            if (stream) {
                stream.getTracks().forEach(track => track.stop());
            }

            stream = await navigator.mediaDevices.getUserMedia({
                video: {
                    width: { ideal: 1280 },
                    height: { ideal: 720 },
                    facingMode: 'user'
                },
                audio: false
            });

            video.srcObject = stream;
            video.classList.remove('d-none');
            preview.classList.add('d-none');
            placeholder.classList.add('d-none');
            capturedImageInput.value = '';
            if (capturedDescriptorInput) {
                capturedDescriptorInput.value = '';
            }
            submitBtn.disabled = true;
            captureBtn.disabled = false;
        } catch (error) {
            setError((error && error.message)
                ? error.message
                : 'Unable to access camera. Please allow camera permission and reload the page.');
        }
    }

    async function capturePhoto() {
        setError('');
        try {
            await ensureModelsLoaded();

            if (!video.videoWidth || !video.videoHeight) {
                setError('Camera is still loading. Please try again in a second.');
                return;
            }

            canvas.width = video.videoWidth;
            canvas.height = video.videoHeight;
            const ctx = canvas.getContext('2d');
            ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

            const detectionOptions = new faceapi.TinyFaceDetectorOptions({ inputSize: 320, scoreThreshold: 0.5 });
            const detections = await faceapi
                .detectAllFaces(canvas, detectionOptions)
                .withFaceLandmarks()
                .withFaceDescriptors();

            if (!detections || detections.length === 0) {
                setError('No face detected. Keep your face centered and try again.');
                submitBtn.disabled = true;
                return;
            }

            if (detections.length > 1) {
                setError('Multiple faces detected. Ensure only one person is in frame.');
                submitBtn.disabled = true;
                return;
            }

            const imageDataUrl = canvas.toDataURL('image/jpeg', 0.9);
            capturedImageInput.value = imageDataUrl;
            if (capturedDescriptorInput) {
                capturedDescriptorInput.value = JSON.stringify(Array.from(detections[0].descriptor || []));
            }

            preview.src = imageDataUrl;
            preview.classList.remove('d-none');
            video.classList.add('d-none');
            submitBtn.disabled = false;

            if (stream) {
                stream.getTracks().forEach(track => track.stop());
                stream = null;
            }
        } catch (error) {
            setError((error && error.message)
                ? error.message
                : 'Could not capture face descriptor. Please try again.');
            submitBtn.disabled = true;
        }
    }

    startCameraBtn.addEventListener('click', startCamera);
    captureBtn.addEventListener('click', capturePhoto);

    faceVerifyForm.addEventListener('submit', function (event) {
        if (!capturedImageInput.value || !capturedDescriptorInput || !capturedDescriptorInput.value) {
            event.preventDefault();
            setError('Capture your face first before continuing.');
        }
    });

    captureBtn.disabled = true;
    submitBtn.disabled = true;

    window.addEventListener('beforeunload', function () {
        if (stream) {
            stream.getTracks().forEach(track => track.stop());
        }
    });
})();
