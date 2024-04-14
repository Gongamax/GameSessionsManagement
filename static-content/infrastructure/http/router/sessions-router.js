import sessionRouter from './session-router.js';
import gameRouter from './game-router.js';
import playerRouter from './player-router.js';
import HomeView from '../../../ui/view/pages/home-view.js';

function handleHomeRoute(mainContent) {
  HomeView(mainContent);
}

export default {
  sessionRouter,
  gameRouter,
  playerRouter,
  handleHomeRoute,
};