import dom from "../../../lib/dom-utils.js";

const { h1, ul, li, div, checkBoxes, btn, input } = dom;

export default async function GamesSearchView(mainContent){
    const content = div(
        h1('Games Search'),
        div(
            checkBoxes('Rpg',
                'Adventure',
                'Shooter',
                'Turn-Based',
                'Action',
                'Multiplayer',
                'Fighting',
                'Sports',
            ),
            input('text', 'developer', ''),
            input('number', 'skip', undefined),
            input('number', 'limit', undefined),
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