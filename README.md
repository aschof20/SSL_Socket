## SSL_Socket
___
#### ZIP file contents
- readme.md
- SSLClient.java
- SSLServer.java
- AminoAcids.java*
- OptimalCodons.java**
- startServer.sh
- startClient.sh
- Protocol.pdf
- Report.pdf
- resources directory***

#####* AminoAcids.java enum contains the amino acids associated with each codon.
#####** OptimalCodons.java enum contains the optimal codons associated with the respective amino acids.
#####*** resources directory contains server and client directories that respectively hold the keys and certificates for the Client and the Server.


## Instructions
___
**NOTE: The Server must be started prior to the client.**

#### Generate Keys and Certificates
**NOTE: The keys and certificates have previously been generated and are located in the /server and /client directories, therefore this step does not need to be performed.

 Using the OpenSSL toolkit, generate the Client and Server keys and certificates, by running the following in the command line:
 ```
openssl req -newkey rsa:2048 -nodes -keyout client-key.pem -x509 -days 365 -out client-certificate.pem
```

 ```
openssl req -newkey rsa:2048 -nodes -keyout server-key.pem -x509 -days 365 -out server-certificate.pem
```
 Combine the key and certificate pairs into a file and confirm using the appropriate password:
```
openssl pkcs12 -inkey client-key.pem -in client-certificate.pem -export -out client-certificate.p12
```

```
openssl pkcs12 -inkey server-key.pem -in server-certificate.pem -export -out server-certificate.p12
```
#### Server instructions
___
**The startServer.sh, SSLServer.java, AminoAcids.java and OptimalCodons.java must be in the same directory.**

- The DNA Optimisation server is started by running "./startServer.sh [PORT]" in the terminal.

- The server will remain open while the client socket is open or until its forced termination is commanded with a keyboard interrupt (usually CTRL+C).
 
#### Client instructions
___
**The startClient.sh, SSLClient.java files must be in the same directory.**

- The DNA Optimisation client is started by running "./startClient.sh [HOST] [PORT]" in the terminal.

- A user interface will be displayed were the user can choose between the following options:
    1. DNA Optimisation
    2. Exit

    Option **[1]** will be prompted to input a DNA sequence to be sent to the server.
    Option **[2]** will close the socket and end the client and server session.

##### Client Input
Valid client input must have the following characteristics:

- A length divisable by 3 (the length of a codon).
- Contain only 'A', 'C', 'G' or 'T' characters that represent the DNA nucleotides.

 
