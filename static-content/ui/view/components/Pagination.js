import dom from '../../lib/dom-utils.js';

const { btn } = dom;

function Pagination(currentPage, hasNextPage, updatePage) {
  const prevButton = btn('Previous Page', () => {
    updatePage(Math.max(1, currentPage - 1));
  }, currentPage === 1);
  const nextButton = btn('Next Page', () => {
    updatePage(currentPage + 1);
  }, !hasNextPage);

  //mainContent.append(prevButton, nextButton);

  return [prevButton, nextButton];
}

export default Pagination;