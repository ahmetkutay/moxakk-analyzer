// Main application JavaScript

// Show notification function
function showNotification(message, type = 'info') {
    const event = new CustomEvent('showMessage', {
        detail: {
            message: message,
            type: type
        }
    });
    document.body.dispatchEvent(event);
}

// Initialize any components that need JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Add event listeners for form submissions
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            // Add any form validation or processing here
            console.log('Form submitted:', form.id || 'unnamed form');
        });
    });

    // Initialize any custom components
    initializeDropdowns();
    initializeModals();
});

// Dropdown initialization
function initializeDropdowns() {
    const dropdowns = document.querySelectorAll('[data-dropdown]');
    dropdowns.forEach(dropdown => {
        const button = dropdown.querySelector('[data-dropdown-toggle]');
        const menu = dropdown.querySelector('[data-dropdown-menu]');

        if (button && menu) {
            button.addEventListener('click', function() {
                menu.classList.toggle('hidden');
            });

            // Close when clicking outside
            document.addEventListener('click', function(e) {
                if (!dropdown.contains(e.target)) {
                    menu.classList.add('hidden');
                }
            });
        }
    });
}

// Modal initialization
function initializeModals() {
    const modalTriggers = document.querySelectorAll('[data-modal-trigger]');
    modalTriggers.forEach(trigger => {
        const modalId = trigger.getAttribute('data-modal-trigger');
        const modal = document.getElementById(modalId);

        if (modal) {
            const closeButtons = modal.querySelectorAll('[data-modal-close]');

            trigger.addEventListener('click', function() {
                modal.classList.remove('hidden');
                document.body.classList.add('overflow-hidden');
            });

            closeButtons.forEach(button => {
                button.addEventListener('click', function() {
                    modal.classList.add('hidden');
                    document.body.classList.remove('overflow-hidden');
                });
            });

            // Close when clicking outside the modal content
            modal.addEventListener('click', function(e) {
                if (e.target === modal) {
                    modal.classList.add('hidden');
                    document.body.classList.remove('overflow-hidden');
                }
            });
        }
    });
}
