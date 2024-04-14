import renders from "../../../lib/renders.js";

export default async function SessionView(mainContent, sessionViewModel) {
  const params = window.location.hash.split('/');
  const sessionId = parseInt(params[params.length - 1]);
  if (isNaN(sessionId)) {
    return;
  }

  const { sid, numberOfPlayers, date, gid, associatedPlayers, capacity } = await sessionViewModel.getSession(sessionId);

  const content = renders.renderSessionView({sid, numberOfPlayers, date, gid, associatedPlayers, capacity});

  mainContent.replaceChildren(content);
}