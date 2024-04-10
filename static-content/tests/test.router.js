import router from "../infrastructure/http/router/router.js";
import services from "../infrastructure/http/services/services.js";
import playerRouter from "../infrastructure/http/router/player-router.js";
import sessionRouter from "../infrastructure/http/router/session-router.js";

describe('Router', function () {

    before(function () {
    router.addRouteHandler('home', services.getHome);
    router.addRouteHandler('player/:id', playerRouter);
    router.addRouteHandler('session/:id', sessionRouter.handleSessionRoute);
    router.addRouteHandler('sessions', sessionRouter.handleSessionsRoute);

    router.addDefaultNotFoundRouteHandler(() => window.location.hash = 'home');
    })

    context('when is found', function () {
        it('should find home handler', function () {

            router.getRouteHandler('home').should.equal(services.getHome);
            router.getRouteHandler('home').name.should.equal('getHome');
            router.getRouteHandler('home').should.be.a('function');
        })


        it('should find session handler', function () {

            router.getRouteHandler('session/:id').should.equal(sessionRouter.handleSessionRoute);
            router.getRouteHandler('session/:id').name.should.equal('handleSessionRoute');
            router.getRouteHandler('session/:id').should.be.a('function');
        })

        it('should find sessions handler', function () {

            router.getRouteHandler('sessions').should.equal(sessionRouter.handleSessionsRoute);
            router.getRouteHandler('sessions').name.should.equal('handleSessionsRoute');
            router.getRouteHandler('sessions').should.be.a('function');
        })

        // it('should find player handler', function () {
        //
        //     router.getRouteHandler('player/:id').should.equal(playerRouter);
        //     router.getRouteHandler('player/:id').name.should.equal('playerRouter');
        //     router.getRouteHandler('player/:id').should.be.a('function');
        // })
    })

    context('when is not found', function () {
        it('should find home', function () {
            router.getRouteHandler('home').should.equal(services.getHome);
            router.getRouteHandler('home').name.should.equal('getHome');
            router.getRouteHandler('home').should.be.a('function');
        })
    })

});