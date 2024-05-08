import dom from './dom-elements.js';
import Pagination from '../view/components/Pagination.js';
import { genres } from '../../domain/types/game.js';

const { form, li, ul, div, h1, a, br, input, label, button } = dom;

export default {
  renderPlayerView,
  renderGameView,
  renderSessionView,
  renderGamesView,
  renderSessionsView,
  renderGetHome,
  renderGamesSearch,
  renderGameCreate,
};

function createListItem(key, value) {
  return li({}, key + ': ' + value);
}

function renderGetHome(mainContent) {
  return div({ id: 'home' }, h1({ id: 'getHome' }, mainContent), a({ href: '#home' }, 'Go to Home'));
}

function renderPlayerView(player) {
  return div(
    {},
    h1({}, 'Player'),
    div({},
      ul({},
        createListItem('Id', player.id),
        createListItem('Name', player.name),
        createListItem('Email', player.email),
      ),
    ),
    br({}),
    a({ href: `#sessions?gid=&date=&state=&pid=${player.id}` }, 'Player sessions'),
    br({}),
    a({ href: '#home' }, 'Go to Home'),
  );
}

function renderGameView(game) {
  return div(
    {},
    h1({}, 'Game'),
    div(
      {},
      ul(
        {},
        createListItem('Id', game.gid),
        createListItem('Name', game.name),
        createListItem('Developer', game.developer),
        createListItem('Genres', game.genres),
      ),
      br(),
      a({ href: '#sessions?gid=' + game.gid }, 'Sessions of the game: ' + game.name),
    ),
    br(),
    a({ href: '#home' }, 'Go to Home'),
  );
}

function renderGamesSearch() {
  return div(
    {},
    h1({}, 'Games Search'),
    div(
      {},
      ul(
        {},
        ...genres.map(genre => {
          return li({}, label({}, genre), input({ type: 'checkbox', name: 'genre', value: genre }), br());
        }),
      ),
      div({}, label({}, 'Developer '), input({ type: 'text', name: 'developer', value: '' })),
      br({}),
      button({ type: 'submit' }, 'Search'),
    ),
    br(),
    a({ href: '#home' }, 'Go to Home'),
  );
}

function renderGameCreate() {
  return div({},
    h1({}, 'Create Game'),
    form({ id: 'game' }, [
      label({ htmlFor: 'name' }, 'Name: '),
      input({ type: 'text', id: 'name', name: 'name' }),
      br(),
      label({ htmlFor: 'developer' }, 'Developer: '),
      input({ type: 'text', id: 'developer', name: 'developer' }),
      br(),
      ...genres.map(genre => {
        return li({}, label({}, genre), input({ type: 'checkbox', name: 'genre', value: genre }), br());
      }),

      br({}),
      button({ type: 'submit' }, 'Create Game'),
      br({}),
    ]),
    a({ href: '#home' }, 'Go to Home'),
  );
}

function renderSessionView(session, update = false) {
  if (update === true) {
    console.log('Capacity: ' + session.capacity + ' Date: ' + session.date);
    return renderSessionUpdate(session.capacity, session.date);
  } else {
    const playersDiv = session.associatedPlayers.map((player, index) => {
      const playerLink = a({ href: `#player/${player.pid}` }, player.name);
      const separator = document.createTextNode(',');
      return index < session.associatedPlayers.length - 1 ? div({}, playerLink, separator) : div({}, playerLink);
    });
    const gameLink = a({ href: `#game/${session.gid}` }, 'Game: ' + session.gid);
    const playersLabel = div({}, 'Associated Players: ', ...playersDiv);
    return div(
      {},
      h1({}, 'Session'),
      div(
        {},
        ul(
          {},
          createListItem('Id', session.sid),
          createListItem('Number of Players', session.numberOfPlayers),
          createListItem('Date', session.date),
          li({}, gameLink),
          li({}, playersLabel),
          createListItem('Capacity', session.capacity),
        ),
      ),
      br(),
      button({ type: 'submit', id: 'delete' }, 'Delete'),
      button({ type: 'button', id: 'update' }, 'Update'),
      br(),
      br(),
      div({}, label({}, 'PlayerName: '), input({ type: 'text', name: 'playerName', value: '' })),
      br(),
      button({ type: 'submit', id: 'addPlayer' }, 'Add Player'),
      br(),
    );
  }
}

function renderSessionsView(sessions, page, hasNextPage, params) {
  return div(
    {},
    h1({}, 'Sessions'),
    ul(
      {},
      ...sessions.map(session => {
        return li(
          {},
          div(
            {},
            li({}, a({ href: `#session/${session.sid}` }, `Session ID: ${session.sid}`)),
            ul(
              {},
              createListItem('Date', session.date),
              createListItem('Game ID', session.gid),
              createListItem('Capacity', session.capacity),
              br(),
            ),
          ),
        );
      }),
    ),
    br(),
    ...Pagination(page, hasNextPage, newPage => {
      page = newPage;
      window.location.hash = `#sessions?gid=${params.gid}&date=${params.date}&state=${params.state}&pid=${params.pid}&page=${page}`;
    }),
    br(),
    br(),
    a({ href: '#session' }, 'Go to Sessions Search'),
  );
}

function renderGamesView(games, page, hasNextPage, params) {
  if (games.length === 0) {
    return renderGetHome('No games found matching the provided parameters. Please try again.');
  } else {
    return div(
      {},
      h1({}, 'Games'),
      ul(
        {},
        ...games.map(game => {
          return li(
            {},
            div(
              {},
              li({}, a({ href: `#game/${game.gid}` }, `Game: ${game.name}`)),
              ul(
                {},
                createListItem('Id', game.gid),
                createListItem('Developer', game.developer),
                createListItem('Genres', game.genres),
                br({}),
              ),
            ),
          );
        }),
      ),
      ...Pagination(page, hasNextPage, newPage => {
        page = newPage;
        window.location.hash = `#games?developer=${params.developer}&genres=${params.genres}&page=${page}`;
      }),
      br({}),
      br({}),
      a({ href: '#game' }, 'Go to Games Search'),
    );
  }
}

function renderSessionUpdate(capacity, date) {
  return div(
    {},
    h1({}, 'Update Session'),
    div({}, label({}, 'Capacity '), input({ type: 'text', name: 'capacity', value: capacity })),
    br({}),
    div({}, label({}, 'Date '), input({ type: 'datetime-local', name: 'date', value: date })),
    br({}),
    button({ type: 'button', id: 'cancel' }, 'Cancel'),
    button({ type: 'submit', id: 'update' }, 'Update'),
  );
}
