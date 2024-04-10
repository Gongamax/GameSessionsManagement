import dom from "../../../lib/dom-utils.js";

const { h1, ul, li, div, btn, input } = dom;

export default async function GamesSearchView(mainContent){
    const genres = ['Rpg', 'Adventure', 'Shooter', 'Turn-Based', 'Action', 'Multiplayer', 'Fighting', 'Sports'];
    const content = div(
        h1('Games Search'),
        div(
            ul(
                genres.map(genre => li(input('checkbox', 'genre', genre, genre))),
            ),
            input('text', 'developer', ''),
            input('text', 'skip', ''),
            input('text', 'limit', ''),
            btn('Search', () => {
                const genres = Array.from(document.querySelectorAll('input[type=checkbox]:checked')).map(checkbox => checkbox.value);
                const developer = document.querySelector('input[name=developer]').value;
                const skip = document.querySelector('input[name=skip]').value;
                const limit = document.querySelector('input[name=limit]').value;
                window.location.hash = `games?genres=${genres.join(',')}&developer=${developer}&skip=${skip}&limit=${limit}`;
            }),
        ),
    ) ;
    mainContent.replaceChildren(content);
}