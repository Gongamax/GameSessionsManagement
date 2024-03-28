import router from "./infrastructure/http/router/router.js";
import handlers from "./infrastructure/http/handlers/handlers.js";
import playerRouter from "./infrastructure/http/router/player-router.js";

// For more information on ES6 modules, see https://www.javascripttutorial.net/es6/es6-modules/ or
// https://www.w3schools.com/js/js_modules.asp

window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)

function loadHandler(){

  router.addRouteHandler("", handlers.getHome)
  router.addRouteHandler("players", playerRouter)
  router.addDefaultNotFoundRouteHandler(() => window.location.hash = "")

  hashChangeHandler()
}

function hashChangeHandler(){
  const mainContent = document.getElementById("mainContent")
  const path =  window.location.hash.replace("#", "")

  const handler = router.getRouteHandler(path)
  handler(mainContent)
}