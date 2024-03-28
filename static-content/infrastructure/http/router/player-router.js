import Router from './router';
import PlayerView from '../../../ui/view/pages/PlayerView';
import PlayerHandler from '../handlers/player-handler';
import PlayerViewModel from '../../../ui/viewmodel/PlayerViewModel';

const router = Router;
const playerHandler = PlayerHandler();
const playerViewModel = PlayerViewModel(playerHandler);

const notFoundRouteHandler = () => {
  throw 'Route handler for unknown routes not defined';
}

router.addRouteHandler('/:id', (mainContent) => PlayerView(mainContent, playerViewModel))
router.addDefaultNotFoundRouteHandler(notFoundRouteHandler)

export default router;