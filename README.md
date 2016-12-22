Coding
======

DB Schema
---------

DB schema is specified in `src/main/sql/inAuth.sql`, which also has the username and password used in building the
coding war.  It is best to use the same credential when creating the schema.  Otherwise, the properties in 
src/main/conf/local need to be changed.

The seed data file is in `src/main/sql/location.sql`.

Java `Random` is used to generate the 10,000 random entries.

Web Service Endpoints
---------------------

a) The endpoint for getAllDataSets() is `GET http://localhost:8080/coding/inauth/locations`.

b) The endpoint for getData(latitude,longitude) is `GET http://localhost:8080/coding/inauth/location/{latitude}/{longitude}/`.  

c) The endpoint for addData(latitude,longitude) is `POST http://localhost:8080/coding/inauth/location` and takes payload of the form `{"latitude":"1.10000","longitude":"3.4"}`.

Initial Data Load and Calculation Endpoints
-------------------------------------------

The initial load endpoint is to load 10,000 random locations into the database.  Its form is `POST http://localhost:8080/coding/inauth/initialLoad` with empty body.  

The calculation endpoint reads all the data from db and calculate the answers to the three questions for each location.  
The result is written in file *inAuthSolution.sql*.

