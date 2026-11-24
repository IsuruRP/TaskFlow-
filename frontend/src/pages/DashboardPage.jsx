import { AlertTriangle, Bell, CheckCircle2, FolderKanban } from 'lucide-react'
import { useEffect, useState } from 'react'
import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from 'recharts'
import { api, errorMessage } from '../api'
import { useAuth } from '../auth'

export default function DashboardPage() {
  const { user } = useAuth()
  const [stats, setStats] = useState({ projectCount: 0, assignedTasks: 0, overdueTasks: 0, unreadNotifications: 0 })
  const [error, setError] = useState('')
  useEffect(() => { api.get('/dashboard').then(({ data }) => setStats(data)).catch((e) => setError(errorMessage(e))) }, [])
  const cards = [
    ['Active projects', stats.projectCount, FolderKanban, 'blue'],
    ['Assigned tasks', stats.assignedTasks, CheckCircle2, 'violet'],
    ['Overdue', stats.overdueTasks, AlertTriangle, 'orange'],
    ['Unread updates', stats.unreadNotifications, Bell, 'green'],
  ]
  const chart = [{ name: 'On track', value: Math.max(stats.assignedTasks - stats.overdueTasks, 0) }, { name: 'Overdue', value: stats.overdueTasks }]
  return (
    <>
      <div className="page-heading"><div><span className="eyebrow">OVERVIEW</span><h1>Good to see you, {user?.fullName?.split(' ')[0]}.</h1><p>Here is what is moving across your workspace.</p></div></div>
      {error && <div className="alert">{error}</div>}
      <section className="stat-grid">{cards.map(([label, value, Icon, tone]) => <article className="stat-card" key={label}><div className={`stat-icon ${tone}`}><Icon /></div><div><span>{label}</span><strong>{value}</strong></div></article>)}</section>
      <section className="dashboard-grid">
        <article className="panel"><div className="panel-heading"><div><h2>Delivery health</h2><p>Your assigned work at a glance</p></div></div>
          <div className="chart-wrap"><ResponsiveContainer width="100%" height={250}><PieChart><Pie data={chart} dataKey="value" innerRadius={70} outerRadius={95} paddingAngle={5}><Cell fill="#21c997"/><Cell fill="#ff8a65"/></Pie><Tooltip /></PieChart></ResponsiveContainer><div className="chart-center"><strong>{stats.assignedTasks}</strong><span>tasks</span></div></div>
        </article>
        <article className="panel focus-panel"><span className="eyebrow">FOCUS FOR TODAY</span><h2>Make the important work visible.</h2><p>Open a project, assign ownership, and move tasks through the board as the team makes progress.</p><a className="button primary" href="/projects">Explore projects</a></article>
      </section>
    </>
  )
}
