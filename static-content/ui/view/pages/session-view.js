import dom from '../../lib/dom-utils.js';

const { h1, ul, li, div,a } = dom;

export default async function SessionView(mainContent, sessionViewModel) {
  const params =  window.location.hash.split('/');
  const sessionId = parseInt(params[params.length - 1]);
  if (isNaN(sessionId)) {
    return;
  }

  const { date,game,capacity } = await sessionViewModel.getSession(sessionId);
  console.log(date,game,capacity)

  const content = div(
    h1('Session'),
    div(
      ul(
        li('Id : ' + sessionId),
        li('Date: ' + date),
        li('Game: ' + game),
        li('Capacity: ' + capacity),
      ),
    ),
  );

  const home = a('#home', 'Go to Home');
   mainContent.replaceChildren(content,home);
}