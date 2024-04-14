import renders from "../../../lib/renders.js";

export default async function PlayerView(mainContent, playerViewModel) {
  const params = window.location.hash.split('/');
  const playerId = parseInt(params[params.length - 1]);
  if (isNaN(playerId)) {
    return;
  }

  const { name, email } = await playerViewModel.getPlayer(playerId);
  console.log(name, email);

  const content = renders.renderPlayerView({id: playerId, name, email})

  mainContent.replaceChildren(content);
}