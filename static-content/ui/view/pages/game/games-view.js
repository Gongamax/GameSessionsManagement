import dom from '../../../lib/dom-utils.js';
import Pagination from '../../components/Pagination.js';

const { h1, ul, li, div, a, br } = dom;

export default async function GamesView(mainContent, gamesViewModel, page) {
  const params = new URLSearchParams(window.location.hash.split('?')[1]);
  const developer = params.get('developer');
  const limit = 5;
  const skip = (page - 1) * limit;
  const genres = params.get('genres');

  if (!genres || !developer) {
    mainContent.replaceChildren(div('Invalid parameters provided'));
    return;
  }

  const games = await gamesViewModel.getGames(developer, genres, skip, limit + 1);

  console.log(games);

  if (games === undefined) {
    mainContent.replaceChildren(div('An error occurred while fetching games'));
    return;
  }

  const hasNextPage = games.length > limit;
  if (hasNextPage) {
    games.pop();
  }

  let content;
  if (games.length === 0) {
    content = div(
      h1('Games'),
      ul(
        li('No games found matching the provided parameters'),
      ));
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
  const paginationButtons = Pagination(page, hasNextPage, newPage => {
    page = newPage;
    window.location.hash = `#games?developer=${developer}&genres=${genres}&page=${page}`;
  });

  mainContent.replaceChildren(content, ...paginationButtons, br(), home);
}