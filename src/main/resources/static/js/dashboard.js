/**
 * AI Friend - Dashboard JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {
    // Check authentication
    checkDashboardAuth();
    
    // Initialize components
    initDashboardTheme();
    initSidebar();
    initUserInfo();
    initMessageListeners();
    initSettingsForm();
    initAccountForms();
    initDeleteAccount();
    initUpgradePlan();
    initMessagesChart();
});

/**
 * API base URL
 */
const API_URL = '/api';

/**
 * Check if user is authenticated for dashboard
 */
function checkDashboardAuth() {
    const token = localStorage.getItem('token');
    
    if (!token) {
        // No token, redirect to login
        window.location.href = '/#signup';
        return;
    }
    
    // Show auth checking modal
    const authCheckModal = new bootstrap.Modal(document.getElementById('authCheckModal'));
    authCheckModal.show();
    
    // Validate token
    fetch(`${API_URL}/auth/validate`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        // Hide modal
        authCheckModal.hide();
        
        if (!response.ok) {
            // Token is invalid, redirect to login
            clearAuthData();
            window.location.href = '/#signup';
        }
    })
    .catch(error => {
        console.error('Auth validation error:', error);
        
        // Hide modal
        authCheckModal.hide();
        
        // Redirect to login
        clearAuthData();
        window.location.href = '/#signup';
    });
}

/**
 * Clear authentication data
 */
function clearAuthData() {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userFirstName');
}

/**
 * Initialize dashboard theme
 */
function initDashboardTheme() {
    const themeToggle = document.getElementById('theme-toggle');
    const prefersDarkScheme = window.matchMedia('(prefers-color-scheme: dark)');
    const currentTheme = localStorage.getItem('theme');
    
    // Set initial theme based on local storage or system preference
    if (currentTheme === 'dark' || (!currentTheme && prefersDarkScheme.matches)) {
        document.body.classList.add('dark-mode');
        themeToggle.innerHTML = '<i class="fas fa-sun"></i>';
    } else {
        document.body.classList.add('light-mode');
        themeToggle.innerHTML = '<i class="fas fa-moon"></i>';
    }
    
    // Toggle theme on button click
    themeToggle.addEventListener('click', function() {
        const isDarkMode = document.body.classList.contains('dark-mode');
        
        if (isDarkMode) {
            document.body.classList.replace('dark-mode', 'light-mode');
            localStorage.setItem('theme', 'light');
            themeToggle.innerHTML = '<i class="fas fa-moon"></i>';
        } else {
            document.body.classList.replace('light-mode', 'dark-mode');
            localStorage.setItem('theme', 'dark');
            themeToggle.innerHTML = '<i class="fas fa-sun"></i>';
        }
        
        // Update chart colors if chart exists
        updateChartTheme();
    });
}

/**
 * Initialize sidebar navigation
 */
function initSidebar() {
    const menuItems = document.querySelectorAll('.sidebar-menu-item');
    const sections = document.querySelectorAll('.dashboard-section');
    const navLinks = document.querySelectorAll('.navbar .nav-link');
    
    // Handle sidebar menu clicks
    menuItems.forEach(item => {
        item.addEventListener('click', function() {
            const sectionId = this.getAttribute('data-section');
            
            // Update active state
            menuItems.forEach(i => i.classList.remove('active'));
            this.classList.add('active');
            
            // Show selected section
            sections.forEach(section => {
                section.classList.remove('active');
                if (section.id === sectionId) {
                    section.classList.add('active');
                }
            });
        });
    });
    
    // Handle navbar links
    navLinks.forEach(link => {
        if (link.getAttribute('href').startsWith('#')) {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                
                const sectionId = this.getAttribute('href').substring(1);
                
                // Find corresponding sidebar item
                menuItems.forEach(item => {
                    if (item.getAttribute('data-section') === sectionId) {
                        item.click();
                    }
                });
            });
        }
    });
    
    // Handle logout
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            logout();
        });
    }
}

/**
 * Logout user
 */
function logout() {
    clearAuthData();
    window.location.href = '/';
}

/**
 * Initialize user information
 */
