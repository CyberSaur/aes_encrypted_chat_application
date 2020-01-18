/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat_application;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 *
 * @author Suneth
 */
public class Server extends JFrame{
    
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;
    final String secretKey = "ssshhhhhhhhhhh!!!!";
    AESencryption aes = new AESencryption();
	
    //constructor
    public Server(){
        super("WhatsChat Instant Messenger");
        try{
            userText = new JTextField();
            userText.setEditable(false);
            userText.addActionListener(
            new ActionListener(){
                public void actionPerformed(ActionEvent event){
                    sendMessage(event.getActionCommand());
                    userText.setText("");
                }
            });
            add(userText, BorderLayout.NORTH);
            chatWindow = new JTextArea();
            add(new JScrollPane(chatWindow));
            setSize(300,150);
            setVisible(true);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error occured","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
	
    //set up and run the server
    public void startRunning(){
        try{    
            server = new ServerSocket(6789, 100);
            while(true){
			try{
                                waitForConnection();
                                setupStreams();
                                whileChatting();
                           }catch(Exception ex){
                                showMessage("\n Server ended the connection! ");
                           }finally{
                                 closeCrap();
                           }
                        }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error occured","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
	
    //wait for connection, then display connection information
    private void waitForConnection(){
        try{
            showMessage(" Waiting for someone to connect... \n");
            connection = server.accept();
            showMessage(" Now connected to " + connection.getInetAddress().getHostName());
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error occured","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
	
    //get stream to send and receive data
    private void setupStreams(){
        try{
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            showMessage("\n Streams are now setup! \n");
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error occured","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
	
    //during the chat conversation
    private void whileChatting(){
        try{
            String message = " You are now connected! ";
            sendMessage(message);
            ableToType(true);
            do{
                try{
                        message = (String) input.readObject();
                        //AES encryption
                        String encryptedString = message;
                        System.out.println(encryptedString);
                        String decryptedString = aes.decrypt(encryptedString, secretKey) ;
                        System.out.println(decryptedString);            
                        showMessage("\n" + decryptedString);
                   }catch(ClassNotFoundException classNotFoundException){
                        JOptionPane.showMessageDialog(null,"Unable to send the message","Error",JOptionPane.ERROR_MESSAGE);
                        System.out.println("Error occured: " + classNotFoundException.toString());
                   }
            }while(!message.equals("CLIENT - END"));
        }catch(Exception ex){
            System.out.println("Error occured: " + ex.toString());
        }
    }
	
    //close streams and sockets after you are done chatting
    private void closeCrap(){
        try{
            showMessage("\n Closing connections... \n");
            ableToType(false);
            try{
                    output.close();
                    input.close();
                    connection.close();
               }catch(IOException ioException){
                    System.out.println("Error occured: " + ioException.toString());
               }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error occured","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
	
    //input validation
    public boolean inputValidation(String m) throws Exception{
        String pattern= "^[a-zA-Z0-9\\t\\n ./<>?;:\"'`!@#$%^&*()\\[\\]{}_+=|\\\\-]+$";
        return m.matches(pattern);
    }
    
    //send a message to client
    private void sendMessage(String message){
        try{
            if(inputValidation(message) == true)
            {
                System.out.println("SERVER - " + message);
                String encryptedString = aes.encrypt("SERVER - " + message, secretKey) ;
                System.out.println(encryptedString);
                output.writeObject(encryptedString);
                output.flush();
                showMessage("\nSERVER - " + message);
            }
            else
            {
                JOptionPane.showMessageDialog(null,"Unable to send the message","Error",JOptionPane.ERROR_MESSAGE);
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Unable to send the message","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
	
    //updates chat window
    private void showMessage(final String text){
        try{
            SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        chatWindow.append(text);
                    }
                });
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Unable to send the message","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
	
    //let the user type a message in the text box
    private void ableToType(final boolean tof){
        try{
            SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        userText.setEditable(tof);
                    }
                });
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error occured","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }
}
