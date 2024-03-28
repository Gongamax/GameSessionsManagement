import playerHandler from './player-handler';

function getHome(mainContent) {
  const h1 = document.createElement('h1');
  const text = document.createTextNode('Home');
  h1.appendChild(text);
  mainContent.replaceChildren(h1);
}

export const handlers = {
  getHome,
  playerHandler,
};

export default handlers;