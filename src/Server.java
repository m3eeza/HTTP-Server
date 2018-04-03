
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;


public class Server {

    private ServerSocket serverSocket;
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {

        serverSocket = new ServerSocket(port);
        System.out.println("Starting the socket server at port:" + port);

        Socket client = null;

        while (true) {
            System.out.println("Waiting for clients...");
            client = serverSocket.accept();
            System.out.println("The following client has connected:" + client.getInetAddress().getCanonicalHostName());
            Thread thread = new Thread(new ClientHandler(client));
            thread.start();
        }
    }

    public static void main(String[] args) {

        int portNumber = 8080;

        try {
            Server socketServer = new Server(portNumber);
            socketServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {

    private Socket client;

    public ClientHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        ArrayList<File>files = new ArrayList<>();
        // TODO: Add file name
        files.add(new File("docroot/vid1.mp4"));
        send(files);
//        try {
//            System.out.println("Thread started with name:" + Thread.currentThread().getName());
//
////            readResponse();
//            return;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public void send(ArrayList<File>files){

        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
            System.out.println(files.size());
            //write the number of files to the server
            dos.writeInt(files.size());
            dos.flush();

            //write file names
            for(int i = 0 ; i < files.size();i++){
                dos.writeUTF(files.get(i).getName());
                dos.flush();
            }

            //buffer for file writing, to declare inside or outside loop?
            int n = 0;
            byte[]buf = new byte[4092];
            //outer loop, executes one for each file
            for(int i =0; i < files.size(); i++){

                System.out.println(files.get(i).getName());
                //create new fileinputstream for each file
                FileInputStream fis = new FileInputStream(files.get(i));

                //write file to dos
                dos.writeLong(files.get(i).getTotalSpace());
                while((n =fis.read(buf)) != -1){
                    dos.write(buf,0,n);
                    dos.flush();

                }
                //should i close the dataoutputstream here and make a new one each time?
            }
            //or is this good?
            dos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    private void readResponse() throws IOException, InterruptedException {

        try {

            BufferedReader request = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedWriter response = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            String requestHeader = "";
            String temp = ".";
            while (!temp.equals("")) {
                temp = request.readLine();
                System.out.println(temp);
                requestHeader += temp + "\n";
            }

            // Get the method from HTTP header
            StringBuilder sb = new StringBuilder();
            String file = requestHeader.split("\n")[0].split(" ")[1].split("/")[1];

            if (requestHeader.split("\n")[0].contains("GET") && checkURL(file)) {

                constructResponseHeader(200, sb);
                response.write(sb.toString());
                response.write(getData(file));
                sb.setLength(0);
                response.flush();

            } else {
                constructResponseHeader(404, sb);
                response.write(sb.toString());
                sb.setLength(0);
                response.flush();
            }

            request.close();
            response.close();

            client.close();
            return;
        } catch (Exception e) {
        }

    }

    private static boolean checkURL(String file) {
        File myFile = new File("docroot/" + file);
        return myFile.exists() && !myFile.isDirectory();
    }

    private static void constructResponseHeader(int responseCode, StringBuilder sb) {

        if (responseCode == 200) {

            sb.append("HTTP/1.1 200 OK\r\n");
            sb.append("Date: " + getTimeStamp() + "\r\n");
            sb.append("Server: localhost\r\n");
            sb.append("Content-Type: text/html\r\n");
            sb.append("Connection: Closed\r\n\r\n");

        } else if (responseCode == 404) {

            sb.append("HTTP/1.1 404 Not Found\r\n");
            sb.append("Date: " + getTimeStamp() + "\r\n");
            sb.append("Server: localhost\r\n");
            sb.append("\r\n");
        }
    }

    private static String getData(String file) {

        File myFile = new File("docroot/" + file);
        String responseToClient = "";
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(myFile));
            String line = null;
            while (!(line = reader.readLine()).contains("</html>")) {
                responseToClient += line;
            }
            responseToClient += line;
            reader.close();

        } catch (Exception e) {

        }
        return responseToClient;
    }

    private static String getTimeStamp() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

}
