import renders from '../../../lib/renders.js';

async function PlayerView(playerViewModel) {
  const params = window.location.hash.split('/');
  const playerId = params[params.length - 1];
  if (playerId !== String(parseInt(playerId))) {
    return renders.renderGetHome(`Invalid player id, is not a number ${playerId}`);
  }

  const player = await playerViewModel.getPlayer(playerId);
  if (!player) {
    return renders.renderGetHome('An error occurred while fetching the player. Please try again later.');
  }

  return renders.renderPlayerView({ id : playerId, ...player});
}

export default PlayerView;