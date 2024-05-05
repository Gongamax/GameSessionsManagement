import dom from '../../lib/dom-elements.js';

const { nav, ul, li, a, div } = dom;

function NavBar(content) {
  return div(
    {},
    nav(
      {},
      ul(
        {},
        li({}, a({ href: '#' }, 'Home')),
        li({}, a({ href: '#players' }, 'Players')),
        li(
          {},
          a({ href: '#sessions' }, 'Sessions'),
          li({}, a({ href: '#games' }, 'Games')),
          li({}, a({ href: '#about' }, 'About'))
        )
      )
    )
  );
}

export default NavBar;
