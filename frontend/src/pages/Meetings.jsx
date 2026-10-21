import { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { api, apiList } from '../api/api';
import { EmptyState, Field, Loading, MeetingLink, Notice, PageHeader, StatusBadge } from '../components/UI';

const blank = { title: '', description: '', meetingDate: '', startTime: '', endTime: '', location: '', meetingLink: '' };

export default function Meetings() {
  const [meetings, setMeetings] = useState([]); const [invitations, setInvitations] = useState([]);
  const [loading, setLoading] = useState(true); const [busy, setBusy] = useState(false); const [error, setError] = useState(''); const [form, setForm] = useState(blank);
  const [showForm, setShowForm] = useState(false); const [tab, setTab] = useState('meetings');
  const location = useLocation(); const navigate = useNavigate();
  const reload = async () => {
    setLoading(true); setError('');
    const results = await Promise.allSettled([apiList('/meetings'), apiList('/meetings/invitations')]);
    if (results[0].status === 'fulfilled') setMeetings(results[0].value); else setError(results[0].reason.message);
    if (results[1].status === 'fulfilled') setInvitations(results[1].value); else if (results[0].status === 'fulfilled') setError(results[1].reason.message);
    setLoading(false);
  };
  useEffect(() => { reload(); }, []);
  useEffect(() => { if (new URLSearchParams(location.search).has('new')) setShowForm(true); }, [location.search]);
  const create = async (event) => {
    event.preventDefault(); setBusy(true); setError('');
    try { const meeting = await api('/meetings', { method: 'POST', body: form }); setShowForm(false); setForm(blank); navigate(`/meetings/${meeting.id}`); }
    catch (err) { setError(err.message); }
    finally { setBusy(false); }
  };
  const respond = async (id, accept) => {
    setError('');
    try { await api(`/meetings/${id}/participants/${accept ? 'accept' : 'decline'}`, { method: 'POST' }); await reload(); }
    catch (err) { setError(err.message); }
  };
  return <><PageHeader eyebrow="YOUR CALENDAR" title="Meetings" description="Plan conversations and keep the outcomes easy to find." action={<button className="button primary" onClick={() => setShowForm((value) => !value)}><span>＋</span> Schedule meeting</button>} />
    <Notice>{error}</Notice>
    {showForm && <section className="panel form-panel"><div className="panel-heading"><div><div className="eyebrow">NEW EVENT</div><h2>Schedule a meeting</h2></div><button className="icon-button" aria-label="Close form" onClick={() => setShowForm(false)}>×</button></div><form className="form-grid" onSubmit={create}><Field label="Meeting title" required maxLength="150" placeholder="e.g. Project planning" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} /><Field label="Date" type="date" required value={form.meetingDate} onChange={(e) => setForm({ ...form, meetingDate: e.target.value })} /><Field label="Start time" type="time" value={form.startTime} onChange={(e) => setForm({ ...form, startTime: e.target.value })} /><Field label="End time" type="time" value={form.endTime} onChange={(e) => setForm({ ...form, endTime: e.target.value })} /><Field label="Location" placeholder="Room or video platform" value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} /><Field label="Meeting link" type="url" placeholder="https://…" value={form.meetingLink} onChange={(e) => setForm({ ...form, meetingLink: e.target.value })} /><label className="field field-wide"><span>Description</span><textarea rows="3" maxLength="2000" placeholder="What would you like to cover?" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} /></label><div className="form-actions field-wide"><button type="button" className="button ghost" onClick={() => setShowForm(false)}>Cancel</button><button className="button primary" disabled={busy}>{busy ? 'Creating…' : 'Create meeting'} <span>→</span></button></div></form></section>}
    <div className="tabs"><button className={tab === 'meetings' ? 'selected' : ''} onClick={() => setTab('meetings')}>Meetings <span>{meetings.length}</span></button><button className={tab === 'invitations' ? 'selected' : ''} onClick={() => setTab('invitations')}>Invitations <span>{invitations.length}</span></button></div>
    {loading ? <Loading label="Loading meetings…" /> : tab === 'meetings' ? <section className="panel list-panel">{meetings.length ? <div className="meeting-list">{meetings.map((meeting) => <div className="meeting-row expanded" key={meeting.id}><MeetingLink meeting={meeting} /><div className="meeting-extra"><span>{meeting.location || 'Location not set'}</span><StatusBadge value={meeting.status} /></div></div>)}</div> : <EmptyState title="No meetings yet" action={<button className="button primary small" onClick={() => setShowForm(true)}>Schedule your first meeting</button>}>Your meetings will appear here once you create one.</EmptyState>}</section> : <section className="panel list-panel">{invitations.length ? <div className="invite-list">{invitations.map((inv) => <article className="invite-row" key={inv.meetingId}><div className="meeting-date-icon"><strong>{inv.meetingDate ? inv.meetingDate.slice(8, 10) : '—'}</strong><small>{inv.meetingDate ? new Date(`${inv.meetingDate}T00:00:00`).toLocaleString('en', { month: 'short' }) : ''}</small></div><div className="invite-info"><h3>{inv.title}</h3><p>Invited by {inv.invitedByName || 'a teammate'} · {inv.meetingDate || 'Date not set'}{inv.startTime ? ` at ${inv.startTime.slice(0, 5)}` : ''}</p><StatusBadge value={inv.status} /></div><div className="invite-actions"><button className="button primary small" onClick={() => respond(inv.meetingId, true)}>Accept</button><button className="button ghost small" onClick={() => respond(inv.meetingId, false)}>Decline</button></div></article>)}</div> : <EmptyState title="You’re all caught up" >Pending meeting invitations will appear here.</EmptyState>}</section>}
  </>;
}
