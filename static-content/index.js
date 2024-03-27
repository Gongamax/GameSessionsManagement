import router from "./infrastructure/http/router/router.js";
import handlers from "./infrastructure/http/handlers/handlers.js";

// For more information on ES6 modules, see https://www.javascripttutorial.net/es6/es6-modules/ or
// https://www.w3schools.com/js/js_modules.asp

window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)

function loadHandler(){

  router.addRouteHandler("home", handlers.getAStudent)
  router.addRouteHandler("player", handlers.playerHandler.getPlayer(1))
  router.addRouteHandler("students/10", handlers.getStudent)
  router.addDefaultNotFoundRouteHandler(() => window.location.hash = "home")

  hashChangeHandler()
}

function hashChangeHandler(){
  const mainContent = document.getElementById("mainContent")
  const path =  window.location.hash.replace("#", "")

  const handler = router.getRouteHandler(path)
  handler(mainContent)
}