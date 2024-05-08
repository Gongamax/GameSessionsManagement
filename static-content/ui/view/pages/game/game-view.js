import renders from '../../../lib/renders.js';

async function GameView(gameViewModel) {
  const params = window.location.hash.split('/');
  const gameId = params[params.length - 1];
  if (gameId !== String(parseInt(gameId))) {
    return renders.renderGetHome(`Invalid game ID (${gameId}). Please try again.`);
  }

  const { gid, name, developer, genres } = await gameViewModel.getGame(gameId);
  console.log(gid, name, developer, genres);
  if (!gid) {
    return renders.renderGetHome('An error occurred while fetching the game. Please try again later.');
  }

  return renders.renderGameView({ gid, name, developer, genres });
}

export default GameView;