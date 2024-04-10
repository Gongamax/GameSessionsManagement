import dom from '../../lib/dom-utils.js';

const { nav, ul, li, a, div ,p ,h1} = dom;

async function HomeView(content) {
  return div(
    h1({class: 'APP'}, "Welcome to Sessions App!"),
    p(
      "Author: ",
      a({href: "https://github.com/saraiva22"}, "Francisco Saraiva")," - ",
      a({href: "https://github.com/Gongamax"},"Gonçalo Frutuoso")," - ",
      a({href: "https://github.com/daniel-m-carvalho"},"Daniel Carvalho")
    )
  );
}

export default HomeView;