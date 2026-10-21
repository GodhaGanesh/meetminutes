import { useMemo, useState } from 'react';
import { authStorage } from '../api/api';
import AuthContext from './context';

function decodePayload(token) {
  try { return JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/'))); }
  catch { return {}; }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => authStorage.getToken());
  const [profile, setProfile] = useState(() => {
    try { return JSON.parse(localStorage.getItem('meet-minutes-profile') || '{}'); } catch { return {}; }
  });
  const login = (newToken, email) => {
    authStorage.setToken(newToken);
    const claims = decodePayload(newToken);
    const next = { email, userId: claims.userId, role: claims.role };
    localStorage.setItem('meet-minutes-profile', JSON.stringify(next));
    setProfile(next);
    setToken(newToken);
  };
  const logout = () => {
    authStorage.clear();
    localStorage.removeItem('meet-minutes-profile');
    setToken(null);
    setProfile({});
  };
  const value = useMemo(() => ({ token, profile, login, logout }), [token, profile]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
