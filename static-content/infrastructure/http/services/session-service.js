import uris from '../uris.js';
import HttpService from './http-service.js';
import { Session } from '../../../domain/types/session.js';

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

  function getSessions() {
    return httpService.get(uris.getSessions + '?gid=1').then(sessions => {
      return sessions.map(session =>
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