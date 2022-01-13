#!/bin/bash
grep -q services.publicKey ./src/main/resources/application.properties || (
    openssl genrsa > secretKey.txt &&
    openssl rsa -in secretKey.txt -pubout > publicKey.txt &&
    awk 'BEGIN { a = "services.publicKey="; } ! /^---/ { printf ("%s\\\n", a); a = $1; } END { print a; }' < publicKey.txt >> ./src/main/resources/application.properties
)
