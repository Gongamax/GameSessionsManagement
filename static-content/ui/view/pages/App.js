import dom from '../../lib/dom-utils.js';
import Router from '../../../infrastructure/http/router/router.js';
import HomeView from './home-view.js';
import PlayerView from './player/player-view.js';
import NavBar from '../components/NavBar.js';

const { h1, ul, li, div, nav, a } = dom;

const router = Router;


router.addRouteHandler('/', HomeView);
router.addRouteHandler('/players', PlayerView);

async function App(content) {
  return div(
    NavBar(content),

  );
}

export default App;