package com.company;

import ithakimodem.Modem;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class ARQ {
    public String ack_command;
    public String nack_command;
    Modem modem;

    ARQ(String ack_command, String nack_command, Modem modem){
        this.ack_command = ack_command;
        this.nack_command = nack_command;
        this.modem = modem;
    }

    public void arq_code(){

        int input; // byte input from server
        String content = "";
        long []response_times = new long[300]; // we store the response times
        int [] packet = new int[300]; //  we store how many time each packet was sent
        Arrays.fill(packet,1); // All packets will be sent at least once
        int i = 0;
        long start_time;
        long finish_time;
        String finish = "abcde";
        int ack_counter = 0; //  we count the correct packets (We expect to be equal to 300)
        int nack_counter = 0; // we count the incorrect packets

        String new_command = this.ack_command;

        for(int j = 0; j < 300; j++) {
            start_time = System.currentTimeMillis();
            modem.write(new_command.getBytes());
            while (true) {
                input = modem.read();
                content += (char)input;

                // finish is about "PSTOP"
                finish += (char) input;
                finish = finish.substring(1, 6);

                if (finish.equals("PSTOP")) {
                    break;
                }

            }
            int [] array = sequence(content);
            int FCS = FCS(content);

            // Controlling if the packet was correct with FCS = X^X^X......
            if (FCS == (array[0] ^ array[1] ^ array[2] ^ array[3] ^ array[4] ^ array[5] ^
                    array[6] ^ array[7] ^ array[8] ^ array[9] ^ array[10] ^ array[11] ^ array[12] ^
                    array[13] ^ array[14] ^ array[15])) {

                finish_time = System.currentTimeMillis();
                response_times[j] = finish_time - start_time;
                content = "";
                new_command = this.ack_command;
                ack_counter++;
                i++; // Counting for the next packet how many times was sent
            } else {
                new_command = this.nack_command;
                packet[i] += 1;
                content = "";
                nack_counter++;
                j--; // to collect exactly 300 correct packets
            }
        }
            // Writing the times in a file
            try{
                // Creating a file which will contain the time responses
                FileWriter file = new FileWriter("response_ack_nack.txt");
                FileWriter file2 = new FileWriter("packet.txt");

                // Storing the responses in the file
                for( int k = 0; k < 300; k++){
                    String time = String.valueOf(response_times[k]);
                    file.write(time+"\n");
                }
                file.close();
                for(int l = 0; l < 300; l++){
                    file2.write(String.valueOf(packet[l]) + "\n");
                }
                file2.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("ACK Counter is: " + ack_counter);
            System.out.println("NACK Counter is: " + nack_counter);
        }

    // We use this function to determine the sequence XXXXX...
    public int[] sequence(String content) {
        content = content.substring(content.indexOf("<") + 1);
        content = content.substring(0, content.indexOf(">"));

        int []numbers = new int [16];
        char []array = content.toCharArray();
        for (int i = 0; i < 16; i++){
            numbers[i] = array[i];
        }
        return numbers;
    }
    // We use this functino to determine FCS
    public int FCS(String content){
        content = content.substring(content.indexOf(">")+2, content.indexOf(">") + 5);
        int FCS = Integer.parseInt(content);
        return FCS;
    }
}

