import dom from '../../../lib/dom-utils.js';

const { h1, div, a, input, btn, label, br } = dom;

export default async function SessionSearchView(mainContent) {
  const content = div(
    h1('Search Sessions'),
    div(
      div(
        label('Game Id '),
        input('text', 'gameId', ''),
      ),
      br(),
      div(
        label('Date '),
        input('datetime-local', 'date', ''),
      ),
      br(),
      div(
        label('State '),
        input('text', 'state', ''),
      ),
      br(),
      div(
        label('Player Id '),
        input('text', 'playerId', ''),
      ),
      br(),
      btn('Search', () => {
          const gameId = document.querySelector('input[name = gameId]').value;
          const date = document.querySelector('input[name = date]').value;
          const state = document.querySelector('input[name = state]').value;
          const playerId = document.querySelector('input[name = playerId]').value;
          if (!gameId || isNaN(gameId)) {
            alert('Please fill the Game Id field');
            return;
          } else if (state && state !== 'open' && state !== 'close') {
            alert('State must be open or close');
            return;
          }
          console.log('Sessions-Search-View: ' + gameId, date, state, playerId);
          window.location.hash = `#sessions?gid=${gameId}&date=${date}&state=${state}&pid=${playerId}`;
        },
      ),
    ),
    br(),
  );

  const home = a('#home', 'Go to Home');
  mainContent.replaceChildren(content, home);
}