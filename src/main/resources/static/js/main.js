/**
 * AI Friend - Main JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {
    // Initialize components
    initThemeToggle();
    initPersonaCards();
    // initDemoChat(); // Removed as demo section was removed
    initOnboarding();
    initNavbarScroll();
    initFormValidation();
    initFeedbackForm();
    initAccessibility();
    
    // Add loading indicators to buttons
    initLoadingIndicators();
    
    // Track analytics (if analytics API is available)
    if (typeof gtag !== 'undefined') {
        trackPageView();
    }
});

/**
 * Initialize theme toggle functionality
 */
function initThemeToggle() {
    const themeToggle = document.getElementById('theme-toggle');
    const prefersDarkScheme = window.matchMedia('(prefers-color-scheme: dark)');
    const currentTheme = localStorage.getItem('theme');
    
    // Set initial theme to light mode by default, or use saved preference if exists
    if (currentTheme === 'dark') {
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
    });
}

/**
 * Initialize persona card selection in signup form and persona section
 */
function initPersonaCards() {
    // Signup form persona cards
    const personaCards = document.querySelectorAll('.persona-card');
    const personaIdInput = document.getElementById('personaId');
    
    personaCards.forEach(card => {
        card.addEventListener('click', function() {
            // Clear previous selection
            personaCards.forEach(c => c.classList.remove('selected'));
            
            // Set new selection
            this.classList.add('selected');
            personaIdInput.value = this.getAttribute('data-persona-id');
        });
    });
    
    // Set default selection
    if (personaCards.length > 0) {
        personaCards[0].classList.add('selected');
    }
    
    // Make persona showcase cards clickable
    const personaShowcaseCards = document.querySelectorAll('.persona-showcase');
    
    personaShowcaseCards.forEach((card, index) => {
        // Add hover effect
        card.style.cursor = 'pointer';
        card.classList.add('transition-transform');
        
        // Add click event
        card.addEventListener('click', function() {
            // Scroll to signup section
            const signupSection = document.getElementById('signup');
            if (signupSection) {
                signupSection.scrollIntoView({ behavior: 'smooth' });
                
                // Pre-select the corresponding persona in the signup form
                setTimeout(() => {
                    const signupPersonaCards = document.querySelectorAll('.persona-selection .persona-card');
                    if (signupPersonaCards[index]) {
                        // Clear previous selection
                        signupPersonaCards.forEach(c => c.classList.remove('selected'));
                        
                        // Set new selection
                        signupPersonaCards[index].classList.add('selected');
                        personaIdInput.value = signupPersonaCards[index].getAttribute('data-persona-id');
                    }
                }, 800); // Delay to allow smooth scrolling to complete
            }
        });
    });
    
    // Make persona card buttons clickable
    const personaButtons = document.querySelectorAll('.persona-showcase .card-footer button');
    
    personaButtons.forEach((button, index) => {
        button.addEventListener('click', function(e) {
            e.stopPropagation(); // Prevent triggering the card click event
            
            // Scroll to signup section
            const signupSection = document.getElementById('signup');
            if (signupSection) {
                signupSection.scrollIntoView({ behavior: 'smooth' });
                
                // Pre-select the corresponding persona in the signup form
                setTimeout(() => {
                    const signupPersonaCards = document.querySelectorAll('.persona-selection .persona-card');
                    if (signupPersonaCards[index]) {
                        // Clear previous selection
                        signupPersonaCards.forEach(c => c.classList.remove('selected'));
                        
                        // Set new selection
                        signupPersonaCards[index].classList.add('selected');
                        personaIdInput.value = signupPersonaCards[index].getAttribute('data-persona-id');
                    }
                }, 800); // Delay to allow smooth scrolling to complete
            }
        });
    });
}

/**
 * Initialize demo chat persona switching
 */
