import router from './infrastructure/http/router/router.js';
import services from './infrastructure/http/services/services.js';
import playerRouter from './infrastructure/http/router/player-router.js';

window.addEventListener('load', loadHandler);
window.addEventListener('hashchange', hashChangeHandler);

function loadHandler() {

  router.addRouteHandler('', services.getHome);
  router.addRouteHandler('players', playerRouter);
  router.addDefaultNotFoundRouteHandler(() => window.location.hash = '');

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