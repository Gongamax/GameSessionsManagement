import router from '../infrastructure/http/router/router.js';
import sessionsRouter from '../infrastructure/http/router/sessions-router.js';
import handlePlayerRoute from '../infrastructure/http/router/player-router.js';

describe('Router', function() {

  before(function() {

    router.addRouteHandler('home', sessionsRouter.handleHomeRoute);
    router.addRouteHandler('player/:id', handlePlayerRoute);
    router.addRouteHandler('session', sessionsRouter.sessionRouter.handleSearchSessionsRoute);
    router.addRouteHandler('session/:id', sessionsRouter.sessionRouter.handleSessionRoute);
    router.addRouteHandler('sessions', sessionsRouter.sessionRouter.handleSessionsRoute);
    router.addRouteHandler('game', sessionsRouter.gameRouter.handleSearchGamesRoute);
    router.addRouteHandler('game/:id', sessionsRouter.gameRouter.handleGameRoute);
    router.addRouteHandler('games', sessionsRouter.gameRouter.handleGamesRoute);

    router.addDefaultNotFoundRouteHandler(() => window.location.hash = 'home');
  });

  context('when is found', function() {
    it('should find home handler', function() {

      router.getRouteHandler('home').should.equal(sessionsRouter.handleHomeRoute);
      router.getRouteHandler('home').name.should.equal('handleHomeRoute');
      router.getRouteHandler('home').should.be.a('function');
    });

    it('should find player handler', function() {

      router.getRouteHandler('player/:id').should.equal(handlePlayerRoute);
      router.getRouteHandler('player/:id').name.should.equal('handlePlayerRoute');
      router.getRouteHandler('player/:id').should.be.a('function');
    });

    it('should find sessions search handler', function() {

      router.getRouteHandler('session').should.equal(sessionsRouter.sessionRouter.handleSearchSessionsRoute);
      router.getRouteHandler('session').name.should.equal('handleSearchSessionsRoute');
      router.getRouteHandler('session').should.be.a('function');
    });

    it('should find session handler', function() {

      router.getRouteHandler('session/:id').should.equal(sessionsRouter.sessionRouter.handleSessionRoute);
      router.getRouteHandler('session/:id').name.should.equal('handleSessionRoute');
      router.getRouteHandler('session/:id').should.be.a('function');
    });

    it('should find sessions handler', function() {

      router.getRouteHandler('sessions').should.equal(sessionsRouter.sessionRouter.handleSessionsRoute);
      router.getRouteHandler('sessions').name.should.equal('handleSessionsRoute');
      router.getRouteHandler('sessions').should.be.a('function');
    });

    it('should find games search handler', function() {

      router.getRouteHandler('game').should.equal(sessionsRouter.gameRouter.handleSearchGamesRoute);
      router.getRouteHandler('game').name.should.equal('handleSearchGamesRoute');
      router.getRouteHandler('game').should.be.a('function');
    });

    it('should find game handler', function() {

      router.getRouteHandler('game/:id').should.equal(sessionsRouter.gameRouter.handleGameRoute);
      router.getRouteHandler('game/:id').name.should.equal('handleGameRoute');
      router.getRouteHandler('game/:id').should.be.a('function');
    });

    it('should find games handler', function() {

      router.getRouteHandler('games').should.equal(sessionsRouter.gameRouter.handleGamesRoute);
      router.getRouteHandler('games').name.should.equal('handleGamesRoute');
      router.getRouteHandler('games').should.be.a('function');
    });

  });

  context('when is not found', function() {
    it('should find home', function() {
      router.getRouteHandler('home').should.equal(sessionsRouter.handleHomeRoute);
      router.getRouteHandler('home').name.should.equal('handleHomeRoute');
      router.getRouteHandler('home').should.be.a('function');
    });
  });
});