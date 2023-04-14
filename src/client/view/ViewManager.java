package client.view;

import client.clientModel.User;
import client.controler.ServerConnection;

public class ViewManager implements Runnable {
    public ServerConnection serverConnection;
    public LoginForm loginForm; // LOGIN 0
    public RegistrationForm registrationForm; /// REGISTER 1
    public ContactWindow contactForm; // CONTACT 2
    public ConversationWindow conversationForm; // CHAT 3
    public OptionsWindow optionsWindow; // BAN 4
    public static int currentWindow; // 0 = Login , 1 = Registration, 2 = ContactWindow, 3 = ConversationWindow, 4= BanPage
    public static User chattingWithThisUser; // If you chat with someone, his user ID is here
    public static boolean alreadyDisplay;

    public User user;

    /**
     * Constructor
     *
     * @param serverConnection the server connection
     */
    public ViewManager(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
        currentWindow = 0;
        alreadyDisplay = false;
    }

    /**
     * Run the view Thread
     */
    public void run() {
        do {
            switch (currentWindow) {
                case 0 -> { // LOGIN
                    if (!alreadyDisplay) {
                        alreadyDisplay = true;
                        loginForm = new LoginForm(null, serverConnection,700,600);
                        loginForm.openLoginWindow();
                    }
                }
                case 1 -> { // REGISTER
                    if (!alreadyDisplay) {
                        alreadyDisplay = true;
                        this.registrationForm = new RegistrationForm(null, serverConnection,700,600);
                        registrationForm.openRegisterWindow();
                    }
                }
                case 2 -> { // CONTACT
                    if (!alreadyDisplay) {
                        alreadyDisplay = true;
                        contactForm = new ContactWindow(null, serverConnection,700,600);
                        contactForm.openContactWindow();
                    }
                }
                case 3 -> { // CHAT
                    if (!alreadyDisplay) {
                        alreadyDisplay = true;
                        this.conversationForm = new ConversationWindow(null, serverConnection, chattingWithThisUser,700,600);
                        conversationForm.openConversationWindow();
                    }
                }
                case 4 -> { // Ban user
                    if (!alreadyDisplay) {
                        alreadyDisplay = true;
                        this.optionsWindow = new OptionsWindow(conversationForm, serverConnection, chattingWithThisUser,300,200);
                        optionsWindow.openOptionWindow();
                    }
                }
                default -> System.out.println("Invalid window");
            }

        } while (true);

    }


    /**
     * Set the current window to display
     *
     * @param idOfWindowToDisplay the id of the window to display
     */

    public static void setCurrentDisplay(int idOfWindowToDisplay) {
        alreadyDisplay = false;
        currentWindow = idOfWindowToDisplay;
    }

    /**
     * Set the user you are chatting with
     *
     * @param user the user you are chatting with
     */
    public static void setChattingWithUser(User user) {
        chattingWithThisUser = user;
    }

    /**
     * Reload the display
     */
    public void reloadDisplay() {
        alreadyDisplay = false;
    }

}
