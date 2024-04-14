import Router from './router.js';
import GameService from '../services/game-service.js';
import GameViewModel from '../../../ui/viewmodel/GameViewModel.js';
import GameView from '../../../ui/view/pages/game/game-view.js';
import GamesView from '../../../ui/view/pages/game/games-view.js';
import SearchGamesView from '../../../ui/view/pages/game/games-search-view.js';
import SessionViewModel from '../../../ui/viewmodel/SessionViewModel.js';
import SessionService from '../services/session-service.js';

const router = Router;
const gameService = GameService();
const sessionService = SessionService();
const gameViewModel = GameViewModel(gameService);
const sessionViewModel = SessionViewModel(sessionService);

const notFoundRouteHandler = () => {
  throw 'Route handler for unknown routes not defined';
};

function handleGameRoute(mainContent) {
  GameView(mainContent, gameViewModel, sessionViewModel);
}

function handleGamesRoute(mainContent, page) {
  GamesView(mainContent, gameViewModel, page);
}

function handleSearchGamesRoute(mainContent) {
  SearchGamesView(mainContent, gameViewModel);
}

export default {
  handleGameRoute,
  handleGamesRoute,
  handleSearchGamesRoute,
  notFoundRouteHandler,
};