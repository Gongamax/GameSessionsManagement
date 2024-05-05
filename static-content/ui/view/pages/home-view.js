import dom from '../../lib/dom-elements.js';

const { a, br, p, h1 } = dom;

function HomeView(mainContent) {
  const elements = [
    h1({}, 'Welcome to Sessions App!'),
    p({}, 'We are happy to have you here!'),
    p({}, 'This is a simple app to manage sessions. You can add, edit, and delete sessions.'),
    a({ href: '#player/1' }, 'Go to Player'),
    br(),
    a({ href: '#session' }, 'Go to list of Sessions'),
    br({}),
    a({ href: '#game' }, 'Go to list of Games'),
    br(),
    a({ href: '#sign-up' }, 'Sign Up'),
    br(),
  ];

  mainContent.replaceChildren(...elements);
}

export default HomeView;
