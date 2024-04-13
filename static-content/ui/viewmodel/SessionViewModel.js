export default function SessionViewModel(sessionHandler) {

  return {
    getSession: getSession,
    createSession: createSession,
    getSessions: getSessions,
  };

  function getSession(sessionId) {
    return sessionHandler.getSession(sessionId);
  }

  function createSession(session) {
    return sessionHandler.createSession(session);
  }

  function getSessions(gid, date, state, playerId, skip, limit) {
    return sessionHandler.getSessions(gid, date, state, playerId, limit, skip);
  }
}