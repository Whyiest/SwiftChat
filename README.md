# Introduction:

Welcome to our real-time messaging software built using Java Swing. This project was developed by a team of 5, including Kenza Erraji, Gabriel Trier, Esteban MAGNON, Ines Benabdeljhalil, and Alexandre CURTI. Our application consists of a client and a server, which can be built into two jar files to enable easy launching of the program.

# How to Install:

1. Clone or download the repository from GitHub.
2. Open the project in your preferred Java IDE (we used IntelliJ IDEA for this project).
3. Enter the IP address of the server and credentials of your database computer in the server code.
4. Enter and OPENAI API Key if you want to use one (Go to ConversationWindow class).
6. Build the project to generate the client and server jar files.
7. Launch the server jar file on a computer that will act as the server. You can do this by running the following command in the terminal:
java -jar server.jar
8. Launch the client jar file on two different computers to start chatting. You can do this by running the following command in the terminal:
java -jar client.jar
9. Start messaging in real-time!

# How populate database ?

Please go to server main, in "danger zone" you will have 3 options : 
- Clear current database
- Create database tables 
- Populate all the tables with test data (can be customized)

# Features:

- Real-time messaging between multiple clients connected to a server.
- User-friendly GUI designed using Java Swing.
- Support private chat and group chat.
- Usernames and Timestamp displayed alongside messages for easy identification.

# Contributing:
We welcome contributions to this project. Feel free to fork the repository and make changes. If you find any bugs or issues, please open an issue on GitHub.

# License:
This project is licensed under the MIT License. Please see the LICENSE.md file for more details.
