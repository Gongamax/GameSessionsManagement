import playerService from './player-service.js';
import dom from '../../../ui/lib/dom-utils.js';
import playerRouter from '../router/player-router.js';

const { h1, p, a } = dom;

function getHome(mainContent) {
  const title = h1('Welcome to Sessions App!', mainContent);

  const welcomeMessage = p('We are happy to have you here!', mainContent);

  const description = p('This is a simple app to manage sessions. You can add, edit, and delete sessions.', mainContent);

  const player = a('#player/1' , 'Go to Player', mainContent);

  // Append all elements to the main content
  mainContent.replaceChildren(title, welcomeMessage, description,player);
}

export const services = {
  getHome,
  playerService,
};

export default services;