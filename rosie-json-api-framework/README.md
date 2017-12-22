# Rosie JSON API v1 Framework for Java
Our standard framework for connecting to JSON API v1-compliant services from Java.

## Features
This framework provides the following features:
 - Reusable patterns and base classes for representing and interacting with service data models in 
   an object-oriented way, without coupling client applications to how services represent their data
   internally.
 - A generic, flexible way to interact with models that are identified by either 
   serially-incremented integer primary keys or UUIDs, without requiring any code changes on the
   client side if the identifier changes on the service side in the future.
 - A fluent "builder-style" API for building new service model instances, to isolate client
   applications from the business rules and data structures required on the service side.
 - A separate fluent "builder-style" API for creating filters that can be applied to a data set
   returned by a service to select one or more models on the client side that match a specific or
   approximate set of criteria. The interface performs the filtering internally in the framework,
   freeing client-side logic from having to interrogate models, handle data types, or perform the
   matching manually.
 - Adapters for navigating through paged result sets using standard Java 8 streams and iterators,
   without the client having to fetch and process each page of results manually.

The framework bundles the following two libraries to provide a simple pattern for clients to 
interact with JSON API services:
 - [JSON API Converter](https://github.com/jasminb/jsonapi-converter) by Jasmin Begic, which
   handles serializing and deserializing service models into and out of JSON-API-compliant message
   format.
 - [Retrofit](https://github.com/square/retrofit) by Square, which provides a type-safe way to
   interact with web services using plain old Java interfaces that have been annotated with hints
   about how to translate parameters and calls to appropriate service endpoints.

These libraries are included automatically in the JAR produced by the framework via the Maven Shade
plug-in, so that clients do not have to pull-in the libraries directly.

## How to Use
See the "Java Service Client for Rosie Loyalty" project (described below) for a proof-of-concept
implementation of a service client that makes use of the framework to interact with a Rosie
JSON API service.

Also see the inline Javadocs for the following core interfaces and classes within the framework for 
more information:
 - `com.rosieapp.services.common.model.Model`
 - `com.rosieapp.services.common.model.identification.ModelIdentifier`
 - `com.rosieapp.services.common.model.construction.ModelBuilder`
 - `com.rosieapp.services.common.model.filtering.ModelFilter`
 - `com.rosieapp.services.common.model.filtering.ModelFilterBuilder`
 - `com.rosieapp.services.common.request.PagedCollection`

## Known Limitations
This framework has not been thoroughly tested and is missing comprehensive unit tests. 

See the [RJAR-15](https://rosieapp.atlassian.net/browse/RJAR-15) epic for all of the tickets that 
are opportunities to polish the framework and add the tests that are missing, in order to make the
framework production-ready.
