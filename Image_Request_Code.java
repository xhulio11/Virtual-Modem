/* This class is for the Image_request_code*/

package com.company;
import ithakimodem.Modem;
import java.io.*;


public class Image_Request_Code {
    // Variables
    public String command; // The command changes within some time.
    public Modem modem;
    public String file_name;
    //Constructor
    Image_Request_Code(String command, Modem modem, String file_name){
        this.command = command;
        this.modem = modem;
        this.file_name = file_name;
    }

    public void image_request_code(){

        int input; // this is the input from the server
        String finish = "AB";

        // ByteArrayOutputStream is an resizeable array of bytes
        ByteArrayOutputStream byte_content = new ByteArrayOutputStream();

        // Defining the command to the server
        modem.write(this.command.getBytes());
        // Getting the whole string from the server
        while(true){
            input = modem.read();
            finish +=(char)input;
            finish = finish.substring(1,3);
            // Catching the 0xFF 0xD9
            if (finish.equals(String.valueOf((char)(0xFF)) + String.valueOf((char)(0xD9)))){
                byte_content.write(input);
                break;
            }
            byte_content.write(input);
        }
        try{
            // FileOutputStream  is used with ByteArrayOutputStream
            // to write all the bytes in one file
            FileOutputStream file = new FileOutputStream(this.file_name);

            // storing the data for the image in the .jpg file
            byte_content.writeTo(file);
            file.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