function initDemoChat() {
    const personaToggles = document.querySelectorAll('.persona-toggle');
    const personaName = document.getElementById('demo-persona-name');
    const demoMessage1 = document.getElementById('demo-message-1');
    const demoMessage2 = document.getElementById('demo-message-2');
    const demoMessage3 = document.getElementById('demo-message-3');
    
    // Define demo messages for each persona
    const demoMessages = {
        'friend': {
            name: 'Friend AI',
            messages: [
                "Hey, how's your day going?",
                "I hear you! Remember to take a moment for yourself today. Even a 5-minute break can reset your energy.",
                "That's what I'm here for! I'll check in with you later. 😊"
            ]
        },
        'coach': {
            name: 'Coach AI',
            messages: [
                "Ready to crush your goals today?",
                "Busy days are opportunities to practice prioritization. What's your most important task right now?",
                "Great attitude! Remember: progress over perfection. Let's keep that momentum going!"
            ]
        },
        'philosopher': {
            name: 'Philosopher AI',
            messages: [
                "How are you experiencing the world today?",
                "The busy moments in life often give us the most opportunity for growth and reflection. What can you learn from today's challenges?",
                "Indeed. As Seneca said, 'Life is long if you know how to use it.' I'll send you a thought-provoking question later."
            ]
        }
    };
    
    // Set initial active state
    personaToggles[0].classList.add('active');
    
    // Handle persona switching
    personaToggles.forEach(toggle => {
        toggle.addEventListener('click', function() {
            const persona = this.getAttribute('data-persona');
            
            // Update active state
            personaToggles.forEach(t => t.classList.remove('active'));
            this.classList.add('active');
            
            // Update demo content with animation
            const chatMessages = document.querySelector('.chat-messages');
            chatMessages.style.opacity = 0;
            
            setTimeout(() => {
                // Update content
                personaName.textContent = demoMessages[persona].name;
                demoMessage1.textContent = demoMessages[persona].messages[0];
                demoMessage2.textContent = demoMessages[persona].messages[1];
                demoMessage3.textContent = demoMessages[persona].messages[2];
                
                // Fade back in
                chatMessages.style.opacity = 1;
                chatMessages.style.transition = 'opacity 0.3s ease-in-out';
            }, 300);
        });
    });
}

/**
 * Initialize onboarding modal functionality
 */
function initOnboarding() {
    const onboardingModal = document.getElementById('onboardingModal');
    const carousel = document.getElementById('onboardingCarousel');
    const prevBtn = document.querySelector('.onboarding-prev');
    const nextBtn = document.querySelector('.onboarding-next');
    const doneBtn = document.querySelector('.onboarding-done');
    
    if (!onboardingModal) return;
    
    // Initialize Bootstrap carousel
    const carouselInstance = new bootstrap.Carousel(carousel, {
        interval: false,
        wrap: false
    });
    
    // Handle navigation buttons
    prevBtn.addEventListener('click', () => {
        carouselInstance.prev();
    });
    
    nextBtn.addEventListener('click', () => {
        carouselInstance.next();
    });
    
    doneBtn.addEventListener('click', () => {
        closeOnboardingModal();
    });
    
    // Close modal function
    function closeOnboardingModal() {
        const modalInstance = bootstrap.Modal.getInstance(onboardingModal);
        modalInstance.hide();
        
        // Store that user has seen onboarding
        localStorage.setItem('onboardingCompleted', 'true');
    }
    
    // Update buttons based on slide
    carousel.addEventListener('slide.bs.carousel', event => {
        // Get the number of slides
        const slides = document.querySelectorAll('.carousel-item');
        const slideIndex = event.to;
        
        // Update button visibility
        prevBtn.style.display = slideIndex === 0 ? 'none' : 'inline-block';
        
        if (slideIndex === slides.length - 1) {
            nextBtn.style.display = 'none';
            doneBtn.style.display = 'inline-block';
        } else {
            nextBtn.style.display = 'inline-block';
            doneBtn.style.display = 'none';
        }
    });
    
    // Add keyboard navigation (Escape key closes modal)
    onboardingModal.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            closeOnboardingModal();
        }
    });
    
    // Add click handler for backdrop clicks
    onboardingModal.addEventListener('click', function(event) {
        if (event.target === onboardingModal) {
            closeOnboardingModal();
        }
    });
    
    // Show onboarding on first visit
    const hasSeenOnboarding = localStorage.getItem('onboardingCompleted');
    if (!hasSeenOnboarding && window.location.hash !== '#signup') {
        // Wait for page to load, then show onboarding
        setTimeout(() => {
            const modalInstance = new bootstrap.Modal(onboardingModal);
            modalInstance.show();
        }, 1500);
    }
}

/**
 * Initialize navbar scroll behavior (change background on scroll)
 */
function initNavbarScroll() {
    const navbar = document.querySelector('.navbar');
    
    window.addEventListener('scroll', () => {
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });
}

/**
 * Initialize form validation for signup and login
 */
function initFormValidation() {
    // Fetch all forms that need validation
    const forms = document.querySelectorAll('.needs-validation');
    
    // Add validation behavior
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
    
    // Format phone number as user types
    const phoneInput = document.getElementById('phoneNumber');
    if (phoneInput) {
        phoneInput.addEventListener('input', function(e) {
            const value = e.target.value.replace(/\D/g, '');
            const formattedValue = formatPhoneNumber(value);
            e.target.value = formattedValue;
        });
    }
}

/**
 * Format phone number to (XXX) XXX-XXXX
 */
