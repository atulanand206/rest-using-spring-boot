# Understanding the requirements

Before embarking upon any adventure, one must have a bird's eye view of what they want to achieve and some points along their pathway. You might think you know a substantial portion at the beginning itself, no matter what you're bound to fill the gaps as you go along. Even if you have no clue at the beginning, you will find them on your journey, just sit tight and keep at it.

Software projects have clear separation in terms of implementation technique and are divided into server side logic and client side. These two entities must be able to talk with each other clearly and as humans design both side of things, it would be nice if humans can also understand the communication without going into any sort of assembly language equations which only the computers know.

The simplest mechanism with which servers & clients interact is using REST APIs \(Representational State Transfer\) and is usually facilitated using JSON \(Javascript Object Notation\) Objects. 

### Project Details 

For the purposes of this project, we would be developing a Spring Boot service with following requirements:

1. A set of users and information created by those users. A user can only read and write own content.
2. The information would be of multiple types and we'd be exploiting polymorphism to deliver as much generic code as we can. 
3. We would also be looking into association tables between the informational entities. 
4. There will be different types of users who would have access to the information with read and write permissions.

Administrative permissions of Users : A user can additionally have administrative permissions.

1. Can read content created by everyone but only write own content.
2. Can create new users. \(Nice to have\)

APIs to be delivered:

User Management

1. GET /users : Return all the users in the system.
2. GET /users/{userId} : Return the details of the user.
3. POST /users : Create a new user.
4. PUT /users/{userId} : Update the details of a user.

```text
User information
{
    "id" : "UUID",
    "name" : "Steven Hendry",
    "email" : "thatsadevil@gmail.com"
    "phone" : "+919876654134"
}
```

Content Management

1. POST /content/{userId} : Add information to the database.
2. PUT /content/{userId}/{contentId} : Update information in the database.
3. GET /content/{contentId} : Returns the content information.

```text
Content Information
{
    "id" : "UUID"
    "owner" : "UUID"
    "short_description" : ""
    "long_description" : "" 
    "type" : "ENUM String"
}
```

The implementation would be done in a Test Driven fashion to never take on huge responsibility and keep delivering software in small increments.  





