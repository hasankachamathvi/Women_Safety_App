// Main JavaScript File

// Bottom Navigation Active State
document.addEventListener('DOMContentLoaded', function() {
    // Get the current page filename
    const currentPage = window.location.pathname.split('/').pop();
    
    // Get all bottom nav items
    const bottomNavItems = document.querySelectorAll('.bottom-nav-item');
    
    // Loop through nav items and add active class to current page
    bottomNavItems.forEach(item => {
        const href = item.getAttribute('href');
        if (href === currentPage) {
            item.classList.add('active');
        }
    });
});

