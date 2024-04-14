import router from "../infrastructure/http/router/router.js";
import services from "../infrastructure/http/services/services.js";
import sessionRouter from "../infrastructure/http/router/session-router.js";
import gameRouter from "../infrastructure/http/router/game-router.js";
import renders from "../ui/lib/renders.js";

describe('Views Test', function () {

    before(function () {
        router.addRouteHandler('home', services.getHome);
        router.addRouteHandler('session', sessionRouter.handleSearchSessionsRoute);
        router.addRouteHandler('game', gameRouter.handleSearchGamesRoute);
    });

    it('should show home view', () => {
       let mainContent = document.createElement('div');
       services.getHome(mainContent);
       mainContent.innerHTML.should.be.equal(
           '<h1>Welcome to Sessions App!</h1>' +
           '<p>We are happy to have you here!</p>' +
           '<p>This is a simple app to manage sessions. You can add, edit, and delete sessions.</p>' +
           '<a href="#player/1">Go to Player</a><br>' +
           '<a href="#session/1">Go to Session</a><br>' +
           '<a href="#session">Go to list of Sessions</a><br>' +
           '<a href="#game">Go to list of Games</a><br>' +
           '<a href="#game/1">Go to Game</a>'
       );
    });

    it('should show sessions search view', () => {
        let mainContent = document.createElement('div');
        sessionRouter.handleSearchSessionsRoute(mainContent);
        mainContent.innerHTML.should.be.equal(
            '<div><h1>Search Sessions</h1>' +
            '<div>' +
            '<div><label>Game Id </label>' +
            '<input type="text" name="gameId" value="">' +
            '</div><br>' +
            '<div><label>Date </label>' +
            '<input type="datetime-local" name="date" value="">' +
            '</div><br>' +
            '<div><label>State </label>' +
            '<input type="text" name="state" value="">' +
            '</div><br>' +
            '<div><label>Player Id </label>' +
            '<input type="text" name="playerId" value="">' +
            '</div><br>' +
            '<button>Search</button>' +
            '</div><br></div>' +
            '<a href="#home">Go to Home</a>'
        );
    });

    it('should show games search view', () => {
        let mainContent = document.createElement('div');
        gameRouter.handleSearchGamesRoute(mainContent);
        mainContent.innerHTML.should.be.equal('<div>' +
            '<h1>Games Search</h1>' +
            '<div><ul>' +
            '<li><label>Rpg<input type="checkbox" name="genre" value="Rpg"></label></li>' +
            '<li><label>Adventure<input type="checkbox" name="genre" value="Adventure"></label></li>' +
            '<li><label>Shooter<input type="checkbox" name="genre" value="Shooter"></label></li>' +
            '<li><label>Turn-Based<input type="checkbox" name="genre" value="Turn-Based"></label></li>' +
            '<li><label>Action<input type="checkbox" name="genre" value="Action"></label></li>' +
            '<li><label>Multiplayer<input type="checkbox" name="genre" value="Multiplayer"></label></li>' +
            '<li><label>Fighting<input type="checkbox" name="genre" value="Fighting"></label></li>' +
            '<li><label>Sports<input type="checkbox" name="genre" value="Sports"></label></li>' +
            '</ul>' +
            '<div><label>Developer </label>' +
            '<input type="text" name="developer" value="">' +
            '</div>' +
            '<br><button>Search</button>' +
            '</div><br></div>' +
            '<a href="#home">Go to Home</a>'
        );
    });

    it('should show player info view', () => {
        const mainContent = renders.renderPlayerView({id: 1, name: 'John Doe', email: 'john.doe@example.com'});
        mainContent.innerHTML.should.be.equal(
            '<h1>Player</h1>' +
            '<div><ul>' +
            '<li>Id : 1</li>' +
            '<li>Name : John Doe</li>' +
            '<li>Email : john.doe@example.com</li>' +
            '</ul></div>' +
            '<a href="#home">Go to Home</a>'
        );
    });
});