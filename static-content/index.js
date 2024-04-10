import router from './infrastructure/http/router/router.js';
import playerRouter from './infrastructure/http/router/player-router.js';
import services from './infrastructure/http/services/services.js';
import sessionRouter from './infrastructure/http/router/session-router.js';

const home = 'home';

window.addEventListener('load', loadHandler);
window.addEventListener('hashchange', hashChangeHandler);

function loadHandler() {

  router.addRouteHandler(home, services.getHome);
  router.addRouteHandler('player/:id', playerRouter);
  router.addRouteHandler('session/:id', sessionRouter.handleSessionRoute);
  router.addRouteHandler('sessions', sessionRouter.handleSessionsRoute);

  router.addDefaultNotFoundRouteHandler(() => window.location.hash = home);

  hashChangeHandler();
}

/**
 * Handles the hash change event, and calls the appropriate route handler
 * Renders the main content with the new content
 */
function hashChangeHandler() {

  const mainContent = document.getElementById('mainContent');
  const path = window.location.hash.replace('#', '');


  const handler = router.getRouteHandler(path);
  handler(mainContent);
}