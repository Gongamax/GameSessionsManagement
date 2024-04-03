/**
 * A service for making HTTP requests to the API.
 * It includes methods for GET, POST, PUT, and DELETE requests.
 */
export default function httpService() {
  return {
    get: get,
    post: post,
    put: put,
    del: del,
  };

  /**
   * Makes an API request and returns the response.
   * @param {string} path - The path of the API endpoint.
   * @param {string} method - The HTTP method to use for the request.
   * @param {string} [body] - The body of the request, if applicable.
   * @returns {Promise} - The response from the API request.
   * @throws Will throw an error if the response is not ok.
   */
  async function makeAPIRequest(path, method, body) {
    const headers = {
      'Content-Type': 'application/json',
    };

    const config = {
      method,
      credentials: 'include',
      headers,
      body: body,
    };

    const response = await fetch(path, config);

    if (!response.ok) {
      // if (response.headers.get('Content-Type')?.includes(problemMediaType))
      //   throw await response.json();
      // else
      //   throw new Error(`HTTP error! Status: ${response.status}`);
      throw new Error(`HTTP error! Status: ${response.status}`);
    }
    return await response.json();
  }

  /**
   * Makes a GET request to the specified path.
   * @param {string} path - The path of the API endpoint.
   * @param body
   * @returns {Promise} - The response from the API request.
   */
  async function get(path, body = undefined) {
    return makeAPIRequest(path, 'GET', body);
  }

  /**
   * Makes a POST request to the specified path with the provided body.
   * @param {string} path - The path of the API endpoint.
   * @param {string} [body] - The body of the request.
   * @returns {Promise} - The response from the API request.
   */
  async function post(path, body) {
    return makeAPIRequest(path, 'POST', body);
  }

  /**
   * Makes a PUT request to the specified path with the provided body.
   * @param {string} path - The path of the API endpoint.
   * @param {string} [body] - The body of the request.
   * @returns {Promise} - The response from the API request.
   */
  async function put(path, body) {
    return makeAPIRequest(path, 'PUT', body);
  }

  /**
   * Makes a DELETE request to the specified path with the provided body.
   * @param {string} path - The path of the API endpoint.
   * @param {string} [body] - The body of the request.
   * @returns {Promise} - The response from the API request.
   */
  async function del(path, body) {
    return makeAPIRequest(path, 'DELETE', body);
  }
}