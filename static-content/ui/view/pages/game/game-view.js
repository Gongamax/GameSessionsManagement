import dom from "../../../lib/dom-utils.js";

const { h1, ul, li, div ,a} = dom;

export default async function GameView(mainContent, gameViewModel){
    const params = window.location.hash.split('/');
    const gameId = parseInt(params[params.length - 1]);
    if (isNaN(gameId)) {
        return;
    }

    const { gid , name, developer,genres } = await gameViewModel.getGame(gameId);
    console.log(gid,name, developer, genres);

    const content = div(
        h1('Game'),
        div(
            ul(
                li('Id : ' + gid),
                li('Name : ' + name),
                li('Developer : ' + developer),
                li('Genres : ' + genres),
            ),
        ),
    );
    const home = a('#home', 'Go to Home');
    mainContent.replaceChildren(content, home);
}