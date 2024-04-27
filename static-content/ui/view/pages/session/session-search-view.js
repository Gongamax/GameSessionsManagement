import dom from '../../../lib/dom-utils.js';

const { h1, div, a, input, btn, label, br, select, option } = dom;

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
        select('state', [
         option('', 'All'),
         option('open', 'Open'),
         option('close', 'Close'),
       ]),
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
          const state = document.querySelector('select[name = state]').value;
          const playerId = document.querySelector('input[name = playerId]').value;
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