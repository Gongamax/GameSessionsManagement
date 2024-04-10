import dom from '../../lib/dom-utils.js';

const { h1, ul, li, div } = dom;

export default async function PlayerView(mainContent, playerViewModel) {
  const params = window.location.hash.split('/');
  const playerId = parseInt(params[params.length - 1]);
  if (isNaN(playerId)) {
    return;
  }

  const { name, email } = await playerViewModel.getPlayer(playerId);
  console.log(name, email);

  const content = div(
    h1('Player'),
    div(
      ul(
        li('Id : ' + playerId),
        li('Name : ' + name),
        li('Email : ' + email),
      ),
    ),
  );

  mainContent.replaceChildren(content);
}