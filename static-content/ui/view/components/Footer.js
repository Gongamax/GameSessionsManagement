import dom from '../../lib/dom-elements.js';

const Footer = () => {
  return dom.footer({
      class: 'p-5 bg-dark text-white text-center position-relative',
    },
    dom.div(
      { class: 'container' },
      dom.p({ class: 'lead' }, 'Copyright &copy; 2024 Sessions'),
      dom.a({ href: '#', class: 'position-absolute bottom-0 end-0 p-5' }, ''),
      dom.i({ class: 'bi bi-arrow-up-circle h1' }, ''),
    ),
  );
};

export default Footer;