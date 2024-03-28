import uris from '../uris';
import HttpHandler from './http-handler';
import dom from '../../../ui/lib/dom-utils';

const httpHandler = HttpHandler();

export default function PlayerHandler() {
  return {
    getPlayer: getPlayer,
    createPlayer: createPlayer,
  };

  function getPlayer(playerId) {
    return httpHandler.get(uris.getPlayer + playerId).then(player => {
      return new Player(player.pid, player.name, player.email);
    }).catch(error => {
      return error.detail;
    });
  }

  function createPlayer(player) {
    return httpHandler.post(uris.createPlayer, JSON.stringify(player)).then(res => {
      return res.message;
    }).catch(error => {
      return error.detail;
    });
  }
}