# Booking coding challenge

## Project
### Description
- Based on the dropwizard example (standard dropwizard setup)
- Uses H2 in memory database
- Project uses just one domain object `FlightSchedule`
- Demonstrates the REST API and search implementation

### Requirements
- jre/jdk-11

### Setup
Init h2 DB:
`java -Dfile.encoding=windows-1252 -jar target\dropwizard-example-1.0.0-SNAPSHOT.jar db migrate example.yml`

### Run
`java -Dfile.encoding=windows-1252 -jar target\dropwizard-example-1.0.0-SNAPSHOT.jar server example.yml`<br/>
Should now be running on `https://localhost:8443` <br/>
Optionally can be tested with Postman, import the `"Booking coding challenge.postman_collection.json"`

### Test
Use junit / IT test or postman
Data in DB:<br/>
![](data.png)

### Disclaimer
a lot of things have been omitted and even what I have built is just PoC / WIP.

### Domain model sample
Basic sample domain model:<br/>
![](Domain.png)
