# 📋 Online Exam System

A full-featured desktop-based Online Exam System built with **Java Swing** and **Maven**, using **SQLite** for persistent storage.

---

## ✨ Features

### 👨‍🎓 Student
- Register and login securely
- Join exams using an Exam ID
- Attempt timed quizzes with animated countdown
- Auto-submit when time expires
- View instant scorecard with answer review
- Track past attempt history

### 👨‍🏫 Teacher
- Create quizzes with custom Exam ID
- Add MCQ, True/False, and Short Answer questions
- Attach images to questions
- Set time limits, marks per question, negative marking
- Set max attempts per student
- Schedule quiz with start and end time
- Randomize question order
- Edit and delete quizzes and questions
- Preview quiz before publishing
- View all student scores and results

### 🔑 Admin
- View all users
- Delete accounts
- Reset passwords
- View all quizzes across all teachers
- View all results across all students

---

## 🛠 Tech Stack

| Technology | Details |
|---|---|
| Language | Java 17 |
| UI Framework | Java Swing |
| Build Tool | Maven 3.6+ |
| Database | SQLite via sqlite-jdbc 3.45.1.0 |
| Password Security | jBCrypt 0.4 |

---

## 🚀 Getting Started

### Prerequisites
- Java JDK 17 or higher
- Maven 3.6 or higher

### Clone and Run
```bash
git clone https://github.com/Armaangarg04/online-exam-system.git
cd online-exam-system
mvn clean compile
mvn exec:java -Dexec.mainClass="com.examsystem.Main"
```

### Build Runnable JAR
```bash
mvn clean package
java -jar target/examsystem-1.0-SNAPSHOT.jar
```

---

## 🔑 Default Admin Credentials

| Field | Value |
|---|---|
| Username | Armaangarg04 |
| Password | Armaan@2026 |

---

## 📁 Project Structure
src/main/java/com/examsystem/
├── Main.java
├── db/
│   └── DatabaseManager.java
├── model/
│   ├── User.java
│   ├── Quiz.java
│   ├── Question.java
│   └── Result.java
├── ui/
│   ├── LoginFrame.java
│   ├── RegisterFrame.java
│   ├── StudentDashboard.java
│   ├── QuizFrame.java
│   ├── ResultFrame.java
│   ├── TeacherDashboard.java
│   ├── CreateQuizFrame.java
│   ├── EditQuizFrame.java
│   ├── EditQuestionFrame.java
│   ├── QuizPreviewFrame.java
│   ├── StudentRecordsFrame.java
│   ├── ProfileFrame.java
│   ├── AdminPanel.java
│   └── AnimatedTimerPanel.java
└── util/
└── PasswordUtil.java
---

## 📄 License

MIT License — feel free to use and modify this project.

---

## 👨‍💻 Author

Made by **Armaan Garg,Ratna Khanna,Gehna Khanna**