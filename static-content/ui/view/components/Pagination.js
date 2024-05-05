import dom from '../../lib/dom-elements.js';

const { button } = dom;

function Pagination(currentPage, hasNextPage, updatePage) {
  const prevButton = button(
    {
      type: 'button',
      disabled: currentPage === 1,
    },
    'Previous Page'
  );
  const nextButton = button(
    {
      type: 'button',
      disabled: !hasNextPage,
    },
    'Next Page'
  );

  prevButton.addEventListener('click', () => {
    updatePage(Math.max(1, currentPage - 1));
  });

  nextButton.addEventListener('click', () => {
    updatePage(currentPage + 1);
  });

  return [prevButton, nextButton];
}

export default Pagination;
