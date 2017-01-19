## Builidng Sources

mvn install

## Pre-requisite

1. MongoDB 3.2+
2. ActiveMQ 5.14+

## Running

Instructions are provided for Windows. Other operation systmes should only differ in path format.

1. Start MongoDB 
```
mongod.exe --smallfiles --noprealloc --dbpath c:\temp\Data\
```
2. Start ActiveMQ 
```
activemq.bat start
```

3. Start data generator
```
cd \transactions-monitor\tools\data-generator\target
java -jar data-generator-0.1.0.SNAPSHOT.jar
```
4. Start transactions listener
```
cd transactions-monitor\transactions-publisher\target
java -jar transaction-publisher-0.1.0.SNAPSHOT.jar
```
5. Start web-server
```
cd transactions-monitor\web-client\target\
java -jar web-client-0.1.0.SNAPSHOT.jar
```

6. Open web-client http://localhost:8080/
