# Prototype code using Apache Flink

## Intro
This application takes two files, uses them as stream sources, sends them to Apache Flink to aggregate them them based 
on their timestamp, and then prints the results.  
   
### Notes

I tried to make this application in such a way that it could scale easily to handle virtually an unlimited number of 
symbols and very high throughput.  This application can easily be extended to allow additional data sources such as data
broker feeds.

#### Design Goals
These are the goals of this app:
- Self-documented code
- Robust: log any unexpected input and skip it 
- Easily testable
- Brief

## To Compile and Test:

### Prerequisites:
- JDK 1.8
- [SBT](http://www.scala-sbt.org/download.html)

## Running this application

### Prerequisites:
- JDK 1.8

### Command
> java -jar PositionService.jar file1.txt file2.txt

### Compiling and testing
> cd PositionService
> sbt compile
> sbt test
