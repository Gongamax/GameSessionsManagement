import dom from './dom-elements.js';
import Pagination from '../view/components/Pagination.js';

const { li, ul, div, h1, a, br, input, label, button } = dom;

export default {
  renderPlayerView,
  renderGameView,
  renderSessionView,
  renderGamesView,
  renderSessionsView,
  renderGetHome,
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
    div({}, ul({}, li({}, 'Id: ' + player.id), li({}, 'Name: ' + player.name), li({}, 'Email: ' + player.email))),
    br({}),
    a({ href: `#sessions?gid=&date=&state=&pid=${player.id}` }, 'Player sessions'),
    br({}),
    a({ href: '#home' }, 'Go to Home')
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
        createListItem('Genres', game.genres)
      ),
      br(),
      a({ href: '#sessions?gid=' + game.gid }, 'Sessions of the game: ' + game.name)
    ),
    br(),
    a({ href: '#home' }, 'Go to Home')
  );
}

function renderSessionView(session, deleteSession, updateSession, update = false) {
  if (update === true) {
    return renderSessionUpdate(session.capacity, session.date, updateSession);
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
          createListItem('Capacity', session.capacity)
        )
      )
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
              br()
            )
          )
        );
      })
    ),
    br(),
    ...Pagination(page, hasNextPage, newPage => {
      page = newPage;
      window.location.hash = `#sessions?gid=${params.gid}&date=${params.date}&state=${params.state}&pid=${params.pid}&page=${page}`;
    }),
    br(),
    br(),
    a({ href: '#session' }, 'Go to Sessions Search')
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
                br({})
              )
            )
          );
        })
      ),
      ...Pagination(page, hasNextPage, newPage => {
        page = newPage;
        window.location.hash = `#games?developer=${params.developer}&genres=${params.genres}&page=${page}`;
      }),
      br({}),
      br({}),
      a({ href: '#game' }, 'Go to Games Search')
    );
  }
}

function renderSessionUpdate(capacity, date, updateSession) {
  return div(
    {},
    h1({}, 'Update Session'),
    div({}, label({}, 'Capacity '), input({ type: 'text', name: 'capacity', value: capacity })),
    br({}),
    div({}, label({}, 'Date '), input({ type: 'datetime-local', name: 'date', value: date })),
    br({}),
    button({}, 'Update', () => {
      const newCapacity = document.querySelector('input[name=capacity]').value || capacity;
      const newDate = document.querySelector('input[name=date]').value || date;
      if (newCapacity <= 0) alert('Capacity must be greater than 0');
      if (newDate <= new Date().toISOString()) alert('Date must be greater than today');
      else updateSession(newCapacity, newDate);
    }),
    br({}),
    br({}),
    a({ href: '#home' }, 'Go to Home')
  );
}
