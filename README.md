# How to run the tests
* Unit testes: gradlew clean test
* Integration testes: gradlew clean integrationTest -Dspring.redis.host=192.168.99.100 -Dspring.redis.port=6380

# How to run the application
* Download http://central.maven.org/maven2/com/h2database/h2/1.4.197/h2-1.4.197.jar
* Start H2 database in server mode: java -jar h2-1.4.197.jar -tcpAllowOthers
* Go to the project library
* Run gradlew bootRun -Dspring.datasource.url=jdbc:h2:tcp://192.168.99.1/~/prime -Dspring.redis.host=192.168.99.100 -Dspring.redis.port=6380
  * Please use the right IP addresses
* In case you want to use the jar file
  * Run gradlew clean test build
  * Run java -jar build/libs/prime-0.0.1-SNAPSHOT.jar --spring.datasource.url=jdbc:h2:tcp://192.168.99.1/~/prime --spring.redis.host=192.168.99.100 --spring.redis.port=6380

# Commandline args
* spring.redis.port - Port of redis server, Default: 6380
* spring.redis.host - Host of redis server, Default: 192.168.99.100
* spring.datasource.url - URL of H2 database, Default: jdbc:h2:tcp://192.168.99.1/~/prime
* spring.datasource.username - Username of H2 connection, Default: sa
* spring.datasource.password - Password of H2 connection, Default: sa


# Limitation
* There is a chance that index stuck in the processingPrimeQueue in case the server was terminated
* The max length of a queue is 2^32 - 1 elements

# Recomended changes
* Replace Spring JDBC with jOOQ or Speedment
* Use the Pub/Sub feature of redis instead of fething the queue in every second
* Refactore HTML files, there are code duplications
* Current Queue solution is s Redis Set, a sorted Set would be better solution, it could be sorted by index desc

# Issues
* Eclipse does not use the application.properties from test/resource during unit tests 