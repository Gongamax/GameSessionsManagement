import renders from "../../../lib/renders.js";

export default async function PlayerView(mainContent, playerViewModel) {
  const params = window.location.hash.split('/');
  const playerId = params[params.length - 1];
 if (playerId !== String(parseInt(playerId))) {
    const content = renders.renderGetHome(`Invalid player id, is not a number ${playerId}`);
    mainContent.replaceChildren(content);
    return;
  }

  const { name, email } = await playerViewModel.getPlayer(playerId);

  console.log(name, email);



  const content = renders.renderPlayerView({id: playerId, name, email});

  mainContent.replaceChildren(content);
}