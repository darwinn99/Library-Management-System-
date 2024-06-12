# Library Management System

This is a RESTful API developed using Spring Boot for a Library Management System. The API follows the HATEOAS (Hypermedia as the Engine of Application State) principle, making it self-descriptive and easy to navigate.

## Entities

The system has the following entities:

- **Author**: Information about the authors of the books.
- **Book**: Details of the books available in the library.
- **Customer/User**: Information about the library users.
- **Borrowing Record**: Record of books borrowed by users, including borrowing date and return date.

## Endpoints

The API provides the following endpoints, all prefixed with `api/v1`:

- **Authors**
  - GET /authors: Retrieve all authors.
  - GET /authors/{id}: Retrieve an author by ID.
  - POST /authors: Create a new author.
  - PUT /authors/{id}: Update an existing author.
  - DELETE /authors/{id}: Delete an author by ID.
- **Books**
  - GET /books: Retrieve all books.
  - GET /books/{id}: Retrieve a book by ID.
  - POST /books: Create a new book.
  - PUT /books/{id}: Update an existing book.
  - DELETE /books/{id}: Delete a book by ID.
  - GET /books/search?title={title}: Search for books by title.
  - GET /books/search?author={author}: Search for books by author.
  - GET /books/search?isbn={isbn}: Search for books by ISBN.
- **Customers/Users**
  - GET /customers: Retrieve all customers/users.
  - GET /customers/{id}: Retrieve a customer/user by ID.
  - POST /customers: Create a new customer/user.
  - PUT /customers/{id}: Update an existing customer/user.
  - DELETE /customers/{id}: Delete a customer/user by ID.
- **Borrowing Records**
  - GET /borrowings: Retrieve all borrowing records.
  - GET /borrowings/{id}: Retrieve a borrowing record by ID.
  - POST /borrowings: Create a new borrowing record.
  - PUT /borrowings/{id}: Update an existing borrowing record.
  - DELETE /borrowings/{id}: Delete a borrowing record by ID.
  - GET /borrowings/search?userId={userId}: Retrieve borrowing records for a specific user.
  - GET /borrowings/search?bookId={bookId}: Retrieve borrowing records for a specific book.

## Database

The system uses a SQL database to store and retrieve data for the entities. CRUD (Create, Read, Update, Delete) operations are implemented to manage data.

## Validation and Exception Handling

The system implements validation checks to ensure data integrity and handles exceptions gracefully.

## Documentation

API documentation is generated using Swagger with the SpringDoc dependency at this link: http://localhost:8080/api/v1/swagger-ui/index.html to describe the endpoints, request parameters, response formats, etc.

## Code Quality

The code follows clean code practices and adheres to SOLID principles to ensure maintainability, readability, and scalability. Design patterns are utilized where applicable to solve common design problems and promote code reuse and flexibility.


