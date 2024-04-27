import dom from '../../../lib/dom-utils.js';
import { genres } from '../../../../domain/types/game.js';

const { h1, ul, li, div, btn, input, label, br, a, inputWithLabel } = dom;

export default async function GamesSearchView(mainContent) {
  const content = div(
    h1('Games Search'),
    div(
      ul(
        ...genres.map(genre => {
          return li(
            inputWithLabel('checkbox', 'genre', genre, genre),
            br(),
          );
        }),
      ),
      div(
        label('Developer '),
        input('text', 'developer', ''),
      ),
      br(),
      btn('Search', () => {
          let genres = Array.from(document.querySelectorAll('input[type=checkbox]:checked')).map(checkbox => checkbox.value);
          const developer = document.querySelector('input[name=developer]').value;
          window.location.hash = `#games?developer=${developer}&genres=${genres.join(",")}`;
      }),
    ),
    br(),
  );

  const home = a('#home', 'Go to Home');
  mainContent.replaceChildren(content, home);
}