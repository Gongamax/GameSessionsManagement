import dom from '../../../lib/dom-utils.js';
import { genres } from '../../../../domain/types/game.js';

const { h1, ul, li, div, btn, input, label, br,a,inputWithLabel} = dom;

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
        input('text', 'developer', '')
      ),
      br(),
      div(
        label('Skip (optional) '),
        input('text', 'skip', '')
      ),
      br(),
      div(
        label('Limit (optional) '),
        input('text', 'limit', '')
      ),
      br(),
      br(),
      btn('Search', () => {
        const genres = Array.from(document.querySelectorAll('input[type=checkbox]:checked')).map(checkbox => checkbox.value);
        const developer = document.querySelector('input[name=developer]').value;
        const skip = document.querySelector('input[name=skip]').value ? document.querySelector('input[name=skip]').value : 0;
        const limit = document.querySelector('input[name=limit]').value ? document.querySelector('input[name=limit]').value : 10;
        if(genres.length === 0 || developer === '' || isNaN(skip) || isNaN(limit)){
          alert('Invalid input');
          return;
        }
        window.location.hash = `#games?skip=${skip}&limit=${limit}&developer=${developer}&genres=${genres.join(',')}`;

      }),
    ),
  );

  const home = a('#home', 'Go to Home');
  mainContent.replaceChildren(content, home);
}