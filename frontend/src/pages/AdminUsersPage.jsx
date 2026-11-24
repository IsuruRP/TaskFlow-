import { ShieldCheck, UserCheck, UserX } from 'lucide-react'
import { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { api, errorMessage } from '../api'
import { useAuth } from '../auth'

export default function AdminUsersPage() {
  const { user } = useAuth()
  const [users, setUsers] = useState([]), [error, setError] = useState('')
  const load = () => api.get('/users', { params: { size: 100, sort: 'fullName,asc' } }).then(({ data }) => setUsers(data.content)).catch((e) => setError(errorMessage(e)))
  useEffect(() => { api.get('/users', { params: { size: 100, sort: 'fullName,asc' } }).then(({ data }) => setUsers(data.content)).catch((e) => setError(errorMessage(e))) }, [])
  if (user?.role !== 'ADMIN') return <Navigate to="/" replace />
  const role = (id, value) => api.patch(`/users/${id}/role`, { role: value }).then(load).catch((e) => setError(errorMessage(e)))
  const enabled = (id, value) => api.patch(`/users/${id}/enabled`, null, { params: { value } }).then(load).catch((e) => setError(errorMessage(e)))
  return <><div className="page-heading"><div><span className="eyebrow">ADMINISTRATION</span><h1>User access</h1><p>Manage roles and account availability.</p></div><ShieldCheck size={32}/></div>{error&&<div className="alert">{error}</div>}<div className="table-wrap"><table><thead><tr><th>User</th><th>Role</th><th>Status</th><th>Action</th></tr></thead><tbody>{users.map((item)=><tr key={item.id}><td><strong>{item.fullName}</strong><small>{item.email}</small></td><td><select value={item.role} onChange={(e)=>role(item.id,e.target.value)} disabled={item.id===user.id}><option>ADMIN</option><option>PROJECT_MANAGER</option><option>TEAM_MEMBER</option></select></td><td><span className={item.enabled?'status-dot enabled':'status-dot'}>{item.enabled?'Active':'Disabled'}</span></td><td><button className="table-action" disabled={item.id===user.id} onClick={()=>enabled(item.id,!item.enabled)}>{item.enabled?<UserX size={16}/>:<UserCheck size={16}/>} {item.enabled?'Disable':'Enable'}</button></td></tr>)}</tbody></table></div></>
}
