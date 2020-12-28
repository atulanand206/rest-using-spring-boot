# Features in the application

This is a guide to develop a server side application using Spring boot and tested using JUnit.

For the purposes of this project, we would be developing a Spring Boot service with following requirements:

1. A set of users and notes created by those users. A user can only read and write own content.
2. The notes would be of different types and we'd be exploiting polymorphism to deliver as much generic code as we can. 
3. There will be different types of users who would have access to the information with read and write permissions.

The implementation would be done in a Test Driven fashion to never take on huge responsibility and keep delivering software in small increments. The test and production code would be refactor several times and it may feel useless at times but in the end, it will all lead to a robust system and after all that's what every stakeholder wants.
