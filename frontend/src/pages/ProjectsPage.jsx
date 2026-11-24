import { ArrowRight, CalendarDays, Plus } from 'lucide-react'
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, errorMessage } from '../api'
import { useAuth } from '../auth'
import Modal from '../components/Modal'

const emptyForm = { name: '', description: '', startDate: '', dueDate: '', status: 'PLANNING' }

export default function ProjectsPage() {
  const { user } = useAuth()
  const [projects, setProjects] = useState([]), [query, setQuery] = useState(''), [modal, setModal] = useState(false)
  const [form, setForm] = useState(emptyForm), [error, setError] = useState('')
  const canCreate = user?.role === 'ADMIN' || user?.role === 'PROJECT_MANAGER'
  const load = () => api.get('/projects', { params: { query, size: 50 } }).then(({ data }) => setProjects(data.content)).catch((e) => setError(errorMessage(e)))
  useEffect(() => {
		api.get('/projects', { params: { query, size: 50 } })
			.then(({ data }) => setProjects(data.content))
			.catch((e) => setError(errorMessage(e)))
	}, [query])
  const create = async (e) => { e.preventDefault(); try { await api.post('/projects', { ...form, startDate: form.startDate || null, dueDate: form.dueDate || null }); setModal(false); setForm(emptyForm); load() } catch (err) { setError(errorMessage(err)) } }
  return (
    <>
      <div className="page-heading"><div><span className="eyebrow">WORKSPACE</span><h1>Projects</h1><p>Everything your team is planning and delivering.</p></div>{canCreate && <button className="button primary" onClick={() => setModal(true)}><Plus size={18}/>New project</button>}</div>
      <div className="toolbar"><input className="search-input" value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search projects…"/><span>{projects.length} projects</span></div>
      {error && <div className="alert">{error}</div>}
      <section className="project-grid">{projects.map((project) => <Link className="project-card" to={`/projects/${project.id}`} key={project.id}><div className="project-accent"/><div className="project-card-top"><span className={`badge ${project.status.toLowerCase()}`}>{project.status.replaceAll('_', ' ')}</span><ArrowRight size={19}/></div><h2>{project.name}</h2><p>{project.description || 'No description added yet.'}</p><footer><span><CalendarDays size={16}/>{project.dueDate || 'No deadline'}</span><span>{project.ownerName}</span></footer></Link>)}</section>
      {!projects.length && <div className="empty"><FolderIcon/><h2>No projects found</h2><p>Create your first project or try another search.</p></div>}
      {modal && <Modal title="Create project" onClose={() => setModal(false)}><form className="stack-form" onSubmit={create}><label>Project name<input required maxLength="120" value={form.name} onChange={(e) => setForm({...form, name:e.target.value})}/></label><label>Description<textarea maxLength="2000" value={form.description} onChange={(e) => setForm({...form, description:e.target.value})}/></label><div className="form-row"><label>Start date<input type="date" value={form.startDate} onChange={(e) => setForm({...form, startDate:e.target.value})}/></label><label>Due date<input type="date" value={form.dueDate} onChange={(e) => setForm({...form, dueDate:e.target.value})}/></label></div><button className="button primary">Create project</button></form></Modal>}
    </>
  )
}

function FolderIcon(){ return <div className="empty-icon">TF</div> }
