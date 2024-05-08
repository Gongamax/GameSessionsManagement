// Very simple view model that just passes through to the player handler for now
export default function PlayerViewModel(playerHandler) {
  return {
    getPlayer: getPlayer,
    createPlayer: createPlayer,
    getPlayerByName: getPlayerByName
  };

  function getPlayer(playerId) {
    return playerHandler.getPlayer(playerId);
  }

  function createPlayer(player) {
    return playerHandler.createPlayer(player);
  }

  function getPlayerByName(playerName) {
    return playerHandler.getPlayerByName(playerName);
  }
}