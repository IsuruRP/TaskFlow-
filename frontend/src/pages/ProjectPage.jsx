import { ArrowLeft, CalendarDays, MessageSquare, Plus, Trash2, UserRound } from 'lucide-react'
import { useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api, errorMessage } from '../api'
import Modal from '../components/Modal'
import { useAuth } from '../auth'

const columns = ['BACKLOG', 'TODO', 'IN_PROGRESS', 'REVIEW', 'DONE']
const emptyTask = { title: '', description: '', priority: 'MEDIUM', assigneeId: '', dueDate: '' }

export default function ProjectPage() {
  const { projectId } = useParams()
	const { user } = useAuth()
  const [project, setProject] = useState(null), [tasks, setTasks] = useState([]), [modal, setModal] = useState(false)
  const [form, setForm] = useState(emptyTask), [error, setError] = useState('')
	const [memberModal, setMemberModal] = useState(false), [directory, setDirectory] = useState([]), [selectedUser, setSelectedUser] = useState('')
	const [commentTask, setCommentTask] = useState(null), [comments, setComments] = useState([]), [commentText, setCommentText] = useState('')
  const load = () => Promise.all([api.get(`/projects/${projectId}`), api.get(`/projects/${projectId}/tasks`, { params: { size: 200 } })])
    .then(([p, t]) => { setProject(p.data); setTasks(t.data.content) }).catch((e) => setError(errorMessage(e)))
  useEffect(() => {
		Promise.all([api.get(`/projects/${projectId}`), api.get(`/projects/${projectId}/tasks`, { params: { size: 200 } })])
			.then(([p, t]) => { setProject(p.data); setTasks(t.data.content) })
			.catch((e) => setError(errorMessage(e)))
	}, [projectId])
  const grouped = useMemo(() => Object.fromEntries(columns.map((s) => [s, tasks.filter((t) => t.status === s)])), [tasks])
  const create = async (e) => { e.preventDefault(); try { await api.post(`/projects/${projectId}/tasks`, { ...form, assigneeId: form.assigneeId ? Number(form.assigneeId) : null, dueDate: form.dueDate || null }); setForm(emptyTask); setModal(false); load() } catch (err) { setError(errorMessage(err)) } }
  const move = async (task, status) => { try { await api.patch(`/tasks/${task.id}/status`, { status }); load() } catch (err) { setError(errorMessage(err)) } }
  const remove = async (task) => { if (!confirm(`Delete “${task.title}”?`)) return; try { await api.delete(`/tasks/${task.id}`); load() } catch (err) { setError(errorMessage(err)) } }
	const openMembers = async () => { try { const { data } = await api.get('/users/directory'); setDirectory(data.filter((item) => !project.members.some((m) => m.userId === item.id))); setMemberModal(true) } catch (err) { setError(errorMessage(err)) } }
	const addMember = async (e) => { e.preventDefault(); try { await api.post(`/projects/${projectId}/members`, { userId: Number(selectedUser) }); setSelectedUser(''); setMemberModal(false); load() } catch (err) { setError(errorMessage(err)) } }
	const removeMember = async (member) => { if (!confirm(`Remove ${member.fullName} from this project?`)) return; try { await api.delete(`/projects/${projectId}/members/${member.userId}`); load() } catch (err) { setError(errorMessage(err)) } }
	const openComments = async (task) => { try { const { data } = await api.get(`/tasks/${task.id}/comments`); setComments(data); setCommentTask(task) } catch (err) { setError(errorMessage(err)) } }
	const addComment = async (e) => { e.preventDefault(); try { const { data } = await api.post(`/tasks/${commentTask.id}/comments`, { content: commentText }); setComments([...comments, data]); setCommentText('') } catch (err) { setError(errorMessage(err)) } }
	const canManage = user?.role === 'ADMIN' || project?.project.ownerId === user?.id
  if (!project) return <div className="loading">Loading project…</div>
  return (
    <>
      <Link className="back-link" to="/projects"><ArrowLeft size={17}/>All projects</Link>
      <div className="page-heading"><div><span className={`badge ${project.project.status.toLowerCase()}`}>{project.project.status.replaceAll('_',' ')}</span><h1>{project.project.name}</h1><p>{project.project.description}</p></div>{canManage&&<button className="button primary" onClick={() => setModal(true)}><Plus size={18}/>Add task</button>}</div>
      <div className="project-meta"><span><UserRound size={17}/>{project.members.length} members</span><span><CalendarDays size={17}/>Due {project.project.dueDate || 'not set'}</span>{canManage&&<button className="text-button" onClick={openMembers}>Manage members</button>}</div>
      {error && <div className="alert">{error}</div>}
      <div className="kanban">{columns.map((status) => <section className="kanban-column" key={status}><header><span>{status.replaceAll('_',' ')}</span><b>{grouped[status].length}</b></header><div className="task-list">{grouped[status].map((task) => <article className="task-card" key={task.id}><div className="task-card-head"><span className={`priority ${task.priority.toLowerCase()}`}>{task.priority}</span>{canManage&&<button onClick={() => remove(task)}><Trash2 size={15}/></button>}</div><h3>{task.title}</h3><p>{task.description}</p><footer><span><UserRound size={14}/>{task.assigneeName || 'Unassigned'}</span><button className="comment-button" onClick={()=>openComments(task)}><MessageSquare size={14}/>Comments</button></footer><select value={task.status} onChange={(e) => move(task, e.target.value)}>{columns.map((s) => <option value={s} key={s}>{s.replaceAll('_',' ')}</option>)}</select></article>)}</div></section>)}</div>
      {modal && <Modal title="Create task" onClose={() => setModal(false)}><form className="stack-form" onSubmit={create}><label>Task title<input required maxLength="160" value={form.title} onChange={(e)=>setForm({...form,title:e.target.value})}/></label><label>Description<textarea maxLength="3000" value={form.description} onChange={(e)=>setForm({...form,description:e.target.value})}/></label><div className="form-row"><label>Priority<select value={form.priority} onChange={(e)=>setForm({...form,priority:e.target.value})}><option>LOW</option><option>MEDIUM</option><option>HIGH</option><option>URGENT</option></select></label><label>Assignee<select value={form.assigneeId} onChange={(e)=>setForm({...form,assigneeId:e.target.value})}><option value="">Unassigned</option>{project.members.map((m)=><option value={m.userId} key={m.userId}>{m.fullName}</option>)}</select></label></div><label>Due date<input type="date" value={form.dueDate} onChange={(e)=>setForm({...form,dueDate:e.target.value})}/></label><button className="button primary">Create task</button></form></Modal>}
		{memberModal && <Modal title="Manage project members" onClose={()=>setMemberModal(false)}><div className="member-list">{project.members.map((member,index)=><div key={member.userId}><span><strong>{member.fullName}</strong><small>{member.email}</small></span>{index>0&&<button onClick={()=>removeMember(member)}><Trash2 size={16}/></button>}</div>)}</div><form className="inline-form" onSubmit={addMember}><select required value={selectedUser} onChange={(e)=>setSelectedUser(e.target.value)}><option value="">Choose a user...</option>{directory.map((item)=><option value={item.id} key={item.id}>{item.fullName} - {item.email}</option>)}</select><button className="button primary">Add member</button></form></Modal>}
		{commentTask && <Modal title={`Comments - ${commentTask.title}`} onClose={()=>setCommentTask(null)}><div className="comment-list">{comments.map((comment)=><article key={comment.id}><strong>{comment.authorName}</strong><p>{comment.content}</p><small>{new Date(comment.createdAt).toLocaleString()}</small></article>)}{!comments.length&&<p className="muted">No comments yet.</p>}</div><form className="inline-form" onSubmit={addComment}><input required maxLength="2000" value={commentText} onChange={(e)=>setCommentText(e.target.value)} placeholder="Write a comment..."/><button className="button primary">Send</button></form></Modal>}
    </>
  )
}
