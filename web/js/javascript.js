
const translatebutton = document.getElementById('translate');
var codingskills = document.getElementById('codingskills');
var softskills = document.getElementById('softskills');
var body = document.getElementById('body');
var web = document.getElementById('web');
var industrial = document.getElementById('industrial');

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

function showWeb() {


  web.style.display = 'block';
  industrial.style.display = 'none';
  button7.classList.remove('btn-primary');
  button3.classList.remove('btn-light');
  button3.classList.add('btn-primary');
  button7.classList.add('btn-light');



}


function showIndustrial() {

  web.style.display = 'none';
  industrial.style.display = 'block';
  button3.classList.remove('btn-primary');
  button3.classList.add('btn-light');

  button7.classList.remove('btn-light');
  button7.classList.add('btn-primary');



}





const tooltipTriggerList = document.querySelectorAll(
  '[data-bs-toggle="tooltip"]'
);
const tooltipList = [...tooltipTriggerList].map(
  (tooltipTriggerEl) => new bootstrap.Tooltip(tooltipTriggerEl)
);


const buttons = document.querySelectorAll('button');


buttons.forEach(button => {
  button.addEventListener('click', function (event) {
    // Prevent the default 
    event.preventDefault();

  });
});




function showCodeSkills() {


  button2.classList.add('btn-light')
  codingskills.style.display = 'flex';
  softskills.style.display = 'none';
  button2.classList.remove('btn-primary');
  button.classList.remove('btn-light')
  button.classList.add('btn-primary')


}

function showSoftSkills() {


  button.classList.add('btn-light')
  button2.classList.remove('btn-light')
  softskills.style.display = 'flex';
  codingskills.style.display = 'none';
  button2.classList.add('btn');
  button2.classList.add('btn-primary');
  button.classList.remove('btn-primary');


}



function openMenu() {
  document.getElementById('menu').style.width = '200px';
}

function closeMenu() {
  document.getElementById('menu').style.width = '0';
}

fetch('navigation.html')
  .then(response => response.text())
  .then(content => {
    document.getElementById('nav').innerHTML = content;
  });



