package client.view;

import client.clientModel.User;
import client.controler.ServerConnection;

public class ViewManagement implements Runnable {
     public ServerConnection serverConnection;
     public LoginForm loginForm; // LOGIN 0
     public RegistrationForm registrationForm; /// REGISTER 1
     public ContactWindow contactForm; // CONTACT 2
     public ConversationWindow conversationForm; // CHAT 3
     public BanUserFrame banUserFrame; // BAN 4
     public static int currentWindow; // 0 = Login , 1 = Registration, 2 = ContactWindow, 3 = ConversationWindow
     public static User chattingWithThisUser; // If you chat with someone, his user ID is here
     public static boolean alreadyDisplay;

     public User user;

     /**
      * Constructor
      * @param serverConnection the server connection
      */
     public ViewManagement(ServerConnection serverConnection) {
          this.serverConnection = serverConnection;
          currentWindow = 0;
          alreadyDisplay = false;
     }

     /**
      * Run the view Thread
      */
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
                    case 4 -> { // Ban user

                         if (!alreadyDisplay) {
                              alreadyDisplay = true;
                              this.banUserFrame = new BanUserFrame(conversationForm, serverConnection, chattingWithThisUser);
                              banUserFrame.openBanWindow();
                         }
                    }
                    default -> System.out.println("Invalid choice");
               }
          } while (true);

     }

     /**
      * Initialize the windows
      */
     public void initWindows () {

          this.loginForm = new LoginForm(null, serverConnection);
          this.registrationForm = new RegistrationForm(null, serverConnection);
          this.contactForm = new ContactWindow(null, serverConnection);
     }

     /**
      * Set the current window to display
      * @param idOfWindowToDisplay the id of the window to display
      */

     public static void setCurrentDisplay (int idOfWindowToDisplay) {
          alreadyDisplay = false;
          currentWindow = idOfWindowToDisplay;
     }

     /**
      * Set the user you are chatting with
      * @param user the user you are chatting with
      */
     public static void setChattingWithUser(User user) {
          chattingWithThisUser = user;
     }

}
