import dom from '../../../lib/dom-utils.js';
import Pagination from '../../components/Pagination.js';

const { h1, ul, li, div, a, br } = dom;

const limit = 5;

export default async function SessionsView(mainContent, sessionViewModel, page = 1) {
  const skip = (page - 1) * limit;
  const params = new URLSearchParams(window.location.hash.split('?')[1]);
  const gameId = params.get('gid');
  const date = params.get('date');
  const state = params.get('state');
  const playerId = params.get('pid');
  const sessions = await sessionViewModel.getSessions(Number(gameId), date, state, playerId, skip, limit + 1);
  const hasNextPage = sessions.length > limit;
  if (hasNextPage) {
    sessions.pop();
  }

  const content = div(
    h1('Sessions'),
    ul(
      ...sessions.map(session => {
        return li(
          div(
            li(a(`#session/${session.sid}`, `Session ID: ${session.sid}`)),
            ul(
              li('Date: ' + session.date),
              li('Game ID: ' + session.gid),
              li('Capacity: ' + session.capacity),
              br(),
            ),
          ),
        );
      }),
    ),
  );

  const paginationButtons = Pagination(page, hasNextPage, newPage => {
    page = newPage;
    window.location.hash = `#sessions?page=${page}`;
  });

  const space = br();
  const space2 = br();

  const home = a('#home', 'Go to Home');
  mainContent.replaceChildren(content, space, ...paginationButtons, space, space2, home);
}