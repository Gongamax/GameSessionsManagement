import playerService from './player-service.js';
import sessionService from './session-service.js';
import gameService from './game-service.js';
import dom from '../../../ui/lib/dom-utils.js';

const { h1, p, a, br } = dom;

function getHome(mainContent) {
  const title = h1('Welcome to Sessions App!', mainContent);

  const welcomeMessage = p('We are happy to have you here!', mainContent);

  const description = p('This is a simple app to manage sessions. You can add, edit, and delete sessions.', mainContent);

  const player = a('#player/1', 'Go to Player', mainContent);
  //add a space
  const space = br();

  const session = a('#session/1', 'Go to Session', mainContent);

  const space2 = br();

  const sessions = a('#sessions', 'Go to list of Sessions', mainContent);

  const space3 = br();

  const games = a('#games', 'Go to list of Games', mainContent);

  // Append all elements to the main content
  mainContent.replaceChildren(title, welcomeMessage, description, player, space, session, space2, sessions, space3, games);
}

export const services = {
  getHome,
  sessionService,
  playerService,
  gameService,
};

export default services;