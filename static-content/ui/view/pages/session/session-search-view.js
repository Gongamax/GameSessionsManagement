import dom from '../../../lib/dom-elements.js';

const { h1, div, a, input, button, label, br, select, option } = dom;

export default function SessionSearchView(mainContent) {
  const handleSearch = () => {
    const gameId = document.querySelector('input[name = gameId]').value;
    const date = document.querySelector('input[name = date]').value;
    const state = document.querySelector('select[name = state]').value;
    const playerId = document.querySelector('input[name = playerId]').value;
    console.log(gameId, date, state, playerId);
    window.location.hash = `#sessions?gid=${gameId}&date=${date}&state=${state}&pid=${playerId}`;
  };

  const content = div(
    {},
    h1({}, 'Search Sessions'),
    div(
      {},
      div({}, label({}, 'Game Id '), input({ type: 'text', name: 'gameId', value: '' })),
      br(),
      div({}, label({}, 'Date '), input({ type: 'datetime-local', name: 'date', value: '' })),
      br(),
      div(
        {},
        label({}, 'State '),
        select({ name: 'state' }, [
          option({ value: '' }, 'All'),
          option({ value: 'open' }, 'Open'),
          option({ value: 'close' }, 'Close'),
        ])
      ),
      br(),
      div({}, label({}, 'Player Id '), input({ type: 'text', name: 'playerId', value: '' })),
      br(),
      button({ type: 'submit' }, 'Search')
    ),
    br()
  );

  const home = a({ href: '#home' }, 'Go to Home');
  mainContent.replaceChildren(content, home);

  mainContent.querySelector('button[type=submit]').addEventListener('click', handleSearch);
}
