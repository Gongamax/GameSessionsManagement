import uris from '../uris.js';
import HttpService from './http-service.js';
import { Player } from '../../../domain/types/player.js';

const httpHandler = HttpService();

export default function PlayerService() {
  return {
    getPlayer: getPlayer,
    createPlayer: createPlayer,
    getPlayerByName: getPlayerByName,
  };

  function getPlayer(playerId) {
    return httpHandler.get(uris.getPlayer + playerId).then(player => {
      return new Player(playerId, player.name, player.email);
    }).catch(error => {
      return error.detail;
    });
  }

  function getPlayerByName(playerName) {
    return httpHandler.get(uris.getPlayerByName + '?name=' + playerName).then(players => {
      if (players.length > 0) {
        const player = players[0];
        return new Player(player.pid, player.name, player.email);
      } else {
        return undefined;
      }
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