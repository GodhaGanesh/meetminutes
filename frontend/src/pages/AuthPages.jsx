import { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { api } from '../api/api';
import { useAuth } from '../auth/useAuth';
import { Field, Notice } from '../components/UI';

function AuthFrame({ mode, children }) {
  return <div className="auth-page"><div className="auth-showcase"><Link to="/login" className="brand auth-brand"><span className="brand-mark">mm</span><span>meet<span className="brand-light">minutes</span></span></Link><div className="showcase-content"><span className="showcase-kicker">MEET WITH PURPOSE</span><h1>Good meetings<br />move work forward.</h1><p>Bring your conversations, decisions, and next steps together in one calm workspace.</p><div className="showcase-graphic"><span className="orb orb-one" /><span className="orb orb-two" /><div className="graphic-card"><div className="graphic-top"><span>✦</span> MEETING SNAPSHOT</div><strong>Weekly team sync</strong><small>Decisions made · Actions assigned</small><div className="graphic-lines"><i /><i /><i /></div><div className="graphic-people"><b>AR</b><b>SK</b><b>+3</b><span>5 participants</span></div></div></div></div><div className="showcase-bottom">A little more clarity after every conversation.</div></div><div className="auth-panel"><div className="auth-mobile-brand"><span className="brand-mark">mm</span> meetminutes</div><div className="auth-card"><div className="eyebrow">{mode === 'login' ? 'WELCOME BACK' : 'GET STARTED'}</div>{children}</div><div className="auth-foot">© {new Date().getFullYear()} Meet Minutes <span>·</span> Built for better collaboration</div></div></div>;
}

export function LoginPage() {
  const { token, login } = useAuth();
  const navigate = useNavigate(); const location = useLocation();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  if (token) return <Navigate to="/" replace />;
  const submit = async (event) => {
    event.preventDefault(); setError(''); setBusy(true);
    try { const result = await api('/auth/login', { method: 'POST', body: form }); login(result.token, form.email); navigate('/', { replace: true }); }
    catch (err) { setError(err.message); }
    finally { setBusy(false); }
  };
  return <AuthFrame mode="login"><h2>Sign in to your workspace</h2><p className="auth-subtitle">Pick up where your team left off.</p><Notice type="success">{location.state?.message}</Notice><form onSubmit={submit} className="auth-form"><Field label="Email address" type="email" autoComplete="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="you@college.edu" /><Field label="Password" type="password" autoComplete="current-password" required value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} placeholder="Enter your password" /><Notice>{error}</Notice><button className="button primary full" disabled={busy}>{busy ? 'Signing in…' : 'Sign in'}<span>→</span></button></form><div className="auth-switch">New to Meet Minutes? <Link to="/register">Create an account</Link></div><p className="secure-note"><span>◈</span> Your workspace is protected with secure sign-in.</p></AuthFrame>;
}

export function RegisterPage() {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ fullName: '', email: '', password: '' });
  const [error, setError] = useState(''); const [busy, setBusy] = useState(false);
  if (token) return <Navigate to="/" replace />;
  const submit = async (event) => {
    event.preventDefault(); setError(''); setBusy(true);
    try { await api('/auth/register', { method: 'POST', body: form }); navigate('/login', { state: { message: 'Account created. Sign in with your new credentials.' } }); }
    catch (err) { setError(err.message); }
    finally { setBusy(false); }
  };
  return <AuthFrame mode="register"><h2>Create your account</h2><p className="auth-subtitle">Set up your workspace in a few seconds.</p><form onSubmit={submit} className="auth-form"><Field label="Full name" autoComplete="name" required maxLength="120" value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} placeholder="Your name" /><Field label="Email address" type="email" autoComplete="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="you@college.edu" /><Field label="Password" type="password" autoComplete="new-password" required minLength="1" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} placeholder="Create a password" /><Notice>{error}</Notice><button className="button primary full" disabled={busy}>{busy ? 'Creating account…' : 'Create account'}<span>→</span></button></form><div className="auth-switch">Already have an account? <Link to="/login">Sign in</Link></div><p className="secure-note"><span>◈</span> Your workspace is protected with secure sign-in.</p></AuthFrame>;
}
