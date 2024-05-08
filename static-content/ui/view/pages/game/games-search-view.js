import renders from '../../../lib/renders.js';

export default function GamesSearchView(mainContent) {
  const content = renders.renderGamesSearch();
  mainContent.replaceChildren(content);
  mainContent.querySelector('button[type=submit]').addEventListener('click', () => handleSearch());
}

function handleSearch() {
  let genres = Array.from(document.querySelectorAll('input[type=checkbox]:checked')).map(checkbox => checkbox.value);
  const developer = document.querySelector('input[name=developer]').value;
  window.location.hash = `#games?developer=${developer}&genres=${genres.join(',')}`;
}