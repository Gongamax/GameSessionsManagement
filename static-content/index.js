import router from './infrastructure/http/router/router.js';
import handlers from './infrastructure/http/router/handlers.js';



window.addEventListener('load', loadHandler);
window.addEventListener('hashchange', hashChangeHandler);

function loadHandler() {

 // router.addRouteHandler('home', services.getHome);
//  router.addRouteHandler('players', playerRouter);
 // router.addRouteHandler('sessions', sessionRouter);
  router.addRouteHandler('home', handlers.getHome);
  router.addRouteHandler('player/1',handlers.getPlayer)
  router.addRouteHandler('session/1',handlers.getSession)


  router.addDefaultNotFoundRouteHandler(() => window.location.hash = "home")

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