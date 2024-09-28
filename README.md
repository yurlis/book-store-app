# **BookStoreApp.**<br>Java-based RESTful API for Online Bookstore


## **INTRODUCTION**
BookStoreApp is a comprehensive RESTful API backend for managing an online bookstore. It provides secure user authentication, streamlined book management, and efficient order processing. The app is designed to solve key challenges such as ensuring authentication security, effective management of the book collection, and enhancing the shopping experience for users.

### User Interaction
The application allows different types of users, including administrators and regular users, to interact seamlessly with the book collection and manage their orders.

- **Admin Users**: Admins can perform full CRUD operations on the book collection, including adding new titles, updating existing information, and deleting books. They can also manage user accounts and oversee orders, ensuring that inventory levels are maintained and orders are processed efficiently.

- **Regular Users**: Users can browse the collection, add books to their shopping cart, and place orders. The system facilitates a user-friendly experience by allowing users to view their shopping cart, update item quantities, and remove items as needed. Once an order is placed, users can track the order status and receive updates on delivery.

This structure ensures that both administrators and users have the tools they need to manage books and orders effectively, creating an optimized shopping experience for all.

## **GENERAL INFO**
### **In this app we will have the following domain models (entities):**
- **User:** Contains information about the registered user including their authentication details and personal information.
- **Role:** Represents the role of a user in the system, for example, admin or user.
- **Book:** Represents a book available in the store.
- **Category:** Represents a category that a book can belong to.
- **ShoppingCart:** Represents a user's shopping cart.
- **CartItem:** Represents an item in a user's shopping cart.
- **Order:** Represents an order placed by a user.
- **OrderItem:** Represents an item in a user's order.

## **PROJECT FEATURES**
- Language: Java 17. Build System: Maven.
- The application is built using the Controller - Service - Repository architecture.
- Security: Spring Boot Security is implemented to manage user authentication and authorization, ensuring secure access to the application.
- Data Handling: Data Transfer Objects (DTOs) facilitate safe data transmission between the client and server, with MapStruct utilized for efficient mapping between entities and DTOs.
- All endpoints are documented using Swagger.
- Books can be filtered by author and title using Criteria Query.
- Liquibase is used to create tables and add data to the database.
- CustomGlobalExceptionHandler is used for exception handling, providing more descriptive exception messages.
- Tests were written using Testcontainers for repository-level, Mockito for service-level, and MockMvc for controller-level.
- The tests were written using an embedded database for repository-level tests, Mockito for service-level tests, and MockMvc for controller-level tests.
- The application is deployed using Docker.
- User Authentication & Authorization: Secure registration and login using JWT tokens.
- Book Management: Full CRUD operations for books and categories, with certain actions restricted to admins.
- Shopping Cart: Add, update, and remove books from the cart with real-time updates.
- Order Processing: Create and manage orders, track order status and items within each order.


## **TECHNOLOGIES USED**
- **Java 17:** The primary programming language used for the backend of the application.
- **Spring Boot:** A framework that simplifies Java development, allowing for quick and easy application setup.
- **Spring Security:** Used for managing authentication and authorization.
- **Spring Data JPA:** Provides easy integration with relational databases and simplifies data access.
- **JUnit & Testcontainers:** Tools for running unit and integration tests to ensure code reliability.
- **Lombok:** A library that reduces boilerplate code in Java by generating getters, setters, and other common methods at compile time.
- **MapStruct:** A code generator that simplifies the mapping between Java bean types, providing a fast and efficient way to convert between DTOs and entities.
- **MySQL:** The database used for storing data in the production environment.
- **H2 Database:** An in-memory database used for development and testing.
- **Docker:** Used for containerizing the application for consistent deployment across various environments.
- **Maven:** Used for project management, including building, testing, and managing dependencies.
- **Git:** A version control system used to track changes in the source code.
- **Continuous Integration/Deployment (CI/CD):** Tools such as Jenkins or GitHub Actions can be used to automate testing and deployment processes.
- **Liquibase:** A database migration tool that helps manage and track changes in the database schema.
- **Swagger:** Automatically generates API documentation, making the API easy to explore.

## SETUP and USAGE

### Requirements
- **Java 17 or above**
- **MySQL Server 8.0.33 or above**
- **Apache Maven 3.6.3 or above**
- **Docker 25.0.3 or above**
- **Git (latest version)**

### Installation
- **Clone the Repository.**
   Clone the repository to your local machine:
   ```bash
   git clone https://github.com/yurlis/book-store-app.git

- **Open the Project.** 
  Open the project in your favorite Integrated Development Environment (IDE)
  

- **Configure the Database**
  Open the application.properties or application.yml file in your project and set the database connection properties. Replace database_name, your_login, and your_pass with your actual database settings:
  ```
  spring.datasource.url=jdbc:mysql://localhost:3306/database_name
  spring.datasource.username=your_login
  spring.datasource.password=your_pass
  spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
  ```

- **Build the Project Use Maven to build the project:**
    ```
    mvn clean package
    ```
- **Run the Application Run the application using the command:**
    ```
    java -jar target/*.jar
    ```
    Alternatively, you can hit the play button if using IntelliJ IDEA.


- **Access the API documentation at:**
    ```
    http://localhost:8080/api/swagger-ui.html
    ```

- **Docker Containerization**
<br>
  This project is Dockerized for easy deployment. To build a Docker container, use the following:
  - Fetch this project to you local IDE.
  - Open this project in your IDE.
  - Create in the root directory .env file. As an example you can use .env template file.
  - Run Docker Desktop.
  - To run the application for the first time via docker, run the commands:
    ```
    docker-compose up --build
    ```
    in the IDE terminal. In the future, to run the application use
    ```
    docker-compose up
      ```
  - You can use to try application by this [link](http://localhost:8081/swagger-ui/index.html)


- **Users details and Admin password**

  - Only one user has the ADMIN role (email: admin@example.com, password: sequritypas1!).

  - All new users are assigned the USER role by default.

## **LIST of AVAILABLE ENDPOINTS**
### **All endpoints were documented using Swagger**
**Available for non authenticated users:**
- POST: /api/auth/register
- POST: /api/auth/login

**Available for users with role USER:**
- GET: /api/books
- GET: /api/books/{id}
- GET: /api/categories
- GET: /api/categories/{id}
- GET: /api/categories/{id}/books
- GET: /api/cart
- POST: /api/cart
- PUT: /api/cart/cart-items/{cartItemId}
- DELETE: /api/cart/cart-items/{cartItemId}
- GET: /api/orders
- POST: /api/orders
- GET: /api/orders/{orderId}/items
- GET: /api/orders/{orderId}/items/{itemId}

**Available for users with role ADMIN:**
- POST: /api/books/
- PUT: /api/books/{id}
- DELETE: /api/books/{id}
- POST: /api/categories
- PUT: /api/categories/{id}
- DELETE: /api/categories/{id}
- PATCH: /api/orders/{id}

## CONCLUSION
This project provided me with a great opportunity to learn Spring Boot development. I gained valuable insights into security, dependency management, and testing. It allowed me to explore and practice building applications using Spring Boot. 💻📚
