import Router from "./router";
import SessionService from '../services/session-service';
import SessionViewModel from '../../../ui/viewmodel/SessionViewModel';
import SessionView from '../../../ui/view/pages/session-view';


const router  = Router;
const sessionHandler = SessionService();
const sessionViewModel = SessionViewModel(sessionHandler);

const notFoundRouteHandler = () => {
    throw 'Route handler for unknown routes not defined';
}

router.addRouteHandler('/:id', (mainContent) => SessionView(mainContent, sessionViewModel))
router.addDefaultNotFoundRouteHandler(notFoundRouteHandler)

export default router;