import dom from '../../../lib/dom-utils.js';
import Pagination from '../../components/Pagination.js';

const { h1, ul, li, div, a, br } = dom;

const limit = 5;

export default async function SessionsView(mainContent, sessionViewModel, page = 1) {
  const skip = (page - 1) * limit;
  const gameId = new URLSearchParams(window.location.search).get('gid') ?? 1;
  const sessions = await sessionViewModel.getSessions(Number(gameId), skip, limit + 1);
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

  const home = a('#home', 'Go to Home');
  mainContent.replaceChildren(content, space, ...paginationButtons, space, home);
}