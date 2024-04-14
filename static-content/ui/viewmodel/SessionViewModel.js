export default function SessionViewModel(sessionService) {

  return {
    getSession: getSession,
    createSession: createSession,
    getSessions: getSessions,
  };

  function getSession(sessionId) {
    return sessionService.getSession(sessionId);
  }

  function createSession(session) {
    return sessionService.createSession(session);
  }

  function getSessions(gid, date, state, playerId, skip, limit) {
    return sessionService.getSessions(gid, date, state, playerId, limit, skip);
  }
}