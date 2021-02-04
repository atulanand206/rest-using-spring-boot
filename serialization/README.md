# Serialization

In a software system, the various components has to interact with each other and maintain isolation. There are several boundaries to be accounted for. We cannot pass around the model across boundaries. In case of `API` interactions, we'd need a transfer mechanism like `JSON` strings. 

We should have capabilities to easily convert a model to and from a `JSON` string. This process is called Serialization. This also helps in components handling the data as required. `JSON` strings can also be sent across `HTTP` requests and the web or mobile clients requesting the server could implement their own serialization techniques to handle the data. 

This is an integral part of the system and as the in-built implementation of `Jackson`, it'd be wise to develop a wrapper for extensibility purposes.

