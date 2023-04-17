package client.view;

import client.clientModel.User;
import client.controler.ServerConnection;

import javax.swing.*;

public class ViewManager implements Runnable {
    public ServerConnection serverConnection;
    public LoginForm loginForm; // LOGIN 0
    public RegistrationForm registrationForm; /// REGISTER 1
    public ContactWindow contactForm; // CONTACT 2
    public ConversationWindow conversationForm; // CHAT 3
    public OptionsWindow optionsWindow; // BAN 4
    public ReportingWindow reportingWindow; //REPORTS 5
    public static int currentWindow; // 0 = Login , 1 = Registration, 2 = ContactWindow, 3 = ConversationWindow, 4= BanPage, 5=ReportWindow
    public static User chattingWithThisUser; // If you chat with someone, his user ID is here
    public static boolean alreadyDisplay;
    public static boolean isClientBanned;   // If the client is banned, this boolean is true

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
        displayWindow();
    }

    public void displayWindow () {
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
                    if (isClientBanned) {
                        contactForm.closeContactWindow();
                        isClientBanned = false;
                    }
                }
                case 3 -> { // CHAT
                    if (!alreadyDisplay) {
                        alreadyDisplay = true;
                        this.conversationForm = new ConversationWindow(null, serverConnection, chattingWithThisUser,700,600);
                        conversationForm.openConversationWindow();
                    }
                    if (isClientBanned) {
                        conversationForm.closeConversationWindow();
                        isClientBanned = false;
                    }
                }
                case 4 -> { // BAN WINDOW
                    if (!alreadyDisplay) {
                        alreadyDisplay = true;
                        this.optionsWindow = new OptionsWindow(conversationForm, serverConnection, chattingWithThisUser,300,200);
                        optionsWindow.openOptionWindow();
                    }
                    if (isClientBanned) {
                        optionsWindow.closeOptionWindow();
                        isClientBanned = false;
                    }
                }
                case 5-> { // REPORTS
                    if (!alreadyDisplay) {
                        alreadyDisplay = true;
                        this.reportingWindow = new ReportingWindow(conversationForm, serverConnection,600,500);
                        reportingWindow.openReportWindow();
                    }
                    if (isClientBanned) {
                        reportingWindow.closeReportWindow();
                        isClientBanned = false;
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

    public static void setCurrentDisplay(final int idOfWindowToDisplay) {
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

    public static void setIsClientBanned (boolean isClientBanned) {
        ViewManager.isClientBanned = isClientBanned;
    }
}
