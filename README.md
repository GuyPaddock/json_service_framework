# Rosie JSON API v1 Framework and Connectors for Java
This project contains both Rosie's Java-based framework for interacting with web services that 
follow the JSON API v1 standard, as well as several projects that make use of the framework to
provide access to existing Rosie services that follow the JSON API v1 specification.

Here's a run-down of each project included within this repository:

## Rosie JSON API v1 Framework for Java (`rosie-json-api-framework`)
Our standard framework for connecting to JSON API v1-compliant services from Java.

See `rosie-json-api-framework/README.md` for details.

## Java Service Client for Rosie Loyalty (`rosie-loyalty-client`)
A Java-based client for connecting to Rosie's Loyalty Service (using the JSON API framework).

See `rosie-loyalty-client/README.md` for details.

## OpenICF Connector for Rosie Loyalty (`openicf-rosie-loyalty-connector`)
An OpenICF connector that allows systems like OpenIDM to interact with the Rosie Loyalty service 
over JSON-API-compliant REST.

See `openicf-rosie-loyalty-connector/README.md` for details.
