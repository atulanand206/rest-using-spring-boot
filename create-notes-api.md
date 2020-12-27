# Create Notes API

We can create a model with an id and some description for the time being. In note-apps, notes are displayed as a list with a short description to show for which when opened could show the full text. As these two texts can be totally unrelated, two fields will be required to handle this. Earlier, we decided that the notes could be of different types and hence a field would be added. The app could have specific functionality and it would be important to have the type in the system and not allow unknown types to be added as the UI might not be able to handle a new type out of the box unless its specifically handled. Hence, it would be prudent to add an enum to list all of the acceptable types. We can modify the enum if a new type comes and ask the client side developers to do the same on their end. Every note must be associated with the user

```javascript
//Notes Information
{
    "id" : "UUID"
    "owner" : "UUID"
    "short_description" : ""
    "long_description" : "" 
    "type" : "ENUM String"
}
```

