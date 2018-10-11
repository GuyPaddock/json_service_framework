# Rosie JSON-API Service Client Framework (OSS)
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

## Pre-Requisites
In order to compile the tests in this framework, you will need to check-out, build, and install a 
copy of [Rosie's v1.2.0 fork of the Spectrum test runner](https://github.com/RosieApp/spectrum/releases/tag/1.2.0-rosie1)
into your local Maven repo. This is necessary until the maintainer of that project publishes a new 
version that includes Rosie's enhancements that address 
[issue #132](https://github.com/greghaskins/spectrum/pull/133).

Simply run the following commands:
```
git clone https://github.com/RosieApp/spectrum -b rosie/master
cd spectrum
./gradlew publishToMavenLocal
```

If, after completing the steps above, you continue to receive errors from Maven that it still cannot 
locate `spectrum-1.2.0-rosie1.jar`, this may be due to 
[Maven caching the repository](https://stackoverflow.com/questions/12517129/maven-fails-to-find-local-artifact) 
where it was last looking for the file. To fix this issue, you may need to purge the contents of the
`~/.m2/repository/com/greghaskins/spectrum/1.2.0-rosie1` folder,
recompile & re-install Spectrum using the Gradle command above, and try again.

## How to Use
See the inline Javadocs for the following core interfaces and classes within the framework for 
more information:
 - `com.rosieapp.services.common.model.Model`
 - `com.rosieapp.services.common.model.identification.ModelIdentifier`
 - `com.rosieapp.services.common.model.construction.ModelBuilder`
 - `com.rosieapp.services.common.model.filtering.ModelFilter`
 - `com.rosieapp.services.common.model.filtering.ModelFilterBuilder`
 - `com.rosieapp.services.common.request.PagedCollection`
 
 ## License
 Copyright 2017-2018 Rosie Applications Inc.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, version 3 of the License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
