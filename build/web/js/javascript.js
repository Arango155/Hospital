function darkMode() {
  var body = document.body; // Get the body element directly

  // Get the icon elements
  var icon = document.getElementById('icon');
  var icon2 = document.getElementById('icon2'); // Assuming you have an element with id "icon2"

  // Get the dark mode preference from localStorage
  var displayDark = localStorage.getItem('darkMode');

  if (displayDark === '1' || displayDark === null) { // Default to dark mode if not previously set
    body.classList.add('dark');
    if (icon) {
      icon.classList.remove('bi-moon');
      icon.classList.add('bi-sun');
    }
    if (icon2) {
      icon2.classList.remove('bi-moon');
      icon2.classList.add('bi-sun');
    }

    displayDark = '0';
  } else if (displayDark === '0') {
    body.classList.remove('dark');
    if (icon2) {
      icon2.classList.remove('bi-sun');
      icon2.classList.add('bi-moon');
    }
    if (icon) {
      icon.classList.add('bi-moon');
      icon.classList.remove('bi-sun');
    }

    displayDark = '1';
  }

  localStorage.setItem('darkMode', displayDark); // Save the dark mode preference to localStorage
}

function openMenu() {
  document.getElementById('menu').style.width = '200px';
}

function closeMenu() {
  document.getElementById('menu').style.width = '0';
}

// Remove the fetch call for 'navigation.html' as it's not relevant to dark mode toggling
