import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends Thread {

    private ServerSocket serverSocket;

    private SocketServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                System.out.println("...waiting for client");
                Socket clientSock = serverSocket.accept();
                readMessage(clientSock);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readMessage(Socket clientSock) throws IOException {
        System.out.println("readMessage is started");
        DataInputStream inputStream = new DataInputStream(clientSock.getInputStream());

        do {
            //byte[] data = new byte[4096];
            //inputStream.read(data);
            byte[] message = new byte[4096];
            if (inputStream.read(message) == -1) {
                System.out.println("failed to read");
                break;
            }
            String strMsg = new String(message);
            System.out.println("message:" + strMsg);
            if (strMsg.contains("quit")) {
                System.out.println("quit");
                break;
            }
            //String data = inputStream.readUTF();
            //System.out.println("data:" + data);
            //if ("quit".equals(data)) break;
        } while(true);

        inputStream.close();
        System.out.println("readMessage is finished");
    }

    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer(1988);
        socketServer.start();
    }
}