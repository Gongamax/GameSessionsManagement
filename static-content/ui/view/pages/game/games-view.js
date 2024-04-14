import dom from '../../../lib/dom-utils.js';
import renders from "../../../lib/renders.js";

const { div } = dom;

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
    const content = renders.renderGetHome('An error occurred while fetching games. Please try again later.')
    mainContent.replaceChildren(content);
    return;
  }

  const hasNextPage = games.length > limit;
  if (hasNextPage) {
    games.pop();
  }

  const content = renders.renderGamesView(games, page, hasNextPage, { developer, genres });

  mainContent.replaceChildren(content);
}