function initUserInfo() {
    const userName = document.getElementById('user-name');
    const welcomeName = document.getElementById('welcome-name');
    const userFirstName = localStorage.getItem('userFirstName');
    
    if (userFirstName) {
        userName.textContent = userFirstName;
        welcomeName.textContent = userFirstName;
    }
    
    // Fetch user data
    fetchUserData()
        .then(user => {
            if (user) {
                // Update user info
                userName.textContent = user.firstName;
                welcomeName.textContent = user.firstName;
                
                // Update plan info
                const userPlan = document.getElementById('user-plan');
                const currentPlanBadge = document.getElementById('current-plan-badge');
                const planDescription = document.getElementById('plan-description');
                
                if (userPlan && currentPlanBadge) {
                    const planDisplayNames = {
                        'FREE': 'Free Plan',
                        'PREMIUM': 'Premium Plan',
                        'UNLIMITED': 'Unlimited Plan'
                    };
                    
                    const planDescriptions = {
                        'FREE': 'Basic access with limited features',
                        'PREMIUM': 'Enhanced features with priority support',
                        'UNLIMITED': 'Full access to all features'
                    };
                    
                    userPlan.textContent = planDisplayNames[user.planType] || 'Free Plan';
                    currentPlanBadge.textContent = user.planType === 'FREE' ? 'Free' : user.planType === 'PREMIUM' ? 'Premium' : 'Unlimited';
                    planDescription.textContent = planDescriptions[user.planType] || planDescriptions['FREE'];
                    
                    // Update plan cards
                    updatePlanCards(user.planType);
                }
                
                // Fill account form
                fillAccountForm(user);
            }
        })
        .catch(error => {
            console.error('Error fetching user data:', error);
        });
        
    // Fetch user preferences
    fetchUserPreferences()
        .then(preferences => {
            if (preferences) {
                // Update persona selection
                updatePersonaSelection(preferences.preferredPersonaId);
                
                // Update message preferences
                updateMessagePreferences(preferences);
            }
        })
        .catch(error => {
            console.error('Error fetching user preferences:', error);
        });
}

/**
 * Fetch user data
 */
function fetchUserData() {
    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');
    
    if (!token || !userId) {
        return Promise.reject('No token or user ID');
    }
    
    return fetch(`${API_URL}/users/${userId}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch user data');
        }
        return response.json();
    });
}

/**
 * Fetch user preferences
 */
function fetchUserPreferences() {
    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');
    
    if (!token || !userId) {
        return Promise.reject('No token or user ID');
    }
    
    return fetch(`${API_URL}/users/${userId}/preferences`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch user preferences');
        }
        return response.json();
    });
}

/**
 * Update persona selection based on user preferences
 */
function updatePersonaSelection(personaId) {
    const personaCards = document.querySelectorAll('.persona-settings .persona-card');
    
    // Clear previous selection
    personaCards.forEach(card => card.classList.remove('selected'));
    
    // Set new selection
    personaCards.forEach(card => {
        if (card.getAttribute('data-persona-id') === personaId.toString()) {
            card.classList.add('selected');
        }
    });
}

/**
 * Update message preferences form
 */
function updateMessagePreferences(preferences) {
    // Set message frequency
    const messageFrequency = document.getElementById('message-frequency');
    if (messageFrequency && preferences.messageFrequency) {
        messageFrequency.value = preferences.messageFrequency;
    }
    
    // Set time window
    try {
        const timeWindow = JSON.parse(preferences.preferredTimeWindow);
        const startTime = document.getElementById('time-window-start');
        const endTime = document.getElementById('time-window-end');
        
        if (startTime && timeWindow.start) {
            startTime.value = timeWindow.start;
        }
        
        if (endTime && timeWindow.end) {
            endTime.value = timeWindow.end;
        }
    } catch (e) {
        console.error('Error parsing time window:', e);
    }
    
    // Set do not disturb
    const doNotDisturb = document.getElementById('do-not-disturb');
    if (doNotDisturb) {
        doNotDisturb.checked = preferences.doNotDisturb || false;
    }
    
    // Set max messages
    const maxMessages = document.getElementById('max-messages');
    const maxMessagesValue = document.getElementById('max-messages-value');
    if (maxMessages && maxMessagesValue && preferences.maxDailyMessages) {
        maxMessages.value = preferences.maxDailyMessages;
        maxMessagesValue.textContent = preferences.maxDailyMessages;
    }
}

/**
 * Fill account form with user data
 */
function fillAccountForm(user) {
    const firstNameInput = document.getElementById('account-first-name');
    const lastNameInput = document.getElementById('account-last-name');
    const emailInput = document.getElementById('account-email');
    const phoneInput = document.getElementById('account-phone');
    
    if (firstNameInput) {
        firstNameInput.value = user.firstName || '';
    }
    
    if (lastNameInput) {
        lastNameInput.value = user.lastName || '';
    }
    
    if (emailInput) {
        emailInput.value = user.email || '';
    }
    
    if (phoneInput) {
        phoneInput.value = user.phoneNumber || '';
    }
}

/**
 * Update plan cards based on current plan
 */
function updatePlanCards(currentPlan) {
    const planCards = document.querySelectorAll('.plan-card');
    
    planCards.forEach(card => {
        const planType = card.getAttribute('data-plan');
        const button = card.querySelector('button');
        
        if (planType === currentPlan) {
            button.textContent = 'Current Plan';
            button.disabled = true;
            button.classList.remove('btn-primary');
            button.classList.add('btn-outline-primary');
        } else {
            button.textContent = 'Upgrade';
            button.disabled = false;
            button.classList.add('btn-primary');
            button.classList.remove('btn-outline-primary');
        }
    });
}

/**
 * Initialize message related listeners and functionality
 */
function initMessageListeners() {
    // Fetch messages
    fetchMessages();
    
    // Initialize message filter
    const filterDropdown = document.querySelector('.dropdown-menu');
    if (filterDropdown) {
        filterDropdown.addEventListener('click', function(e) {
            if (e.target.hasAttribute('data-filter')) {
                e.preventDefault();
                const filter = e.target.getAttribute('data-filter');
                filterMessages(filter);
            }
        });
    }
}

/**
 * Fetch user messages
 */
function fetchMessages() {
    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');
    
    if (!token || !userId) {
        return;
    }
    
    fetch(`${API_URL}/messages?userId=${userId}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch messages');
        }
        return response.json();
    })
    .then(messages => {
        displayMessages(messages);
        updateSentimentChart(messages);
    })
    .catch(error => {
        console.error('Error fetching messages:', error);
        showEmptyState();
    });
}

