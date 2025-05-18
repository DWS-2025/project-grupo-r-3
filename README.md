<p align="center">
  <img src="src/main/resources/static/images/unitalklogo1.png" alt="UniTalk Logo" width="200" height="auto">
</p>

<p align="center">
  <a href="#-description">Description</a> •
  <a href="#-technologies">Technologies</a> •
  <a href="#-relations">Relations</a> •
  <a href="#-structure">Structure</a> •
  <a href="#-collaborators">Collaborators</a>
</p>

## 📋 Description

UniTalk is a web platform specifically designed for university students, creating a digital space where they can collaborate, share knowledge, and support each other throughout their academic journey.

## 🔧 Technologies

- **Frontend**: HTML, CSS, JavaScript, Mustache
- **Backend**: Java, Spring Boot, API Rest, AJAX
- **Database**: MySQL
- **Dependency Management**: Maven
- **Version Control**: Git, GitHub

## 🏷️ Entities
- User
- Subject
- Post
- Comment

## 🫂 Relations

- User - Subject: **N:M**
- User - Post: **1:N**
- User - Comment: **1:N**
- Subject - Post: **1:N**
- Post - Comment: **1:N**

## 🪪 User Permissions
- Anon: They will not be able to access any content on the website; they will only be able to log in or register.
- User: They will be able to access all available subjects, create posts, and edit or delete posts they have previously created, as well as view posts from other users. Additionally, they can view comments from other users and add their own comments, but they can only delete and edit comments they have made themselves.
- Admin: They will be able to see all the subjects, posts, comments, and users, as well as delete or edit them.

## 🖼️ Image
- Users will can upload image in a comment.

## 🗺️ Diagram
![Entity-Relation Diagram](/src/main/resources/static/images/diagram.png)

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
│   │   │   ├── DTOS/                        # REST and Web DTOs
│   │   │   ├── exceptions/                  # Custom exception handling
│   │   │   ├── models/                      # Entities and data models
│   │   │   ├── repository/                  # Repository interfaces for persistence
│   │   │   ├── restControllers/             # Controllers for the API Rest
│   │   │   ├── services/                    # Business services
│   │   │   └── UniTalkApplication.java      # Main Spring Boot application class
│   │   │
│   │   ├── resources/                       # Application resources
│   │   │   ├── static/                      # Static files
│   │   │   │   ├── css/                     # CSS stylesheets
│   │   │   │   ├── images/                  # Images and graphic resources
│   │   │   │   └── js/                      # JavaScript scripts
│   │   │   ├── templates/                   # HTML templates
│   │   │   └── application.properties       # Spring Boot application configuration
│   │   │
│   ├── test/java/com/example/unitalk/       # Automated tests
│   └───┴── UniTalkApplicationTests.java     # Main application tests
│   
│
├── target/                                  # Compilation directory (generated)
├── .gitattributes                           # Git attributes configuration
├── .gitignore                               # Files and directories ignored by Git
├── mvnw                                     # Maven Wrapper script for Unix/Linux
├── mvnw.cmd                                 # Maven Wrapper script for Windows
└── pom.xml                                  # Maven dependencies and build configuration
```

## 👨‍💻 Collaborators

## **Ignacio Díez Chacón** (i.diez.2022@alumnos.urjc.es) - [GitHub](https://github.com/netzus1)
- Bug and error fixes, vulnerabilities, database, error pages, user authentication
### Most important commits
  - [Nearly fully migrated to DB](https://github.com/DWS-2025/project-grupo-r-3/commit/f8668430a848d74fd9310c6875a9a5b53f5d6b5d)
  - [Major improvements: Various bug fix and added Files html (not functional yet)](https://github.com/DWS-2025/project-grupo-r-3/commit/0acb48114141a550f369e060ec41f3e05b4e9ce9)
  - [Fully functional DTOS and web](https://github.com/DWS-2025/project-grupo-r-3/commit/fcd8cd1683bb9a8b15fcbc84bf8e69557b26930d)
  - [Fully functional api rest and web](https://github.com/DWS-2025/project-grupo-r-3/commit/98e80b14a80a9bd284ad33d7e1227c36d0344c76)
  - [Implemented local file storage and comment restrictions for users](https://github.com/DWS-2025/project-grupo-r-3/commit/21844c63744f11fff73fcab5c8aa1f68f850a02d)
### Most edited files
  - SubjectService.java
  - SubjectController.java
  - PostService.java
  - UserService.java
  - CommentService.java
  
## **Pablo Bardón Alonso** (p.bardon.2022@alumnos.urjc.es) - [GitHub](https://github.com/p4b4al)
- Bug fixes and errors, REST API, user permissions, README
### Most important commits
  - [API Rest Created](https://github.com/DWS-2025/project-grupo-r-3/commit/cdc7e4cadf03c98dfdfb42a20365eb41fdf985c0)
  - [Merge pull request #2 from DWS-2025/Test](https://github.com/DWS-2025/project-grupo-r-3/commit/683c0baf3e542ced2cf84528006d46a9ce594327)
  - [Fix: Solved infinite redirect loop and correctly implemented the login page.](https://github.com/DWS-2025/project-grupo-r-3/commit/e26c955ae696fd6ff9f74588131bdbac11f069f4)
  - [Fixed bug: now when you delete a post, comments are also being deleted from the post and the user](https://github.com/DWS-2025/project-grupo-r-3/commit/c9c296fb430b0ecf7e7e9ebb890e4d5774f70ef3)
  - [Comment user permisions](https://github.com/DWS-2025/project-grupo-r-3/commit/8614af72af8a5e992f8ce95f20352f54b43fea34)
### Most edited files
  - UserService.java
  - subjectPosts.html
  - CommentRestController.java
  - user.html
  - header.html
