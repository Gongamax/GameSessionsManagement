import dom from '../../lib/dom-elements.js';

const { a, p, h1, h2, div, img } = dom;

async function HomeView() {
  return div(
    { class: 'bg-dark text-light p-5 p-lg-0 pt-lg-5 text-center text-sm-start' },
    div({ class: 'container' },
      div({ class: 'd-sm-flex align-items-center justify-content-between' },
        div({},
          h1({}, 'Welcome to the Ultimate Sessions App!'),
          p({ class: 'lead my-4' }, 'Dive into the world of gaming sessions. Discover, join, or create sessions for your favorite games. Unleash your gaming potential with us.'),
          a({ class: 'btn btn-primary btn-lg', href: '#sign-up' }, 'Start Your Journey'),
        ),
        img({ class: 'img-fluid w-50 d-none d-sm-block', src: '/images/sessions.png', alt: '' }),
      ),
    ),
    div(
      { class: 'bg-dark text-dark p-5' },
      div({ class: 'container mt-5 text-center' },
        h2({ class: 'text-light' }, 'Ready to Level Up?'),
        p({ class: 'lead text-light' }, 'Join our community of gamers. Find sessions that match your interests, or create new ones and invite others. Your next gaming adventure awaits.'),
        a({ class: 'btn btn-primary btn-lg', href: '#sign-up' }, 'Join Now'),
      ),
    ),
  );
}

export default HomeView;
