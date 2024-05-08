import dom from '../../lib/dom-elements.js';

const { nav, ul, li, a, div } = dom;

function NavBar() {
  // const currentUser = useCurrentUser();

  const navBar = nav(
    { class: 'navBar' },
    a({ href: '#' }, 'Home'),
    a({ href: '#sessions' }, 'Sessions'),
    a({ href: '#games' }, 'Games'),
    a({ href: '#about' }, 'About'),
  );

  return navBar;
}

export default NavBar;
