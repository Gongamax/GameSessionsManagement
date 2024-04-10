import Router from './router.js';
import GameService from "../services/game-service.js";
import GameViewModel from "../../../ui/viewmodel/GameViewModel.js";


const router = Router;
const gameService = GameService();
const gameViewModel = GameViewModel(gameService);

const notFoundRouteHandler = () => {
    throw 'Route handler for unknown routes not defined';
};


export default function handleGameRoute(mainContent) {
}