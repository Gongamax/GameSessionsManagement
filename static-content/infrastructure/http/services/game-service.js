import uris from "../uris";
import httpService from "./http-service";

const httpSer = httpService();

export default function GameService() {
    return {
        //createGame: createGame,
        getGames: getGames,
        getGame: getGame
    }

    // function createGame(game) {
    //     return httpService.post(uris.createGame, JSON.stringify(game)).then(res => {
    //         return res.message
    //     }).catch(error => {
    //         return error.detail
    //     })
    // }

    function getGames(developer, genres, skip, limit) {
        return httpSer.get(
            uris.getGames + '?skip=' + skip + '&limit=' + limit,
            JSON.stringify({
                developer: developer,
                genres: genres
            })
        ).then(games => {
            return games.map(game => {
                return new Game(game.gid, game.name, game.developer, game.genres)
            })
        }).catch(error => {
            return error.detail
        })
    }

    function getGame(gameId) {
        return httpSer.get(uris.getGame + gameId).then(game => {
            return new Game(game.gid, game.name, game.developer, game.genres)
        }).catch(error => {
            return error.detail
        })
    }
}
