import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class Screen {


	private JFrame frame;
	private JTextArea InstructionTextArea;
	private JTable instructionTable, dataTable, stackTable, labelTable;
	private JTextField textfieldR0, textfieldR1, textfieldR2,textfieldStackPointer, textfieldOutr, textfieldInpr, textfieldAddr, textfieldPc, textfieldInsr, textfieldT0, textfieldT1, textfieldT2, textfieldT3 ,textfieldT4, textfieldT5;
	private Memory instructionMemory, dataMemory, stackMemory, labelMemory;

	private DefaultTableModel stackDtm = new DefaultTableModel();
	private DefaultTableModel dataDtm = new DefaultTableModel();
	private DefaultTableModel instructionDtm = new DefaultTableModel();
	private DefaultTableModel labelDtm = new DefaultTableModel();

	private JPanel debugAndRun;
	private JButton debug, run;


	private String allString;//For readFile function

	private int[][] tableDecimalInstruction;
	private String[][] tableInstruction;

	private String[][] tableData;

	private int[][] tableDecimalStack;
	private String[][] tableStack;

	private int[][] tableDecimalLabel;
	private String[][] tableLabel;

	Parsing parse;

	// IMPORTANT THÝNGS THAT YOU MUST REMEMBER.

	// tableInstruction[][0] means - I
	// tableInstruction[][1] means - Opcode
	// tableInstruction[][2] means - D
	// tableInstruction[][3] means - S1
	// tableInstruction[][4] means - S2

	///////////////////////////////////////////


	final String ADD = "1";




	Screen () {

		tableInstruction = new String[32][5];
		tableDecimalInstruction = new int[32][5];
		//table[2][1] = "ahmet";

		tableData = new String[16][3];
		//tableData[2][2] = "cabbar";

		tableStack = new String[16][5];
		tableDecimalStack = new int[16][5];
		//tableStack[2][1] = "val";

		tableLabel = new String[16][4];
		tableDecimalLabel = new int[16][4];
		tableLabel[2][1] = "val";


		parse = new Parsing("deneme1.txt");
		tableDecimalInstruction = parse.getInstructionDecimal();
		tableData = parse.getDataDecimal();
		
		frame = Management.frame;
		addContentsToFrame();

		fillTable();

		//Reading a file
		readFile("deneme1.txt");
		InstructionTextArea.setText(allString);
		//		parseCode("deneme1.txt");
	}

	private void fillTable () {


		for (int i = 0; i < tableInstruction.length; i++) {

			for (int j = 0; j < tableInstruction[0].length; j++) {

				if (tableDecimalInstruction[i][0] != -9) {

					instructionDtm.setValueAt(tableDecimalInstruction[i][j], i, j+1);
				} else {
					break;
				}
			}
		}//For Instruction Memory

		///////////////////////////////////////////////////////
		for (int i = 0; i < tableData.length; i++) {

			dataDtm.setValueAt(tableData[i][1], i, 1);

		}//For Data Memory

		///////////////////////////////////////////////////////

		for (int i = 0; i < tableStack.length; i++) {

			stackDtm.setValueAt(tableStack[i][1], i, 1);

		}//For Stack Memory

		/////////////////////////////////////////////////////
		for (int i = 0; i < tableLabel.length; i++) {

			labelDtm.setValueAt(tableLabel[i][1], i, 1);
			labelDtm.setValueAt(tableLabel[i][2], i, 2);

		}//For Label Memory (La Label ne ara Memory oldu :D)





	}

	private String[][] toHex (int[][] arr) {

		String[][] butterfly = new String[32][5];

		for (int i = 0; i < arr.length; i++) {

			for (int j = 0; j < arr[1].length; j++) {

				butterfly[i][j] = "xx" + Integer.toHexString(arr[i][j]).toUpperCase(); // 'xx' mean is that the value is Hexadecimal.
			}
		}

		return butterfly;
	}

	private String[][] toBinary (int[][] arr) {

		String[][] butterfly = new String[32][5];

		for (int i = 0; i < arr.length; i++) {

			for (int j = 0; j < arr[1].length; j++) {

				butterfly[i][j] = Integer.toBinaryString(arr[i][j]);
			}
		}

		return butterfly;
	}

	private void addContentsToFrame() {


		JLabel labelOutputRegister = new JLabel("OUTR : ");
		labelOutputRegister.setBounds(923, 430, 50, 20);
		frame.getContentPane().add(labelOutputRegister);

		JLabel labelInputRegister = new JLabel("INPR : ");
		labelInputRegister.setBounds(923, 465, 50, 20);
		frame.getContentPane().add(labelInputRegister);

		JLabel labelAddressRegister = new JLabel("ADDR : ");
		labelAddressRegister.setBounds(923, 500, 50, 20);
		frame.getContentPane().add(labelAddressRegister);

		JLabel labelPc = new JLabel("PC : ");
		labelPc.setBounds(923, 535, 50, 20);
		frame.getContentPane().add(labelPc);

		JLabel labelInstructionRegister = new JLabel("INSR : ");
		labelInstructionRegister.setBounds(923, 570, 50, 20);
		frame.getContentPane().add(labelInstructionRegister);

		JLabel labelStackPointer = new JLabel("SP : ");
		labelStackPointer.setBounds(923, 605, 50, 20);
		frame.getContentPane().add(labelStackPointer);
		//////////////////////////////////////////////////
		JLabel labelR0 = new JLabel("R0 : ");
		labelR0.setBounds(823, 535, 50, 20);
		frame.getContentPane().add(labelR0);

		JLabel labelR1 = new JLabel("R1 : ");
		labelR1.setBounds(823, 570, 50, 20);
		frame.getContentPane().add(labelR1);

		JLabel labelR2 = new JLabel("R2 : ");
		labelR2.setBounds(823, 605, 50, 20);
		frame.getContentPane().add(labelR2);
		/////////////////////////////////////////////////////
		JLabel labelT0 = new JLabel("T0 : ");
		labelT0.setBounds(400, 430, 50, 20);
		frame.getContentPane().add(labelT0);

		JLabel labelT1 = new JLabel("T1 : ");
		labelT1.setBounds(400, 465, 50, 20);
		frame.getContentPane().add(labelT1);

		JLabel labelT2 = new JLabel("T2 : ");
		labelT2.setBounds(400, 500, 50, 20);
		frame.getContentPane().add(labelT2);

		JLabel labelT3 = new JLabel("T3 : ");
		labelT3.setBounds(400, 535, 50, 20);
		frame.getContentPane().add(labelT3);

		JLabel labelT4 = new JLabel("T4 : ");
		labelT4.setBounds(400, 570, 50, 20);
		frame.getContentPane().add(labelT4);

		JLabel labelT5 = new JLabel("T5 : ");
		labelT5.setBounds(400, 605, 50, 20);
		frame.getContentPane().add(labelT5);
		////////////////////////////////////////////////////////


		textfieldR0 = new JTextField();
		textfieldR0.setBounds(850, 535, 50, 20); // solsað, aþaðýyukarý, en, boy
		textfieldR0.setEditable(false);
		frame.getContentPane().add(textfieldR0);

		textfieldR1 = new JTextField();
		textfieldR1.setBounds(850, 570, 50, 20); // solsað, aþaðýyukarý, en, boy
		textfieldR1.setEditable(false);
		frame.getContentPane().add(textfieldR1);

		textfieldR2 = new JTextField();
		textfieldR2.setBounds(850, 605, 50, 20); // solsað, aþaðýyukarý, en, boy
		textfieldR2.setEditable(false);
		frame.getContentPane().add(textfieldR2);
		///////////////////////////////////////////////////

		textfieldStackPointer = new JTextField();
		textfieldStackPointer.setBounds(965, 605, 50, 20); // solsað, aþaðýyukarý, en, boy
		textfieldStackPointer.setEditable(false);
		frame.getContentPane().add(textfieldStackPointer);

		textfieldInsr = new JTextField();
		textfieldInsr.setBounds(965, 570, 85, 20); // solsað, aþaðýyukarý, en, boy
		textfieldInsr.setEditable(false);
		frame.getContentPane().add(textfieldInsr);

		textfieldPc = new JTextField();
		textfieldPc .setBounds(965, 535, 50, 20); // solsað, aþaðýyukarý, en, boy
		textfieldPc.setEditable(false);
		frame.getContentPane().add(textfieldPc);

		textfieldAddr = new JTextField();
		textfieldAddr.setBounds(965, 500, 50, 20); // solsað, aþaðýyukarý, en, boy
		textfieldAddr.setEditable(false);
		frame.getContentPane().add(textfieldAddr);

		textfieldInpr = new JTextField();
		textfieldInpr.setBounds(965, 465, 50, 20); // solsað, aþaðýyukarý, en, boy
		//textfieldInpr.setEditable(false);
		frame.getContentPane().add(textfieldInpr);

		textfieldOutr = new JTextField();
		textfieldOutr.setBounds(965, 430, 50, 20); // solsað, aþaðýyukarý, en, boy
		textfieldOutr.setEditable(false);
		frame.getContentPane().add(textfieldOutr);
		///////////////////////////////////////////

		textfieldT0 = new JTextField();
		textfieldT0.setBounds(427, 430, 80, 20); // solsað, aþaðýyukarý, en, boy
		textfieldT0.setEditable(false);
		frame.getContentPane().add(textfieldT0);

		textfieldT1 = new JTextField();
		textfieldT1.setBounds(427, 465, 80, 20); // solsað, aþaðýyukarý, en, boy
		textfieldT1.setEditable(false);
		frame.getContentPane().add(textfieldT1);

		textfieldT2 = new JTextField();
		textfieldT2 .setBounds(427, 500, 80, 20); // solsað, aþaðýyukarý, en, boy
		textfieldT2.setEditable(false);
		frame.getContentPane().add(textfieldT2);

		textfieldT3 = new JTextField();
		textfieldT3.setBounds(427, 535, 80, 20); // solsað, aþaðýyukarý, en, boy
		textfieldT3.setEditable(false);
		frame.getContentPane().add(textfieldT3);

		textfieldT4 = new JTextField();
		textfieldT4.setBounds(427, 570, 80, 20); // solsað, aþaðýyukarý, en, boy
		textfieldT4.setEditable(false);
		frame.getContentPane().add(textfieldT4);

		textfieldT5 = new JTextField();
		textfieldT5.setBounds(427, 605, 80, 20); // solsað, aþaðýyukarý, en, boy
		textfieldT5.setEditable(false);
		frame.getContentPane().add(textfieldT5);
		///////////////////////////////////////////////////////////////
		labelT0.setVisible(false);
		labelT1.setVisible(false);
		labelT2.setVisible(false);
		labelT3.setVisible(false);
		labelT4.setVisible(false);
		labelT5.setVisible(false);

		textfieldT0.setVisible(false);
		textfieldT1.setVisible(false);
		textfieldT2.setVisible(false);
		textfieldT3.setVisible(false);
		textfieldT4.setVisible(false);
		textfieldT5.setVisible(false);
		//////////////////////////////

		JRadioButton radioBinary = new JRadioButton("Binary", true);
		radioBinary.setBounds(441, 9, 81, 25);

		JRadioButton radioDecimal = new JRadioButton("Decimal");
		radioDecimal.setBounds(520, 9, 81, 25);

		JRadioButton radioHexa = new JRadioButton("HexaDecimal");
		radioHexa.setBounds(610, 9, 105, 25);



		/*
		radioBinary.setMnemonic(KeyEvent.VK_C);
		radioHexa.setMnemonic(KeyEvent.VK_M);
		 */


		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(radioBinary);
		group.add(radioHexa);
		group.add(radioDecimal);
		frame.getContentPane().setLayout(null);



		radioBinary.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				//				dataDtm.setValueAt("as", 1, 1);
				if (radioBinary.isSelected() == true) {
					System.out.println("BINARY' E TIKLADIN BRO.");
					if (group.getSelection().isSelected() == radioBinary.isSelected()) {

						System.out.println("radio seçili");
					}
					else {

						System.out.println("hexa seçili");
					}
				}


			}
		});
		radioDecimal.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				//				dataDtm.setValueAt("as", 1, 1);
				if (radioDecimal.isSelected() == true) {
					System.out.println("BINARY' E TIKLADIN BRO.");
					if (group.getSelection().isSelected() == radioDecimal.isSelected()) {

						System.out.println("radio seçili");
					}
					else {

						System.out.println("hexa seçili");
					}
				}


			}
		});
		radioHexa.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				//				dataDtm.setValueAt("sa", 2, 1);

				if (radioHexa.isSelected() == true) {
					System.out.println("HEX' E TIKLADIN BRO.");
					if (group.getSelection().isSelected() == radioBinary.isSelected()) {

						System.out.println("radio seçili");
					}
					else {

						System.out.println("hexa seçili");
					}
				}
			}
		});



		frame.getContentPane().add(radioBinary);
		frame.getContentPane().add(radioHexa);
		frame.getContentPane().add(radioDecimal);
		///////////////////////////////////////////////////
		instructionTable = new JTable();
		instructionTable.setEnabled(false);
		instructionTable.setBounds(20,40,60,80);

		dataTable = new JTable();
		dataTable.setEnabled(false);
		dataTable.setBounds(20,40,60,80);

		stackTable = new JTable();
		stackTable.setEnabled(false);
		stackTable.setBounds(20,40,60,80);

		labelTable = new JTable();
		labelTable.setEnabled(false);
		labelTable.setBounds(100,80,60,200);


		String[] columnNames = {"Adrs","I", "Opcode", "D", "S1", "S2"};

		dataMemory = new Memory(16,new String[] { "Adrs","Data Memory"});
		dataDtm = dataMemory.getButterfly();
		dataTable.setModel(dataDtm);

		stackMemory = new Memory(16,new String[] { "Adrs","Stack Memory" });
		stackDtm = stackMemory.getButterfly();
		stackTable.setModel(stackDtm);

		instructionMemory = new Memory(32,columnNames);

		labelMemory = new Memory(16, new String[] { "Adrs","Var","LabelTable","Memory"});
		labelDtm = labelMemory.getButterfly();
		labelTable.setModel(labelDtm);




		//		instructionDtm.setColumnIdentifiers(columnNames);
		instructionDtm = instructionMemory.getButterfly(); // 32 satýrlýk bir hafýza alaný yaratýyor.
		instructionTable.setModel(instructionDtm);


		JScrollPane scrollPaneInstructionMemoryTable = new JScrollPane(instructionTable);
		scrollPaneInstructionMemoryTable.setBounds(64, 33, 287, 242);
		frame.getContentPane().add(scrollPaneInstructionMemoryTable);



		JScrollPane scrollPaneDataTable = new JScrollPane(dataTable);
		scrollPaneDataTable.setBounds(64, 299, 199, 159);
		frame.getContentPane().add(scrollPaneDataTable);

		JScrollPane scrollPaneStackTable = new JScrollPane(stackTable);
		scrollPaneStackTable.setBounds(64, 489, 199, 159);
		frame.getContentPane().add(scrollPaneStackTable);

		JScrollPane scrollPaneLabelTable = new JScrollPane(labelTable);
		scrollPaneLabelTable.setBounds(795, 50, 279, 159);
		frame.getContentPane().add(scrollPaneLabelTable);

		InstructionTextArea = new JTextArea();
		InstructionTextArea.setEditable(false);
		InstructionTextArea.setBounds(380, 50, 352, 361);
		frame.getContentPane().add(InstructionTextArea);


		/*
		debugAndRun = new JPanel();
		debug = new JButton(new ImageIcon("debug.png"));


		debug.setForeground(Color.WHITE);
		debug.setBackground(Color.BLACK);

		frame.getContentPane().add(debug);

		debug.setLocation(795, 100);
		 */

	}

	void readFile(String FileName) { // Read file and print same.

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

}