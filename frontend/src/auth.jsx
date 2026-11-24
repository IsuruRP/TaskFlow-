import { createContext, useContext, useState } from 'react'
import { api } from './api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('taskflow_user') || 'null'))

  const authenticate = async (path, payload) => {
    const { data } = await api.post(`/auth/${path}`, payload)
    localStorage.setItem('taskflow_token', data.token)
    localStorage.setItem('taskflow_user', JSON.stringify(data.user))
    setUser(data.user)
  }

  const logout = () => {
    localStorage.removeItem('taskflow_token')
    localStorage.removeItem('taskflow_user')
    setUser(null)
  }

  const value = { user, login: (payload) => authenticate('login', payload), register: (payload) => authenticate('register', payload), logout }
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)
