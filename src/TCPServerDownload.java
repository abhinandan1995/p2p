import java.io.*;
import java.net.*;

public class TCPServerDownload {

    private final static String fileToSend = "e:/video/Owl City - Fireflies.mp4";

    public static void main(String args[]) {

        while (true) {
            ServerSocket welcomeSocket = null;
            Socket connectionSocket = null;
            BufferedOutputStream outToClient = null;

            try {
                welcomeSocket = new ServerSocket(3249);
                connectionSocket = welcomeSocket.accept();
                outToClient = new BufferedOutputStream(connectionSocket.getOutputStream());
                send(connectionSocket);
            } catch (IOException ex) {
                // Do exception handling
            }

            if (false && outToClient != null) {
                File myFile = new File( fileToSend );
                byte[] mybytearray = new byte[(int) myFile.length()];

                FileInputStream fis = null;

                try {
                    fis = new FileInputStream(myFile);
                } catch (FileNotFoundException ex) {
                    // Do exception handling
                }
                BufferedInputStream bis = new BufferedInputStream(fis);

                try {
                    bis.read(mybytearray, 0, mybytearray.length);
                    outToClient.write(mybytearray, 0, mybytearray.length);
                    outToClient.flush();
                    outToClient.close();
                    connectionSocket.close();

                    // File sent, exit the main method
                    return;
                } catch (IOException ex) {
                    // Do exception handling
                }
            }
        }
    }
    
    public static void send(Socket socket){

        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            
            dos.writeLong((new File(fileToSend)).length());
            dos.flush();

            //buffer for file writing, to declare inside or outside loop?
            int n = 0;
            byte[]buf = new byte[4092];
            //outer loop, executes one for each file

                System.out.println("Sending...");
                //create new fileinputstream for each file
                FileInputStream fis = new FileInputStream(new File(fileToSend));

                //write file to dos
                while((n =fis.read(buf)) != -1){
                    dos.write(buf,0,n);
                    dos.flush();

                }
                //should i close the dataoutputstream here and make a new one each time?
       
            //or is this good?
            dos.close();
            fis.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}