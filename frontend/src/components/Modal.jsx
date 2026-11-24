import { X } from 'lucide-react'

export default function Modal({ title, children, onClose }) {
  return (
    <div className="modal-backdrop" role="presentation" onMouseDown={onClose}>
      <section className="modal" role="dialog" aria-modal="true" onMouseDown={(e) => e.stopPropagation()}>
        <header><h2>{title}</h2><button onClick={onClose}><X /></button></header>
        {children}
      </section>
    </div>
  )
}
