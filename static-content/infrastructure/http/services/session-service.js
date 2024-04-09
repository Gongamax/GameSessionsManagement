import uris from '../uris.js';
import HttpService from './http-service.js';
import { Session } from '../../../domain/types/session.js';

const httpHandler = HttpService();
export default function SessionService() {
  return {
    createSession: createSession,
    getSession: getSession,
  };

  function createSession(session) {
    return httpHandler.post(uris.createSession, JSON.stringify(session)).then(res => {
      return res.message;
    }).catch(error => {
      return error.detail;
    });
  }
  function getSession(sessionId) {
    return httpHandler.get(uris.getSession + sessionId).then(session => {
      return new Session(session.sid, session.date, session.gid, session.capacity);
    }).catch(error => {
      return error.detail;
    });
  }


}