/**
 * Display user messages in the timeline
 */
function displayMessages(messages) {
    const messageList = document.getElementById('message-list');
    const emptyState = document.querySelector('.message-empty-state');
    
    if (!messages || messages.length === 0) {
        // Show empty state
        messageList.innerHTML = '';
        emptyState.style.display = 'block';
        return;
    }
    
    // Hide empty state
    emptyState.style.display = 'none';
    
    // Clear loading indicator
    messageList.innerHTML = '';
    
    // Sort messages by date (newest first)
    messages.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    
    // Create message elements
    messages.forEach(message => {
        const messageItem = document.createElement('div');
        messageItem.className = 'message-item';
        messageItem.setAttribute('data-id', message.id);
        messageItem.setAttribute('data-sentiment', message.sentiment || 'NEUTRAL');
        
        // Format date
        const messageDate = new Date(message.createdAt);
        const formattedDate = messageDate.toLocaleDateString('en-US', {
            weekday: 'long',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
        
        // Determine sentiment class
        let sentimentClass = 'sentiment-neutral';
        let sentimentText = 'Neutral';
        
        if (message.sentiment === 'POSITIVE') {
            sentimentClass = 'sentiment-positive';
            sentimentText = 'Positive';
        } else if (message.sentiment === 'NEGATIVE') {
            sentimentClass = 'sentiment-negative';
            sentimentText = 'Negative';
        }
        
        // Create message content
        messageItem.innerHTML = `
            <div class="message-date">${formattedDate}</div>
            <div class="message-content">${message.content}</div>
            <div class="message-meta">
                <div class="message-persona">
                    <i class="fas fa-user-circle"></i> ${message.personaName || 'AI Friend'}
                </div>
                <div class="message-sentiment">
                    <span class="sentiment-badge ${sentimentClass}">${sentimentText}</span>
                </div>
            </div>
        `;
        
        // Add click event to view message details
        messageItem.addEventListener('click', function() {
            showMessageDetails(message);
        });
        
        // Add to list
        messageList.appendChild(messageItem);
    });
}

/**
 * Show message details in modal
 */
function showMessageDetails(message) {
    const modal = document.getElementById('messageModal');
    const messageDate = modal.querySelector('.message-date');
    const messageContent = modal.querySelector('.message-content');
    const sentimentBadge = modal.querySelector('.sentiment-badge');
    const personaName = modal.querySelector('.message-persona');
    
    // Format date
    const date = new Date(message.createdAt);
    const formattedDate = date.toLocaleDateString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
    
    // Determine sentiment class
    let sentimentClass = 'sentiment-neutral';
    let sentimentText = 'Neutral';
    
    if (message.sentiment === 'POSITIVE') {
        sentimentClass = 'sentiment-positive';
        sentimentText = 'Positive';
    } else if (message.sentiment === 'NEGATIVE') {
        sentimentClass = 'sentiment-negative';
        sentimentText = 'Negative';
    }
    
    // Update modal content
    messageDate.textContent = formattedDate;
    messageContent.textContent = message.content;
    sentimentBadge.className = `sentiment-badge ${sentimentClass}`;
    sentimentBadge.textContent = sentimentText;
    personaName.textContent = message.personaName || 'AI Friend';
    
    // Show modal
    const modalInstance = new bootstrap.Modal(modal);
    modalInstance.show();
}

/**
 * Show empty state when no messages are available
 */
function showEmptyState() {
    const messageList = document.getElementById('message-list');
    const emptyState = document.querySelector('.message-empty-state');
    
    messageList.innerHTML = '';
    emptyState.style.display = 'block';
}

/**
 * Filter messages by sentiment
 */
function filterMessages(filter) {
    const messageItems = document.querySelectorAll('.message-item');
    
    messageItems.forEach(item => {
        const sentiment = item.getAttribute('data-sentiment');
        
        if (filter === 'all') {
            item.style.display = 'block';
        } else if (filter === 'positive' && sentiment === 'POSITIVE') {
            item.style.display = 'block';
        } else if (filter === 'neutral' && sentiment === 'NEUTRAL') {
            item.style.display = 'block';
        } else if (filter === 'question' && item.querySelector('.message-content').textContent.includes('?')) {
            item.style.display = 'block';
        } else {
            item.style.display = 'none';
        }
    });
}

/**
 * Initialize message preferences form
 */
function initSettingsForm() {
    // Handle persona card selection
    const personaCards = document.querySelectorAll('.persona-settings .persona-card');
    let selectedPersonaId = '1'; // Default selection
    
    personaCards.forEach(card => {
        card.addEventListener('click', function() {
            // Clear previous selection
            personaCards.forEach(c => c.classList.remove('selected'));
            
            // Set new selection
            this.classList.add('selected');
            selectedPersonaId = this.getAttribute('data-persona-id');
        });
    });
    
    // Handle max messages range input
    const maxMessages = document.getElementById('max-messages');
    const maxMessagesValue = document.getElementById('max-messages-value');
    
    if (maxMessages && maxMessagesValue) {
        maxMessages.addEventListener('input', function() {
            maxMessagesValue.textContent = this.value;
        });
    }
    
    // Handle preferences form submission
    const preferencesForm = document.getElementById('message-preferences-form');
    
    if (preferencesForm) {
        preferencesForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const messageFrequency = document.getElementById('message-frequency').value;
            const startTime = document.getElementById('time-window-start').value;
            const endTime = document.getElementById('time-window-end').value;
            const doNotDisturb = document.getElementById('do-not-disturb').checked;
            const maxMessages = document.getElementById('max-messages').value;
            
            // Get selected topics
            const selectedTopics = [];
            document.querySelectorAll('.topics-container input[type="checkbox"]:checked').forEach(checkbox => {
                selectedTopics.push(checkbox.value);
            });
            
            // Create time window JSON
            const timeWindow = JSON.stringify({
                start: startTime,
                end: endTime
            });
            
            // Create notification settings JSON
            const notificationSettings = JSON.stringify({
                sms: true,
                email: false
            });
            
            // Create topics JSON
            const topics = JSON.stringify(selectedTopics);
            
            // Save preferences
            saveUserPreferences({
                preferredPersonaId: selectedPersonaId,
                messageFrequency: messageFrequency,
                doNotDisturb: doNotDisturb,
                maxDailyMessages: maxMessages,
                preferredTimeWindow: timeWindow,
                notificationSettings: notificationSettings,
                interests: topics
            });
        });
    }
}

