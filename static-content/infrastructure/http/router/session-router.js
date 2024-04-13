import SessionService from '../services/session-service.js';
import SessionView from '../../../ui/view/pages/session/session-view.js';
import SessionViewModel from '../../../ui/viewmodel/SessionViewModel.js';
import SessionsView from '../../../ui/view/pages/session/sessions-view.js';
import SessionSearchView from "../../../ui/view/pages/session/session-search-view.js";


const sessionHandler = SessionService();
const sessionViewModel = SessionViewModel(sessionHandler);

const notFoundRouteHandler = () => {
  throw 'Route handler for unknown routes not defined';
};

function handleSessionRoute(mainContent) {
  SessionView(mainContent, sessionViewModel);
}

function handleSessionsRoute(mainContent, page) {
  SessionsView(mainContent, sessionViewModel, page);
}

function handleSearchSessionsRoute(mainContent) {
  SessionSearchView(mainContent, sessionViewModel);
}

export default {
  handleSearchSessionsRoute,
  handleSessionRoute,
  handleSessionsRoute,
  notFoundRouteHandler,
}