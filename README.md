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

I was asked to create a Java solution to answer three questions for all the data in DB.  I decide to create the calculation endpoint as
it is easy to both develop and test the solution in a web app.   
The result is written into file *inAuthSolution.sql*.  Its form is `POST http://localhost:8080/coding/inauth/calcAllLocations` with empty body.

Build and Run
-------------

### Manual

```bash
mvn clean install -P local
cd target
tar -zxvf in-auth-0.0.1-local.tar.gz 
cd in-auth-0.0.1-local
./bin/startInAuth.sh (./bin/debugInAuth.sh) 
```

### fabric
```bash
fab build_and_start (or build_and_debug)
```

