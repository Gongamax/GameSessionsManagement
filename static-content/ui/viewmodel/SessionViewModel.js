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

  function getSessions(gid, skip, limit) {
    return sessionHandler.getSessions(gid, limit, skip);
  }
}