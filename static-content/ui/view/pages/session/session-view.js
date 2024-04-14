import renders from "../../../lib/renders.js";

export default async function SessionView(mainContent, sessionViewModel) {
  const params = window.location.hash.split('/');
  const sessionId = params[params.length - 1];
  if (sessionId !== String(parseInt(sessionId))) {
    const content = renders.renderGetHome(`Invalid session id, is not a number ${sessionId}`);
    mainContent.replaceChildren(content);
    return;
  }

  const { sid, numberOfPlayers, date, gid, associatedPlayers, capacity } = await sessionViewModel.getSession(sessionId);
  if (sid === undefined) {
    const content = renders.renderGetHome('An error occurred while fetching the session. Please try again later.');
    mainContent.replaceChildren(content);
    return;

  }

  const content = renders.renderSessionView({sid, numberOfPlayers, date, gid, associatedPlayers, capacity});

  mainContent.replaceChildren(content);
}