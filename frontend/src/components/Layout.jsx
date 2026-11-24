import { Bell, FolderKanban, LayoutDashboard, LogOut, Menu, Search, ShieldCheck, X } from 'lucide-react'
import { useState } from 'react'
import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth'

const baseLinks = [
  { to: '/', label: 'Overview', icon: LayoutDashboard, end: true },
  { to: '/projects', label: 'Projects', icon: FolderKanban },
  { to: '/notifications', label: 'Notifications', icon: Bell },
]

export default function Layout() {
  const [open, setOpen] = useState(false)
  const { user, logout } = useAuth()
  const links = user?.role === 'ADMIN' ? [...baseLinks, { to: '/admin/users', label: 'User admin', icon: ShieldCheck }] : baseLinks
  const navigate = useNavigate()
  const leave = () => { logout(); navigate('/login') }

  return (
    <div className="app-shell">
      <aside className={`sidebar ${open ? 'open' : ''}`}>
        <div className="brand"><span className="brand-mark">T</span><span>TaskFlow</span></div>
        <button className="mobile-close" onClick={() => setOpen(false)} aria-label="Close menu"><X /></button>
        <nav>
          <p className="nav-label">Workspace</p>
          {links.map(({ to, label, icon: Icon, end }) => (
            <NavLink key={to} to={to} end={end} onClick={() => setOpen(false)} className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
              <Icon size={19} />{label}
            </NavLink>
          ))}
        </nav>
        <div className="user-card">
          <div className="avatar">{user?.fullName?.[0]}</div>
          <div><strong>{user?.fullName}</strong><small>{user?.role?.replaceAll('_', ' ')}</small></div>
          <button onClick={leave} aria-label="Log out"><LogOut size={18} /></button>
        </div>
      </aside>
      {open && <button className="overlay" onClick={() => setOpen(false)} aria-label="Close menu" />}
      <main className="main-area">
        <header className="topbar">
          <button className="menu-button" onClick={() => setOpen(true)}><Menu /></button>
          <div className="search"><Search size={18} /><input placeholder="Search your workspace" /></div>
          <NavLink to="/notifications" className="icon-button"><Bell size={20} /></NavLink>
        </header>
        <div className="page"><Outlet /></div>
      </main>
    </div>
  )
}
