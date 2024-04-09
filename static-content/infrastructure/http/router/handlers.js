const API_BASE_URL = "http://localhost:1904/api";

function getHome(mainContent) {

  const h1 = document.createElement('h1');
  const text = document.createTextNode('Home');
  h1.appendChild(text);
  mainContent.replaceChildren(h1);
}


function getPlayer(mainContent) {
  const tokenClient = crypto.randomUUID();
  const options = {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${tokenClient}`,
    },
  };

  fetch("http://localhost:1904/api/player/1", options)
    .then(response => response.json())
    .then(player => {
      const ulPlayer = document.createElement('ul');

      const liName = document.createElement('li');
      const textName = document.createTextNode('Name : ' + player.name);
      liName.appendChild(textName);

      const liNumber = document.createElement('li');
      const textNumber = document.createTextNode('Number : ' + player.email);
      liNumber.appendChild(textNumber);

      ulPlayer.appendChild(liName);
      ulPlayer.appendChild(liNumber);

      mainContent.replaceChildren(ulPlayer);
    }).catch(error => {
    console.error(error);
  });
}

function getSession(mainContent) {
  const tokenClient = crypto.randomUUID();
  const options = {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${tokenClient}`,
    },
  };

  fetch("http://localhost:1904/api/session/1", options)
    .then(response => response.json())
    .then(session => {
      const ulSession = document.createElement('ul');

      const liName = document.createElement('li');
      const textSid = document.createTextNode('SID : ' + session.sid);
      liName.appendChild(textSid);

      const liNumber = document.createElement('li');
      const textDate = document.createTextNode('Date : ' + session.date);
      liNumber.appendChild(textDate);

      const liGid = document.createElement('li');
      const textGid = document.createTextNode('GID : ' + session.gid);
      liGid.appendChild(textGid);

      const liCapacity = document.createElement('li');
      const textCapacity = document.createTextNode('Capacity : ' + session.capacity);
      liCapacity.appendChild(textCapacity);

      const liNumberOfPlayers = document.createElement('li');
      const textNumberOfPlayers = document.createTextNode('Number of Players : ' + session.numberOfPlayers);
      liNumberOfPlayers.appendChild(textNumberOfPlayers);

      ulSession.appendChild(liName);
      ulSession.appendChild(liNumber);
      ulSession.appendChild(liGid);
      ulSession.appendChild(liCapacity);
      ulSession.appendChild(liNumberOfPlayers);

      mainContent.replaceChildren(ulSession);
    });


}

export const handlers = {
  getHome,
  getPlayer,
  getSession
};

export default handlers;