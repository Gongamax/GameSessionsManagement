import renders from "../../../lib/renders.js";

async function SessionSearchView() {
  const handleSearch = () => {
    const gameId = document.querySelector('input[name = gameId]').value;
    const date = document.querySelector('input[name = date]').value;
    const state = document.querySelector('select[name = state]').value;
    const playerId = document.querySelector('input[name = playerId]').value;
    console.log(gameId, date, state, playerId);
    window.location.hash = `#sessions?gid=${gameId}&date=${date}&state=${state}&pid=${playerId}`;
  };

  const content = renders.renderSessionSearch();

  content.querySelector('button[type=submit]').addEventListener('click', handleSearch);

  return content;
}

export default SessionSearchView;
