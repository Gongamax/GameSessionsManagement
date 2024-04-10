import dom from "../../lib/dom-utils";

const { h1, ul, li, div, a } = dom;

export default async function GamesView(mainContent, gamesViewModel){
    const params = new URLSearchParams(window.location.search); //Provavelmente há outra maneira mais ideal
    const developer = params.get('developer');
    const genres = params.get('genres');
    const skip = parseInt(params.get('skip'));
    const limit = parseInt(params.get('limit'));
    if (isNaN(skip) || isNaN(limit)) {
        return;
    }

    const games = await gamesViewModel.getGames(developer, genres, skip, limit);
    console.log(games);

    const content = div(
        h1('Games'),
        ul(
            games.map(game => {
                return li(
                    div(
                        h1(a('game/' + game.gid, game.name)),
                        ul(
                            li('Developer : ' + game.developer),
                            li('Genres : ' + game.genres),
                        ),
                    ),
                );
            }),
        ),
    );
    mainContent.replaceChildren(content);
}