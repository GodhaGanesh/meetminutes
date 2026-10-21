import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../auth/useAuth';

const links = [
  { to: '/', label: 'Overview', icon: '⌂', end: true },
  { to: '/meetings', label: 'Meetings', icon: '▦' },
  { to: '/tasks', label: 'My tasks', icon: '✓' },
];

export default function AppLayout() {
  const { profile, logout } = useAuth();
  const location = useLocation();
  const pageName = links.find((item) => item.to === location.pathname)?.label || (location.pathname.includes('/meetings/') ? 'Meeting details' : 'Workspace');
  return <div className="app-shell">
    <aside className="sidebar">
      <NavLink to="/" className="brand"><span className="brand-mark">mm</span><span>meet<span className="brand-light">minutes</span></span></NavLink>
      <div className="workspace-label">WORKSPACE</div>
      <nav className="side-nav" aria-label="Main navigation">{links.map((link) => <NavLink key={link.to} to={link.to} end={link.end} className={({ isActive }) => `nav-item${isActive ? ' active' : ''}`}><span className="nav-icon">{link.icon}</span>{link.label}</NavLink>)}</nav>
      <div className="sidebar-bottom"><div className="help-card"><span className="help-star">✦</span><strong>Keep every meeting moving</strong><small>Minutes and follow-ups, all in one place.</small></div><button className="profile-button" onClick={logout}><span className="avatar">{(profile.email || 'U').slice(0, 1).toUpperCase()}</span><span className="profile-text"><strong>{profile.email || 'Your account'}</strong><small>Sign out</small></span><span className="logout-icon">↗</span></button></div>
    </aside>
    <div className="main-column"><header className="topbar"><div className="mobile-brand"><span className="brand-mark">mm</span> meetminutes</div><span className="breadcrumb">Workspace <span>/</span> {pageName}</span><div className="topbar-right"><span className="live-dot" /> Connected workspace <span className="top-avatar">{(profile.email || 'U').slice(0, 1).toUpperCase()}</span></div></header><main className="page-content"><Outlet /></main><footer className="footer">Meet Minutes <span>•</span> Make every conversation count</footer></div>
  </div>;
}
