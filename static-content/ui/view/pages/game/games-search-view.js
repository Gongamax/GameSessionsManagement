import renders from '../../../lib/renders.js';

async function GamesSearchView() {
  const content = renders.renderGamesSearch();
  content.querySelector('button[type=submit]').addEventListener('click', () => handleSearch());
  return content;
}

export default GamesSearchView;

function handleSearch() {
  let genres = Array
    .from(document.querySelectorAll('input[type=checkbox]:checked'))
    .map(checkbox => checkbox.value);
  const developer = document.querySelector('input[name=developer]').value;
  window.location.hash = `#games?developer=${developer}&genres=${genres.join(',')}`;
}