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

  function getSessions() {
    return sessionHandler.getSessions();
  }
}