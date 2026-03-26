import { useEffect, useMemo, useState } from 'react'
import { api } from './services/api'
import StatCard from './components/StatCard'
import DocumentTable from './components/DocumentTable'

const emptyLogin = { email: '', password: '' }
const emptyUser = { name: '', email: '', password: '', role: 'STUDENT' }

export default function App() {
  const [auth, setAuth] = useState(() => {
    const token = localStorage.getItem('token')
    const user = localStorage.getItem('user')
    return token && user ? { token, user: JSON.parse(user) } : { token: null, user: null }
  })
  const [loginForm, setLoginForm] = useState(emptyLogin)
  const [createUserForm, setCreateUserForm] = useState(emptyUser)
  const [assignForm, setAssignForm] = useState({ studentId: '', mentorId: '' })
  const [uploadForm, setUploadForm] = useState({ documentName: '', file: null })
  const [students, setStudents] = useState([])
  const [mentors, setMentors] = useState([])
  const [documents, setDocuments] = useState([])
  const [assignedStudents, setAssignedStudents] = useState([])
  const [reviewModal, setReviewModal] = useState(null)
  const [reviewForm, setReviewForm] = useState({ status: 'APPROVED', mentorComment: '' })
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)

  const role = auth.user?.role

  const stats = useMemo(() => ({
    total: documents.length,
    pending: documents.filter((d) => d.status === 'PENDING').length,
    approved: documents.filter((d) => d.status === 'APPROVED').length,
    disapproved: documents.filter((d) => d.status === 'DISAPPROVED').length,
  }), [documents])

  useEffect(() => {
    if (role) refreshData()
  }, [role])

  async function refreshData() {
    try {
      if (role === 'ADMIN') {
        const [studentList, mentorList, docs] = await Promise.all([
          api.getUsersByRole('STUDENT'),
          api.getUsersByRole('MENTOR'),
          api.getAllDocuments(),
        ])
        setStudents(studentList)
        setMentors(mentorList)
        setDocuments(docs)
      } else if (role === 'STUDENT') {
        const docs = await api.getStudentDocuments()
        setDocuments(docs)
      } else if (role === 'MENTOR') {
        const [studentList, docs] = await Promise.all([
          api.getAssignedStudents(),
          api.getAssignedDocuments(),
        ])
        setAssignedStudents(studentList)
        setDocuments(docs)
      }
    } catch (error) {
      setMessage(error.message)
    }
  }

  async function handleLogin(e) {
    e.preventDefault()
    setLoading(true)
    setMessage('')
    try {
      const data = await api.login(loginForm)
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data))
      setAuth({ token: data.token, user: data })
      setLoginForm(emptyLogin)
      setMessage(`Logged in as ${data.role}`)
    } catch (error) {
      setMessage(error.message)
    } finally {
      setLoading(false)
    }
  }

  async function handleLogout() {
    try {
      await api.logout()
    } catch {
      // ignore logout failure
    }
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setAuth({ token: null, user: null })
    setStudents([])
    setMentors([])
    setDocuments([])
    setAssignedStudents([])
    setMessage('Logged out')
  }

  async function handleCreateUser(e) {
    e.preventDefault()
    setLoading(true)
    setMessage('')
    try {
      await api.createUser(createUserForm)
      setCreateUserForm(emptyUser)
      setMessage('User created successfully')
      refreshData()
    } catch (error) {
      setMessage(error.message)
    } finally {
      setLoading(false)
    }
  }

  async function handleAssignMentor(e) {
    e.preventDefault()
    setLoading(true)
    setMessage('')
    try {
      await api.assignMentor({
        studentId: Number(assignForm.studentId),
        mentorId: Number(assignForm.mentorId),
      })
      setAssignForm({ studentId: '', mentorId: '' })
      setMessage('Mentor assigned successfully')
    } catch (error) {
      setMessage(error.message)
    } finally {
      setLoading(false)
    }
  }

  async function handleUpload(e) {
    e.preventDefault()
    if (!uploadForm.file) {
      setMessage('Choose a file first')
      return
    }
    setLoading(true)
    setMessage('')
    try {
      await api.uploadDocument(uploadForm.documentName, uploadForm.file)
      setUploadForm({ documentName: '', file: null })
      document.getElementById('upload-file').value = ''
      setMessage('Document uploaded successfully')
      refreshData()
    } catch (error) {
      setMessage(error.message)
    } finally {
      setLoading(false)
    }
  }

  async function submitReview(e) {
    e.preventDefault()
    if (!reviewModal) return
    setLoading(true)
    setMessage('')
    try {
      await api.reviewDocument(reviewModal.id, reviewForm)
      setReviewModal(null)
      setReviewForm({ status: 'APPROVED', mentorComment: '' })
      setMessage('Review submitted successfully')
      refreshData()
    } catch (error) {
      setMessage(error.message)
    } finally {
      setLoading(false)
    }
  }

  if (!auth.user) {
    return (
      <div className="login-shell">
        <div className="login-card">
          <h1>Academic Compliance Portal</h1>
          {/* <p>Admin default login: <strong>admin@portal.com</strong> / <strong>admin123</strong></p> */}
          <form onSubmit={handleLogin} className="form-grid">
            <input
              type="email"
              placeholder="Email"
              value={loginForm.email}
              onChange={(e) => setLoginForm({ ...loginForm, email: e.target.value })}
              required
            />
            <input
              type="password"
              placeholder="Password"
              value={loginForm.password}
              onChange={(e) => setLoginForm({ ...loginForm, password: e.target.value })}
              required
            />
            <button type="submit" disabled={loading}>{loading ? 'Signing in...' : 'Login'}</button>
          </form>
          {message && <div className="message">{message}</div>}
        </div>
      </div>
    )
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <h2>Compliance Portal</h2>
        <p className="sidebar-role">{auth.user.name}</p>
        <span className="badge neutral">{role}</span>
        <button className="logout-btn" onClick={handleLogout}>Logout</button>
      </aside>

      <main className="content">
        <div className="hero">
          <div>
            <h1>{role} Dashboard</h1>
            <p>Manage academic document submission and mentor approval from one place.</p>
          </div>
          {message && <div className="message inline">{message}</div>}
        </div>

        <div className="stats-grid">
          <StatCard label="Total Documents" value={stats.total} />
          <StatCard label="Pending" value={stats.pending} />
          <StatCard label="Approved" value={stats.approved} />
          <StatCard label="Disapproved" value={stats.disapproved} />
        </div>

        {role === 'ADMIN' && (
          <>
            <div className="grid-two">
              <section className="panel">
                <h3>Create User</h3>
                <form onSubmit={handleCreateUser} className="form-grid">
                  <input placeholder="Full name" value={createUserForm.name} onChange={(e) => setCreateUserForm({ ...createUserForm, name: e.target.value })} required />
                  <input type="email" placeholder="Email" value={createUserForm.email} onChange={(e) => setCreateUserForm({ ...createUserForm, email: e.target.value })} required />
                  <input type="password" placeholder="Password" value={createUserForm.password} onChange={(e) => setCreateUserForm({ ...createUserForm, password: e.target.value })} required />
                  <select value={createUserForm.role} onChange={(e) => setCreateUserForm({ ...createUserForm, role: e.target.value })}>
                    <option value="STUDENT">Student</option>
                    <option value="MENTOR">Mentor</option>
                  </select>
                  <button type="submit" disabled={loading}>Create User</button>
                </form>
              </section>

              <section className="panel">
                <h3>Assign Mentor to Student</h3>
                <form onSubmit={handleAssignMentor} className="form-grid">
                  <select value={assignForm.studentId} onChange={(e) => setAssignForm({ ...assignForm, studentId: e.target.value })} required>
                    <option value="">Select student</option>
                    {students.map((student) => (
                      <option key={student.id} value={student.id}>{student.name}</option>
                    ))}
                  </select>
                  <select value={assignForm.mentorId} onChange={(e) => setAssignForm({ ...assignForm, mentorId: e.target.value })} required>
                    <option value="">Select mentor</option>
                    {mentors.map((mentor) => (
                      <option key={mentor.id} value={mentor.id}>{mentor.name}</option>
                    ))}
                  </select>
                  <button type="submit" disabled={loading}>Assign</button>
                </form>
              </section>
            </div>
            <DocumentTable documents={documents} role={role} />
          </>
        )}

        {role === 'STUDENT' && (
          <>
            <section className="panel">
              <h3>Upload Document</h3>
              <form onSubmit={handleUpload} className="form-grid">
                <input
                  placeholder="Document title"
                  value={uploadForm.documentName}
                  onChange={(e) => setUploadForm({ ...uploadForm, documentName: e.target.value })}
                  required
                />
                <input
                  id="upload-file"
                  type="file"
                  accept=".pdf,.doc,.docx,.png,.jpg,.jpeg"
                  onChange={(e) => setUploadForm({ ...uploadForm, file: e.target.files?.[0] || null })}
                  required
                />
                <button type="submit" disabled={loading}>Upload</button>
              </form>
            </section>
            <DocumentTable documents={documents} role={role} />
          </>
        )}

        {role === 'MENTOR' && (
          <>
            <section className="panel">
              <h3>Assigned Students</h3>
              <div className="chips">
                {assignedStudents.length === 0 ? <span>No students assigned yet.</span> : assignedStudents.map((student) => (
                  <span key={student.studentId} className="chip">{student.studentName}</span>
                ))}
              </div>
            </section>
            <DocumentTable documents={documents} role={role} onReview={(doc) => setReviewModal(doc)} />
          </>
        )}
      </main>

      {reviewModal && (
        <div className="modal-backdrop">
          <div className="modal">
            <h3>Review {reviewModal.documentName}</h3>
            <form onSubmit={submitReview} className="form-grid">
              <select value={reviewForm.status} onChange={(e) => setReviewForm({ ...reviewForm, status: e.target.value })}>
                <option value="APPROVED">Approved</option>
                <option value="DISAPPROVED">Disapproved</option>
              </select>
              <textarea
                rows="4"
                placeholder="Add mentor comment"
                value={reviewForm.mentorComment}
                onChange={(e) => setReviewForm({ ...reviewForm, mentorComment: e.target.value })}
              />
              <div className="modal-actions">
                <button type="button" className="secondary-btn" onClick={() => setReviewModal(null)}>Cancel</button>
                <button type="submit">Submit Review</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
