import renders from '../../../lib/renders.js';
import dom from '../../../lib/dom-elements.js';

const { div, button, input, label, br} = dom;

export default async function SessionView(mainContent, sessionViewModel, playerViewModel) {
  const params = window.location.hash.split('/');
  const query = new URLSearchParams(window.location.hash.split('?')[1]);
  const sessionId = params[params.length - 1].split('?')[0];
  const update = query.get('update') !== null ? query.get('update') : '';


  if (sessionId !== String(parseInt(sessionId))) {
    const content = renders.renderGetHome(`Invalid session id, is not a number ${sessionId}`);
    mainContent.replaceChildren(content);
    return;
  }

  const { sid, numberOfPlayers, date, gid, associatedPlayers, capacity } = await sessionViewModel.getSession(sessionId);

  async function updateSession(newCapacity, newDate) {
    await sessionViewModel.updateSession(sid, newCapacity, newDate);
    window.location.hash = `#session/${sid}`;
  }


  if (update === 'true') {
    const content = renders.renderSessionView(
      { sid, numberOfPlayers, date, gid, associatedPlayers, capacity },
      true
    );
    mainContent.replaceChildren(content);
    const cancelButton = mainContent.querySelector('#cancel');
    cancelButton.addEventListener('click', () => {
        window.location.hash = `#session/${sid}`;
    });
    const updateButton = mainContent.querySelector('#update');
    updateButton.addEventListener('click', () => {
      const newCapacity = document.querySelector('input[name=capacity]').value || capacity;
      const newDate = document.querySelector('input[name=date]').value || date;
      if (newCapacity <= 0) {
        alert('Capacity must be greater than 0');
        return;
      }
      if (newDate <= new Date().toISOString()) {
        alert('Date must be greater than now');
      }
      else updateSession(newCapacity, newDate);
    });
  } else {
    if (sid === undefined) {
      const content = renders.renderGetHome('An error occurred while fetching the session. Please try again later.');
      mainContent.replaceChildren(content);
      return;
    }

    async function deleteSession(sid) {
      await sessionViewModel.deleteSession(sid);
      window.location.hash = '#sessions';
    }

    const content = renders.renderSessionView({ sid, numberOfPlayers, date, gid, associatedPlayers, capacity });

    async function addPlayerToSession(sid, pid) {
      const addPlayer = await sessionViewModel.addPlayerToSession(sid, pid);
      console.log(addPlayer);
      if (addPlayer === 'HTTP error! Status: 400') {
        alert('Player already in session or session is full');
      } else {
        alert('Player added to session');
        location.reload();
      }
    }

    mainContent.replaceChildren(content);
    const deleteButton = mainContent.querySelector('#delete');
    deleteButton.addEventListener('click', () => {
      deleteSession(sid);
    });
    const updateButton = mainContent.querySelector('#update');
    updateButton.addEventListener('click', () => {
      window.location.hash = `#session/${sid}?update=true`;
    });
    const addPlayer = mainContent.querySelector('#addPlayer');
    addPlayer.addEventListener('click', async () => {
      const playerName = document.querySelector('input[name=playerName]').value;
      if (playerName === '') {
        alert('Player name is required');
        return;
      }
      const player = await playerViewModel.getPlayerByName(playerName);
      if (player === undefined) {
        alert('Player not found');
      } else {
        await addPlayerToSession(sid, player.pid);
      }
    });
  }
}