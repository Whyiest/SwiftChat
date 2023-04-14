package client.view;

import client.controler.ServerConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class RegistrationForm extends JDialog {
    private JPanel registerPanel;
    private JTextField textFieldFirstname;
    private JTextField textFieldLastName;
    private JTextField textFieldUsername;
    private JTextField textFieldEmail;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private ServerConnection serverConnection;
    private JButton btnRegister;
    private JButton btnCancel;


    /**
     * Constructor
     * @param parent the parent frame
     * @param serverConnection the server connection
     */
    public RegistrationForm(JFrame parent, ServerConnection serverConnection){
        super(parent);

        this.serverConnection = serverConnection;

        // SETUP

        setTitle("Registration Form");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(700,600));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // REGISTER BUTTON
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        // CANCEL BUTTON
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Go back to login
                ViewManager.setCurrentDisplay(0);

                // Close the current window
                closeRegisterWindow();
            }
        });
    }

    /**
     * Opens the register window
     */
    public void openRegisterWindow () {
        setVisible(true);
    }

    /**
     * Closes the register window
     */
    public void closeRegisterWindow () {
        setVisible(false);
        dispose();
    }

    /**
     * Method to check if a char is alphanumeric
     * @param c the char to check
     * @return true if the char is alphanumeric, false otherwise
     */
    public static boolean isAlphanumeric(char c){
        // if the character is alphanumeric (either an arabic number, an uppercase letter, or a lowercase letter), the method returns true
        if((c >= 48 && c <= 57) || (c >= 65 && c <= 90) || (c >= 97 && c <= 122)){
            return true;
        } // else the method returns false
        return false;
    }

    /**
     * Method to check if a char is a valid prefix character
     * @param c the char to check
     * @return true if the char is a valid prefix character, false otherwise
     */
    public static boolean isValidPrefixChar(char c){
        // if the character is alphanumeric, a period, a dash, or an underscore, the method returns true
        if(isAlphanumeric(c) || c == 45 || c == 46 || c == 95 ){
            return true;
        } // else the method returns false
        return false;
    }

    /**
     * Method to check if a char is a valid domain character
     * @param c the char to check
     * @return true if the char is a valid domain character, false otherwise
     */

    public static boolean isValidDomainChar(char c){
        // if the character is alphanumeric, a period, or a dash, the method returns true
        if(isAlphanumeric(c) || c == 45 || c == 46){
            return true;
        } // else the method returns false
        return false;
    }

    /**
     * Method to check if a String contains exactly one '@'
     * @param s the String to check
     * @return true if the String contains exactly one '@', false otherwise
     */
    public static boolean exactlyOneAt(String s){
        // declaring and initializing the variable counting the number of '@' in the String
        int numberOfAt = 0;
        // loop that counts the number of '@' occurences in the String
        for(int i = 0; i < s.length(); i++){
            // for each '@' encountered, numberOfAt is increased
            if(s.charAt(i) == '@'){
                numberOfAt++;
            }
        }
        // if there is exactly one '@' in the String, the method returns true
        if(numberOfAt == 1){
            return true;
        } // else the method returns false
        return false;
    }

    /**
     * Method to check if prefix is valid
     * @param s the prefix to check
     * @return true if the prefix is valid, false otherwise
     */
    public static String getPrefix(String s){
        // declaring and initializing the String which will contain the prefix
        String prefix = "";
        // loop that goes through all the characters in the input String
        for(int i = 0; i < s.length(); i++){
            // if an '@' is encountered, we go out of the loop: the new String is complete
            if(s.charAt(i) == '@'){
                break;
            }
            // else the new String is updated by adding a new character to it to match the input String
            prefix += s.charAt(i);
        }
        // the method returns the new String
        return prefix;
    }

    /**
     * Method to get the domain of an email address
     * @param s the email address
     * @return the domain of the email address
     */
    public static String getDomain(String s){
        // declaring and initializing the String which will contain the domain
        String domain = "";
        // loop that goes through all the characters in the input String starting from the '@' character
        for(int i = s.indexOf("@")+1; i < s.length(); i++){
            // the new String is updated by adding a new character to it to match the input String
            domain += s.charAt(i);
        }
        // the method returns the new String
        return domain;
    }

    /**
     * Method to check if a String is a valid prefix
     */
    public static boolean isValidPrefix(String s){
        // if the input String is not empty, and if the first and last characters are alphanumeric
        if(!s.isEmpty() && isAlphanumeric(s.charAt(0)) && isAlphanumeric(s.charAt(s.length()-1))){
            // loop that goes through all the characters in the input String
            for(int i = 0; i < s.length(); i++){
                // if the character is not valid for a prefix, or there are 2 non-alphanumeric characters next to each other
                // the method returns false
                if(!isValidPrefixChar(s.charAt(i)) || (!isAlphanumeric(s.charAt(i)) && !isAlphanumeric(s.charAt(i+1)))){
                    return false;
                }
            }
            // the method returns true if all the requirements are met
            return true;
        }
        // else the method returns false
        return false;
    }

    /**
     * Method to check if a String is a valid domain
     * @param s the String to check
     * @return true if the String is a valid domain, false otherwise
     */
    public static boolean isValidDomain(String s){
        // if there is no period in the input String, the method returns false
        if(s.indexOf(".") == -1){
            return false;
        }

        // declaring and initializing 2 Strings as the 2 portions of the domain
        String portion1 = "";
        String portion2 = "";

        // separating the input String as the 2 domain portions
        for(int i = 0; i < s.lastIndexOf("."); i++){ portion1 += s.charAt(i); }
        for(int i = s.lastIndexOf(".")+1; i < s.length(); i++){ portion2 += s.charAt(i); }

        // if the first portion is empty, or if the first and last characters are not alphanumeric, the method returns false
        if(portion1.isEmpty() || !isAlphanumeric(portion1.charAt(0)) || !isAlphanumeric(portion1.charAt(portion1.length()-1))){
            return false;
        }
        // loop that goes through all the characters in the first portion
        for(int i = 0; i < portion1.length(); i++){
            // if the character is not valid for a domain, or there are 2 non-alphanumeric characters next to each other
            // the method returns false
            if(!isValidDomainChar(portion1.charAt(i)) || (!isAlphanumeric(portion1.charAt(i)) && !isAlphanumeric(portion1.charAt(i+1)))){
                return false;
            }
        }

        // if the second portion has not at least 2 characters, the method returns false
        if(portion2.length() < 2){
            return false;
        }
        // loop that goes through all the characters in the second portion
        for(int i = 0; i < portion2.length(); i++){
            // if the character is not a letter, the method returns false
            if(portion2.charAt(i) < 65 || portion2.charAt(i) > 90 && portion2.charAt(i) < 97 || portion2.charAt(i) > 122){
                return false;
            }
        }

        // in all the other cases, the method returns true
        return true;
    }

    /**
     * Method to check if the mail address is valid
     * @param s the mail address
     * @return true if the mail address is valid, false otherwise
     */
    public static boolean isValidEmail(String s){
        // if the input String has exactly one '@', a valid prefix, and a valid domain, the method returns true
        if(exactlyOneAt(s) && isValidPrefix(getPrefix(s)) && isValidDomain(getDomain(s))){
            return true;
        } // else the method returns false
        return false;
    }

    /**
     * Method to register a new user
     */
    private void registerUser() {

        String serverResponse = "";
        // Get the content of all fields :

        String firstName= textFieldFirstname.getText();
        String lastName= textFieldLastName.getText();
        String userName= textFieldUsername.getText();
        String mail= textFieldEmail.getText();
        String password = String.valueOf(passwordField.getPassword());
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword());

        // Check validity of fields :

         if( firstName.isEmpty() || lastName.isEmpty() || userName.isEmpty()|| password.isEmpty()){
             JOptionPane.showMessageDialog(this,"Invalid fields","Try again",JOptionPane.ERROR_MESSAGE);
             return;
         }
         if(!password.equals(confirmPassword)){
             JOptionPane.showMessageDialog(this,"Passwords do not match","Try again",JOptionPane.ERROR_MESSAGE);
             return;
         }
        if(!isValidEmail(mail)){
            JOptionPane.showMessageDialog(this,"Incorrect mail format","Try again",JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create an user with the provided parameter
        do {
            try {
                serverResponse = serverConnection.addUser(userName, firstName, lastName, mail, password, "CLASSIC");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,"Connection lost, please wait we try to reconnect you.","Connection error",JOptionPane.ERROR_MESSAGE);
                try {
                    Thread.sleep(1000);
                } catch (Exception e1) {
                    return;
                }
                return;
            }
        } while (!serverResponse.equals("ADD-USER;SUCCESS"));


        // Go back to login page to log in
        ViewManager.setCurrentDisplay(0);

        // Close the current window
         dispose();
    }

}
