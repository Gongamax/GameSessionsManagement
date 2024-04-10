import Router from './router.js';

import PlayerService from '../services/player-service.js';
import PlayerViewModel from '../../../ui/viewmodel/PlayerViewModel.js';
import PlayerView from '../../../ui/view/pages/player/player-view.js';

const router = Router;
const playerHandler = PlayerService();
const playerViewModel = PlayerViewModel(playerHandler);

const notFoundRouteHandler = () => {
  throw 'Route handler for unknown routes not defined';
};

// router.addRouteHandler(':id', (mainContent) => PlayerView(mainContent, playerViewModel));
// router.addDefaultNotFoundRouteHandler(notFoundRouteHandler);

function handlePlayerRoute(mainContent) {
  PlayerView(mainContent, playerViewModel);
}

export default handlePlayerRoute;