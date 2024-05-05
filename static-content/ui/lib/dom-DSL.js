/**
 * Create an element with the given name, attributes, and children.
 * @param name
 * @param attrs
 * @param children
 */
function createElement(name, attrs, ...children) {
  const element = document.createElement(name);
  if (attrs) {
    for (const key in attrs) {
      const value = attrs[key];
      if (typeof value === 'string') element.setAttribute(key, value);
      else if (value) element.setAttribute(key, '');
    }
  }
  children.forEach(child => {
    if (typeof child === 'string') element.appendChild(document.createTextNode(child));
    else if (Array.isArray(child)) child.forEach(it => element.appendChild(it));
    else element.appendChild(child);
  });
  return element;
}

export default createElement;