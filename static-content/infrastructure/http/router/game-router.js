import Router from './router.js';
import GameService from '../services/game-service.js';
import GameViewModel from '../../../ui/viewmodel/GameViewModel.js';
import GameView from '../../../ui/view/pages/game/game-view.js';
import GamesView from '../../../ui/view/pages/game/games-view.js';
import SearchGamesView from '../../../ui/view/pages/game/games-search-view.js';
import SessionViewModel from '../../../ui/viewmodel/SessionViewModel.js';
import SessionService from '../services/session-service.js';
import CreateGameView from '../../../ui/view/pages/game/game-create-view.js';

const gameService = GameService();
const sessionService = SessionService();
const gameViewModel = GameViewModel(gameService);
const sessionViewModel = SessionViewModel(sessionService);

const notFoundRouteHandler = () => {
  throw 'Route handler for unknown routes not defined';
};

function handleGameRoute() {
  return GameView(gameViewModel, sessionViewModel);
}

function handleGamesRoute(page) {
  return GamesView(gameViewModel, page);
}

function handleSearchGamesRoute() {
  return SearchGamesView(gameViewModel);
}

function handleCreateGameRoute() {
  return CreateGameView(gameViewModel);
}

export default {
  handleGameRoute,
  handleCreateGameRoute,
  handleGamesRoute,
  handleSearchGamesRoute,
  notFoundRouteHandler,
};