/**
 * Save user preferences to server
 */
function saveUserPreferences(preferences) {
    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');
    
    if (!token || !userId) {
        return;
    }
    
    // Show loading state
    const submitButton = document.querySelector('#message-preferences-form button[type="submit"]');
    const originalButtonText = submitButton.innerHTML;
    submitButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Saving...';
    submitButton.disabled = true;
    
    fetch(`${API_URL}/users/${userId}/preferences`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(preferences)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to save preferences');
        }
        
        // Show success message
        const successMessage = document.querySelector('.preferences-saved-message');
        successMessage.style.display = 'block';
        
        // Hide success message after delay
        setTimeout(() => {
            successMessage.style.display = 'none';
        }, 3000);
    })
    .catch(error => {
        console.error('Error saving preferences:', error);
        alert('Failed to save preferences. Please try again.');
    })
    .finally(() => {
        // Reset button
        submitButton.innerHTML = originalButtonText;
        submitButton.disabled = false;
    });
}

/**
 * Initialize account forms
 */
function initAccountForms() {
    // Handle personal info form submission
    const personalInfoForm = document.getElementById('personal-info-form');
    
    if (personalInfoForm) {
        personalInfoForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const firstName = document.getElementById('account-first-name').value;
            const lastName = document.getElementById('account-last-name').value;
            const phoneNumber = document.getElementById('account-phone').value;
            
            updateUserInfo({
                firstName: firstName,
                lastName: lastName,
                phoneNumber: phoneNumber
            });
        });
    }
    
    // Handle password change form submission
    const passwordForm = document.getElementById('change-password-form');
    
    if (passwordForm) {
        passwordForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const currentPassword = document.getElementById('current-password').value;
            const newPassword = document.getElementById('new-password').value;
            const confirmPassword = document.getElementById('confirm-password').value;
            
            // Validate passwords match
            if (newPassword !== confirmPassword) {
                alert('New passwords do not match');
                return;
            }
            
            // Change password
            changePassword(currentPassword, newPassword);
        });
    }
}

