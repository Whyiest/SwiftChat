package client.view;

import client.clientModel.User;
import client.controler.ServerConnection;

import javax.swing.*;

public class ViewManagement implements Runnable {
     public ServerConnection serverConnection;
     public LoginForm loginForm;
     public RegistrationForm registrationForm;
     public ContactsWindow myContactWindows;
     public ConversationWindow conversationWindow;
     public int choiceForCase; // 0=Login , 1=Registration, 2=ContactsWindow, 3=ConversationWindow
     public User user;

     public ViewManagement(ServerConnection serverConnection) {
          this.serverConnection = serverConnection;
          this.choiceForCase=0;
     }

     public void setChoiceForCase(int choiceForCase) {
          this.choiceForCase = choiceForCase;
     }

     public void run () {
          do {
               switch (choiceForCase) {
                    case 0: //For Login form
                         this.loginForm= new LoginForm(null,serverConnection);//new
                         this.user= loginForm.user;//new
                         setChoiceForCase(this.loginForm.getNumberForCase());
                         break;
                    case 1://For registration form
                         this.registrationForm=new RegistrationForm(null);
                         this.user= registrationForm.user;//new
                         serverConnection.addUser("CLASSIC", user.getFirstName(), user.getLastName(),user.getUserName(), user.getMail(), user.getPassword());//new
                         setChoiceForCase(this.registrationForm.getNumberForCase());
                         break;
                    case 2://for Contacts window
                         this.myContactWindows = new ContactsWindow(null, serverConnection);
                         initContact();
                         setChoiceForCase(this.myContactWindows.getNumberForCase());
                         break;
                    case 3://for Conversation window
                         this.conversationWindow = new ConversationWindow(null,"SwiftChat",myContactWindows.getSize());
                         setChoiceForCase(conversationWindow.getNumberForCase());
                    default:
                         System.out.println("Invalid choice");
                         break;
               }
          } while (true);

     }

     public void initContact () {
          myContactWindows.initComponents();
     }

}
