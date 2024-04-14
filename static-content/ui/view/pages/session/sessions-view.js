import renders from '../../../lib/renders.js';

const limit = 5;

export default async function SessionsView(mainContent, sessionViewModel, page = 1) {
  const skip = (page - 1) * limit;
  const params = new URLSearchParams(window.location.hash.split('?')[1]);
  const gameId = params.get('gid');
  const date = params.get('date');
  const state = params.get('state');
  const playerId = params.get('pid');
  const sessions = await sessionViewModel.getSessions(Number(gameId), date, state, playerId, skip, limit + 1);

  if (!sessions) {
    const content = renders.renderGetHome('An error occurred while fetching sessions. Please try again later.');
    mainContent.replaceChildren(content);
    return;
  }

  const hasNextPage = sessions.length > limit;
  if (hasNextPage) {
    sessions.pop();
  }

  const content = renders.renderSessionsView(sessions, page, hasNextPage, { gid: gameId, date, state, pid: playerId });
  mainContent.replaceChildren(content);
}