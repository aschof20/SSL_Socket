#!/bin/bash

# Exit if one argument is not entered in command line.
if [ "$#" -ne 1 ]; then
    echo "Incorrect number of arguments, 1 required."
    exit
fi

# Compile Server Java Class.
javac SSLServer.java
# Execute Server with port number ($1).
java SSLServer $1