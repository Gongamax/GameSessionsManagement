import dom from './dom-utils.js';
import Pagination from "../view/components/Pagination.js";

const { li, ul, div, h1, a, br } = dom;

export default{
    renderPlayerView,
    renderGameView,
    renderSessionView,
    renderGamesView,
    renderSessionsView,
    renderGetHome
}

function renderGetHome(mainContent){
    return div(
      h1(mainContent),
      a('#home', 'Go to Home')
    )
}


function renderPlayerView(player){
    return div(
        h1('Player'),
        div(
            ul(
                li('Id: ' + player.id),
                li('Name: ' + player.name),
                li('Email: ' + player.email),
            ),
        ),
        a('#home', 'Go to Home')
    );
}

function renderGameView(game){
    return div(
        h1('Game'),
        div(
            ul(
                li('Id: ' + game.gid),
                li('Name: ' + game.name),
                li('Developer: ' + game.developer),
                li('Genres: ' + game.genres),
            ),
        ),
        a('#home', 'Go to Home')
    );
}

function renderSessionView(session){
    const playersDiv = session.associatedPlayers.map((player, index) => {
        const playerLink = a(`#player/${player.pid}`, player.name);
        const separator = document.createTextNode(',');
        return index < session.associatedPlayers.length - 1 ? div(playerLink, separator) : div(playerLink);
    });

    const playersLabel = div('Associated Players: ', ...playersDiv);
    return div(
        h1('Session'),
        div(
            ul(
                li('Id: ' + session.sid),
                li('Number of Players: ' + session.numberOfPlayers),
                li('Date: ' + session.date),
                li('Game: ' + session.gid),
                li(playersLabel),
                li('Capacity: ' + session.capacity),
            ),
        ),
        a('#home', 'Go to Home')
    );
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
        a('#home', 'Go to Home')
    );
}

function renderGamesView(games, page, hasNextPage, params) {
    if (games.length === 0) {
        return renderGetHome('No games found matching the provided parameters. Please try again.')
    } else {
        return  div(
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
            a('#home', 'Go to Home')
        );
    }

}