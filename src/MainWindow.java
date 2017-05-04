import java.awt.EventQueue;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JRadioButton;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JTextArea;

public class MainWindow {

	private JFrame frame;
	private JTable InstructionMemoryTable;
	DefaultTableModel dtm = new DefaultTableModel();
	private static String allString;//For readFile function
	
	JTextArea InstructionTextArea;
	
	
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		
		
		
		
		
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(450, 150, 1100, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addContentsToFrame();
		
		
		//Reading a file
		readFile("Instructions.txt");
		InstructionTextArea.setText(allString);
		
		
	}

	private void addContentsToFrame() {

		JRadioButton radioBinary = new JRadioButton("Binary", true);
		radioBinary.setBounds(441, 9, 81, 25);
		radioBinary.setVerticalAlignment(SwingConstants.TOP);
		JRadioButton radioHexa = new JRadioButton("HexaDecimal");
		radioHexa.setBounds(543, 9, 105, 25);

		radioBinary.setMnemonic(KeyEvent.VK_C);
		radioHexa.setMnemonic(KeyEvent.VK_M);

		radioBinary.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {

			}
		});
		radioHexa.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {

			}
		});

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();

		group.add(radioBinary);
		group.add(radioHexa);
		frame.getContentPane().setLayout(null);

		frame.getContentPane().add(radioBinary);
		frame.getContentPane().add(radioHexa);
		///////////////////////////////////////////////////
		InstructionMemoryTable = new JTable();
		InstructionMemoryTable.setBounds(20,40,60,80);
		
		String []columnNames = {"Column1","Column 2","Column 3","Column4"};
		dtm.setColumnIdentifiers(columnNames);
		InstructionMemoryTable.setModel(dtm);
		
		
		
		JScrollPane scrollPane = new JScrollPane(InstructionMemoryTable);
		scrollPane.setBounds(64, 30, 169, 245);
		frame.getContentPane().add(scrollPane);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(64, 299, 169, 159);
		frame.getContentPane().add(scrollPane_1);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(64, 489, 169, 159);
		frame.getContentPane().add(scrollPane_2);
		
		InstructionTextArea = new JTextArea();
		InstructionTextArea.setEditable(false);
		InstructionTextArea.setBounds(380, 107, 352, 361);
		frame.getContentPane().add(InstructionTextArea);

	}

	private static void readFile(String FileName){
		BufferedReader reader = null;
		allString = "";
		
		try {
		    File file = new File(FileName);
		    reader = new BufferedReader(new FileReader(file));

		    String line;
		    while ((line = reader.readLine()) != null) {
		    	allString += line + "\n";
		    }

		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        reader.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
