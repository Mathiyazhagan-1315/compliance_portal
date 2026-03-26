import { api } from '../services/api'

export default function DocumentTable({ documents, role, onReview }) {
  const token = localStorage.getItem('token')

  const downloadFile = async (doc) => {
    try {
      const response = await fetch(api.fileUrl(doc.id), {
        headers: { 'X-Auth-Token': token },
      })
      if (!response.ok) throw new Error('Download failed')
      const blob = await response.blob()
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = doc.originalFileName
      link.click()
      window.URL.revokeObjectURL(url)
    } catch (error) {
      alert(error.message)
    }
  }

  return (
    <div className="panel">
      <h3>Documents</h3>
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Student</th>
              <th>Document</th>
              <th>File</th>
              <th>Status</th>
              <th>Comment</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {documents.length === 0 ? (
              <tr>
                <td colSpan="6">No documents found.</td>
              </tr>
            ) : (
              documents.map((doc) => (
                <tr key={doc.id}>
                  <td>{doc.studentName}</td>
                  <td>{doc.documentName}</td>
                  <td>
                    <button type="button" className="link-btn" onClick={() => downloadFile(doc)}>
                      {doc.originalFileName}
                    </button>
                  </td>
                  <td>
                    <span className={`badge ${doc.status.toLowerCase()}`}>{doc.status}</span>
                  </td>
                  <td>{doc.mentorComment || '-'}</td>
                  <td>
                    {role === 'MENTOR' ? (
                      <button className="secondary-btn" onClick={() => onReview(doc)}>
                        Review
                      </button>
                    ) : (
                      '-'
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}
