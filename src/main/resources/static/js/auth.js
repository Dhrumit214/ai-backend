/**
 * AI Friend - Authentication Functionality
 */
document.addEventListener('DOMContentLoaded', function() {
    initAuthForms();
    checkAuthStatus();
});

/**
 * API base URL
 */
const API_URL = '/api';

/**
 * Initialize registration and login forms
 */
function initAuthForms() {
    // Registration form
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', handleSignup);
    }
    
    // Login form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
}

/**
 * Handle signup form submission
 */
function handleSignup(event) {
    event.preventDefault();
    
    // Check form validity
    if (!this.checkValidity()) {
        this.classList.add('was-validated');
        return;
    }
    
    // Get form values
    const firstName = document.getElementById('firstName').value;
    const lastName = document.getElementById('lastName').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const phoneNumber = document.getElementById('phoneNumber').value;
    const personaId = document.getElementById('personaId').value;
    const messageFrequency = document.getElementById('messageFrequency').value;
    
    // Show loading state
    const submitButton = document.querySelector('.signup-submit');
    const originalButtonText = submitButton.innerHTML;
    submitButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Signing up...';
    submitButton.disabled = true;
    
    // Send registration request
    fetch(`${API_URL}/auth/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            firstName: firstName,
            lastName: lastName,
            email: email,
            password: password,
            phoneNumber: phoneNumber
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.token) {
            // Store auth token
            localStorage.setItem('token', data.token);
            localStorage.setItem('userId', data.userId);
            localStorage.setItem('userEmail', data.email);
            localStorage.setItem('userFirstName', data.firstName);
            
            // Save user preferences
            saveUserPreferences(personaId, messageFrequency)
                .then(() => {
                    // Show success message and redirect
                    showRegistrationSuccess();
                })
                .catch(error => {
                    console.error('Error saving preferences:', error);
                    redirectToDashboard(); // Redirect anyway
                });
        } else {
            // Show error message
            const errorDiv = document.querySelector('.signup-error');
            errorDiv.textContent = data.message || 'An error occurred during registration. Please try again.';
            errorDiv.style.display = 'block';
            
            // Reset button
            submitButton.innerHTML = originalButtonText;
            submitButton.disabled = false;
        }
    })
    .catch(error => {
        console.error('Registration error:', error);
        
        // Show error message
        const errorDiv = document.querySelector('.signup-error');
        errorDiv.textContent = 'An error occurred during registration. Please try again.';
        errorDiv.style.display = 'block';
        
        // Reset button
        submitButton.innerHTML = originalButtonText;
        submitButton.disabled = false;
    });
}

/**
 * Save user preferences after registration
 */
function saveUserPreferences(personaId, messageFrequency) {
    const token = localStorage.getItem('token');
    
    return fetch(`${API_URL}/preferences`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
            preferredPersonaId: personaId,
            messageFrequency: messageFrequency
        })
    });
}

/**
 * Show registration success and begin onboarding
 */
function showRegistrationSuccess() {
    // Hide signup form and show success message
    const signupForm = document.getElementById('signupForm');
    signupForm.innerHTML = `
        <div class="text-center py-5">
            <div class="mb-4">
                <i class="fas fa-check-circle text-success" style="font-size: 5rem;"></i>
            </div>
            <h3>Welcome to MsgMeAI!</h3>
            <p class="mb-4">Your account has been created successfully.</p>
            <div class="spinner-border text-primary mb-3" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
            <p>Redirecting to your dashboard...</p>
        </div>
    `;
    
    // Redirect to dashboard after delay
    setTimeout(redirectToDashboard, 2000);
}

/**
 * Redirect to user dashboard
 */
function redirectToDashboard() {
    window.location.href = '/dashboard.html';
}

/**
 * Handle login form submission
 */
function handleLogin(event) {
    event.preventDefault();
    
    // Get form values
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;
    
    // Show loading state
    const submitButton = this.querySelector('button[type="submit"]');
    const originalButtonText = submitButton.innerHTML;
    submitButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Logging in...';
    submitButton.disabled = true;
    
    // Send login request
    fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            email: email,
            password: password
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.token) {
            // Store auth token
            localStorage.setItem('token', data.token);
            localStorage.setItem('userId', data.userId);
            localStorage.setItem('userEmail', data.email);
            localStorage.setItem('userFirstName', data.firstName);
            
            // Hide modal and redirect
            const loginModal = document.getElementById('loginModal');
            const modalInstance = bootstrap.Modal.getInstance(loginModal);
            modalInstance.hide();
            
            // Redirect to dashboard
            window.location.href = '/dashboard.html';
        } else {
            // Show error message
            const errorDiv = document.querySelector('.login-error');
            errorDiv.textContent = data.message || 'Invalid email or password';
            errorDiv.style.display = 'block';
            
            // Reset button
            submitButton.innerHTML = originalButtonText;
            submitButton.disabled = false;
        }
    })
    .catch(error => {
        console.error('Login error:', error);
        
        // Show error message
        const errorDiv = document.querySelector('.login-error');
        errorDiv.textContent = 'An error occurred. Please try again.';
        errorDiv.style.display = 'block';
        
        // Reset button
        submitButton.innerHTML = originalButtonText;
        submitButton.disabled = false;
    });
}

/**
 * Check if user is authenticated and handle UI accordingly
 */
function checkAuthStatus() {
    const token = localStorage.getItem('token');
    
    if (!token) {
        // No token, user is not logged in
        return;
    }
    
    // Validate token
    fetch(`${API_URL}/auth/validate`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        if (response.ok) {
            // User is logged in
            updateNavForLoggedInUser();
        } else {
            // Token is invalid, clear storage
            clearAuthData();
        }
    })
    .catch(error => {
        console.error('Auth validation error:', error);
        clearAuthData();
    });
}

/**
 * Update navigation for logged-in users
 */
function updateNavForLoggedInUser() {
    const loginLink = document.querySelector('.login-link');
    const signupBtn = document.querySelector('.signup-btn');
    
    if (loginLink) {
        loginLink.textContent = 'Dashboard';
        loginLink.href = '/dashboard.html';
        loginLink.removeAttribute('data-bs-toggle');
        loginLink.removeAttribute('data-bs-target');
    }
    
    if (signupBtn) {
        signupBtn.textContent = 'Logout';
        signupBtn.addEventListener('click', (e) => {
            e.preventDefault();
            logout();
        });
    }
}

/**
 * Logout the current user
 */
function logout() {
    // Clear auth data
    clearAuthData();
    
    // Reload page
    window.location.reload();
}

/**
 * Clear authentication data from local storage
 */
function clearAuthData() {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userFirstName');
}