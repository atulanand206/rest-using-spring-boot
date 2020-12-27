# Database considerations

When defining a model, we must think about how the entity could be stored in a database. As there can be multiple entities in the database and those entities could be related and require associations. There are various ways to associate entities. We must weigh the pros and cons of the approaches as changing  database schemas is normally the most expensive operation in an API service. As a service is required to perform fast retrievals and order entries, the database decisions are quite prudent to begin with. Whether we should be okay with using foreign keys and it would be nice to have a relational table

