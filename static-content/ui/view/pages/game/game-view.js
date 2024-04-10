import dom from "../../../lib/dom-utils.js";

const { h1, ul, li, div } = dom;

export default async function GameView(mainContent, gameViewModel){
    const params = new URLSearchParams(window.location.search); //Provavelmente há outra maneira mais ideal
    const gameId = parseInt(params.get('gid'));
    if (isNaN(gameId)) {
        return;
    }

    const { name, developer, genres } = await gameViewModel.getGame(gameId);
    console.log(name, developer, genres);

    const content = div(
        h1('Game'),
        div(
            ul(
                li('Id : ' + gameId),
                li('Name : ' + name),
                li('Developer : ' + developer),
                li('Genres : ' + genres),
            ),
        ),
    );
    mainContent.replaceChildren(content);
}