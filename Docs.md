### JPA
JPA is a specification (not an implementation) that defines **how** Java Objects should be **mapped** to relational database tables.
### JPA Entity 
A JPA entity is a Java class that **represents** a table in your database.
- Each instance of the entity is a row in the table
- Each field in the class is a column in the table
### Hibernate
Hibernate is an implementation of the JPA specification (interface), Hibernate specifies how to implement the JPA interface, and it generates SQL requests based on annotations, and uses the JDBC Client and Driver to communicate with the database.
