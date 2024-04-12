import router from './infrastructure/http/router/router.js';
import playerRouter from './infrastructure/http/router/player-router.js';
import services from './infrastructure/http/services/services.js';
import sessionRouter from './infrastructure/http/router/session-router.js';
import gameRouter from './infrastructure/http/router/game-router.js';

const home = 'home';

window.addEventListener('load', loadHandler);
window.addEventListener('hashchange', hashChangeHandler);

function loadHandler() {

  router.addRouteHandler(home, services.getHome);
  router.addRouteHandler('player/:id', playerRouter);
  router.addRouteHandler('session/:id', sessionRouter.handleSessionRoute);
  router.addRouteHandler('sessions', sessionRouter.handleSessionsRoute);
  router.addRouteHandler('game', gameRouter.handleSearchGamesRoute);
  router.addRouteHandler('game/:id', gameRouter.handleGameRoute);
  router.addRouteHandler('games', gameRouter.handleGamesRoute);

  router.addDefaultNotFoundRouteHandler(() => window.location.hash = home);

  hashChangeHandler();
}

/**
 * Handles the hash change event, and calls the appropriate route handler
 * Renders the main content with the new content
 */
function hashChangeHandler() {
  const mainContent = document.getElementById('mainContent');
  let path = window.location.hash.replace('#', '');

  let page = 1;
  const pageIndex = path.indexOf('page=');
  if (pageIndex !== -1) {
    page = parseInt(path.substring(pageIndex + 5));
    path = path.substring(0, pageIndex - 1);
  }

  const handler = router.getRouteHandler(path);
  handler(mainContent, page);
}