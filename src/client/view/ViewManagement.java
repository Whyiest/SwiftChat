package client.view;

import client.clientModel.User;
import client.controler.ServerConnection;

public class ViewManagement implements Runnable {
     public ServerConnection serverConnection;
     public LoginForm loginForm; // LOGIN 0
     public RegistrationForm registrationForm; /// REGISTER 1
     public ContactWindow contactForm; // CONTACT 2
     public ConversationWindow conversationForm; // CHAT 3
     public static int currentWindow; // 0 = Login , 1 = Registration, 2 = ContactWindow, 3 = ConversationWindow
     public static User chattingWithThisUser; // If you chat with someone, his user ID is here
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
                              loginForm.openLoginWindow();
                         }
                    }
                    case 1 -> { // REGISTER
                         if (!alreadyDisplay) {
                              alreadyDisplay = true;
                              registrationForm.openRegisterWindow();
                         }
                    }
                    case 2 -> { // CONTACT
                         if (!alreadyDisplay) {
                              alreadyDisplay = true;
                              contactForm.openContactWindow();
                         }
                    }
                    case 3 -> { // CHAT

                         if (!alreadyDisplay) {
                              alreadyDisplay = true;
                              this.conversationForm = new ConversationWindow(null, serverConnection, chattingWithThisUser, contactForm.getSize());
                              conversationForm.openConversationWindow();
                         }
                    }
                    default -> System.out.println("Invalid choice");
               }
          } while (true);

     }

     public void initWindows () {

          this.loginForm = new LoginForm(null, serverConnection);
          this.registrationForm = new RegistrationForm(null, serverConnection);
          this.contactForm = new ContactWindow(null, serverConnection);
     }


     public static void setCurrentDisplay (int idOfWindowToDisplay) {
          alreadyDisplay = false;
          currentWindow = idOfWindowToDisplay;
     }

     public static void setChattingWithUserID (User user) {
          chattingWithThisUser = user;
     }

}
