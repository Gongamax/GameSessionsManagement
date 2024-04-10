export default function GameViewModel(gameService) {
    return {
        getGames: getGames,
        getGame: getGame,
        // createGame: createGame,
    };

    function getGames(developer, genres, skip, limit) {
        return gameService.getGames(developer, genres, skip, limit)
    }

    function getGame(gameId) {
        return gameService.getGame(gameId);
    }

    // function createGame(game) {
    //     return gameService.createGame(game);
    // }
}