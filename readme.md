# Java Sockets P2P Encrypted Chat App
![](https://raw.githubusercontent.com/karatayemre/readmeFiles/main/network.png)


## Features

- All messages are encrypted and sent to the local subnet and the relay between subnets are handled by gateways nodes. 
- Two types of peers; 
-- (1) client peers that will act as chat applications 
-- (2) gateway peers that will act as both chat applications and relays between two subnets.
- Before a user can connect to the Chat Network, s/he should create public and private keys using the Generate Keys menu item (in File->Generate Keys).
- All messages send using IPv6 global multicasting (ff02::1), with the exception of unicast communication between subnet gateway relay nodes
- All the messages send with RSA encryption
- All the nodes on the overlay network  keep track of active users and their public keys.
- The gateway nodes tunnel two IPv6 subnets via IPv4 infrastructure.

## Message Codes
I have used custom pair structure to handle messages differently.
Custom pair consists of message code and username, data.

Code 8: user list send
Code 9: add new User and send code 8
Code 1: message
Code 7: disconnect


## Installation and Run

App requires JRE 11.

```sh
cd 471term\out\artifacts\471term_jar
java -jar 471term.jar
```
or build with your IDE




**Free Software!!**
