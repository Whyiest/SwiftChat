package client.view;

import client.clientModel.User;
import client.controler.ServerConnection;

import javax.swing.*;

public class ViewManagement implements Runnable {
     public ServerConnection serverConnection;

     public ContactsWindow myContactWindows;

     public ViewManagement(ServerConnection serverConnection) {
          this.serverConnection = serverConnection;

     }

     public void run () {
          init();
          LoginForm loginForm= new LoginForm(null,serverConnection);//ne
          User user = loginForm.user;//new
          serverConnection.addUser("CLASSIC", user.getFirstName(), user.getLastName(),user.getUserName(), user.getMail(), user.getPassword());//new
          this.myContactWindows = new ContactsWindow(null, serverConnection);
     }

     public void init () {
          myContactWindows.initComponents();
     }
     //serverConnexion.addUser("CLASSIC", user.getFirstName(), user.getLastName(),user.getUserName(), user.getMail(), user.getPassword());//new

    //LoginForm loginForm= new LoginForm(null);//new
    //User user =loginForm.user;//new
}
