import dom from '../../lib/dom-utils.js';

const { h1, ul, li, div } = dom;

export default async function PlayerView(mainContent, playerViewModel) {
  const params = new URLSearchParams(window.location.search); //Provavelmente há outra maneira mais ideal
  const playerId = parseInt(params.get('pid'));
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