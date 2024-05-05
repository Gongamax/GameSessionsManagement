import renders from '../../../lib/renders.js';

export default async function SessionView(mainContent, sessionViewModel) {
  const params = window.location.hash.split('/');
  const query = new URLSearchParams(window.location.hash.split('?')[1]);
  const sessionId = params[params.length - 1].split('?')[0];
  const update = query.get('update') !== null ? query.get('update') : '';
  if (sessionId !== String(parseInt(sessionId))) {
    const content = renders.renderGetHome(`Invalid session id, is not a number ${sessionId}`);
    mainContent.replaceChildren(content);
    return;
  }

  const { sid, numberOfPlayers, date, gid, associatedPlayers, capacity } = await sessionViewModel.getSession(sessionId);

  async function updateSession(newCapacity, newDate) {
    await sessionViewModel.updateSession(sid, newCapacity, newDate);
    window.location.hash = `#session/${sid}`;
  }

  if (update === 'true') {
    const content = renders.renderSessionView(
      { sid, numberOfPlayers, date, gid, associatedPlayers, capacity },
      () => deleteSession(sid),
      (newCapacity, newDate) => updateSession(newCapacity, newDate),
      true
    );
    mainContent.replaceChildren(content);
  } else {
    if (sid === undefined) {
      const content = renders.renderGetHome('An error occurred while fetching the session. Please try again later.');
      mainContent.replaceChildren(content);
      return;
    }

    async function deleteSession(sid) {
      await sessionViewModel.deleteSession(sid);
      window.location.hash = '#sessions';
    }

    const content = renders.renderSessionView(
      { sid, numberOfPlayers, date, gid, associatedPlayers, capacity },
      () => deleteSession(sid),
      () => (window.location.hash = `#session/${sid}?update=true`)
    );

    mainContent.replaceChildren(content);
  }
}
