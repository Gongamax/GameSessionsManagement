import dom from '../../lib/dom-utils.js';

const { h1, ul, li, div } = dom;

export default async function SessionView(mainContent, sessionViewModel) {
  const params = new URLSearchParams(window.location.search); //Provavelmente há outra maneira mais ideal
  const sessionId = parseInt(params.get('sid'));
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

   mainContent.replaceChildren(content);
}