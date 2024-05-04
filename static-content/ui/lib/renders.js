import dom from './dom-utils.js';
import Pagination from '../view/components/Pagination.js';

const { li, ul, div, h1, a, br, btn, input, label } = dom;


export default {
  renderPlayerView,
  renderGameView,
  renderSessionView,
  renderGamesView,
  renderSessionsView,
  renderGetHome,
};

function renderGetHome(mainContent) {
  return div(
    h1(mainContent),
    a('#home', 'Go to Home'),
  );
}


function renderPlayerView(player) {

  return div(
    h1('Player'),
    div(
      ul(
        li('Id: ' + player.id),
        li('Name: ' + player.name),
        li('Email: ' + player.email),
      ),
    ),
    br(),
    a(`#sessions?gid=&date=&state=&pid=${player.id}`, 'Player sessions'),
    br(),
    a('#home', 'Go to Home'),
  );
}

function renderGameView(game) {
  return div(
    h1('Game'),
    div(
      ul(
        li('Id: ' + game.gid),
        li('Name: ' + game.name),
        li('Developer: ' + game.developer),
        li('Genres: ' + game.genres),
      ),
      br(),
      a('#sessions?gid=' + game.gid, 'Sessions of the game: ' + game.name),
    ),
    br(),
    a('#home', 'Go to Home'),
  );
}

function renderSessionView(session, deleteSession, updateSession , update = false) {
    if (update === true) {
        return renderSessionUpdate(session.capacity, session.date, updateSession)
    }
    else {
        const playersDiv = session.associatedPlayers.map((player, index) => {
            const playerLink = a(`#player/${player.pid}`, player.name);
            const separator = document.createTextNode(',');
            return index < session.associatedPlayers.length - 1 ? div(playerLink, separator) : div(playerLink);
        });
        const gameLink = a(`#game/${session.gid}`, 'Game: ' + session.gid);
        const playersLabel = div('Associated Players: ', ...playersDiv);
        return div(
            h1('Session'),
            div(
                ul(
                    li('Id: ' + session.sid),
                    li('Number of Players: ' + session.numberOfPlayers),
                    li('Date: ' + session.date),
                    li(gameLink),
                    li(playersLabel),
                    li('Capacity: ' + session.capacity),
                ),
            ),
            br(),
            btn('Delete', deleteSession, false),
            btn('Update', updateSession, false),
            br(),
            br(),
            a('#home', 'Go to Home'),
        );
    }
}

function renderSessionsView(sessions, page, hasNextPage, params) {
  return div(
    h1('Sessions'),
    ul(
      ...sessions.map(session => {
        return li(
          div(
            li(a(`#session/${session.sid}`, `Session ID: ${session.sid}`)),
            ul(
              li('Date: ' + session.date),
              li('Game ID: ' + session.gid),
              li('Capacity: ' + session.capacity),
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
    //go back to search sessions
    a('#session', 'Go to Sessions Search'),
  );
}

function renderGamesView(games, page, hasNextPage, params) {
  if (games.length === 0) {
    return renderGetHome('No games found matching the provided parameters. Please try again.');
  } else {
    return div(
      h1('Games'),
      ul(
        ...games.map(game => {
          return li(
            div(
              li(a(`#game/${game.gid}`, `Game: ${game.name}`)),
              ul(
                li('Id: ' + game.gid),
                li('Developer: ' + game.developer),
                li('Genres: ' + game.genres),
                br(),
              ),
            ),
          );
        }),
      ),
      ...Pagination(page, hasNextPage, newPage => {
        page = newPage;
        window.location.hash = `#games?developer=${params.developer}&genres=${params.genres}&page=${page}`;
      }),
      br(),
      br(),
      a('#game', 'Go to Games Search'),
    );
  }
}

function renderSessionUpdate(capacity, date, updateSession) {
    return div(
        h1('Update Session'),
        div(
            label('Capacity '),
            input('text', 'capacity', capacity),
        ),
        br(),
        div(
            label('Date '),
            input('datetime-local', 'date', date),
        ),
        br(),
        btn('Update', () => {
            const newCapacity = document.querySelector('input[name=capacity]').value || capacity;
            const newDate = document.querySelector('input[name=date]').value || date;
            if (newCapacity <= 0 )
                alert('Capacity must be greater than 0');
            if (newDate <= new Date().toISOString())
                alert('Date must be greater than today');
            else updateSession(newCapacity, newDate);
        }),
        br(),
        br(),
        a('#home', 'Go to Home'),
    );
}