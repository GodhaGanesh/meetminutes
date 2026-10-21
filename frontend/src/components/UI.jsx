import { Link } from 'react-router-dom';

export function PageHeader({ eyebrow, title, description, action }) {
  return <div className="page-header"><div><div className="eyebrow">{eyebrow}</div><h1>{title}</h1>{description && <p>{description}</p>}</div>{action && <div className="header-action">{action}</div>}</div>;
}

export function StatusBadge({ value }) {
  const key = String(value || 'unknown').toLowerCase().replaceAll('_', '-');
  return <span className={`badge badge-${key}`}>{String(value || 'Unknown').replaceAll('_', ' ')}</span>;
}

export function Notice({ children, type = 'error' }) {
  return children ? <div className={`notice notice-${type}`} role={type === 'error' ? 'alert' : 'status'}>{children}</div> : null;
}

export function Loading({ label = 'Loading…' }) {
  return <div className="loading"><span className="spinner" aria-hidden="true" />{label}</div>;
}

export function EmptyState({ title, children, action }) {
  return <div className="empty-state"><div className="empty-icon">✦</div><h3>{title}</h3><p>{children}</p>{action}</div>;
}

export function MeetingLink({ meeting }) {
  return <Link className="meeting-link" to={`/meetings/${meeting.id}`}><span className="meeting-mark">M</span><span><strong>{meeting.title}</strong><small>{meeting.meetingDate || 'Date not set'}{meeting.startTime ? ` · ${meeting.startTime.slice(0, 5)}` : ''}</small></span><span className="link-arrow">↗</span></Link>;
}

export function Field({ label, ...props }) {
  return <label className="field"><span>{label}</span><input {...props} /></label>;
}
