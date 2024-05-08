import NavBar from './components/NavBar.js';
// import Footer from "./components/Footer.js";
import dom from '../lib/dom-elements.js';

const App = (pageContent) => {
  return dom.div({},
    NavBar(),
    dom.hr(),
    pageContent,
    // Footer()
  );
};

export default App;