/**
 * Update user information
 */
function updateUserInfo(userData) {
    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');
    
    if (!token || !userId) {
        return;
    }
    
    // Show loading state
    const submitButton = document.querySelector('#personal-info-form button[type="submit"]');
    const originalButtonText = submitButton.innerHTML;
    submitButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Updating...';
    submitButton.disabled = true;
    
    fetch(`${API_URL}/users/${userId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(userData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to update user information');
        }
        
        // Update local storage
        if (userData.firstName) {
            localStorage.setItem('userFirstName', userData.firstName);
            
            // Update displayed name
            document.getElementById('user-name').textContent = userData.firstName;
            document.getElementById('welcome-name').textContent = userData.firstName;
        }
        
        // Show success message
        const successMessage = document.querySelector('.info-updated-message');
        successMessage.style.display = 'block';
        
        // Hide success message after delay
        setTimeout(() => {
            successMessage.style.display = 'none';
        }, 3000);
    })
    .catch(error => {
        console.error('Error updating user information:', error);
        alert('Failed to update information. Please try again.');
    })
    .finally(() => {
        // Reset button
        submitButton.innerHTML = originalButtonText;
        submitButton.disabled = false;
    });
}

/**
 * Change user password
 */
function changePassword(currentPassword, newPassword) {
    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');
    
    if (!token || !userId) {
        return;
    }
    
    // Show loading state
    const submitButton = document.querySelector('#change-password-form button[type="submit"]');
    const originalButtonText = submitButton.innerHTML;
    submitButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Updating...';
    submitButton.disabled = true;
    
    fetch(`${API_URL}/users/${userId}/password`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
            currentPassword: currentPassword,
            newPassword: newPassword
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to change password');
        }
        
        // Reset form
        document.getElementById('change-password-form').reset();
        
        // Show success message
        const successMessage = document.querySelector('.password-updated-message');
        successMessage.style.display = 'block';
        
        // Hide success message after delay
        setTimeout(() => {
            successMessage.style.display = 'none';
        }, 3000);
    })
    .catch(error => {
        console.error('Error changing password:', error);
        alert('Failed to change password. Please ensure your current password is correct.');
    })
    .finally(() => {
        // Reset button
        submitButton.innerHTML = originalButtonText;
        submitButton.disabled = false;
    });
}

/**
 * Initialize delete account functionality
 */
function initDeleteAccount() {
    const deleteButton = document.getElementById('delete-account-btn');
    
    if (deleteButton) {
        deleteButton.addEventListener('click', function() {
            // Show confirmation modal
            const modal = new bootstrap.Modal(document.getElementById('deleteAccountModal'));
            modal.show();
        });
    }
    
    // Handle confirmation input
    const confirmationInput = document.getElementById('delete-confirmation');
    const confirmButton = document.querySelector('#confirm-delete-form button[type="submit"]');
    
    if (confirmationInput && confirmButton) {
        confirmationInput.addEventListener('input', function() {
            // Enable delete button when input matches "DELETE"
            confirmButton.disabled = this.value !== 'DELETE';
        });
    }
    
    // Handle form submission
    const confirmForm = document.getElementById('confirm-delete-form');
    
    if (confirmForm) {
        confirmForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Delete account
            deleteAccount();
        });
    }
}

/**
 * Delete user account
 */
function deleteAccount() {
    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');
    
    if (!token || !userId) {
        return;
    }
    
    // Show loading state
    const submitButton = document.querySelector('#confirm-delete-form button[type="submit"]');
    const originalButtonText = submitButton.innerHTML;
    submitButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Deleting...';
    submitButton.disabled = true;
    
    fetch(`${API_URL}/users/${userId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to delete account');
        }
        
        // Clear auth data
        clearAuthData();
        
        // Redirect to home
        window.location.href = '/';
    })
    .catch(error => {
        console.error('Error deleting account:', error);
        alert('Failed to delete account. Please try again.');
        
        // Reset button
        submitButton.innerHTML = originalButtonText;
        submitButton.disabled = false;
        
        // Hide modal
        const modal = bootstrap.Modal.getInstance(document.getElementById('deleteAccountModal'));
        modal.hide();
    });
}

