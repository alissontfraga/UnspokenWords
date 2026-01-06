
# 📝 Unspoken Words

A web application where users can write and store words they never said.


## 🚀 Features

- User authentication (JWT bearer Auth0 + HttpOnly cookies)
- Create personal messages
- View your own messages
- Delete messages
- Categories for unspoken words


## 🛠️ Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Security
- JPA / Hibernate
- PostgreSQL
- JWT Authentication (Auth0)

### Frontend
- React
- Vite
- Axios
- CSS


## 📸 Screenshots


### Login
<img src="screenshots/login.png" width="600" />

### My Unspoken Words
<img src="screenshots/messages.png" width="600" />

### New Message
<img src="screenshots/new-message.png" width="600" />

## 📌 API Documentation

| Method | Route          | Description    |
| ------ | -------------- | -------------- |
| POST   | /auth/login    | Login          |
| POST   | /auth/singup   | Register       |
| POST   | /auth/logout   | Logout         |
| POST   | /messages      | Create message |
| GET    | /messages      | List messages  |
| DELETE | /messages/{id} | Delete message |


## 🧑‍💻 Author

- Alisson Teles Fraga [@alissontfraga](https://github.com/alissontfraga)

