import renders from '../ui/lib/renders.js';
import homeView from "../ui/view/pages/home-view.js";

describe('Views Test', function() {

  it('should show home view', () => {
    let mainContent = homeView();
    mainContent.then(res => {
      res.innerHTML.should.be.equal(
          '<h1>Welcome to Sessions App!</h1>' +
          '<p>We are happy to have you here!</p>' +
          '<p>This is a simple app to manage sessions. You can add, edit, and delete sessions.</p>' +
          '<a href="#player/1">Go to Player</a><br>' +
          '<a href="#session">Go to list of Sessions</a><br>' +
          '<a href="#game">Go to list of Games</a><br>' +
          '<a href="#sign-up">Sign Up</a><br>' +
          '<a href="#game-create">Create Game</a>'
      );
    }
  );
  });

  it('should show player info view', () => {
    const mainContent = renders.renderPlayerView({ id: 1, name: 'John Doe', email: 'john.doe@example.com' });
    mainContent.innerHTML.should.be.equal(
        '<h1>Player</h1>' +
        '<div><ul><li>Id: 1</li>' +
        '<li>Name: John Doe</li>' +
        '<li>Email: john.doe@example.com</li></ul></div><br>' +
        '<a href="#sessions?gid=&amp;date=&amp;state=&amp;pid=1">Player sessions</a><br>' +
        '<a href="#home">Go to Home</a>'
    );
  });

  it('should show sessions search view', () => {
    let mainContent = renders.renderSessionSearch();
    mainContent.innerHTML.should.be.equal(
        '<h1>Search Sessions</h1>' +
        '<div>' +
        '<div><label>Game Id </label>' +
        '<input type="text" name="gameId" value="">' +
        '</div><br>' +
        '<div><label>Date </label>' +
        '<input type="datetime-local" name="date" value="">' +
        '</div><br>' +
        '<div><label>State </label>' +
        '<select name="state">' +
        '<option value="">All</option>' +
        '<option value="open">Open</option>' +
        '<option value="close">Close</option>' +
        '</select></div><br>' +
        '<div><label>Player Id </label>' +
        '<input type="text" name="playerId" value="">' +
        '</div><br>' +
        '<button type="submit">Search</button>' +
        '</div><br>' +
        '<a href="#home">Go to Home</a>',
    );
  });

  it('should show session info view', () => {
    const mainContent = renders.renderSessionView({
      sid: 1,
      date: '2021-01-01T10:00:00', state: 'Open', gid: 1, pid: 1,
      numberOfPlayers: 2, capacity: 4,
      associatedPlayers: [{ pid: 1, name: 'John Doe' }, { pid: 2, name: 'Jane Doe' }],
    });
    mainContent.innerHTML.should.be.equal(
      '<h1>Session</h1>' +
      '<div><ul>' +
      '<li>Id: 1</li>' +
      '<li>Number of Players: 2</li>' +
      '<li>Date: 2021-01-01T10:00:00</li>' +
      '<li><a href="#game/1">Game: 1</a></li>' +
      '<li><div>Associated Players: <div><a href="#player/1">John Doe</a>,</div>' +
      '<div><a href="#player/2">Jane Doe</a></div></div></li>' +
      '<li>Capacity: 4</li>' +
      '</ul></div>' +
      '<br><button type="submit" id="delete">Delete</button>' +
      '<button type="button" id="update">Update</button><br>' +
      '<br><div><label>PlayerName: </label>' +
      '<input type="text" name="playerName" value=""></div><br>' +
      '<button type="submit" id="addPlayer">Add Player</button><br>'
    );
  });

  it('should show session update view', () => {
    const mainContent = renders.renderSessionView({
          capacity: 4,
          date: '2021-01-01T10:00:00',
        },
        true
    );
    mainContent.innerHTML.should.be.equal(
      '<h1>Update Session</h1>' +
      '<div><label>Capacity </label>' +
      '<input type="text" name="capacity" value="4">' +
      '</div><br>' +
      '<div><label>Date </label>' +
      '<input type="datetime-local" name="date" value="2021-01-01T10:00:00">' +
      '</div><br>' +
      '<button type="button" id="cancel">Cancel</button>' +
      '<button type="submit" id="update">Update</button>'
    );
  });

  it('should show sessions view', () => {
    const mainContent = renders.renderSessionsView([
      {
        'sid': 1,
        'numberOfPlayers': 2,
        'date': '2022-01-01T10:00',
        'gid': 1,
        'associatedPlayers': [
          {
            'pid': 1,
            'name': 'John Doe',
            'email': 'john.doe@example.com',
          },
          {
            'pid': 2,
            'name': 'Jane Doe',
            'email': 'jane.doe@example.com',
          },
        ],
        'capacity': 5,
      },
      {
        'sid': 3,
        'numberOfPlayers': 0,
        'date': '2025-03-14T10:58',
        'gid': 1,
        'associatedPlayers': [],
        'capacity': 20,
      },
    ], 1, false, { gid: 1 });
    mainContent.innerHTML.should.be.equal(
      '<h1>Sessions</h1>' +
      '<ul><li><div><li>' +
      '<a href="#session/1">Session ID: 1</a></li>' +
      '<ul><li>Date: 2022-01-01T10:00</li>' +
      '<li>Game ID: 1</li>' +
      '<li>Capacity: 5</li><br></ul></div></li>' +
      '<li><div><li>' +
      '<a href="#session/3">Session ID: 3</a></li>' +
      '<ul><li>Date: 2025-03-14T10:58</li>' +
      '<li>Game ID: 1</li>' +
      '<li>Capacity: 20</li><br></ul></div></li></ul><br>' +
      '<button type="button" disabled="">Previous Page</button>' +
      '<button type="button" disabled="">Next Page</button>' +
      '<br><br><a href="#session">Go to Sessions Search</a>',
    );
  });

  it('should show games search view', () => {
    let mainContent = renders.renderGamesSearch()
    mainContent.innerHTML.should.be.equal(
        '<h1>Games Search</h1>' +
        '<div><ul>' +
        '<li><label>Rpg</label><input type="checkbox" name="genre" value="Rpg"><br></li>' +
        '<li><label>Adventure</label><input type="checkbox" name="genre" value="Adventure"><br></li>' +
        '<li><label>Shooter</label><input type="checkbox" name="genre" value="Shooter"><br></li>' +
        '<li><label>Turn-Based</label><input type="checkbox" name="genre" value="Turn-Based"><br></li>' +
        '<li><label>Action</label><input type="checkbox" name="genre" value="Action"><br></li>' +
        '<li><label>Multiplayer</label><input type="checkbox" name="genre" value="Multiplayer"><br></li>' +
        '<li><label>Fighting</label><input type="checkbox" name="genre" value="Fighting"><br></li>' +
        '<li><label>Sports</label><input type="checkbox" name="genre" value="Sports"><br></li>' +
        '</ul>' +
        '<div><label>Developer </label>' +
        '<input type="text" name="developer" value="">' +
        '</div>' +
        '<br><button type="submit">Search</button></div><br>' +
        '<a href="#home">Go to Home</a>'
    );
  });

  it('should show game info view', () => {
    const mainContent = renders.renderGameView({
      gid: 1,
      name: 'Game 1',
      developer: 'Developer 1',
      genres: ['Rpg', 'Adventure'],
    });
    mainContent.innerHTML.should.be.equal(
        '<h1>Game</h1>' +
        '<div><ul>' +
        '<li>Id: 1</li>' +
        '<li>Name: Game 1</li>' +
        '<li>Developer: Developer 1</li>' +
        '<li>Genres: Rpg,Adventure</li>' +
        '</ul><br>' +
        '<a href="#sessions?gid=1">Sessions of the game: Game 1</a>' +
        '</div><br>' +
        '<a href="#home">Go to Home</a>',
    );
  });

  it('should show games view', () => {
    context('when there are no games', () => {
      const mainContent = renders.renderGamesView([], 1, false, { genre: 'Rpg' });
      mainContent.innerHTML.should.be.equal(
        '<h1 id="getHome">No games found matching the provided parameters. Please try again.</h1>' +
        '<a href="#home">Go to Home</a>',
      );
    });
    context('when there are games', () => {
      const mainContent = renders.renderGamesView([
        {
          'gid': 1,
          'name': 'Game 1',
          'developer': 'Developer 1',
          'genres': ['Rpg', 'Adventure'],
        },
        {
          'gid': 2,
          'name': 'Game 2',
          'developer': 'Developer 2',
          'genres': ['Action', 'Shooter'],
        },
      ], 1, false, { genre: 'Rpg' });
      mainContent.innerHTML.should.be.equal(
        '<h1>Games</h1>' +
        '<ul><li><div><li>' +
        '<a href="#game/1">Game: Game 1</a></li>' +
        '<ul><li>Id: 1</li>' +
        '<li>Developer: Developer 1</li>' +
        '<li>Genres: Rpg,Adventure</li><br></ul></div></li>' +
        '<li><div><li>' +
        '<a href="#game/2">Game: Game 2</a></li>' +
        '<ul><li>Id: 2</li>' +
        '<li>Developer: Developer 2</li>' +
        '<li>Genres: Action,Shooter</li><br></ul></div></li></ul>' +
        '<button type="button" disabled="">Previous Page</button>' +
        '<button type="button" disabled="">Next Page</button>' +
        '<br><br><a href="#game">Go to Games Search</a>',
      );
    });
  });
  it('should show create game view', () => {
    let mainContent = renders.renderGameCreate();
    mainContent.innerHTML.should.be.equal(
      '<h1>Create Game</h1>' +
      '<form id="game">' +
      '<label htmlfor="name">Name: </label>' +
      '<input type="text" id="name" name="name">' +
      '<br>' +
      '<label htmlfor="developer">Developer: </label>' +
      '<input type="text" id="developer" name="developer">' +
      '<br>' +
      '<li><label>Rpg</label><input type="checkbox" name="genre" value="Rpg"><br></li>' +
      '<li><label>Adventure</label><input type="checkbox" name="genre" value="Adventure"><br></li>' +
      '<li><label>Shooter</label><input type="checkbox" name="genre" value="Shooter"><br></li>' +
      '<li><label>Turn-Based</label><input type="checkbox" name="genre" value="Turn-Based"><br></li>' +
      '<li><label>Action</label><input type="checkbox" name="genre" value="Action"><br></li>' +
      '<li><label>Multiplayer</label><input type="checkbox" name="genre" value="Multiplayer"><br></li>' +
      '<li><label>Fighting</label><input type="checkbox" name="genre" value="Fighting"><br></li>' +
      '<li><label>Sports</label><input type="checkbox" name="genre" value="Sports"><br></li>' +
      '<br><button type="submit">Create Game</button>' +
      '<br></form>' +
      '<a href="#home">Go to Home</a>'
    );
  });
});