import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.TextField;
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
import javax.swing.JCheckBox;
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

	private JPanel checkBoxPanel;
	private JCheckBox chkInput, chkOutput;

	private JFrame frame;
	private JTextArea InstructionTextArea;
	private JTable instructionTable, dataTable, stackTable, labelTable;
	private JTextField textfieldR0, textfieldR1, textfieldR2,textfieldStackPointer, 
	textfieldOutr, textfieldInpr, textfieldAddr, textfieldPc, textfieldInsr, 
	textfieldT0, textfieldT1, textfieldT2, textfieldT3 ,textfieldT4, textfieldT5, textfieldOverflow;
	private Memory instructionMemory, dataMemory, stackMemory, labelMemory;

	private JLabel labelT0, labelT1, labelT2, labelT3, labelT4, labelT5;


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

	int debugCounter, howInvisible, tableLabelCounter;

	boolean isNextLine = false, isBinary = true, isHexa = false, isDecimal = false;

	// IMPORTANT THİNGS THAT YOU MUST REMEMBER.

	// tableInstruction[][0] means - I
	// tableInstruction[][1] means - Opcode
	// tableInstruction[][2] means - D
	// tableInstruction[][3] means - S1
	// tableInstruction[][4] means - S2

	///////////////////////////////////////////


	final String ADD = "1";




	Screen () {

		debugCounter = 0;
		tableLabelCounter = 0;

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

		parse = new Parsing("deneme1.txt");
		tableDecimalInstruction = parse.getInstructionDecimal();
		tableData = parse.getDataDecimal();



		frame = Management.frame;
		addContentsToFrame();
		tableInstruction = toBinary(tableDecimalInstruction);
		
		fillLabelArrayFromData(tableData);
		fillTable();

		//Reading a file
		readFile("deneme1.txt");
		InstructionTextArea.setText(allString);
		//		parseCode("deneme1.txt");




		setPC();





	}

	private void fillTable () {


		for (int i = 0; i < tableInstruction.length; i++) {

			for (int j = 0; j < tableInstruction[0].length; j++) {

				if (tableInstruction[i][0].substring(0, 1).equalsIgnoreCase("x")) {

					if (Integer.parseInt(tableInstruction[i][0].substring(2)) > -1) {

						instructionDtm.setValueAt(tableInstruction[i][j], i, j+1);
					} else break;
				}
				else {

					if (Integer.parseInt(tableInstruction[i][0]) > -1) {

						instructionDtm.setValueAt(tableInstruction[i][j], i, j+1);
					} else break;
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
			labelDtm.setValueAt(tableLabel[i][3], i, 3);

		}//For Label Memory (La Label ne ara Memory oldu :D)
	}

	private void updateR0 () {

		if (textfieldR0.getText().length() > 0) {

			int butterfly = Integer.parseInt(textfieldR0.getText());
			if (isHexa) textfieldR0.setText(Integer.toHexString(butterfly));
			else if (isDecimal) textfieldR0.setText(String.valueOf(butterfly));
			else if (isBinary) textfieldR0.setText(Integer.toBinaryString(butterfly));
		}
	}

	private void updateR1 () {

		if (textfieldR1.getText().length() > 0) {

			int butterfly = Integer.parseInt(textfieldR1.getText());
			if (isHexa) textfieldR1.setText(Integer.toHexString(butterfly));
			else if (isDecimal) textfieldR1.setText(String.valueOf(butterfly));
			else if (isBinary) textfieldR1.setText(Integer.toBinaryString(butterfly));
		}
	}

	private void updateR2 () {

		if (textfieldR2.getText().length() > 0) {

			int butterfly = Integer.parseInt(textfieldR2.getText());
			textfieldR2.setText(" ");
			if (isHexa) textfieldR2.setText(Integer.toHexString(butterfly));
			else if (isDecimal) textfieldR2.setText(String.valueOf(butterfly));
			else if (isBinary) textfieldR2.setText(Integer.toBinaryString(butterfly));
		}
	}

	private String[][] toHex (int[][] arr) {

		String[][] butterfly = new String[32][5];

		for (int i = 0; i < arr.length; i++) {

			for (int j = 0; j < arr[1].length; j++) {

				if (arr[i][j] > -1) {

					butterfly[i][j] = "xx" + Integer.toHexString(arr[i][j]).toUpperCase(); // 'xx' mean is that the value is Hexadecimal.
				}
				else {

					butterfly[i][j] = String.valueOf(arr[i][j]);
				}
			}
		}

		return butterfly;
	}

	private String[][] toBinary (int[][] arr) {

		String[][] butterfly = new String[32][5];

		for (int i = 0; i < arr.length; i++) {

			for (int j = 0; j < arr[1].length; j++) {

				if (arr[i][j] > -1) { 

					if (j == 3 && arr[i][4] > -1) {

						if (arr[i][4] < 2) {

							butterfly[i][3] = "00";
							butterfly[i][4] = "0";
							butterfly[i][4] += Integer.toBinaryString(arr[i][4]);
						}
						else if (arr[i][4] < 4) { // 2 <= x < 4

							butterfly[i][3] = "00";
							System.out.println("i:" + i + " - " + Integer.toBinaryString(arr[i][4]));
							butterfly[i][4] = Integer.toBinaryString(arr[i][4]);
						}
						else if (arr[i][4] < 8) { // 4 <= x < 8

							String butterMıFly = Integer.toBinaryString(arr[i][4]);
							System.out.println("i:" + i + " - " + Integer.toBinaryString(arr[i][4]));
							butterfly[i][3] = "0" + butterMıFly.substring(0,1);
							butterfly[i][4] = butterMıFly.substring(1);
						}
						else if (arr[i][4] < 16) { // 8 <= x < 16

							String butterMıFly = Integer.toBinaryString(arr[i][4]);
							butterfly[i][3] = butterMıFly.substring(0,2);
							butterfly[i][4] = butterMıFly.substring(2);
						}
						j++;
					}
					else if (j == 0) {

						butterfly[i][j] = Integer.toBinaryString(arr[i][j]);
					}
					else if (j == 2) {

						if (arr[i][j] < 2) butterfly[i][j] = "0" + Integer.toBinaryString(arr[i][j]);
						else if (arr[i][j] < 4) butterfly[i][j] = Integer.toBinaryString(arr[i][j]);
					}
					else {

						if (arr[i][j] < 2) butterfly[i][j] = "000";
						else if (arr[i][j] < 4) butterfly[i][j] = "00";
						else if (arr[i][j] < 8) butterfly[i][j] = "0";
						else butterfly[i][j] = "";
						butterfly[i][j] += Integer.toBinaryString(arr[i][j]);
					}

				}
				else { // Sadece tek bir yerde arr[i][j] < -1 Oda OpCode kısmında. [][1]

					butterfly[i][j] = String.valueOf(arr[i][j]);
				}
			}
		}

		return butterfly;
	}

	private String[][] toDecimal (int[][] arr) {

		String[][] butterfly = new String[32][5];

		for (int i = 0; i < arr.length; i++) {

			for (int j = 0; j < arr[1].length; j++) {

				butterfly[i][j] = String.valueOf(arr[i][j]);
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
		labelT0 = new JLabel("T0 : ");
		labelT0.setBounds(400, 430, 50, 20);
		frame.getContentPane().add(labelT0);

		labelT1 = new JLabel("T1 : ");
		labelT1.setBounds(400, 465, 50, 20);
		frame.getContentPane().add(labelT1);

		labelT2 = new JLabel("T2 : ");
		labelT2.setBounds(400, 500, 50, 20);
		frame.getContentPane().add(labelT2);

		labelT3 = new JLabel("T3 : ");
		labelT3.setBounds(400, 535, 50, 20);
		frame.getContentPane().add(labelT3);

		labelT4 = new JLabel("T4 : ");
		labelT4.setBounds(400, 570, 50, 20);
		frame.getContentPane().add(labelT4);

		labelT5 = new JLabel("T5 : ");
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
		textfieldT0.setBounds(427, 430, 130, 20); // solsað, aþaðýyukarý, en, boy
		textfieldT0.setEditable(false);
		frame.getContentPane().add(textfieldT0);

		textfieldT1 = new JTextField();
		textfieldT1.setBounds(427, 465, 80, 20); // solsað, aþaðýyukarý, en, boy
		textfieldT1.setEditable(false);
		frame.getContentPane().add(textfieldT1);

		textfieldT2 = new JTextField();
		textfieldT2 .setBounds(427, 500, 230, 20); // solsað, aþaðýyukarý, en, boy
		textfieldT2.setEditable(false);
		frame.getContentPane().add(textfieldT2);

		textfieldT3 = new JTextField();
		textfieldT3.setBounds(427, 535, 120, 20); // solsað, aþaðýyukarý, en, boy
		textfieldT3.setEditable(false);
		frame.getContentPane().add(textfieldT3);

		textfieldT4 = new JTextField();
		textfieldT4.setBounds(427, 570, 120, 20); // solsað, aþaðýyukarý, en, boy
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

						isBinary = true;
						isHexa = false;
						isDecimal = false;

						tableInstruction = toBinary(tableDecimalInstruction);
						fillTable();
					}
				}
			}
		});
		radioDecimal.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				//				dataDtm.setValueAt("as", 1, 1);
				if (radioDecimal.isSelected() == true) {

					if (group.getSelection().isSelected() == radioDecimal.isSelected()) {

						isBinary = false;
						isHexa = false;
						isDecimal = true;

						tableInstruction = toDecimal(tableDecimalInstruction);
						fillTable();
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
					if (group.getSelection().isSelected() == radioHexa.isSelected()) {

						isBinary = false;
						isHexa = true;
						isDecimal = false;

						tableInstruction = toHex(tableDecimalInstruction);
						fillTable();
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
		scrollPaneInstructionMemoryTable.setBounds(64, 41, 287, 242);
		frame.getContentPane().add(scrollPaneInstructionMemoryTable);

		JScrollPane scrollPaneDataTable = new JScrollPane(dataTable);
		scrollPaneDataTable.setBounds(64, 308, 199, 159);
		frame.getContentPane().add(scrollPaneDataTable);

		JScrollPane scrollPaneStackTable = new JScrollPane(stackTable);
		scrollPaneStackTable.setBounds(64, 489, 199, 159);
		frame.getContentPane().add(scrollPaneStackTable);

		JScrollPane scrollPaneLabelTable = new JScrollPane(labelTable);
		scrollPaneLabelTable.setBounds(795, 41, 279, 159);
		frame.getContentPane().add(scrollPaneLabelTable);

		InstructionTextArea = new JTextArea();
		InstructionTextArea.setEditable(false);
		InstructionTextArea.setBounds(395, 40, 352, 361);
		frame.getContentPane().add(InstructionTextArea);




		debug = new JButton(new ImageIcon("debug.png"));
		run = new JButton(new ImageIcon("run.png"));

		debug.setBounds(840, 250, 30, 30);
		run.setBounds(800, 250, 30, 30);

		/*

		debugAndRun.setBounds(800, 300, 0, 40);

		debugAndRun.add(debug);
		debugAndRun.add(run);
		 */


		debug.setBackground(Color.GRAY);
		run.setBackground(Color.GRAY);


		frame.getContentPane().add(debug);
		frame.getContentPane().add(run);

		//RUN and DEBUG ClickListener
		run.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				/*
				JDialog d = new JDialog(frame, "Hello", true);
			    d.setLocationRelativeTo(frame);
			    d.setVisible(true);
				 */
			}
		});
		debug.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				/*
				JDialog d = new JDialog(frame, "Hello", true);
			    d.setLocationRelativeTo(frame);
			    d.setVisible(true);
				 */

				int i = instructionMemory.getCounter() - 1; // Current ProgramCounter
				int selection = 0;
				if (i > -1) selection = tableDecimalInstruction[ i ][1];
				
				if (isNextLine) {
					debugCounter = 0;
					isNextLine = false;
					textBeInvisible(howInvisible);
					textfieldAddr.setText("");
					textfieldInsr.setText("");
				}


				switch (debugCounter) {
				case 0: // T0

					textfieldInsr.setText(T0(tableDecimalInstruction));
					textfieldT0.setText(" IR <- IM[PC], AR <- PC");
					textfieldT0.setVisible(true);
					labelT0.setVisible(true);
					textfieldAddr.setText(String.valueOf(instructionMemory.getCounter()));

					break;
				case 1: // T1

					textfieldT1.setText(" PC <- PC + 1");
					textfieldT1.setVisible(true);
					labelT1.setVisible(true);
					T1();
					break;
				case 2: // T2

					textfieldT2.setText(" D <- IR[5..6], S1 <- IR[7..8], S2 <- IR[9..10]");
					textfieldT2.setVisible(true);
					labelT2.setVisible(true);
					break;
				case 3: // T3

					T3(tableDecimalInstruction, i);
					break;
				case 4: // T4

					T4(selection); // Last selection after change the Adress Register.
					break;
				case 5:


					break;
				default:
					break;
				}

				textfieldPc.setText(String.valueOf(instructionMemory.getCounter()));
				debugCounter++;

			}
		});

		showCheckBoxDemo();
		
		JLabel labelOverlow = new JLabel (" - OverFlow - ");
		labelOverlow.setBounds(920, 240, 80, 20);
		frame.getContentPane().add(labelOverlow);
		
		textfieldOverflow = new JTextField();
		textfieldOverflow.setBounds(941, 265, 30, 20);
		textfieldOverflow.setEditable(false);
		frame.getContentPane().add(textfieldOverflow);

	}

	public void setPC () {

		for (int i = 0; i < tableDecimalInstruction.length; i++) {

			if (tableDecimalInstruction[i][1] == -1) {

				System.out.println(tableDecimalInstruction[i][3]);
				instructionMemory.setCounter(tableDecimalInstruction[i][3]);
			}
		}
	}

	public String T0 (int arr[][]) {

		String opcode = "";
		///
		for (int j = 0; j < arr[instructionMemory.getCounter()].length; j++) {

			opcode += tableInstruction[instructionMemory.getCounter()][j];
		}
		return opcode;
	}

	public void T1 () {

		instructionMemory.increaseCounter();
	}

	public void T3 (int[][] arr, int i) {

		switch ( arr[ i ][1] ) {

		case 0: // ADD
			
			int R0Value = 0, R1Value = 0, R2Value = 0;
			int whichCase = -1;
			int sum = 0;
			
			textfieldT3.setText(" D <- S1+S2, SC <- 0");
			textfieldT3.setVisible(true);
			labelT3.setVisible(true);
			isNextLine = true;
			howInvisible = 3;

			int case0 = Integer.parseInt(textfieldAddr.getText());
			
			
			
			switch (tableDecimalInstruction[ case0 ][3]) {
			
			case 0:
				
				whichCase = 0;
				break;
			case 1:
				
				whichCase = 1;
				
				
				break;
			case 2:
				
				whichCase = 2;
				
				break;

			default:
				System.out.println("Değeri olmayan bir register seçtiniz");
				break;
			}
			
			
			R0Value = getInteger(textfieldR0.getText());// it will return integer according to clicked button. (hexa, binary, decimal)
			R1Value = getInteger(textfieldR1.getText());// it will return integer according to clicked button. (hexa, binary, decimal)
			R2Value = getInteger(textfieldR2.getText()); // it will return integer according to clicked button. (hexa, binary, decimal)
			
			
			if (R0Value == -1 || R1Value == -1 || R2Value == -2) {
				
				
				break;
			}
			
			
			System.out.println(textfieldR1.getText());
			System.out.println("0 : " + R0Value + " 1 : " + R1Value + " 2 : " + R2Value);
			
			
			
			
			if (tableDecimalInstruction[ case0 ][2] == 0) {
				
			// ALLAHSEN FONKSİYONA AL.
				
				switch (tableDecimalInstruction[ case0 ][4]) {
				
				case 0:
					textfieldR0.setText(" ");
					if (whichCase == 0) textfieldR0.setText(String.valueOf(R0Value + R0Value));
					else if (whichCase == 1) textfieldR0.setText(String.valueOf(R1Value + R0Value));
					else if (whichCase == 2) textfieldR0.setText(String.valueOf(R2Value + R0Value));
					updateR0();
					break;
				case 1:
					textfieldR0.setText(" ");
					if (whichCase == 0) textfieldR0.setText(String.valueOf(R0Value + R1Value));
					else if (whichCase == 1) textfieldR0.setText(String.valueOf(R1Value + R1Value));
					else if (whichCase == 2) textfieldR0.setText(String.valueOf(R2Value + R1Value));
					updateR0();
					break;
				case 2:
					textfieldR0.setText(" ");
					if (whichCase == 0) textfieldR0.setText(String.valueOf(R0Value + R2Value));
					else if (whichCase == 1) textfieldR0.setText(String.valueOf(R1Value + R2Value));
					else if (whichCase == 2) textfieldR0.setText(String.valueOf(R2Value + R2Value));
					updateR0();
					break;

				default:
					System.out.println("Add işleminde yanlış Register galiba");
					break;
				}
				
			} else if (tableDecimalInstruction[ case0 ][2] == 1) {
				
				
				switch (tableDecimalInstruction[ case0 ][4]) {
				case 0:
					
					textfieldR1.setText(" ");
					if (whichCase == 0) textfieldR1.setText(String.valueOf(R0Value + R0Value));
					else if (whichCase == 1) textfieldR1.setText(String.valueOf(R1Value + R0Value));
					else if (whichCase == 2) textfieldR1.setText(String.valueOf(R2Value + R0Value));
					updateR1();
					break;
				case 1:
					textfieldR1.setText(" ");
					if (whichCase == 0) textfieldR1.setText(String.valueOf(R0Value + R1Value));
					else if (whichCase == 1) textfieldR1.setText(String.valueOf(R1Value + R1Value));
					else if (whichCase == 2) textfieldR1.setText(String.valueOf(R2Value + R1Value));
					updateR1();
					break;
				case 2:
					textfieldR1.setText(" ");
					if (whichCase == 0) textfieldR1.setText(String.valueOf(R0Value + R2Value));
					else if (whichCase == 1) textfieldR1.setText(String.valueOf(R1Value + R2Value));
					else if (whichCase == 2) textfieldR1.setText(String.valueOf(R2Value + R2Value));
					updateR1();
					break;

				default:
					System.out.println("Add işleminde yanlış Register galiba");
					break;
				}
				
				
				
			} else if (tableDecimalInstruction[ case0 ][2] == 2) {
				
				switch (tableDecimalInstruction[ case0 ][4]) {
				case 0:
					textfieldR2.setText(" ");
					if (whichCase == 0) textfieldR2.setText(String.valueOf(R0Value + R0Value));
					else if (whichCase == 1) textfieldR2.setText(String.valueOf(R1Value + R0Value));
					else if (whichCase == 2) textfieldR2.setText(String.valueOf(R2Value + R0Value));
					updateR2();
					break;
				case 1:
					textfieldR2.setText(" ");
					if (whichCase == 0) textfieldR2.setText(String.valueOf(R0Value + R1Value));
					else if (whichCase == 1) textfieldR2.setText(String.valueOf(R1Value + R1Value));
					else if (whichCase == 2) textfieldR2.setText(String.valueOf(R2Value + R1Value));
					updateR2();
					break;
				case 2:
					textfieldR2.setText(" ");
					if (whichCase == 0) textfieldR2.setText(String.valueOf(R0Value + R2Value));
					else if (whichCase == 1) textfieldR2.setText(String.valueOf(R1Value + R2Value));
					else if (whichCase == 2) textfieldR2.setText(String.valueOf(R2Value + R2Value));
					updateR2();
					break;

				default:
					System.out.println("Add işleminde yanlış Register galiba");
					break;
				}
			}
			
			


			break;
		case 1: // INC

			isNextLine = true;
			break;
		case 2: // DBL

			isNextLine = true;
			break;
		case 3: // DBT

			isNextLine = true;
			break;
		case 4: // NOT

			System.out.println("NOT GELDIN");
			isNextLine = true;
			break;
		case 5: // AND

			isNextLine = true;
			break;
		case 6: // LD

			if (arr[i][0] == 0) { // Q == 0 ( @uploadInteger ) I need to find the value of integer.

				textfieldT3.setText(" AR <- S1S2");
				textfieldT3.setVisible(true);
				labelT3.setVisible(true);

				int butterfly = Integer.parseInt(textfieldAddr.getText());
				textfieldAddr.setText(tableInstruction[ butterfly ][3] + tableInstruction[ butterfly ][4]);
			}
			else { // Q == 1 ( #directInteger ) Integer value equals S1S2.

				textfieldT3.setText("D <- S1S2, SC <- 0");
				textfieldT3.setVisible(true);
				labelT3.setVisible(true);

				int butterfly = Integer.parseInt(textfieldAddr.getText());
				textfieldAddr.setText(tableInstruction[ butterfly ][3] + tableInstruction[ butterfly ][3]);

				if (tableInstruction[ butterfly ][2].equalsIgnoreCase("00")) {

					textfieldR0.setText("");
					textfieldR0.setText(String.valueOf(tableDecimalInstruction[ butterfly][4]));
					updateR0();
				}

				else if (tableInstruction[ butterfly ][2].equalsIgnoreCase("01")) { 

					textfieldR1.setText("");
					textfieldR1.setText(String.valueOf(tableDecimalInstruction[ butterfly][4]));
					updateR1();
				}

				else if (tableInstruction[ butterfly ][2].equalsIgnoreCase("02")) {

					textfieldR2.setText("");
					textfieldR2.setText(String.valueOf(tableDecimalInstruction[ butterfly][4]));
					updateR2();
				}
			}
			break;
		case 7: // ST
			
			
			if (arr[i][0] == 0) { // Q == 0 ( @uploadInteger ) I need to find the value of integer.

				textfieldT3.setText(" AR <- S1S2");
				textfieldT3.setVisible(true);
				labelT3.setVisible(true);
				
				int butterfly = Integer.parseInt(textfieldAddr.getText());
				textfieldAddr.setText(tableInstruction[ butterfly ][3] + tableInstruction[ butterfly ][4]);
				
			}
			else { // Q == 1 ( #directInteger ) Integer value equals S1S2.


				// NE YAPMAM GEREKTİĞİNİ ANLAMADIM, YA DA ARTIK UYUMAM LAZIM
				
				textfieldT3.setText(" S2 <- D, SC <- 0");
				textfieldT3.setVisible(true);
				labelT3.setVisible(true);
				
				
//				int butterfly = Integer.parseInt(textfieldPc.getText()) - 1;
//				if (tableInstruction[ butterfly ][2].equalsIgnoreCase("00")) {
//
//					textfieldR0.setText("");
//					textfieldR0.setText(String.valueOf(tableDecimalInstruction[ butterfly][4]));
//					updateR0();
//				}
//
//				else if (tableInstruction[ butterfly ][2].equalsIgnoreCase("01")) { 
//
//					textfieldR1.setText("");
//					textfieldR1.setText(String.valueOf(tableDecimalInstruction[ butterfly][4]));
//					updateR1();
//				}
//
//				else if (tableInstruction[ butterfly ][2].equalsIgnoreCase("02")) {
//
//					textfieldR2.setText("");
//					textfieldR2.setText(String.valueOf(tableDecimalInstruction[ butterfly][4]));
//					updateR2();
//				}
			}
			
			isNextLine = true;
			
			break;
		case 8: // HLT

			isNextLine = true;
			break;
		case 9: // TSF

			isNextLine = true;
			break;
		case 10: // CAL

			isNextLine = true;
			break;
		case 11: // RET

			isNextLine = true;
			break;
		case 12: // JMP

			isNextLine = true;
			break;
		case 13: // JMR

			isNextLine = true;
			break;
		case 14: // PSH

			isNextLine = true;
			break;
		case 15: // POP

			isNextLine = true;
			break;
		default:
			break;
		}
	}

	public void T4 (int i) {



		switch ( i ) {

		case 6: // LD

			textfieldT4.setText(" D <- DM[AR], SC <- 0");
			textfieldT4.setVisible(true);
			labelT4.setVisible(true);

			int butterfly = Integer.parseInt(textfieldPc.getText()) - 1;

			if (tableInstruction[ butterfly ][2].equalsIgnoreCase("00")) {

				textfieldR0.setText("");
				textfieldR0.setText(String.valueOf(tableDecimalInstruction[ butterfly][4]));
				updateR0();
			}

			else if (tableInstruction[ butterfly ][2].equalsIgnoreCase("01")) { 

				textfieldR1.setText("");
				textfieldR1.setText(String.valueOf(tableDecimalInstruction[ butterfly][4]));
				updateR1();
			}

			else if (tableInstruction[ butterfly ][2].equalsIgnoreCase("02")) {

				textfieldR2.setText("");
				textfieldR2.setText(String.valueOf(tableDecimalInstruction[ butterfly][4]));
				updateR2();
			}

			isNextLine = true;
			howInvisible = 4;

			break;
		case 7: // ST

			break;
		case 8: // HLT

			break;
		case 9: // TSF

			break;
		case 10: // CAL

			break;
		case 11: // RET

			break;
		case 12: // JMP

			break;
		case 13: // JMR

			break;
		case 14: // PSH

			break;
		case 15: // POP

			break;
		default:
			break;
		}
	}
	
	public void fillLabelArrayFromData (String[][] str) {
		
		for (int i = 0; i < str.length; i++) {
			
			if (str[i][1] != null) {
				
				tableLabel[tableLabelCounter][1] = str[i][0];
				tableLabel[tableLabelCounter][2] = str[i][1];
				tableLabel[tableLabelCounter][3] = "DM";
				tableLabelCounter++;
			}	
		}
	}
	
	private void textBeInvisible(int i) { // i equals the last operation

		if (i == 3) {

			textfieldT0.setVisible(false);
			textfieldT1.setVisible(false);
			textfieldT2.setVisible(false);
			textfieldT3.setVisible(false);

			labelT0.setVisible(false);
			labelT1.setVisible(false);
			labelT2.setVisible(false);
			labelT3.setVisible(false);
		} else if (i == 4){

			textfieldT0.setVisible(false);
			textfieldT1.setVisible(false);
			textfieldT2.setVisible(false);
			textfieldT3.setVisible(false);
			textfieldT4.setVisible(false);

			labelT0.setVisible(false);
			labelT1.setVisible(false);
			labelT2.setVisible(false);
			labelT3.setVisible(false);
			labelT4.setVisible(false);
		} else if (i == 5){

			textfieldT0.setVisible(false);
			textfieldT1.setVisible(false);
			textfieldT2.setVisible(false);
			textfieldT3.setVisible(false);
			textfieldT4.setVisible(false);
			textfieldT5.setVisible(false);

			labelT0.setVisible(false);
			labelT1.setVisible(false);
			labelT2.setVisible(false);
			labelT3.setVisible(false);
			labelT4.setVisible(false);
			labelT5.setVisible(false);
		}
	}
	
	public int getInteger (String s) {
		
		int butterfly = -1;
		
		if (!s.equalsIgnoreCase("")) {
			
			if (isHexa) butterfly = Integer.parseInt(s, 16);
			else if (isBinary) butterfly = Integer.parseInt(s, 2);
			else if (isDecimal) butterfly = Integer.parseInt(s);
		}
		return butterfly;
	}

	private void showCheckBoxDemo(){

		checkBoxPanel = new JPanel();
		chkInput = new JCheckBox("Input Flag");
		chkOutput = new JCheckBox("Output Flag");

		//chkInput.setSelected(true);
		chkInput.setBounds(800, 320, 90, 20);
		chkOutput.setBounds(800, 350, 100, 20);

		chkInput.setEnabled(false);
		chkOutput.setEnabled(false);

		chkInput.setSelected(true);

		frame.getContentPane().add(chkInput);
		frame.getContentPane().add(chkOutput);
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