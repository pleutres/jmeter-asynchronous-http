# jmeter-asynchronous-http

## Quick description

A quick Jmeter plugin to handle asynchronous HTTP responses.

The use case is the following : 


A (Jmeter + NanoHTTP Asynchronous plugin)   
B (the HTTP server to be tested)  

## Use case

1) A : NotificationReceiverCreation sampler starts and notify NanoHTTP that it waits a response
2) A : send a request to B (with the sampler you want)
3) A : NotificationReceiverWait sampler starts and wait
4) B : send a answer to B (on NanoHTTP)
5) A : NanoHTTP notify JMeter NotificationReceiverWait sampler that response is arrived
6) A : NotificationReceiverWait Sample is closed, the time is recorded 


## More details

- NotificationReceiverCreation and NotificationReceiverWait take an argument called "FUNCTIONAL_ID"

## To be continue...

- Jmeter sample project
- Unit tests with mockserver