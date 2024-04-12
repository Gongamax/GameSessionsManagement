import dom from '../../../lib/dom-utils.js';

const { h1, ul, li, div, a, br, h_number } = dom;

export default async function GamesView(mainContent, gamesViewModel) {
  const params = new URLSearchParams(window.location.hash.split('?')[1]);
  const skip = parseInt(params.get('skip'));
  const limit = parseInt(params.get('limit'));
  const developer = params.get('developer');
  const genres = params.get('genres');
  if (genres === undefined || developer === undefined) {
    return;
  }
  if (isNaN(skip) || isNaN(limit)) {
    return;
  }

  // ALERT:  Error handling required !!!!!!!!!!!!!!!!!!!
  const games = await gamesViewModel.getGames(developer, genres, skip, limit);
  if(games === undefined){
    alert("Error")
    window.location.hash = '#home';
    return;
  }

  console.log(games);

  let content;
  if (games.length === 0) {
    content = div(
      h1('Games'),
      ul(
        li('No games found matching the provided parameters'),
      ));
    const home = a('#home', 'Go to Home');
    mainContent.replaceChildren(content, home);
  } else {
    content = div(
      h1('Games'),
      ul(
        ...games.map(game => {
          return li(
            div(
              li(a(`#game/${game.gid}`, `Game: ${game.name}`)),
              ul(
                li('Id : ' + game.gid),
                li('Developer : ' + game.developer),
                li('Genres : ' + game.genres),
                br(),
              ),
            ),
          );
        }),
      ),
    );
  }

  const home = a('#home', 'Go to Home');
  mainContent.replaceChildren(content, home);
}