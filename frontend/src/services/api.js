const API_BASE = 'https://complianceportal-production.up.railway.app/'

function authHeaders() {
  const token = localStorage.getItem('token')
  return token ? { 'X-Auth-Token': token } : {}
}

async function parseResponse(response) {
  if (!response.ok) {
    let message = 'Request failed'
    try {
      const text = await response.text()
      message = text || message
    } catch {
      // ignore
    }
    throw new Error(message)
  }

  const contentType = response.headers.get('content-type') || ''
  if (contentType.includes('application/json')) return response.json()
  return response.text()
}

export const api = {
  login: (payload) =>
    fetch(`${API_BASE}api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    }).then(parseResponse),

  logout: () =>
    fetch(`${API_BASE}api/auth/logout`, {
      method: 'POST',
      headers: authHeaders(),
    }).then(parseResponse),

  createUser: (payload) =>
    fetch(`${API_BASE}api/admin/users`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...authHeaders() },
      body: JSON.stringify(payload),
    }).then(parseResponse),

  getUsersByRole: (role) =>
    fetch(`${API_BASE}api/admin/users/${role}`, {
      headers: authHeaders(),
    }).then(parseResponse),

  assignMentor: (payload) =>
    fetch(`${API_BASE}api/admin/assign-mentor`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...authHeaders() },
      body: JSON.stringify(payload),
    }).then(parseResponse),

  getAllDocuments: () =>
    fetch(`${API_BASE}api/admin/documents`, { headers: authHeaders() }).then(parseResponse),

  uploadDocument: (documentName, file) => {
    const formData = new FormData()
    formData.append('documentName', documentName)
    formData.append('file', file)
    return fetch(`${API_BASE}api/student/upload`, {
      method: 'POST',
      headers: authHeaders(),
      body: formData,
    }).then(parseResponse)
  },

  getStudentDocuments: () =>
    fetch(`${API_BASE}api/student/documents`, { headers: authHeaders() }).then(parseResponse),

  getAssignedStudents: () =>
    fetch(`${API_BASE}api/mentor/students`, { headers: authHeaders() }).then(parseResponse),

  getAssignedDocuments: () =>
    fetch(`${API_BASE}api/mentor/documents`, { headers: authHeaders() }).then(parseResponse),

  reviewDocument: (documentId, payload) =>
    fetch(`${API_BASE}api/mentor/review/${documentId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', ...authHeaders() },
      body: JSON.stringify(payload),
    }).then(parseResponse),

  fileUrl: (documentId) => `${API_BASE}api/files/${documentId}`,
}
