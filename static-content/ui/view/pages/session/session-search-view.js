import dom from '../../../lib/dom-utils.js';

const { h1, ul, div, a, input, btn } = dom;

export default async function SessionSearchView(mainContent) {
    const content = div(
        h1('Search Sessions'),
        div(
            ul(
                input('text', 'Game Id', ''),
                input('date', 'Date', ''),
                input('text', 'State', ''),
                input('text', 'Player Id', ''),
            ),
            btn('Search', () => {
                    const gameId = document.querySelector('input[type="text"]').value;
                    const date = document.querySelector('input[type="date"]').value;
                    const state = document.querySelector('input[type="text"]').value;
                    const playerId = document.querySelector('input[type="text"]').value;
                    console.log(gameId, date, state, playerId);
                    window.location.hash = `#sessions?gid=${gameId}&date=${date}&state=${state}&pid=${playerId}`;
                }
            )
        ),
    );

    const home = a('#home', 'Go to Home');
    mainContent.replaceChildren(content, home);
}