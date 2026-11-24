import { Bell, Check } from 'lucide-react'
import { useEffect, useState } from 'react'
import { api, errorMessage } from '../api'

export default function NotificationsPage() {
  const [items, setItems] = useState([]), [error, setError] = useState('')
  const load = () => api.get('/notifications').then(({ data }) => setItems(data)).catch((e) => setError(errorMessage(e)))
  useEffect(() => { api.get('/notifications').then(({ data }) => setItems(data)).catch((e) => setError(errorMessage(e))) }, [])
  const read = (id) => api.patch(`/notifications/${id}/read`).then(load).catch((e) => setError(errorMessage(e)))
  return <><div className="page-heading"><div><span className="eyebrow">INBOX</span><h1>Notifications</h1><p>Assignments and updates that need your attention.</p></div></div>{error&&<div className="alert">{error}</div>}<section className="notification-list">{items.map((item)=><article className={item.read?'notification read':'notification'} key={item.id}><div className="notification-icon"><Bell size={19}/></div><div><strong>{item.message}</strong><span>{new Date(item.createdAt).toLocaleString()}</span></div>{!item.read&&<button onClick={()=>read(item.id)}><Check size={18}/>Mark read</button>}</article>)}</section>{!items.length&&<div className="empty"><div className="empty-icon"><Bell/></div><h2>You are all caught up</h2><p>New project activity will appear here.</p></div>}</>
}
