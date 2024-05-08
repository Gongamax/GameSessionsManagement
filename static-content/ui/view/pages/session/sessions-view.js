import renders from '../../../lib/renders.js';
import constants from '../../../../domain/constants.js';

async function SessionsView(sessionViewModel, page = 1) {
  const limit = constants.limit;
  const skip = (page - 1) * limit;
  const params = new URLSearchParams(window.location.hash.split('?')[1]);
  const gameId = params.get('gid') !== null ? params.get('gid') : '';
  const date = params.get('date') !== null ? params.get('date') : '';
  const state = params.get('state') !== null ? params.get('state') : '';
  const playerId = params.get('pid') !== null ? params.get('pid') : '';

  console.log('Sessions-View: ' + gameId, date, state, playerId, skip, limit + 1);

  const sessions = await sessionViewModel.getSessions(gameId, date, state, playerId, skip, limit + 1);
  if (!sessions) {
    return renders.renderGetHome('An error occurred while fetching sessions. Please try again later.');
  }

  const hasNextPage = sessions.length > limit;
  if (hasNextPage) {
    sessions.pop();
  }

  return renders.renderSessionsView(sessions, page, hasNextPage, { gid: gameId, date, state, pid: playerId });
}

export default SessionsView;
