const configuredBaseUrl = import.meta.env.VITE_API_BASE_URL || '/api';
const API_BASE_URL = (() => {
  if (import.meta.env.DEV) {
    try {
      const target = new URL(configuredBaseUrl, window.location.origin);
      if (['localhost', '127.0.0.1'].includes(target.hostname) && target.port === '8080') return '/api';
      if (target.origin === window.location.origin) return target.pathname.replace(/\/$/, '');
    } catch { /* Keep the configured path or URL for the request error below. */ }
  }
  return configuredBaseUrl.replace(/\/$/, '');
})();
const TOKEN_KEY = 'meet-minutes-token';

function parseResponseBody(text, contentType) {
  if (!text) return null;
  if (contentType.includes('application/json')) {
    try { return JSON.parse(text); } catch { return { message: text }; }
  }
  return text;
}

function errorMessage(body, status) {
  if (body && typeof body === 'object') {
    if (body.message) return body.message;
    if (body.error) return body.error;
    if (body.errors && typeof body.errors === 'object') return Object.values(body.errors).join(' · ');
    if (body.details) return body.details;
  }
  if (typeof body === 'string' && body.trim()) return body;
  const labels = { 400: 'The request could not be processed.', 401: 'Please sign in again.', 403: 'You do not have permission to do that.', 404: 'The requested item was not found.', 500: 'The server encountered an error.' };
  return labels[status] || `Request failed (${status}).`;
}

export async function api(path, options = {}) {
  const { body, headers = {}, ...fetchOptions } = options;
  const token = localStorage.getItem(TOKEN_KEY);
  const requestHeaders = new Headers(headers);
  if (token) requestHeaders.set('Authorization', `Bearer ${token}`);
  let requestBody = body;
  if (body !== undefined && !(body instanceof FormData) && typeof body !== 'string') {
    requestHeaders.set('Content-Type', 'application/json');
    requestBody = JSON.stringify(body);
  }
  let response;
  try {
    response = await fetch(`${API_BASE_URL}${path.startsWith('/') ? path : `/${path}`}`, {
      ...fetchOptions, headers: requestHeaders, body: requestBody,
    });
  } catch {
    throw new Error(`Could not reach the backend at ${API_BASE_URL}. Check that Spring Boot is running and VITE_API_BASE_URL is correct.`);
  }
  const text = await response.text();
  const data = parseResponseBody(text, response.headers.get('content-type') || '');
  if (!response.ok) throw new Error(errorMessage(data, response.status));
  return data;
}

export async function apiList(path, options = {}) {
  const data = await api(path, options);
  if (!Array.isArray(data)) throw new Error('The backend returned an unexpected response format. Please try again.');
  return data;
}

export const authStorage = {
  tokenKey: TOKEN_KEY,
  getToken: () => localStorage.getItem(TOKEN_KEY),
  setToken: (token) => localStorage.setItem(TOKEN_KEY, token),
  clear: () => localStorage.removeItem(TOKEN_KEY),
};