/**
 * Initialize upgrade plan functionality
 */
function initUpgradePlan() {
    const upgradeButtons = document.querySelectorAll('.upgrade-plan-btn');
    
    if (upgradeButtons) {
        upgradeButtons.forEach(button => {
            button.addEventListener('click', function() {
                const planType = this.getAttribute('data-plan');
                showUpgradePlanModal(planType);
            });
        });
    }
    
    // Handle payment form submission
    const paymentForm = document.getElementById('payment-form');
    
    if (paymentForm) {
        paymentForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Process upgrade (demo only, just show success)
            const planName = document.getElementById('plan-name').textContent;
            processUpgrade(planName);
        });
    }
}

/**
 * Show upgrade plan modal
 */
function showUpgradePlanModal(planType) {
    const modal = document.getElementById('upgradePlanModal');
    const planName = document.getElementById('plan-name');
    const planPrice = document.getElementById('plan-price');
    const planFeatures = document.getElementById('plan-features-summary');
    
    // Set plan details based on type
    if (planType === 'PREMIUM') {
        planName.textContent = 'Premium';
        planPrice.textContent = '$4.99';
        planFeatures.innerHTML = `
            <ul>
                <li>Up to 5 messages per day</li>
                <li>All persona options</li>
                <li>Advanced customization</li>
                <li>Priority delivery</li>
            </ul>
        `;
    } else if (planType === 'UNLIMITED') {
        planName.textContent = 'Unlimited';
        planPrice.textContent = '$9.99';
        planFeatures.innerHTML = `
            <ul>
                <li>Unlimited messages</li>
                <li>Exclusive personas</li>
                <li>Full customization</li>
                <li>Priority support</li>
                <li>Early access to new features</li>
            </ul>
        `;
    }
    
    // Show modal
    const modalInstance = new bootstrap.Modal(modal);
    modalInstance.show();
}

/**
 * Process plan upgrade (demo only)
 */
