import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiList } from '../api/api';
import { EmptyState, Loading, MeetingLink, Notice, PageHeader, StatusBadge } from '../components/UI';

function dateKey(value) { return value ? new Date(`${value}T00:00:00`).getTime() : 0; }

export default function Dashboard() {
  const [data, setData] = useState({ meetings: null, tasks: null, invitations: null });
  const [loading, setLoading] = useState(true); const [error, setError] = useState('');
  useEffect(() => {
    Promise.allSettled([apiList('/meetings/my'), apiList('/action-items/my-tasks'), apiList('/meetings/invitations')]).then((results) => {
      const failed = results.find((item) => item.status === 'rejected');
      setData({ meetings: results[0].status === 'fulfilled' ? results[0].value : null, tasks: results[1].status === 'fulfilled' ? results[1].value : null, invitations: results[2].status === 'fulfilled' ? results[2].value : null });
      if (failed) setError(failed.reason.message);
    }).finally(() => setLoading(false));
  }, []);
  const today = new Date(); today.setHours(0, 0, 0, 0);
  const upcoming = (data.meetings || []).filter((meeting) => dateKey(meeting.meetingDate) >= today.getTime() && !['COMPLETED', 'CANCELLED'].includes(meeting.status));
  const completed = (data.meetings || []).filter((meeting) => meeting.status === 'COMPLETED');
  const pending = (data.tasks || []).filter((task) => task.status !== 'COMPLETED');
  return <><PageHeader eyebrow="YOUR WORKSPACE" title="Good to see you." description="Here’s what’s happening across your meetings." action={<Link className="button primary" to="/meetings?new=1"><span>＋</span> New meeting</Link>} />
    <Notice>{error}</Notice>
    {loading ? <Loading label="Gathering your workspace…" /> : <>
      {data.invitations?.length > 0 && <Link className="invitation-banner" to="/meetings"><span className="invite-icon">✉</span><span><strong>You have {data.invitations.length} pending invitation{data.invitations.length === 1 ? '' : 's'}</strong><small>Review and respond to your meeting invitations</small></span><span className="banner-arrow">→</span></Link>}
      <section className="stat-grid" aria-label="Workspace statistics"><article className="stat-card"><div className="stat-label">Total meetings <span className="stat-icon purple">▦</span></div><strong>{data.meetings?.length ?? '—'}</strong><small>Meetings you organized</small><div className="stat-accent purple-bg" /></article><article className="stat-card"><div className="stat-label">Upcoming <span className="stat-icon blue">◷</span></div><strong>{data.meetings ? upcoming.length : '—'}</strong><small>Scheduled ahead</small><div className="stat-accent blue-bg" /></article><article className="stat-card"><div className="stat-label">Completed <span className="stat-icon green">✓</span></div><strong>{data.meetings ? completed.length : '—'}</strong><small>Meetings wrapped up</small><div className="stat-accent green-bg" /></article><article className="stat-card"><div className="stat-label">Open tasks <span className="stat-icon amber">↗</span></div><strong>{data.tasks ? pending.length : '—'}</strong><small>Assigned to you</small><div className="stat-accent amber-bg" /></article></section>
      <div className="dashboard-grid"><section className="panel recent-panel"><div className="panel-heading"><div><div className="eyebrow">STAY IN THE LOOP</div><h2>Recent meetings</h2></div><Link className="text-link" to="/meetings">All meetings <span>→</span></Link></div>{data.meetings === null ? <EmptyState title="Couldn’t load your meetings">Check your connection and reload to try again.</EmptyState> : data.meetings.length ? <div className="meeting-list">{[...data.meetings].sort((a, b) => dateKey(b.meetingDate) - dateKey(a.meetingDate)).slice(0, 5).map((meeting) => <div className="meeting-row" key={meeting.id}><MeetingLink meeting={meeting} /><StatusBadge value={meeting.status} /></div>)}</div> : <EmptyState title="Your meetings will show up here" action={<Link className="button secondary small" to="/meetings?new=1">Schedule a meeting</Link>}>Create your first meeting to keep the details and follow-ups together.</EmptyState>}</section>
        <section className="panel focus-panel"><div className="panel-heading"><div><div className="eyebrow">NEXT STEPS</div><h2>Task focus</h2></div><Link className="text-link" to="/tasks">View tasks <span>→</span></Link></div>{data.tasks === null ? <EmptyState title="Couldn’t load your tasks">Check your connection and reload to try again.</EmptyState> : data.tasks.length ? <div className="focus-list">{data.tasks.slice(0, 4).map((task) => <Link className="focus-task" key={task.id} to={`/meetings/${task.meetingId}`}><span className={`task-check ${task.status === 'COMPLETED' ? 'checked' : ''}`}>{task.status === 'COMPLETED' ? '✓' : ''}</span><span><strong>{task.task}</strong><small>{task.meetingTitle || 'Meeting'}{task.deadline ? ` · Due ${task.deadline}` : ''}</small></span></Link>)}</div> : <EmptyState title="No tasks assigned yet" action={<Link to="/tasks" className="text-link">Explore My tasks →</Link>}>Tasks assigned to you will be ready to track here.</EmptyState>}<Link to="/tasks" className="button soft full focus-cta">Open My tasks <span>→</span></Link></section></div>
      <div className="quick-actions"><div><span className="quick-icon">✦</span><span><strong>Make your next meeting count</strong><small>Keep decisions clear and follow-ups easy to find.</small></span></div><div className="quick-buttons"><Link className="button secondary" to="/meetings?new=1">Schedule a meeting</Link><Link className="button ghost" to="/tasks">Review my tasks <span>→</span></Link></div></div>
    </>}</>;
}
