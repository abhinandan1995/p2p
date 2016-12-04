import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class TCPClientDownload{

    private final static String serverIP = "127.0.0.1";
    private final static int serverPort = 3249;
    private final static String fileOutput = "e:/video/test.mp4";

    public static void main(String args[]) {
        byte[] aByte = new byte[1];
        int bytesRead;

        Socket clientSocket = null;
        InputStream is = null;

        try {
            clientSocket = new Socket( serverIP , serverPort );
            is = clientSocket.getInputStream();
        } catch (IOException ex) {
            // Do exception handling
        }
receive(clientSocket);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        if (is != null) {
//
//            FileOutputStream fos = null;
//            BufferedOutputStream bos = null;
//            try {
//                fos = new FileOutputStream( fileOutput );
//                bos = new BufferedOutputStream(fos);
//                bytesRead = is.read(aByte, 0, aByte.length);
//
//                do {
//                        baos.write(aByte);
//                        bytesRead = is.read(aByte);
//                } while (bytesRead != -1);
//
//                bos.write(baos.toByteArray());
//                bos.flush();
//                bos.close();
//                clientSocket.close();
//            } catch (IOException ex) {
//                // Do exception handling
//            }
//        }
    }
    
    public static void receive(Socket socket){

        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    //read the number of files from the client
            long size= dis.readLong();
            int n = 0;
            byte[]buf = new byte[4092];

            //outer loop, executes one for each file
            

                System.out.println("Receiving file:...");
                //create a new fileoutputstream for each new file
                FileOutputStream fos = new FileOutputStream(fileOutput);
                //read file
                while (size > 0 && (n = dis.read(buf, 0, (int)Math.min(buf.length, size))) != -1)
                		{
                		  fos.write(buf,0,n);
                		  fos.flush();
                		  size -= n;
                		}
                		fos.close();
                

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }


    }
}