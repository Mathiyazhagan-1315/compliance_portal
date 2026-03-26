# Academic Compliance Portal

Full-stack academic compliance portal for:
- **Admin** to create students and mentors
- **Admin** to assign mentors to students
- **Student** to upload documents
- **Mentor** to approve or disapprove documents with comments
- **All roles** to view relevant documents based on access rules

## Stack
- **Frontend:** React + Vite
- **Backend:** Spring Boot
- **Database:** MySQL

## Project Structure
```text
academic-compliance-portal/
├── backend/
└── frontend/
```

## Default Admin Login
- **Email:** `admin@portal.com`
- **Password:** `admin123`

## Backend Setup
1. Create MySQL database or let the app create it automatically.
2. Open `backend/src/main/resources/application.properties`
3. Update these if needed:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=root
   ```
4. Run the backend:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

Backend runs on:
```text
http://localhost:8080
```

## Frontend Setup
1. Run:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

Frontend runs on:
```text
http://localhost:5173
```

## Main Features
### Admin
- Create student accounts
- Create mentor accounts
- Assign mentor to student
- View all uploaded documents

### Student
- Login
- Upload compliance documents
- View upload status
- See mentor comments

### Mentor
- Login
- View assigned students
- View assigned student documents
- Approve/disapprove documents
- Add comments

## Important Notes
- Uploaded files are stored in the backend `uploads/` folder.
- Passwords are stored in hashed form using BCrypt.
- Authentication is handled using a simple session-token mechanism through the `X-Auth-Token` header.
- This is designed for academic/demo use and can be upgraded to JWT later.

## Suggested Demo Flow
1. Login as admin
2. Create one student and one mentor
3. Assign the mentor to the student
4. Login as student and upload a document
5. Login as mentor and approve/disapprove the document
6. Login as student and verify status/comment

## Useful API Endpoints
### Auth
- `POST /api/auth/login`
- `POST /api/auth/logout`

### Admin
- `POST /api/admin/users`
- `POST /api/admin/assign-mentor`
- `GET /api/admin/users/STUDENT`
- `GET /api/admin/users/MENTOR`
- `GET /api/admin/documents`

### Student
- `POST /api/student/upload`
- `GET /api/student/documents`

### Mentor
- `GET /api/mentor/students`
- `GET /api/mentor/documents`
- `PUT /api/mentor/review/{documentId}`

## Future Improvements
- Replace session token with JWT
- Add forgot-password flow
- Add email notifications
- Add document categories and deadlines
- Add re-upload history
- Deploy backend/frontend separately
