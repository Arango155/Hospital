

var body = document.getElementById('body');


var display_Skills = 1;

function darkMode() {

  var body = document.getElementById('body');
  var icon = document.getElementById('icon');
  var icon2 = document.getElementById('icon2');
  var display_Dark = localStorage.getItem('darkMode'); // Get the dark mode preference from localStorage

  if (display_Dark === '1' || display_Dark === null) { // Default to dark mode if not previously set
    body.classList.add('dark');
    icon.classList.remove('bi-moon');
    icon.classList.add('bi-sun');
    icon2.classList.remove('bi-moon');
    icon2.classList.add('bi-sun');

    display_Dark = '0';
  } else if (display_Dark === '0') {
    body.classList.remove('dark');
    icon2.classList.remove('bi-sun');
    icon2.classList.add('bi-moon');
    icon.classList.add('bi-moon');
    icon.classList.remove('bi-sun');

    display_Dark = '1';
  }

  localStorage.setItem('darkMode', display_Dark); // Save the dark mode preference to localStorage
}


function openMenu() {
  document.getElementById('menu').style.width = '200px';
}

function closeMenu() {
  document.getElementById('menu').style.width = '0';
}