function processUpgrade(planName) {
    // Hide upgrade modal
    const upgradeModal = bootstrap.Modal.getInstance(document.getElementById('upgradePlanModal'));
    upgradeModal.hide();
    
    // Update plan name in success modal
    document.querySelector('.upgraded-plan-name').textContent = planName;
    
    // Show success modal
    const successModal = new bootstrap.Modal(document.getElementById('upgradeSuccessModal'));
    successModal.show();
    
    // Update UI to reflect the new plan
    setTimeout(() => {
        const planBadge = document.getElementById('current-plan-badge');
        const userPlan = document.getElementById('user-plan');
        const planDescription = document.getElementById('plan-description');
        
        if (planName === 'Premium') {
            planBadge.textContent = 'Premium';
            userPlan.textContent = 'Premium Plan';
            planDescription.textContent = 'Enhanced features with priority support';
            updatePlanCards('PREMIUM');
        } else if (planName === 'Unlimited') {
            planBadge.textContent = 'Unlimited';
            userPlan.textContent = 'Unlimited Plan';
            planDescription.textContent = 'Full access to all features';
            updatePlanCards('UNLIMITED');
        }
    }, 500);
}

/**
 * Initialize sentiment chart
 */
function initMessagesChart() {
    const ctx = document.getElementById('sentimentChart');
    
    if (!ctx) {
        return;
    }
    
    // Create empty chart initially
    window.sentimentChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label: 'Message Sentiment',
                data: [],
                borderColor: getChartColors().borderColor,
                backgroundColor: getChartColors().backgroundColor,
                tension: 0.3,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    max: 1,
                    ticks: {
                        callback: function(value) {
                            if (value === 0) return 'Negative';
                            if (value === 0.5) return 'Neutral';
                            if (value === 1) return 'Positive';
                            return '';
                        }
                    },
                    grid: {
                        color: getChartColors().gridColor
                    }
                },
                x: {
                    grid: {
                        color: getChartColors().gridColor
                    }
                }
            },
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const value = context.parsed.y;
                            if (value <= 0.3) return 'Negative';
                            if (value <= 0.7) return 'Neutral';
                            return 'Positive';
                        }
                    }
                }
            }
        }
    });
}

/**
 * Update sentiment chart with message data
 */
function updateSentimentChart(messages) {
    if (!window.sentimentChart || !messages || messages.length === 0) {
        return;
    }
    
    // Sort messages by date (oldest first)
    messages.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
    
    // Prepare data for chart
    const labels = [];
    const data = [];
    
    messages.forEach(message => {
        // Format date
        const date = new Date(message.createdAt);
        const formattedDate = date.toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric'
        });
        
        // Map sentiment to value
        let sentimentValue = 0.5; // Neutral
        
        if (message.sentiment === 'POSITIVE') {
            sentimentValue = 1;
        } else if (message.sentiment === 'NEGATIVE') {
            sentimentValue = 0;
        }
        
        labels.push(formattedDate);
        data.push(sentimentValue);
    });
    
    // Update chart data
    window.sentimentChart.data.labels = labels;
    window.sentimentChart.data.datasets[0].data = data;
    
    // Update chart colors
    window.sentimentChart.data.datasets[0].borderColor = getChartColors().borderColor;
    window.sentimentChart.data.datasets[0].backgroundColor = getChartColors().backgroundColor;
    window.sentimentChart.options.scales.y.grid.color = getChartColors().gridColor;
    window.sentimentChart.options.scales.x.grid.color = getChartColors().gridColor;
    
    // Update chart
    window.sentimentChart.update();
}

/**
 * Update chart colors based on theme
 */
function updateChartTheme() {
    if (!window.sentimentChart) {
        return;
    }
    
    const colors = getChartColors();
    
    window.sentimentChart.data.datasets[0].borderColor = colors.borderColor;
    window.sentimentChart.data.datasets[0].backgroundColor = colors.backgroundColor;
    window.sentimentChart.options.scales.y.grid.color = colors.gridColor;
    window.sentimentChart.options.scales.x.grid.color = colors.gridColor;
    
    window.sentimentChart.update();
}

/**
 * Get chart colors based on current theme
 */
function getChartColors() {
    const isDarkMode = document.body.classList.contains('dark-mode');
    
    if (isDarkMode) {
        return {
            borderColor: 'rgba(59, 130, 246, 0.8)',
            backgroundColor: 'rgba(59, 130, 246, 0.2)',
            gridColor: 'rgba(75, 85, 99, 0.2)'
        };
    } else {
        return {
            borderColor: 'rgba(59, 130, 246, 0.8)',
            backgroundColor: 'rgba(59, 130, 246, 0.1)',
            gridColor: 'rgba(229, 231, 235, 0.5)'
        };
    }
}