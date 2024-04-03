const API_BASE_URL = 'http://localhost:1904/api/';

const getPlayer = `${API_BASE_URL}player/`;
const createPlayer = `${API_BASE_URL}player`;
const getGames = `${API_BASE_URL}games`;
const getGame = `${API_BASE_URL}games/`;
const createGame = `${API_BASE_URL}games`;

export default {
    getPlayer,
    createPlayer,
    getGames,
    getGame,
    createGame
}