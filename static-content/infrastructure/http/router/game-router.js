import Router from './router.js';
import GameService from "../services/game-service.js";
import GameViewModel from "../../../ui/viewmodel/GameViewModel.js";
import GameView from "../../../ui/view/pages/game/game-view.js";
import GamesView from '../../../ui/view/pages/game/games-view.js';
import SearchGamesView from '../../../ui/view/pages/game/games-search-view.js';


const router = Router;
const gameService = GameService();
const gameViewModel = GameViewModel(gameService);

const notFoundRouteHandler = () => {
    throw 'Route handler for unknown routes not defined';
};


function handleGameRoute(mainContent) {
    GameView(mainContent, gameViewModel)
}

function handleGamesRoute(mainContent, page) {
    GamesView(mainContent, gameViewModel, page)
}
function handleSearchGamesRoute(mainContent) {
    SearchGamesView(mainContent, gameViewModel)
}

export default{
    handleGameRoute,
    handleGamesRoute,
    handleSearchGamesRoute,
    notFoundRouteHandler,
}