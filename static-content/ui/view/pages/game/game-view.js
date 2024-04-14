import renders from '../../../lib/renders.js';

export default async function GameView(mainContent, gameViewModel, sessionViewModel) {
  const params = window.location.hash.split('/');
  const gameId = params[params.length - 1];
  if (gameId !== String(parseInt(gameId))) {
    const content = renders.renderGetHome(`Invalid game ID (${gameId}). Please try again.`);
    mainContent.replaceChildren(content);
    return;
  }

  const { gid, name, developer, genres } = await gameViewModel.getGame(gameId);
  console.log(gid, name, developer, genres);
  if (gid === undefined) {
    const content = renders.renderGetHome('An error occurred while fetching the game. Please try again later.');
    mainContent.replaceChildren(content);
    return;
  }

  const sessionsOfGame = await sessionViewModel.getSessions(gid, undefined, undefined, undefined, 0, 5);
  if (!sessionsOfGame) {
    const content = renders.renderGetHome('An error occurred while fetching the sessions of the game. Please try again later.');
    mainContent.replaceChildren(content);
    return;
  }

  const content = renders.renderGameView({ gid, name, developer, genres }, sessionsOfGame);

  mainContent.replaceChildren(content);
}