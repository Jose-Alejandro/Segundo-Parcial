import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class client {
  public static void main(String[] args) throws IOException {
    int portNumber=1234;
    String host="localhost";
    Socket client=new Socket(host,portNumber);
    ObjectInputStream input=new ObjectInputStream(client.getInputStream());
    ObjectOutputStream output=new ObjectOutputStream(client.getOutputStream());
  }
}
