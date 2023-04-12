package client.view;

import client.clientModel.User;
import client.controler.ServerConnection;

public class ViewManagement implements Runnable {
     public ServerConnection serverConnection;
     public LoginForm loginForm;
     public RegistrationForm registrationForm;
     public ContactsWindow myContactWindows;
     public ConversationWindow conversationWindow;
     public int currentWindow; // 0 = Login , 1 =Registration, 2 = ContactsWindow, 3 = ConversationWindow

     public User user;

     public ViewManagement(ServerConnection serverConnection) {
          this.serverConnection = serverConnection;
          this.currentWindow = 0;
     }

     public void setCurrentWindow(int currentWindow) {
          this.currentWindow = currentWindow;
     }

     public void run () {

          initWindows();

          do {
               switch (currentWindow) {
                    case 0: // LOGIN
                         loginForm.openLoginWindow();
                         this.user = loginForm.user;
                         break;
                    case 1: // REGISTER
                         this.user = registrationForm.user;
                         serverConnection.addUser(user.getUserName(), user.getFirstName(), user.getLastName(), user.getMail(), user.getPassword(), "CLASSIC");//new
                         break;
                    case 2: // CONTACT
                         initContact();
                         break;
                    case 3: // CHAT
                         break;
                    default:
                         System.out.println("Invalid choice");
                         break;
               }
          } while (true);

     }

     public void initWindows () {

          this.loginForm = new LoginForm(null, serverConnection);
          //this.registrationForm = new RegistrationForm(null);
          //this.myContactWindows = new ContactsWindow(null, serverConnection);
          //this.conversationWindow = new ConversationWindow(null,"SwiftChat",myContactWindows.getSize());
     }

     public void initContact () {
          myContactWindows.initComponents();
     }

     public void setCurrentDisplay (int idOfWindowToDisplay) {
          currentWindow = idOfWindowToDisplay;
     }
}
