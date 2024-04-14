function Game(gid, name, developer, genres) {
    this.gid = gid;
    this.name = name;
    this.developer = developer;
    this.genres = genres;
}

function GameInput(name, developer, genres) {
    this.name = name;
    this.developer = developer;
    this.genres = genres;
}


function Genres(genres){
    this.genres = genres;
}

const genres = ['Rpg', 'Adventure', 'Shooter', 'Turn-Based', 'Action', 'Multiplayer', 'Fighting', 'Sports'];

export { Game, GameInput, Genres, genres };