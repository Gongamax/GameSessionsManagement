import uris from "../uris.js";
import HttpService from './http-service.js';
import { Game } from '../../../domain/types/game.js';

const httpService = HttpService();

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
        return httpService.get(
            uris.getGames + '?skip=' + skip + '&limit=' + limit + '&developer=' + developer + '&genres=' + genres,
        ).then(result => {
            return result.games.map(game => {
                return new Game(game.gid, game.name, game.developer, game.genres)
            })
        }).catch(error => {
            return error.detail
        })
    }

    function getGame(gameId) {
        return httpService.get(uris.getGame + gameId).then(game => {
            return new Game(game.gid, game.name, game.developer, game.genres)
        }).catch(error => {
            return error.detail
        })
    }
}
