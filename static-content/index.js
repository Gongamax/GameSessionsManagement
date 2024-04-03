import router from './infrastructure/http/router/router.js';
import handlers from './infrastructure/http/handlers/handlers.js';
import playerRouter from './infrastructure/http/router/player-router.js';

window.addEventListener('load', loadHandler);
window.addEventListener('hashchange', hashChangeHandler);

function loadHandler() {

  router.addRouteHandler('', handlers.getHome);
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