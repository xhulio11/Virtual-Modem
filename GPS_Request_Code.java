package com.company;
import ithakimodem.*;

import javax.management.StringValueExp;
import java.io.*;

public class GPS_Request_Code {
    public String clear_command;
    public String command;
    public Modem modem;

    // Constructor
    GPS_Request_Code(String command ,String clear_command, Modem modem){
        this.command = command;
        this.clear_command = clear_command;
        this.modem = modem;
    }

    // Getting the image with the traces from the server
    public void gps_image_code(){
        int input; // This is the input from the server
        String new_command = this.clear_command;
        String traces [][] = gps_request_text_code();

        // Converting the .XXXX in to AA, multiplying it by 0.006
        for (String[] trace: traces){
            String good_part = trace[2].substring(0,4);
            int bad_part = Integer.parseInt(trace[2].substring(5,9));
            int result =  (int)Math.round((bad_part*0.006));
            trace[2] = good_part + String.valueOf(result) ;

            String good_part_2 = trace[4].substring(1,5);
            int bad_part_2 = Integer.parseInt(trace[4].substring(6,10));
            int result_2 = (int) Math.round((bad_part*0.006));
            trace[4] = good_part_2 + String.valueOf(result_2) ;
        }
        // Creating the command with all the traces
        for (String[] trace : traces) {
            new_command += "T=" + trace[4]+
                    trace[2];
        }
        new_command +="\r";
        
        System.out.println("The whole command is: " + new_command);
        // Defining the command to the server
        modem.write(new_command.getBytes());

        // ByteArrayOutputStream is an resizeable array of bytes
        ByteArrayOutputStream gps_byte_content = new ByteArrayOutputStream();

        String finish = "CD";

        // The loop collects the bytes and catches 0xFF and 0xD9 delimiter
        while(true){
            input = modem.read();
            finish +=(char)input;
            finish = finish.substring(1,3);
            if(finish.equals(String.valueOf((char)(0xFF)) + String.valueOf((char)(0xD9)))){
                gps_byte_content.write(input);
                break;
            }
            gps_byte_content.write(input);
        }
        try{
            // FileOutputStream  is used with ByteArrayOutputStream
            // to write all the bytes in one file
            FileOutputStream file = new FileOutputStream("gps_request_code.jpg");

            // storing the data  in the .jpg file
            gps_byte_content.writeTo(file);
            file.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        System.out.println("Program got the image with gps traces");
    }
    public String[][] gps_request_text_code() {
        System.out.println("Program is in gps request code getting the gps locations");

        // This is the input from the server
        int input;

        // ByteArrayOutputStream is an resizeable array of bytes
        ByteArrayOutputStream gps_byte_text_content = new ByteArrayOutputStream();

        String[] gps_content = new String[9];
        String[][] gps_split_content = new String[9][14];
        String start = "";
        String finish = "STOP ITHAKI GPS TRACKING\r\n";
        boolean a = false;

        modem.write(this.command.getBytes());

        // The loop catches START ITHAKI GPS TRACKING
        while (true){
            input = modem.read();
            start += (char) input;
            if (start.equals("START ITHAKI GPS TRACKING\r\n")) break;
        }

        int i = 0;
        int j = 0;
        // The loop catches the STOP ITHAKI GPS TRACKING
        while (true) {
            input = modem.read();
            finish += (char)input;
            finish = finish.substring(1,27);
            if(finish.equals("STOP ITHAKI GPS TRACKING\r\n"))break;

            if((char) input == '\n'){
                i++;
                continue;
            }

            // Getting the packet after 6 seconds
            if(i%10 == 0){
                gps_content[i/10] += (char)input;
            }
            gps_byte_text_content.write(input);
        }
        // Splinting the gps packet
        for(i = 0; i < gps_content.length; i++){
            gps_split_content [i] = gps_content[i].split(",");
        }

        try{
            // FileOutputStream  is used with ByteArrayOutputStream
            // to write all the bytes in one file
            FileOutputStream file = new FileOutputStream("gps_request_text_code.txt");

            // storing the data  in the .txt file
            gps_byte_text_content.writeTo(file);
            file.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return gps_split_content;
    }
}









