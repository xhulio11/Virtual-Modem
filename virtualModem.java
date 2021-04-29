package com.company;
import ithakimodem.*;

public class virtualModem {
    //Variables
    Modem modem;
    String echo_code = "E0111\r";
    String image_code = "M0266\r";
    String image_with_errors_code = "G8916\r";
    String gps_code = "P1559";
    String arq = "Q9133\r";
    String nack = "R3186\r";

    public static void main(String[] param) {
        (new virtualModem()).program();

    }
    public void program() {

        open_modem();
        // Sending the echo request code
        System.out.println("**********************************************************");
        System.out.println("We are in Echo Packet Code.");
        Echo_Request_Code echo_request = new Echo_Request_Code(this.echo_code, this.modem);
        echo_request.echo_request_code();
        System.out.println("Echo Packet code is finished.");
        System.out.println("**********************************************************\n");
        modem.close();


        open_modem();
        // Sending the image request code
        System.out.println("**********************************************************");
        System.out.println("We are in Image Packet Code.");
        Image_Request_Code image_request = new Image_Request_Code(this.image_code, modem, "normal_image.jpg");
        image_request.image_request_code();
        System.out.println("Image Packet Code is finished.");
        System.out.println("**********************************************************\n");
        modem.close();


        open_modem();
        // Sending the image request code
        System.out.println("**********************************************************");
        System.out.println("We are in Image Packet Code with Errors.");
        Image_Request_Code image_request2 = new Image_Request_Code(this.image_with_errors_code, modem,"image_with_errors.jpg");
        image_request2.image_request_code();
        System.out.println("Image Packet Code with Errors is finished.");
        System.out.println("**********************************************************\n");
        modem.close();


        open_modem();
        // Sending the gps request code
        System.out.println("**********************************************************");
        System.out.println("We are in GPS Request Code.");
        GPS_Request_Code gps_request = new GPS_Request_Code(this.gps_code+"R=1003181\r",this.gps_code,modem);
        gps_request.gps_image_code();
        System.out.println("GPS Request Code is finished.\n\n");
        modem.close();


        open_modem();
        System.out.println("**********************************************************\n");
        System.out.println("We are in ARQ CODE.");
        ARQ arq = new ARQ(this.arq,this.nack,modem);
        arq.arq_code();
        System.out.println("ARQ CODE is finished.");
        System.out.println("**********************************************************\n");
        modem.close();


        System.out.println("Modem is closed......");
    }
    public void open_modem(){
        int k;
        String sequence = "abcd";
        this.modem=new Modem();
        this.modem.setSpeed(8000);
        this.modem.setTimeout(8000);
        this.modem.open("ithaki");

        for (;;) {
            try {
                k=this.modem.read();
                sequence += (char)k;
                sequence = sequence.substring(1,5);
                if (sequence.equals("\r\n\n\n")) break;
                System.out.print((char)k);
            } catch (Exception x) {
                break;
            }
        }
    }
}
