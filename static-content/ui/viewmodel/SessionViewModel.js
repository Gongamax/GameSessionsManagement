export default function SessionViewModel(sessionHandler) {

  return {
    getSession: getSession,
    createSession: createSession,
  };

  function getSession(sessionId) {
    return sessionHandler.getSession(sessionId);
  }

  function createSession(session) {
    return sessionHandler.createSession(session);
  }

}