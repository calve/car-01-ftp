# UML Diagram

```


+---------------------------------------------+
|            Thread                           |
+---------------------------------------------+
               ^
               |
+---------------------------------------------+               +--------------------------------------------+
|            ftpd.Server                      |               |        ftpd.FtpRequest                     |
+---------------------------------------------+               +--------------------------------------------+
| + FtpRequest(Socket socket)                 |               | + void run                                 |
| + void run()                                |               | + void processRequest(String line)         |
| + void processRequest(String line)          |               | + void answer(int status, String responds) |
| + void answer(int status, String respond)   |               |                                            |
|                                             |               |                                            |
|                                             |               |                                            |
|                                             |               |                                            |
|                                             |               |                                            |
|                                             |               |                                            |
|                                             |               |                                            |
+---------------------------------------------+               +--------------------------------------------+


+--------------------------------------------+
|      user.User                             |
+--------------------------------------------+
| + String getLogin()                        |
| + void setLogin(String login)              |
| + String getPassword()                     |
| + void setPassword(String password)        |
| + boolean isPassword(String pass)          |
|                                            |
+--------------------------------------------+



```
