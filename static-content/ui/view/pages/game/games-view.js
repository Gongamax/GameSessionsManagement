import renders from '../../../lib/renders.js';
import constants from '../../../../domain/constants.js';

async function GamesView(gamesViewModel, page) {
  const params = new URLSearchParams(window.location.hash.split('?')[1]);
  const developer = params.get('developer');
  const limit = constants.limit;
  const skip = (page - 1) * limit;
  const genres = params.get('genres');

  const games = await gamesViewModel.getGames(developer, genres, skip, limit + 1);

  console.log(games)

  if (!games) {
    return renders.renderGetHome('An error occurred while fetching games. Please try again later.');
  }

  const hasNextPage = games.length > limit;
  if (hasNextPage) {
    games.pop();
  }

  return renders.renderGamesView(games, page, hasNextPage, { developer, genres });
}

export default GamesView;
