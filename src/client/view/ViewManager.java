package client.view;

import client.clientModel.Data;
import client.clientModel.User;
import client.controler.ServerConnection;


public class ViewManager implements Runnable {
    public ServerConnection serverConnection;
    public LoginForm loginForm; // LOGIN 0
    private RegistrationForm registrationForm; /// REGISTER 1
    private ContactWindow contactForm; // CONTACT 2
    private GroupWindow groupWindow; //GROUP 3
    private ConversationWindow conversationForm; // CHAT 4
    private OptionsWindow optionsWindow; // BAN 5
    private ReportingWindow reportingWindow; //REPORTS

    private final Data localStorage;
    public static int currentWindow; // 0 = Login , 1 = Registration, 2 = ContactWindow, 3 = ConversationWindow, 4= BanPage, 5=ReportWindow
    public static User chattingWithThisUser; // If you chat with someone, his user ID is here
    public static User currentUser; // If you chat with someone, his user ID is here

    public static boolean alreadyDisplay;
    public static boolean isClientBanned;   // If the client is banned, this boolean is true

    public User user;

    /**
     * Constructor
     *
     * @param serverConnection the server connection
     */
    public ViewManager(ServerConnection serverConnection, Data localStorage) {
        this.serverConnection = serverConnection;
        this.localStorage = localStorage;
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
                        loginForm = new LoginForm(null, serverConnection, localStorage, 700, 600);
                        loginForm.openLoginWindow();
                    }
                }
                case 1 -> { // REGISTER
                    if (!alreadyDisplay) {
                        alreadyDisplay = true;
                        this.registrationForm = new RegistrationForm(null, serverConnection,localStorage,700,600);
                        registrationForm.openRegisterWindow();
                    }
                }
                case 2 -> { // CONTACT
                    if (!alreadyDisplay) {
                        alreadyDisplay = true;
                        contactForm = new ContactWindow(null, serverConnection,currentUser,700,600, localStorage);
                        contactForm.openContactWindow();
                    }
                    if (isClientBanned) {
                        contactForm.closeContactWindow();
                        isClientBanned = false;
                    }
                }
                case 3 -> { // GROUP
                    if (!alreadyDisplay) {
                        alreadyDisplay = true;
                        this.groupWindow = new GroupWindow(null, serverConnection, localStorage, currentUser, 700,600);
                        groupWindow.openConversationWindow();
                    }
                    if (isClientBanned) {
                        conversationForm.closeConversationWindow();
                        isClientBanned = false;
                    }
                }
                case 4 -> { // CHAT
                    if (!alreadyDisplay) {
                        alreadyDisplay = true;
                        this.conversationForm = new ConversationWindow(null, serverConnection, localStorage, currentUser, chattingWithThisUser, 700,600, false);
                        conversationForm.openConversationWindow();
                    }
                    if (isClientBanned) {
                        conversationForm.closeConversationWindow();
                        isClientBanned = false;
                    }
                }
                case 5 -> { // BAN WINDOW
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
                case 6-> { // REPORTS
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
                case 7 -> { // SIMPLE QUESTION AI
                    if (!alreadyDisplay) {
                        alreadyDisplay = true;
                        this.conversationForm = new ConversationWindow(null, serverConnection, localStorage, currentUser, chattingWithThisUser, 700,600, true);
                        conversationForm.openConversationWindow();
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
     * Set the current user of the client
     * @param user the current user
     */
    public static void setCurrentUser(User user){
        currentUser = user;
    }

    /**
     * Set if the client is banned
     * @param isClientBanned if the client is banned
     */
    public static void setIsClientBanned (boolean isClientBanned) {
        ViewManager.isClientBanned = isClientBanned;
    }

    /**
     * Reload the display
     */
    public void reloadDisplay() {
        alreadyDisplay = false;
    }
}
