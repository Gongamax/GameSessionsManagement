/**
 * Create an element with the given name, attributes, and children.
 * @param name
 * @param attrs
 * @param children
 */
export function createElement(name, attrs, ...children) {
  const element = document.createElement(name);
  if (attrs) {
    for (const key in attrs) {
      const value = attrs[key];
      if (typeof value === 'string')
        element.setAttribute(key, value);
      else if (value)
        element.setAttribute(key, '');
    }
  }
  children.forEach(child => {
    if (typeof child === 'string')
      element.appendChild(document.createTextNode(child));
    else if (Array.isArray(child))
      child.forEach(it => element.appendChild(it));
    else
      element.appendChild(child);
  });
  return element;
}

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

function h1(content) {
  const h1Element = document.createElement('h1');
  const textNode = document.createTextNode(content);
  h1Element.appendChild(textNode);
  return h1Element;
}

export default {
  li,
  ul,
  div,
  h1
};