import dom from '../../lib/dom-elements.js';

const { nav, ul, li, a, div } = dom;

function NavBar() {
  const navBar = nav(
    { class: 'navbar navbar-expand-lg bg-dark navbar-dark py-3 sticky-top' },
    div({ class: 'container' },
      a({ class: 'navbar-brand', href: '#' }, 'Home'),
      div(
        { class: 'collapse navbar-collapse' },
        ul(
          { class: 'navbar-nav mr-auto' },
          li({ class: 'nav-item' }, a({ class: 'nav-link', href: '#sessions' }, 'Sessions')),
          li({ class: 'nav-item' }, a({ class: 'nav-link', href: '#games' }, 'Games')),
          li({ class: 'nav-item' }, a({ class: 'nav-link', href: '#about' }, 'About')),
        ),
      ),
    ),
  );

  return navBar;
}

export default NavBar;
