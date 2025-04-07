<p align="center">
  <img src="src/main/resources/static/images/unitalklogo1.png" alt="UniTalk Logo" width="200" height="auto">
</p>

<p align="center">
  <a href="#-description">Description</a> •
  <a href="#-technologies">Technologies</a> •
  <a href="#-structure">Structure</a> •
  <a href="#-collaborators">Collaborators</a>
</p>

## 📋 Description

UniTalk is a web platform specifically designed for university students, creating a digital space where they can collaborate, share knowledge, and support each other throughout their academic journey.

## 🔧 Technologies

- **Frontend**: HTML, CSS, JavaScript, Mustache
- **Backend**: Java, Spring Boot
- **Database**: JPA (compatible with MySQL/PostgreSQL)
- **Dependency Management**: Maven
- **Version Control**: Git, GitHub

## 📁 Structure

```
project-grupo-r-3/
├── .mvn/                                    # Maven Configuration
│   ├── wrapper/                             # Maven wrapper files
│   └───┴── maven-wrapper.properties         # Maven wrapper properties
│
├── .vscode/                                 # Visual Studio Code Configuration
│   │   ├── launch.json                      # Debugging configuration
│   └───┴── settings.json                    # Development environment configuration
│
├── src/                                     # Project source code
│   ├── main/                                # Main application code
│   │   ├── java/com/example/unitalk/        # Main Java package
│   │   │   ├── controllers/                 # REST and MVC controllers
│   │   │   ├── exceptions/                  # Custom exception handling
│   │   │   ├── models/                      # Entities and data models
│   │   │   ├── repository/                  # Repository interfaces for persistence
│   │   │   ├── services/                    # Business services
│   │   │   └── UniTalkApplication.java      # Main Spring Boot application class
│   │   │
│   │   ├── resources/                       # Application resources
│   │   │   ├── static/                      # Static files
│   │   │   │   ├── css/                     # CSS stylesheets
│   │   │   │   │   ├── footer.css           # Footer styles
│   │   │   │   │   ├── header.css           # Header styles
│   │   │   │   │   ├── index.css            # Main page styles
│   │   │   │   │   ├── post.css             # Post view styles
│   │   │   │   │   ├── postFiles.css        # Post files view styles
│   │   │   │   │   ├── subjectPosts.css     # Subject posts styles
│   │   │   │   │   ├── subjects.css         # Subjects view styles
│   │   │   │   │   ├── user.css             # User view styles
│   │   │   │   │   └── userSubjects.css     # User subjects styles
│   │   │   │   │
│   │   │   │   ├── images/                  # Images and graphic resources
│   │   │   │   │   ├── mini.png             # Thumbnail image
│   │   │   │   │   └── unitalklogo1.png     # Main application logo
│   │   │   │   │
│   │   │   │   └── js/                      # JavaScript scripts
│   │   │   │       ├── post.js              # Post functionality
│   │   │   │       ├── subjectsPosts.js     # Subject posts functionality
│   │   │   │       └── subjects.js          # Subjects view functionality
│   │   │   │
│   │   │   ├── templates/                   # Thymeleaf templates
│   │   │   │   ├── error.html               # Error page
│   │   │   │   ├── files.html               # Files view
│   │   │   │   ├── footer.html              # Footer fragment
│   │   │   │   ├── header.html              # Header fragment
│   │   │   │   ├── index.html               # Main page
│   │   │   │   ├── post.html                # Individual post view
│   │   │   │   ├── subjectPosts.html        # Subject posts view
│   │   │   │   ├── subjects.html            # Subjects list view
│   │   │   │   ├── user.html                # User profile view
│   │   │   │   └── userSubjects.html        # User subjects view
│   │   │   │
│   │   │   └── application.properties       # Spring Boot application configuration
│   │   │
│   ├── test/java/com/example/unitalk/       # Automated tests
│   │   └── UniTalkApplicationTests.java     # Main application tests
│   │
│
├── target/                                  # Compilation directory (generated)
├── .gitattributes                           # Git attributes configuration
├── .gitignore                               # Files and directories ignored by Git
├── mvnw                                     # Maven Wrapper script for Unix/Linux
├── mvnw.cmd                                 # Maven Wrapper script for Windows
└── pom.xml                                  # Maven dependencies and build configuration
```

## 👨‍💻 Collaborators

- **Ignacio Díez Chacón** - [GitHub](https://github.com/netzus1)
- **Pablo Bardón Alonso** - [GitHub](https://github.com/p4b4al)
