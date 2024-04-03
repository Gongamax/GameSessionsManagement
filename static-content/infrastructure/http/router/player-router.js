import Router from './router.js';
import PlayerView from '../../../ui/view/pages/PlayerView.js';
import PlayerHandler from '../handlers/player-handler.js';
import PlayerViewModel from '../../../ui/viewmodel/PlayerViewModel.js';

const router = Router;
const playerHandler = PlayerHandler();
const playerViewModel = PlayerViewModel(playerHandler);

const notFoundRouteHandler = () => {
  throw 'Route handler for unknown routes not defined';
}

router.addRouteHandler('/:id', (mainContent) => PlayerView(mainContent, playerViewModel))
router.addDefaultNotFoundRouteHandler(notFoundRouteHandler)

export default router;