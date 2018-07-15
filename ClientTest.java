import javax.swing.JFrame;

public class ClientTest {

	public static void main(String[] args) {
		Client ch = new Client("127.0.0.1");
		ch.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ch.startRunning();
	}

}
