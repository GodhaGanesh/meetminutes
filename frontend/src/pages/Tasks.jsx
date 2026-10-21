import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api, apiList } from '../api/api';
import { EmptyState, Loading, Notice, PageHeader, StatusBadge } from '../components/UI';

const statuses = ['ALL', 'PENDING', 'IN_PROGRESS', 'COMPLETED'];

export default function Tasks() {
  const [tasks, setTasks] = useState([]); const [filter, setFilter] = useState('ALL'); const [loading, setLoading] = useState(true); const [error, setError] = useState('');
  const load = () => { setLoading(true); apiList('/action-items/my-tasks').then(setTasks).catch((err) => setError(err.message)).finally(() => setLoading(false)); };
  useEffect(() => { load(); }, []);
  const update = async (task, status) => { setError(''); try { await api(`/meetings/${task.meetingId}/action-items/${task.id}/status`, { method: 'PATCH', body: { status } }); load(); } catch (err) { setError(err.message); } };
  const visible = filter === 'ALL' ? tasks : tasks.filter((task) => task.status === filter);
  return <><PageHeader eyebrow="YOUR FOLLOW-UPS" title="My tasks" description="The action items assigned to you, across all your meetings." /><Notice>{error}</Notice><div className="task-overview"><div className="task-overview-main"><span className="task-overview-icon">✓</span><span><strong>{tasks.filter((task) => task.status !== 'COMPLETED').length} open items</strong><small>Stay on top of what you’ve committed to.</small></span></div><div className="task-progress"><div className="progress-track"><span style={{ width: `${tasks.length ? (tasks.filter((task) => task.status === 'COMPLETED').length / tasks.length) * 100 : 0}%` }} /></div><small>{tasks.filter((task) => task.status === 'COMPLETED').length} of {tasks.length} completed</small></div></div>
    <div className="tabs task-tabs">{statuses.map((status) => <button key={status} className={filter === status ? 'selected' : ''} onClick={() => setFilter(status)}>{status === 'ALL' ? 'All tasks' : status.replace('_', ' ')}</button>)}</div>
    {loading ? <Loading label="Loading your tasks…" /> : visible.length ? <section className="panel list-panel task-table"><div className="task-table-head"><span>Task</span><span>Meeting</span><span>Due date</span><span>Status</span><span>Update</span></div>{visible.map((task) => <article className="task-table-row" key={`${task.meetingId}-${task.id}`}><div className="task-description"><span className={`task-check ${task.status === 'COMPLETED' ? 'checked' : ''}`}>{task.status === 'COMPLETED' ? '✓' : ''}</span><strong>{task.task}</strong></div><Link className="task-meeting" to={`/meetings/${task.meetingId}`}>{task.meetingTitle || `Meeting #${task.meetingId}`} <span>↗</span></Link><span className="task-deadline">{task.deadline || '—'}</span><StatusBadge value={task.status} /><select aria-label={`Update status for ${task.task}`} value={task.status} onChange={(e) => update(task, e.target.value)}>{['PENDING', 'IN_PROGRESS', 'COMPLETED'].map((status) => <option key={status}>{status}</option>)}</select></article>)}</section> : <section className="panel list-panel"><EmptyState title={filter === 'ALL' ? 'Nothing on your task list yet' : `No ${filter.toLowerCase().replace('_', ' ')} tasks`} action={filter === 'ALL' && <Link className="button secondary small" to="/meetings">Browse meetings</Link>}>Action items assigned to your account will appear here.</EmptyState></section>}
  </>;
}
