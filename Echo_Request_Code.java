/* This class is for the Echo_Request_code */

package com.company;
import ithakimodem.Modem;
import java.io.IOException;
import java.io.*;

public class Echo_Request_Code {
    // Variables
    public String command; // The command it changes within some time.
    public Modem modem;

    // Constructor
    Echo_Request_Code(String command, Modem modem){
        this.command = command; // Initializing the temporary command
        this.modem = modem;
    }

    public void echo_request_code(){
        int input; // this is the input from the server
        ByteArrayOutputStream content = new ByteArrayOutputStream();// here we are going to store the whole sequence of inputs
        String Stop = "abcde"; // here we'll store the break sequence
        long[] response_times = new long[300];

        for(int i = 0; i < 300; i++) {
            // Defining the command to the server
            long finish;
            long start = System.currentTimeMillis();
            modem.write(this.command.getBytes());
            // Getting the whole string from the server
            while (true) {
                // That's the byte from ithaki
                input = modem.read();

                // Catching the PSTOP sequence
                Stop += (char) input;
                content.write(input);
                Stop = Stop.substring(1, 6);
                if (Stop.equals("PSTOP")) {
                    content.write('\n');
                    finish = System.currentTimeMillis();
                    break; // Here we stop to read
                }
            }
            // Storing the times
            response_times[i] = finish - start;

        }
        // Writing the times in a file
        try{
            // Creating a file which will contain the time responses
            FileWriter file = new FileWriter("Echo_Responses.txt");
            FileOutputStream Byte_content = new FileOutputStream("Echo_Packets.txt");
            content.writeTo(Byte_content);
            // Storing the responses in the file
            for(int i = 0; i < 300; i++){
                String time = String.valueOf(response_times[i]);
                file.write(time+"\n");
            }
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
