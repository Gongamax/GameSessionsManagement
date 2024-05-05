import dom from '../../../lib/dom-elements.js';

const { a, br, p, h1, form, input, label, button } = dom;

const SignUpView = (mainContent, playerViewModel) => {
  const elements = [
    h1({}, 'Sign Up'),
    form({ id: 'sign-up-form' }, [
      label({ htmlFor: 'email' }, 'Email:'),
      input({ type: 'email', id: 'email', name: 'email' }),
      br({}),
      label({ htmlFor: 'username' }, 'Username:'),
      input({ type: 'text', id: 'username', name: 'username' }),
      br({}),
      label({ htmlFor: 'password' }, 'Password:'),
      input({ type: 'password', id: 'password', name: 'password' }),
      br({}),
      label({ htmlFor: 'passwordConfirmation' }, 'Password Confirmation:'),
      input({ type: 'password', id: 'passwordConfirmation', name: 'passwordConfirmation' }),
      br({}),
      a({ href: '#sign-in' }, 'Already have an account? Sign In'),
      br({}),
      button({ type: 'submit' }, 'Sign Up'),
    ]),
  ];

  mainContent.replaceChildren(...elements);

  const signUpForm = mainContent.querySelector('#sign-up-form');
  signUpForm.addEventListener('submit', event => handleSignUp(event, playerViewModel));
};

export default SignUpView;

/**
 * Handles the sign-up form submission
 * @param event
 * @param playerViewModel
 * @returns {Promise<void>}
 */
async function handleSignUp(event, playerViewModel) {
  event.preventDefault();
  const form = event.target;
  const formData = new FormData(form);
  const email = formData.get('email');
  const username = formData.get('username');
  const password = formData.get('password');
  const passwordConfirmation = formData.get('passwordConfirmation');

  if (!email || !username || !password || !passwordConfirmation) {
    alert('All fields are required');
    return;
  }

  if (password !== passwordConfirmation) {
    alert('Passwords do not match');
    return;
  }

  try {
    const playerProps = {
      email,
      username,
      password,
    };
    const res = await playerViewModel.createPlayer(playerProps);
    if (res.code === 201) {
      alert('Player created successfully');
      window.location.hash = '#sign-in';
    } else {
      alert('Error signing up');
    }
  } catch (error) {
    alert('Error signing up');
  }
}
