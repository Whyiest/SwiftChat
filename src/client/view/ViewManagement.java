package client.view;

import client.clientModel.User;
import client.controler.ServerConnection;

import javax.swing.plaf.synth.SynthTextAreaUI;

public class ViewManagement implements Runnable {
     public ServerConnection serverConnection;
     public LoginForm loginForm; // LOGIN 0
     public RegistrationForm registrationForm; /// REGISTER 1
     public ContactsWindow myContactWindows; // CONTACT 2
     public ConversationWindow conversationWindow; // CHAT 3
     public static int currentWindow; // 0 = Login , 1 = Registration, 2 = ContactsWindow, 3 = ConversationWindow

     public static boolean alreadyDisplay;

     public User user;

     public ViewManagement(ServerConnection serverConnection) {
          this.serverConnection = serverConnection;
          currentWindow = 0;
          alreadyDisplay = false;
     }

     public void run () {

          initWindows();


          do {

               switch (currentWindow) {
                    case 0 -> { // LOGIN

                         if (!alreadyDisplay) {
                              alreadyDisplay = true;
                              System.out.println(currentWindow);
                              loginForm.openLoginWindow();
                         }
                    }
                    case 1 -> { // REGISTER
                         if (!alreadyDisplay) {
                              alreadyDisplay = true;
                              System.out.println(currentWindow);
                              registrationForm.openRegisterWindow();
                         }
                    }
                    case 2 -> { // CONTACT
                         if (!alreadyDisplay) {
                              alreadyDisplay = true;
                              initContact();
                         }
                    }
                    case 3 -> { // CHAT

                         if (!alreadyDisplay) {
                              alreadyDisplay = true;
                         }
                    }
                    default -> System.out.println("Invalid choice");
               }
          } while (true);

     }

     public void initWindows () {

          this.loginForm = new LoginForm(null, serverConnection);
          this.registrationForm = new RegistrationForm(null, serverConnection);
          //this.myContactWindows = new ContactsWindow(null, serverConnection);
          //this.conversationWindow = new ConversationWindow(null,"SwiftChat",myContactWindows.getSize());
     }

     public void initContact () {
          myContactWindows.initComponents();
     }

     public void setAlreadyDisplay (boolean newSet) {
          alreadyDisplay = newSet;
     }

     public static void setCurrentDisplay (int idOfWindowToDisplay) {
          alreadyDisplay = false;
          currentWindow = idOfWindowToDisplay;
     }

}
