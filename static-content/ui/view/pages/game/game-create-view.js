import dom from '../../../lib/dom-elements.js';
import { genres } from '../../../../domain/types/game.js';

const { a, br,li, h1, form, input, label, button } = dom;


const CreateGameView = (mainContent, gameViewModel) => {
  const elements = [
    h1({}, 'Create Game'),
    form({ id: 'game' }, [
      label({ htmlFor: 'name' }, 'Name:'),
      input({ type: 'text', id: 'name', name: 'name' }),
      br({}),
      label({ htmlFor: 'developer' }, 'Developer:'),
      input({ type: 'text', id: 'developer', name: 'developer' }),
      br({}),
      ...genres.map(genre => {
        return li({}, label({}, genre), input({ type: 'checkbox', name: 'genre', value: genre }), br());
      }),

      br({}),
      button({ type: 'submit' }, 'Create Game'),
      br({}),
    ]),
    a({ href: '#home' }, 'Go to Home'),
  ];

  mainContent.replaceChildren(...elements);

  const createGame = mainContent.querySelector('#game');
  createGame.addEventListener('submit', event => handleCreateGame(event, gameViewModel));
};


export default CreateGameView;


async function handleCreateGame(event, gameViewModel) {
  event.preventDefault();

  const form = event.target;

  const formData = new FormData(form);
  const name = formData.get('name');
  const developer = formData.get('developer');
  const genres = formData.getAll('genre');


  if (!genres || !name || !developer) {
    alert('All fields are required');
    return;
  }

  try {
    const gameProps = {
      name,
      developer,
      genres,
    };

    const res = await gameViewModel.createGame(gameProps);
    if (res.includes('Game created')) {
      const message = res.split(' ');
      const number = parseInt(message[message.length - 1]);
      alert('Game created successfully');
      console.log(res);
      window.location.hash = `#game/${number}`;
    } else {
      alert('Error signing up');
    }
  } catch (error) {
    alert('Error create game');
  }

}



















