import dom from '../../lib/dom-utils.js';

const { nav, ul, li, a, div } = dom;

function NavBar(content) {
  return div(
    nav(
      ul(
        li(a('Home', '#')),
        li(a('Players', '#players')),
        li(a('Sessions', '#sessions'),
          li(a('Games', '#games')),
          li(a('About', '#about')),
        ),
      ),
    )
  );
}

export default NavBar;