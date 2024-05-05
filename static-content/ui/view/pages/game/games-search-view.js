import dom from '../../../lib/dom-elements.js';
import { genres } from '../../../../domain/types/game.js';

const { h1, ul, li, div, button, input, label, br, a } = dom;

export default function GamesSearchView(mainContent) {
  const handleSearch = () => {
    let genres = Array.from(document.querySelectorAll('input[type=checkbox]:checked')).map(checkbox => checkbox.value);
    const developer = document.querySelector('input[name=developer]').value;
    window.location.hash = `#games?developer=${developer}&genres=${genres.join(',')}`;
  };

  const content = div(
    {},
    h1({}, 'Games Search'),
    div(
      {},
      ul(
        {},
        ...genres.map(genre => {
          return li({}, label({}, genre), input({ type: 'checkbox', name: 'genre', value: genre }), br());
        })
      ),
      div({}, label({}, 'Developer '), input({ type: 'text', name: 'developer', value: '' })),
      br({}),
      button({ type: 'submit' }, 'Search')
    ),
    br()
  );

  const home = a({ href: '#home' }, 'Go to Home');
  mainContent.replaceChildren(content, home);

  mainContent.querySelector('button[type=submit]').addEventListener('click', () => handleSearch());
}
