import uris from '../uris.js';
import HttpHandler from './http-handler.js';
import { Player } from '../../../domain/types/player.js';

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