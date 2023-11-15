import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

public class Client {
	public static void main(String[] args) throws IOException {
		String hostname = args[0];
		int port = Integer.parseInt(args[1]);

		try (
						Socket socket = new Socket(hostname, port);
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						DataInputStream in = new DataInputStream(socket.getInputStream());
		) {
			Scanner scanner = new Scanner(System.in);
			for (int i = 0; i < 5; i++) {
				try {
					System.out.print("Enter a number between -32768 and 32767: ");
					int input = scanner.nextInt();
					byte[] bytes;

					if (input >= -32768 && input <= 32767) {
						short number = (short) input;
						bytes = ByteBuffer.allocate(2).putShort(number).array();
					} else {
						// If the input is out of range, create a larger byte array
						bytes = ByteBuffer.allocate(4).putInt(input).array();
					}
					long startTime = System.currentTimeMillis();
					out.write(bytes);
					System.out.println("Sent: 0x" + Integer.toHexString(input & 0xffff));

					byte[] responseBytes = new byte[12];
					int bytesRead = in.read(responseBytes);
					byte[] actualBytes = Arrays.copyOf(responseBytes, bytesRead);
					String response;
					if (bytesRead == 12) {
						response = new String(actualBytes, "UTF-16");
						for (byte b : actualBytes) {
							System.out.printf("%02X ", b);
						}
					} else {
						response = new String(Arrays.copyOfRange(actualBytes,2,10));
						response = response.replace(" ","");
						if (!"* * * *".equals(response)){
							for (byte b : Arrays.copyOfRange(actualBytes,2,10)) {
								System.out.printf("%02X ", b);
							}
						}
					}
					System.out.println();
					System.out.println("Received: " + response);
					long endTime = System.currentTimeMillis();
					long duration = endTime - startTime; // Duration in milliseconds
					System.out.println("Total Duration Time: " + duration + " ms");
				} catch (IOException e) {
					System.out.println("Error: " + e.getMessage());
				}
			}
		}
	}
}