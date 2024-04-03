import playerHandler from './player-handler.js';
import dom from '../../../ui/lib/dom-utils.js';

const { h1, p } = dom;

function getHome(mainContent) {
  const title = h1('Welcome to Sessions App!', mainContent);

  const welcomeMessage = p('We are happy to have you here!', mainContent);

  const description = p('This is a simple app to manage sessions. You can add, edit, and delete sessions.', mainContent);

  // Append all elements to the main content
  mainContent.replaceChildren(title, welcomeMessage, description);
}

export const handlers = {
  getHome,
  playerHandler,
};

export default handlers;