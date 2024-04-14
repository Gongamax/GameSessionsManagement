import dom from '../../lib/dom-utils.js';

const { a, br, p, h1 } = dom;

function HomeView(mainContent) {
  const elements = [
    h1('Welcome to Sessions App!'),
    p('We are happy to have you here!'),
    p('This is a simple app to manage sessions. You can add, edit, and delete sessions.'),
    a('#player/1', 'Go to Player'),
    br(),
    a('#session', 'Go to list of Sessions'),
    br(),
    a('#game', 'Go to list of Games'),
    br(),
  ];

  mainContent.replaceChildren(...elements);
}

export default HomeView;