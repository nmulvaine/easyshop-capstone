
Easyshop-Capstone-Three

Nicholas Mulvaine

12/20/24



———————————————————————————

// Overview //

![easyshop-frontend.png](src/main/resources/images/easyshop-frontend.png)
![200.png](src/main/resources/images/200.png)
![201.png](src/main/resources/images/201.png)
![404.png](src/main/resources/images/404.png)

This project is a Java-based backend for an e-commerce platform, providing functionality to manage users, authentication,
categories, and products. It utilizes Spring Boot, Spring Security, and a MySQL database.

// Features //

User Authentication: Secure login and registration using JWT tokens.
Category Management: CRUD operations for product categories.
Product Management: Search and retrieve products based on category, price, and other attributes.
Role-Based Access Control: Admin-specific operations secured via Spring Security roles.
Project Structure

// Configurations //

DatabaseConfig: Sets up the database connection using BasicDataSource. Credentials are retrieved from application properties.
Controllers
AuthenticationController: Manages login and registration endpoints.
CategoriesController: Handles CRUD operations for product categories and retrieves products by category.
DAO Interfaces
CategoryDao: Defines methods for managing categories.
ProductDao: Defines methods for managing products, including searching and filtering.
DAO Implementations
MySqlCategoryDao: Implements CategoryDao using raw SQL queries.
MySqlDaoBase: Base class for establishing database connections.
Models
Category: Represents a product category with fields for ID, name, and description.
Product: Represents a product with fields for ID, name, price, category ID, description, color, stock, and more.
Endpoints

// Authentication // 

POST /login: Logs in a user and returns a JWT token.
POST /register: Registers a new user and initializes their profile.
Categories
GET /categories: Retrieves all categories.
GET /categories/{id}: Retrieves a category by ID.
GET /categories/{categoryId}/products: Lists all products under a specific category.
POST /categories: Adds a new category (Admin only).
PUT /categories/{id}: Updates an existing category (Admin only).
DELETE /categories/{id}: Deletes a category (Admin only).
 
// Technologies Used //

Java: Programming language.
Spring Boot: Framework for building the backend.
Spring Security: Provides authentication and authorization.
MySQL: Database for persisting data.
Apache DBCP: For database connection pooling.

// Takeaways //

![row-mapping.png](src/main/resources/images/row-mapping.png)

The project employs a row-mapping mechanism to translate SQL query results into Java objects, ensuring seamless 
interaction between the database and application logic.