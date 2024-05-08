import renders from '../../../lib/renders.js';

const CreateGameView = async (mainContent, gameViewModel) => {
  const content = renders.renderGameCreate();
  content.querySelector('#game');
  content.addEventListener('submit', event => handleCreateGame(event, gameViewModel));
  return content;
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
    if (res.status === 201) {
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



















