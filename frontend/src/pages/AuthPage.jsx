import { ArrowRight, CheckCircle2, Layers3 } from 'lucide-react'
import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { errorMessage } from '../api'
import { useAuth } from '../auth'

export default function AuthPage({ mode }) {
  const registering = mode === 'register'
  const [form, setForm] = useState({ fullName: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const { user, login, register } = useAuth()
  const navigate = useNavigate()
  if (user) return <Navigate to="/" replace />

  const submit = async (e) => {
    e.preventDefault(); setBusy(true); setError('')
    try { await (registering ? register(form) : login({ email: form.email, password: form.password })); navigate('/') }
    catch (err) { setError(errorMessage(err)) }
    finally { setBusy(false) }
  }

  return (
    <main className="auth-shell">
      <section className="auth-story">
        <div className="brand light"><span className="brand-mark">T</span><span>TaskFlow</span></div>
        <div><span className="eyebrow">SMART PROJECT MANAGEMENT</span><h1>Move work forward, together.</h1>
          <p>Plan projects, keep teams aligned, and turn ambitious ideas into finished work—all from one focused workspace.</p>
          <ul><li><CheckCircle2 /> Clear project ownership</li><li><CheckCircle2 /> Visual Kanban workflow</li><li><CheckCircle2 /> Live progress insights</li></ul>
        </div>
        <div className="story-card"><Layers3 /><div><strong>Built for momentum</strong><span>One source of truth for every team.</span></div></div>
      </section>
      <section className="auth-panel">
        <form className="auth-form" onSubmit={submit}>
          <span className="eyebrow">WELCOME TO TASKFLOW</span><h2>{registering ? 'Create your workspace' : 'Sign in to continue'}</h2>
          <p>{registering ? 'Start organizing your best work today.' : 'Your projects are waiting for you.'}</p>
          {error && <div className="alert">{error}</div>}
          {registering && <label>Full name<input required maxLength="100" value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} placeholder="Alex Morgan" /></label>}
          <label>Email address<input required type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="you@company.com" /></label>
          <label>Password<input required minLength="8" type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} placeholder="At least 8 characters" /></label>
          <button className="button primary wide" disabled={busy}>{busy ? 'Please wait…' : registering ? 'Create account' : 'Sign in'}<ArrowRight size={18} /></button>
          <p className="switch-auth">{registering ? 'Already have an account?' : 'New to TaskFlow?'} <Link to={registering ? '/login' : '/register'}>{registering ? 'Sign in' : 'Create an account'}</Link></p>
        </form>
      </section>
    </main>
  )
}
