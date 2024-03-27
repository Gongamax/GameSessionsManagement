import uris from '../uris';
import HttpHandler from './http-handler';
import dom from '../../../ui/lib/dom-utils';

const httpHandler = HttpHandler();

export default function PlayerHandler(mainContent) {
  return {
    getPlayer: getPlayer,
    createPlayer: createPlayer,
  };

  function getPlayer(playerId) {
    httpHandler.get(uris.getPlayer + playerId).then(player => {
      console.log(player);
      const ulPlayer = dom.ul(
        dom.li('Player'),
        dom.li('Id : ' + player.pid),
        dom.li('Name : ' + player.name),
        dom.li('Number : ' + player.number)
      );
      mainContent.replaceChildren(ulPlayer);
      return new Player(player.pid, player.name, player.number);
    });
  }

  function createPlayer(player) {
    httpHandler.post(uris.createPlayer, player).then(() => {
      const h1 = document.createElement('h1');
      const text = document.createTextNode('Player Created');
      h1.appendChild(text);
      mainContent.replaceChildren(h1);
    });
  }
}