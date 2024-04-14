import renders from "../../../lib/renders.js";

export default async function GameView(mainContent, gameViewModel){
    const params = window.location.hash.split('/');
    const gameId = parseInt(params[params.length - 1]);
    if (isNaN(gameId)) {
        return;
    }

    const { gid , name, developer,genres } = await gameViewModel.getGame(gameId);
    console.log(gid,name, developer, genres);

    const content = renders.renderGameView({gid, name, developer, genres})

    mainContent.replaceChildren(content);
}