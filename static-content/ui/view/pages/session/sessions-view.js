import dom from '../../../lib/dom-utils.js';

const { h1, ul, li, div, a, br, btn } = dom;

let page = 1;
const limit = 10;

export default async function SessionsView(mainContent, sessionViewModel) {
  const skip = (page - 1) * limit;
  const sessions = await sessionViewModel.getSessions(1, skip, limit + 1);
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
                br()
              )
            ),
        );
      }),
    ),
  );

  const prevButton = btn('Previous Page', () => {
    page = Math.max(1, page - 1);
    window.location.hash = `#sessions?page=${page}`;
  }, skip === 0);
  const nextButton = btn('Next Page', () => {
    page += 1;
    window.location.hash = `#sessions?page=${page}`;
  }, !hasNextPage);

  const space = br();

  const home = a('#home', 'Go to Home');
  mainContent.replaceChildren(content, prevButton, nextButton, space, home);
}