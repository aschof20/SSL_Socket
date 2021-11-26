#!/bin/bash
# Exit if two arguments are not entered in command line.
if [ "$#" -ne 2 ]; then
    echo "Incorrect number of arguments, 2 required."
    exit
fi
# Compile Client Java Class.
javac SSLClient.java
# Execute the Java Code with hostname ($1) and port ($2).
java SSLClient $1 $2