function formatPhoneNumber(value) {
    if (!value) return value;
    
    const phoneNumber = value.replace(/[^\d]/g, '');
    const phoneNumberLength = phoneNumber.length;
    
    if (phoneNumberLength < 4) return phoneNumber;
    if (phoneNumberLength < 7) {
        return `(${phoneNumber.slice(0, 3)}) ${phoneNumber.slice(3)}`;
    }
    
    return `(${phoneNumber.slice(0, 3)}) ${phoneNumber.slice(3, 6)}-${phoneNumber.slice(6, 10)}`;
}

/**
 * Initialize feedback form
 */
function initFeedbackForm() {
    const feedbackForm = document.getElementById('feedbackForm');
    const feedbackMessage = document.querySelector('.feedback-message');
    
    if (feedbackForm) {
        feedbackForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const type = document.getElementById('feedbackType').value;
            const content = document.getElementById('feedbackContent').value;
            
            // Send feedback to server
            fetch('/api/feedback', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify({
                    type: type,
                    content: content
                })
            })
            .then(response => {
                if (response.ok) {
                    feedbackMessage.classList.add('alert-success');
                    feedbackMessage.textContent = 'Thank you for your feedback!';
                    feedbackMessage.style.display = 'block';
                    
                    // Reset form
                    feedbackForm.reset();
                    
                    // Hide message after delay
                    setTimeout(() => {
                        feedbackMessage.style.display = 'none';
                        
                        // Close modal
                        const modalEl = document.getElementById('feedbackModal');
                        const modal = bootstrap.Modal.getInstance(modalEl);
                        modal.hide();
                    }, 2000);
                } else {
                    throw new Error('Failed to submit feedback');
                }
            })
            .catch(error => {
                feedbackMessage.classList.add('alert-danger');
                feedbackMessage.textContent = 'There was an error submitting your feedback. Please try again.';
                feedbackMessage.style.display = 'block';
            });
        });
    }
}

/**
 * Initialize accessibility features
 */
function initAccessibility() {
    // Add proper ARIA labels to all interactive elements
    const interactiveElements = document.querySelectorAll('button, a, input, select, textarea');
    
    interactiveElements.forEach(element => {
        // Add role where needed
        if (element.tagName === 'A' && !element.getAttribute('href')) {
            element.setAttribute('role', 'button');
        }
        
        // Make sure all buttons have aria-label if they don't have text
        if ((element.tagName === 'BUTTON' || element.getAttribute('role') === 'button') && 
            element.innerText.trim() === '' && !element.getAttribute('aria-label')) {
            
            // Try to find an icon and use its class as label
            const icon = element.querySelector('i.fas, i.fab, i.far');
            if (icon) {
                const iconClass = Array.from(icon.classList)
                    .find(cls => cls.startsWith('fa-'))
                    ?.replace('fa-', '') || 'button';
                element.setAttribute('aria-label', iconClass);
            } else {
                element.setAttribute('aria-label', 'button');
            }
        }
        
        // Add aria-required to required form elements
        if (element.required) {
            element.setAttribute('aria-required', 'true');
        }
    });
    
    // Make all images have proper alt text
    const images = document.querySelectorAll('img:not([alt])');
    images.forEach(img => {
        // Try to generate alt text from image src filename
        const filename = img.src.split('/').pop().split('.')[0].replace(/[-_]/g, ' ');
        img.setAttribute('alt', filename || 'Image');
    });
}

/**
 * Initialize loading indicators for forms
 */
function initLoadingIndicators() {
    const buttons = document.querySelectorAll('button[type="submit"], .signup-submit');
    
    buttons.forEach(button => {
        const form = button.closest('form');
        if (form) {
            form.addEventListener('submit', function() {
                if (form.checkValidity()) {
                    // Add loading state
                    const originalText = button.innerHTML;
                    button.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Loading...';
                    button.disabled = true;
                    
                    // For demo purposes, restore button after 2 seconds
                    // In production, this would happen after form submission completes
                    setTimeout(() => {
                        button.innerHTML = originalText;
                        button.disabled = false;
                    }, 2000);
                }
            });
        }
    });
}

/**
 * Track analytics page view
 */
function trackPageView() {
    // Page view event
    gtag('event', 'page_view', {
        page_title: document.title,
        page_location: window.location.href,
        page_path: window.location.pathname
    });
}

/**
 * Smooth scroll to element when clicking on anchor links
 */
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        const href = this.getAttribute('href');
        
        // Skip links that open modals
        if (this.hasAttribute('data-bs-toggle')) return;
        
        // Handle signup link from login modal
        if (href === '#signup' && this.closest('.modal')) {
            const modalEl = this.closest('.modal');
            const modal = bootstrap.Modal.getInstance(modalEl);
            modal.hide();
        }
        
        // Scroll to target
        if (href !== '#') {
            const targetEl = document.querySelector(href);
            if (targetEl) {
                e.preventDefault();
                targetEl.scrollIntoView({ 
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        }
    });
});