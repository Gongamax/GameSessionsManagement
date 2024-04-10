import uris from '../uris.js';
import HttpService from './http-service.js';
import { Player } from '../../../domain/types/player.js';

const httpHandler = HttpService();

export default function PlayerService() {
  return {
    getPlayer: getPlayer,
    createPlayer: createPlayer,
  };

  function getPlayer(playerId) {
    return httpHandler.get(uris.getPlayer + playerId).then(player => {
      return new Player(playerId, player.name, player.email);
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