import javax.swing.JFrame;

public class Management {

	static JFrame frame;
	private Screen screen;
	
	public Management(){
		
		frame = MainWindow.frame;
		screen = new Screen();
	}
	
}
