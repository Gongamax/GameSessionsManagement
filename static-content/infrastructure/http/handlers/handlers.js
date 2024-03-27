import playerHandler from './player-handler';

const API_BASE_URL = 'http://localhost:9000/';

function getHome(mainContent) {

  const h1 = document.createElement('h1');
  const text = document.createTextNode('Home');
  h1.appendChild(text);
  mainContent.replaceChildren(h1);
}

function getStudents(mainContent) {
  fetch(API_BASE_URL + 'students')
    .then(res => res.json())
    .then(students => {
      const div = document.createElement('div');

      const h1 = document.createElement('h1');
      const text = document.createTextNode('Students');
      h1.appendChild(text);
      div.appendChild(h1);

      students.forEach(s => {
        const p = document.createElement('p');
        const a = document.createElement('a');
        const aText = document.createTextNode('Link Example to students/' + s.number);
        a.appendChild(aText);
        a.href = '#students/' + s.number;
        p.appendChild(a);
        div.appendChild(p);
      });
      mainContent.replaceChildren(div);
    });
}

function getStudent(mainContent) {
  fetch(API_BASE_URL + 'students/10')
    .then(res => res.json())
    .then(student => {
      const ulStd = document.createElement('ul');

      const liName = document.createElement('li');
      const textName = document.createTextNode('Name : ' + student.name);
      liName.appendChild(textName);

      const liNumber = document.createElement('li');
      const textNumber = document.createTextNode('Number : ' + student.number);
      liNumber.appendChild(textNumber);

      ulStd.appendChild(liName);
      ulStd.appendChild(liNumber);

      mainContent.replaceChildren(ulStd);
    });
}

async function getAStudent(mainContent) {
  const a = await handlers.playerHandler.getPlayer(1)
  const text = document.createTextNode(a.name);
  mainContent.replaceChildren(text);
}

export const handlers = {
  getHome,
  getStudent,
  getStudents,
  playerHandler,
  getAStudent
};

export default handlers;