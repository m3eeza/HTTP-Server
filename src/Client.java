
import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void getMethod(String host, int port, String path)
            throws IOException {

        Socket clientSocket = null;
        clientSocket = new Socket(host, port);

        System.out.println("======================================");
        System.out.println("Connected");
        System.out.println("======================================");

        // Declare a writer to this url
        PrintWriter request = new PrintWriter(clientSocket.getOutputStream(), true);

        // Declare a listener to this url
        BufferedReader response = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // Sending request to the server
        // Building HTTP request header
        request.print("GET /" + path + "/ HTTP/1.1\r\n"); // "+path+"
        request.print("Host: " + host + "\r\n");
        request.print("Connection: close\r\n");
        request.print("Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n");
        request.print("\r\n");
        request.flush();
        System.out.println("Request Sent!");
        System.out.println("======================================");

//        // Receiving response from server
//        String responseLine;
//        while ((responseLine = response.readLine()) != null) {
//            System.out.println(responseLine);
//        }

        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            //read the number of files from the client
            int number = dis.readInt();
            ArrayList<File>files = new ArrayList<File>(number);
            System.out.println("Number of Files to be received: " +number);
            //read file names, add files to arraylist
            for(int i = 0; i< number;i++){
                File file = new File(dis.readUTF());
                files.add(file);
            }
            int n = 0;
            byte[]buf = new byte[4092];

            //outer loop, executes one for each file
            for(int i = 0; i < files.size();i++){

                System.out.println("Receiving file: " + files.get(i).getName());
                //create a new fileoutputstream for each new file
                FileOutputStream fos = new FileOutputStream("ClientOut/" +files.get(i).getName());
                //read file
                long fileSize = dis.readLong();
//                while((n = dis.read(buf)) != -1){
//                    fos.write(buf,0,n);
//                    fos.flush();
//                }
                while (fileSize >= 0 && (n = dis.read(buf, 0, (int)Math.min(buf.length, fileSize))) != -1)
                {
                  fos.write(buf,0,n);
                  fileSize -= n;
                }
                fos.close();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        System.out.println("======================================");
        System.out.println("Response Recieved!!");
        System.out.println("======================================");

        response.close();
        request.close();
        clientSocket.close();
    }

    public void receive(){


//        try {
//            DataInputStream dis = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
//            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
////read the number of files from the client
//            int number = dis.readInt();
//            ArrayList<File>files = new ArrayList<File>(number);
//            System.out.println("Number of Files to be received: " +number);
//            //read file names, add files to arraylist
//            for(int i = 0; i< number;i++){
//                File file = new File(dis.readUTF());
//                files.add(file);
//            }
//            int n = 0;
//            byte[]buf = new byte[4092];
//
//            //outer loop, executes one for each file
//            for(int i = 0; i < files.size();i++){
//
//                System.out.println("Receiving file: " + files.get(i).getName());
//                //create a new fileoutputstream for each new file
//                FileOutputStream fos = new FileOutputStream("C:\\users\\tom5\\desktop\\salestools\\" +files.get(i).getName());
//                //read file
//                while((n = dis.read(buf)) != -1){
//                    fos.write(buf,0,n);
//                    fos.flush();
//                }
//                fos.close();
//            }
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//
//        }


    }

    public static void main(String[] args) throws IOException {

        // TODO: get user input
        String host = "localhost";
        int port = 8080;
        String command = "GET";
        String path = "EmoMobile.png";

        // Method Check GET or PUT
        if ("GET".equals(command)) {
            Client.getMethod(host, port, path);
        } else {
            System.out.println("Check the HTTP command! It should be either GET or PUT");
            return;
        }

    }
}