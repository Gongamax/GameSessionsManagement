import dom from '../../../lib/dom-utils.js';

const { h1, ul, li, div, a, br } = dom;

export default async function SessionsView(mainContent, sessionViewModel) {
  const sessions = await sessionViewModel.getSessions();
  console.log(sessions);

  const content = div(
    h1('Sessions'),
    ul(
      ...sessions.map(session => {
        return li(
          a(`#session/${session.sid}`,
            div(
              `Session ID: ${session.sid}`,
              br(),
              `Date: ${session.date}`,
              br(),
              `Game ID: ${session.gid}`,
              br(),
              `Capacity: ${session.capacity}`,
            ),
          ),
        );
      }),
    ),
  );

  const home = a('#home', 'Go to Home');
  mainContent.replaceChildren(content, home);
}