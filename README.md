# Understanding the requirements

## Scalable software

Before embarking upon any adventure, one must have a bird's eye view of what they want to achieve and some points along their pathway. You might think you know a substantial portion at the beginning itself, no matter what you're bound to fill the gaps as you go along. Even if you have no clue at the beginning, you will find them on your journey, just sit tight and keep at it.

Software projects have clear separation in terms of implementation technique and are divided into server side logic and client side. These two entities must be able to talk with each other clearly and as humans design both side of things, it would be nice if humans can also understand the communication without going into any sort of assembly language equations which only the computers know.

The simplest mechanism with which servers & clients interact is using REST APIs \(Representational State Transfer\) and is usually facilitated using JSON \(Javascript Object Notation\) Objects. 

## Project details 

For the purposes of this project, we would be developing a Spring Boot service with following requirements:

1. A set of users and notes created by those users. A user can only read and write own content.
2. The notes would be of different types and we'd be exploiting polymorphism to deliver as much generic code as we can. 
3. There will be different types of users who would have access to the information with read and write permissions.

Before any server side application is developed we should look towards establishing the API endpoints, the request and response bodies and the model definition so that it would be easier to implement. The models can initially be minimal and has full freedom to be expanded later but the minimum design level fields must be defined on top of which the architecture would be built. We'll be defining them now.

## Design decisions

When a server side service is being defined, various players comes into the equation. There are certain practices that the industry follows to developed maintainable and scalable systems. We would want the system to work at fast rates and also the code to be easily changeable with concentrated effort on smaller parts without disturbing the whole system. 

The system has 3 components to begin with.

1. Business Domain Logic
2. Rest Controllers
3. Persistent Database

Business domain logic acts as the middle man between other components. Rest Controllers are user facing and database has to be kept away from the user at all times. User should never be able to take actions on the database without going through the domain logic. It makes sense to keep the components decoupled with each other and can be swapped at any time. 

Based on the principle of separation of concerns, the logical point to begin development seems to be the domain and write tests around these components. We will be using interfaces in injecting dependencies so as to hide away the concrete logic and with the freedom to change the concrete logic at any time without disturbing the other components.

To get the domain logic working, we would require a domain model with which we can work. The models are simple classes which specifies the information about the entities in the system. We must think on how these components can reflect the requirements. 

The implementation would be done in a Test Driven fashion to never take on huge responsibility and keep delivering software in small increments.  

