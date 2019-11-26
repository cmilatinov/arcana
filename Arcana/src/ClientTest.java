import java.io.IOException;
import java.net.InetAddress;

import com.arcana.network.Client;

public class ClientTest {

	public static void main(String[] args){
		try {
			Client c = new Client(InetAddress.getLocalHost(), "hehe xd");
			c.connect();
			Thread.sleep(3000);
			Runtime.getRuntime().exec("cmd /c cls");
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
}
