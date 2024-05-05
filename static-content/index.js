import router from './infrastructure/http/router/router.js';
import sessionsRouter from './infrastructure/http/router/sessions-router.js';

const home = 'home';

window.addEventListener('load', loadHandler);
window.addEventListener('hashchange', hashChangeHandler);

function loadHandler() {
  router.addRouteHandler(home, sessionsRouter.handleHomeRoute);
  router.addRouteHandler('player/:id', sessionsRouter.playerRouter.handlePlayerRoute);
  router.addRouteHandler('sign-up', sessionsRouter.playerRouter.handleSignUpRoute);

  router.addRouteHandler('session', sessionsRouter.sessionRouter.handleSearchSessionsRoute);
  router.addRouteHandler('session/:id', sessionsRouter.sessionRouter.handleSessionRoute);
  router.addRouteHandler('sessions', sessionsRouter.sessionRouter.handleSessionsRoute);

  router.addRouteHandler('game', sessionsRouter.gameRouter.handleSearchGamesRoute);
  router.addRouteHandler('game/:id', sessionsRouter.gameRouter.handleGameRoute);
  router.addRouteHandler('games', sessionsRouter.gameRouter.handleGamesRoute);

  router.addDefaultNotFoundRouteHandler(() => (window.location.hash = home));

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
