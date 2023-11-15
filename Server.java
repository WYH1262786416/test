import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Server {
	public static void main(String[] args) throws IOException {
		int port = Integer.parseInt(args[0]);
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (true) {
				Socket clientSocket = serverSocket.accept();
				InetAddress clientAddress = clientSocket.getInetAddress();
				int clientPort = clientSocket.getPort();
				System.out.println("Connected to client at " + clientAddress + ":" + clientPort);

				DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
				DataInputStream in = new DataInputStream(clientSocket.getInputStream());

				while (true) {
					try {
						byte[] buffer = new byte[1024];  // a large enough buffer
						int length = in.read(buffer);
						if (length == -1) {
							System.out.println("Reach buffer size limit");
							break;
						}
						byte[] bytes = Arrays.copyOf(buffer, length);  // only keep the actual data
						short number = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getShort();

						if (bytes.length != 2) {
							String response = "****";
							byte[] responseBytes = response.getBytes("UTF-16");
							out.write(responseBytes);
							System.out.println("Sent: " + response);
						} else {
							System.out.println("Received: 0x" + Integer.toHexString(number & 0xffff));
							String response = Short.toString(number);
							byte[] responseBytes = response.getBytes("UTF-16");
							out.write(responseBytes);
							System.out.println("Sent: " + response);
						}
					} catch (EOFException e) {
						// client has closed the connection
						System.out.println("Client has closed the connection.");
						break;
					} catch (IOException e) {
						System.out.println("Error: " + e.getMessage());
						String response = "****";
						byte[] bytes = response.getBytes("UTF-16");
						out.write(bytes);
						System.out.println("Sent: " + response);
					}
				}
			}
		}
	}
}