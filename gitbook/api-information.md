---
description: The API documentation of the various endpoints in the system.
---

# API Information

## Plan to implement

We have two kinds of entities in our design at the moment, viz., Users and Content. We'll have define the CRUD \(Create - Read - Update - Delete\) operation APIs for both the handles. 

## Bypassing authentication

We are using {requesterId} as a path variable to identify the user making the request. This comes with a security concern that hackers might exploit and use an administrator id to manipulate information in the system but our concern is to learn about APIs, authentication can be handled separately. Off the top of my head, I can think of 2 ways of solving this problem to a certain extent.

1. Add a password field to the database and include the password to the request path as well.
2. Integrate authentication mechanism like Auth0 to handle authentication before it reaches the relevant service.

Working on any of those options increases the effort which could be avoided at the moment. As this is a prototype that we are working with, we should be alright with assuming that the passwords are correct and the requester uses his/her own id only. If all things go well, we can integrate an authentication mechanism before we make it to production.

{% api-method method="post" host="https://api.content.com" path="/v1/{requesterId}/users" %}
{% api-method-summary %}
Create a new user
{% endapi-method-summary %}

{% api-method-description %}
A user with administrative permissions can act as a requester to create a new user.
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="requesterId" type="string" required=false %}
Id of the requester
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}
User created successfully.
{% endapi-method-response-example-description %}

```javascript
{
    "id" : "UUID",
    "name" : "Steven Hendry",
    "email" : "thatsadevil@gmail.com",
    "phone" : "+919876654134",
    "is_administrator" : false
}
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=403 %}
{% api-method-response-example-description %}
Access Denied
{% endapi-method-response-example-description %}

```
{
    "message" : "You do not have access to create users.
}
```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

{% api-method method="get" host="https://api.content.com" path="/v1/{requesterId}/users/{userId}" %}
{% api-method-summary %}
Get user details
{% endapi-method-summary %}

{% api-method-description %}
This endpoint allows you to fetch user details.
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="requesterId" type="string" required=false %}
Id of the requester
{% endapi-method-parameter %}

{% api-method-parameter name="userId" type="string" %}
ID of the user whose profile is being queried.
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}
User successfully retrieved.
{% endapi-method-response-example-description %}

```javascript
{
    "id" : "UUID",
    "name" : "Steven Hendry",
    "email" : "thatsadevil@gmail.com",
    "phone" : "+919876654134",
    "is_administrator" : false
}
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=403 %}
{% api-method-response-example-description %}
Access Denied
{% endapi-method-response-example-description %}

```javascript
{
    "message" : "The user is available but you do not access to fetch the information." 
}
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=404 %}
{% api-method-response-example-description %}
Could not find the user.
{% endapi-method-response-example-description %}

```javascript
{    
    "message": "The user is not available."
}
```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

{% api-method method="put" host="https://api.content.com" path="/v1/{requesterId}/users/{userId}" %}
{% api-method-summary %}
Update user details
{% endapi-method-summary %}

{% api-method-description %}
A requester can pass in their own userId and update their profile details.
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="" type="string" required=false %}

{% endapi-method-parameter %}

{% api-method-parameter name="requesterId" type="string" required=false %}
Id of the requester
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}

{% endapi-method-response-example-description %}

```

```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

{% api-method method="get" host="https://api.content.com" path="/v1/{requesterId}/users" %}
{% api-method-summary %}
Get all users
{% endapi-method-summary %}

{% api-method-description %}
This endpoint returns all the users This is only accessible to the users with administrator privileges.
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="requesterId" type="string" required=false %}
Id of the requester
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}
Users successfully retrieved.
{% endapi-method-response-example-description %}

```javascript
[
    {
        "id" : "UUID",
        "name" : "Steven Smith",
        "email" : "wowthatsamazing@gmail.com",
        "phone" : "+919876698868",
        "is_administrator" : true
    },
    {
        "id" : "UUID",
        "name" : "Steven Hendry",
        "email" : "thatsadevil@gmail.com",
        "phone" : "+919876654134",
        "is_administrator" : false
    }
]
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=403 %}
{% api-method-response-example-description %}
Access Denied
{% endapi-method-response-example-description %}

```javascript
{
    "message": "You do not have access to get information about all the users."
}
```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

