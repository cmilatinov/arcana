import com.arcana.network.Server;

public class ServerTest {
	
	public static void main(String[] args){
		Server s = new Server("redknight990");
		s.host();
		while(true){
			s.tick();
		}
	}
	
}
