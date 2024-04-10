import Router from "./router.js";
import SessionService from '../services/session-service.js';
import SessionView from '../../../ui/view/pages/session-view.js';
import SessionViewModel from '../../../ui/viewmodel/SessionViewModel.js';


const router  = Router;
const sessionHandler = SessionService();
const sessionViewModel = SessionViewModel(sessionHandler);

const notFoundRouteHandler = () => {
    throw 'Route handler for unknown routes not defined';
}

function handleSessionRoute(mainContent){
    SessionView(mainContent,sessionViewModel);
}

export default handleSessionRoute;