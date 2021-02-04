# User Controller

Software service requires delivery mechanisms to interact with the external world. There could be a couple of different ways to do so, like a **Command line interface\(CLI\)** or an **Application programming interface\(API\)**. 

**CLI** requires the users to enter directly into the server where they want to run the command, but that becomes cumbersome for every user as they have to remotely login and work through the steps.

In case of **API**, users can make **HTTP** requests to the server and the server can handle it based on the its contents. Controllers are added on top of the domain code to expose endpoints for interacting with such web requests.

## Bypassing authentication

We are using {requesterId} as a path variable to identify the user making the request. This comes with a security concern that hackers might exploit and use an administrator id to manipulate information in the system but our concern is to learn about APIs, authentication can be handled separately. Off the top of my head, I can think of 2 ways of solving this problem to a certain extent.

1. Add a password field to the database and include the password to the request path as well.
2. Integrate authentication mechanism like Auth0 to handle authentication before it reaches the relevant service.

Working on any of those options increases the effort which could be avoided at the moment. As this is a prototype that we are working with, we should be alright with assuming that the passwords are correct and the requester uses his/her own id only. If all things go well, we can integrate an authentication mechanism before we make it to production.

## Next on Agenda

Let's try to develop them for the User Service.

