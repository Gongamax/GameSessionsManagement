const routes = [];
let notFoundRouteHandler = () => {
  throw 'Route handler for unknown routes not defined';
};

function addRouteHandler(path, handler) {
  routes.push({ path, handler });
}

function addDefaultNotFoundRouteHandler(notFoundRH) {
  notFoundRouteHandler = notFoundRH;
}

function getRouteHandler(path) {
  const pathSegments = path.split('/');
  const route = routes.find(r => {
    const routeSegments = r.path.split('/');
    if (routeSegments.length !== pathSegments.length) {
      return false;
    }
    for (let i = 0; i < routeSegments.length; i++) {
      if (routeSegments[i].startsWith(':')) {
        continue;
      }
      if (routeSegments[i] !== pathSegments[i]) {
        return false;
      }
    }
    return true;
  });

  return route ? (...args) => route.handler(...args) : notFoundRouteHandler;
}

const router = {
  addRouteHandler,
  getRouteHandler,
  addDefaultNotFoundRouteHandler,
};

export default router;