function toggleTheme() {
    const body = document.body;
    const icon = document.querySelector('#theme-toggle i');
    
    if (body.classList.contains('light-mode')) {
        body.classList.remove('light-mode');
        body.classList.add('dark-mode');
        icon.classList.remove('fa-moon');
        icon.classList.add('fa-sun');
        localStorage.setItem('theme', 'dark');
    } else {
        body.classList.remove('dark-mode');
        body.classList.add('light-mode');
        icon.classList.remove('fa-sun');
        icon.classList.add('fa-moon');
        localStorage.setItem('theme', 'light');
    }
}

// Check for saved theme preference
document.addEventListener('DOMContentLoaded', () => {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
        document.body.classList.remove('light-mode');
        document.body.classList.add('dark-mode');
        document.querySelector('#theme-toggle i').classList.replace('fa-moon', 'fa-sun');
    }
}); 