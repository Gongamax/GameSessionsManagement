function li(content) {
  const liElement = document.createElement('li');
  const textNode = document.createTextNode(content);
  liElement.appendChild(textNode);
  return liElement;
}

function ul(...items) {
  const ulElement = document.createElement('ul');
  items.forEach(item => {
    ulElement.appendChild(item);
  });
  return ulElement;
}

function div(...items) {
  const divElement = document.createElement('div');
  items.forEach(item => {
    divElement.appendChild(item);
  });
  return divElement;
}

export default {
  li,
  ul,
  div
}