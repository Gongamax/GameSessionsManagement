import Router from './router.js';

import PlayerService from '../services/player-service.js';
import PlayerViewModel from '../../../ui/viewmodel/PlayerViewModel.js';
import PlayerView from '../../../ui/view/pages/player/player-view.js';
import SignUpView from '../../../ui/view/pages/auth/sign-up-view.js';

const router = Router;
const playerHandler = PlayerService();
const playerViewModel = PlayerViewModel(playerHandler);

const notFoundRouteHandler = () => {
  throw 'Route handler for unknown routes not defined';
};

function handlePlayerRoute(mainContent) {
  PlayerView(mainContent, playerViewModel)
}

function handleSignUpRoute(mainContent) {
  SignUpView(mainContent, playerViewModel);
}

export default {
  handlePlayerRoute,
  handleSignUpRoute,
  notFoundRouteHandler,
}