import dom from '../../../lib/dom-utils.js';

const { h1, ul, li, div, a } = dom;

export default async function SessionView(mainContent, sessionViewModel) {
  const params = window.location.hash.split('/');
  const sessionId = parseInt(params[params.length - 1]);
  if (isNaN(sessionId)) {
    return;
  }

  const { sid, numberOfPlayers, date, gid, associatedPlayers, capacity } = await sessionViewModel.getSession(sessionId);

  const playersDiv = associatedPlayers.map((player, index) => {
    const playerLink = a(`#player/${player.pid}`, player.name);
    const separator = document.createTextNode(', ');
    return index < associatedPlayers.length - 1 ? div(playerLink, separator) : div(playerLink);
  });

  const playersLabel = div('Associated Players: ', ...playersDiv);

  const content = div(
    h1('Session'),
    div(
      ul(
        li('Id : ' + sid),
        li('Number of Players: ' + numberOfPlayers),
        li('Date: ' + date),
        li('Game: ' + gid),
        li(playersLabel),
        li('Capacity: ' + capacity),
      ),
    ),
  );

  const home = a('#home', 'Go to Home');
  mainContent.replaceChildren(content, home);
}