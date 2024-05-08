const API_BASE_URL = 'http://localhost:1904/api/';

const getPlayer = `${API_BASE_URL}player/`;
const createPlayer = `${API_BASE_URL}player`;
const getGames = `${API_BASE_URL}game`;
const getGame = `${API_BASE_URL}game/`;
const createGame = `${API_BASE_URL}game`;
const createSession = `${API_BASE_URL}session`;
const getSession = `${API_BASE_URL}session/`;
const getSessions = `${API_BASE_URL}session`;
const deleteSession = `${API_BASE_URL}session/`;
const updateSession = `${API_BASE_URL}session/`;
const addPlayerToSession = `${API_BASE_URL}session/`;
// const removePlayerFromSession = `${API_BASE_URL}session/`;
const removePlayerFromSession = (sid, pid) => { //TODO: THINK ABOUT HAVING SOME OF THIS IN FUNCTION TO MAKE IT CLEANER
    return `${API_BASE_URL}session/${sid}/player/${pid}`;
}
const getPlayerByName = `${API_BASE_URL}player/search`;

export default {
  getPlayer,
  createPlayer,
  getGames,
  getGame,
  createGame,
  createSession,
  getSession,
  getSessions,
  deleteSession,
  updateSession,
  addPlayerToSession,
  getPlayerByName,
  removePlayerFromSession,
};