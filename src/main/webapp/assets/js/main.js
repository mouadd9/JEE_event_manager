// Main JavaScript file for Event Management System

$(document).ready(function() {
    console.log('Event Management System - Ready!');

    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
});

// Utility functions
const EventManagement = {
    // Format date
    formatDate: function(dateString) {
        const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
        return new Date(dateString).toLocaleDateString('fr-FR', options);
    },

    // Show notification
    showNotification: function(message, type = 'info') {
        // Implementation will be added later
        console.log(`[${type.toUpperCase()}] ${message}`);
    },

    // Confirm action
    confirmAction: function(message) {
        return confirm(message);
    }
};
