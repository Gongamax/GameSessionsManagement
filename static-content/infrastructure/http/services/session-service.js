import uris from '../uris.js';
import HttpService from './http-service.js';
import {Session} from '../../../domain/types/session.js';

const httpService = HttpService();

export default function SessionService() {
  return {
    createSession: createSession,
    getSession: getSession,
    getSessions: getSessions,
  };

  function createSession(session) {
    return httpService.post(uris.createSession, JSON.stringify(session)).then(res => {
      return res.message;
    }).catch(error => {
      return error.detail;
    });
  }

  function getSession(sessionId) {
    return httpService.get(uris.getSession + sessionId).then(session => {
      console.log(session);
      return new Session(
        session.sid,
        session.numberOfPlayers,
        session.date,
        session.gid,
        session.associatedPlayers,
        session.capacity,
      );
    }).catch(error => {
      return error.detail;
    });
  }

  function getSessions(gid, date, state, pid, limit = 10, skip = 0) {
    const params = {
      gid,
      date,
      state,
      pid,
      limit,
      skip
    };

    // Filter out undefined or empty parameters
    const filteredParams = Object.fromEntries(Object.entries(params).filter(([_, v]) => v != null && v !== ''));
    console.log('Filtered Params: ' + JSON.stringify(filteredParams));

    // Convert the parameters object into a query string
    const queryString = new URLSearchParams(filteredParams).toString();
    console.log('Query String: ' + queryString);

    return httpService.get(uris.getSessions + '?' + queryString)
        .then(result => {
          return result.sessions.map(session =>
              new Session(
                  session.sid,
                  session.numberOfPlayers,
                  session.date,
                  session.gid,
                  session.associatedPlayers,
                  session.capacity,
              ),
          );
        }).catch(error => {
          return error.detail;
        });
  }
}