Un serveur ftp écrit en java

  - A. de Busschère
  - P. Dessingue

17 février 2015

# Compile the server

``ant``

# Run the server

``ant run``

# Connect a client

``telnet -d localhost 1515``

Here is an example usage :

    Connected to 127.0.0.1.
    220 ready
    Name (127.0.0.1:goudale): anonymous
    ---> USER anonymous
    331 Username ok, send password.
    Password:
    ---> PASS XXXX
    230 User loged in, proceed
    ---> SYST
    215 UNIX type : L8
    Remote system type is UNIX.
    ftp> ls
    ---> PORT 127,0,0,1,148,159
    200 Active data connection etablished
    ---> LIST
    125 Proceed
    build.xml
    plop
    build.xml~
    README.md
    rfc959.txt
    226 Complete
    ftp> recv plop
    ---> PORT 127,0,0,1,214,77
    200 Active data connection etablished
    ---> RETR plop
    125 Starting transfer
    226 Transfer completed
    11 bytes received in 0,000225 seconds (48888 bytes/s)
    ftp> ---> QUIT
    221 Goodbye

# Implemented verbs

Sorted alphabetically

   + LIST
   + PASS
   + PORT
   + PWD
   + QUIT
   + RETR
   + SYST
   + USER
