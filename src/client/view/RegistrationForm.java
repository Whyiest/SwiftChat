package client.view;

import client.clientModel.User;
import client.controler.ServerConnexion;

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
    private JPasswordField pfPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnRegister;
    private JButton btnCancel;

    public User user;


    public RegistrationForm(JFrame parent){
        super(parent);
        setTitle("Registration Form");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(700,600));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void registerUser() {
        String firstName= textFieldFirstname.getText();
        String lastName= textFieldLastName.getText();
        String userName= textFieldUsername.getText();
        String mail= textFieldEmail.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmPassword = String.valueOf(pfConfirmPassword.getPassword());
         if( firstName.isEmpty() || lastName.isEmpty() || userName.isEmpty()|| mail.isEmpty()|| password.isEmpty()){
             JOptionPane.showMessageDialog(this,"Invalid fields","Try again",JOptionPane.ERROR_MESSAGE);
             return;
         }
         if(!password.equals(confirmPassword)){
             JOptionPane.showMessageDialog(this,"Passwords do not match","Try again",JOptionPane.ERROR_MESSAGE);
             return;
         }
         user = getAuthenticatedUser(firstName,lastName,userName,mail,password);
         dispose();

    }
    private User getAuthenticatedUser(String firstName, String lastName, String username, String email, String password) {
        User users = new User();
        users.setFirstName(firstName);
        users.setLastName(lastName);
        users.setUserName(username);
        users.setMail(email);
        users.setPassword(password);
        return users;
    }
}
