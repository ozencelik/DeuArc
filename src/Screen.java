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
import java.io.FileWriter;
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

	private final String MIF_FILE_NAME = "InstructionMemory.mif";
	private File mifFile;
	private FileWriter fileWriter;

	private JPanel checkBoxPanel;
	private JCheckBox chkInput, chkOutput;

	private JFrame frame;
	private JTextArea InstructionTextArea;
	private JTable instructionTable, dataTable, stackTable, labelTable;
	private JTextField textfieldR0, textfieldR1, textfieldR2, textfieldStackPointer, textfieldOutr, textfieldInpr,
	textfieldAddr, textfieldPc, textfieldInsr, textfieldT0, textfieldT1, textfieldT2, textfieldT3, textfieldT4,
	textfieldT5, textfieldOverflow;
	private Memory instructionMemory, dataMemory, stackMemory, labelMemory;

	private JLabel labelT0, labelT1, labelT2, labelT3, labelT4, labelT5;

	private DefaultTableModel stackDtm = new DefaultTableModel();
	private DefaultTableModel dataDtm = new DefaultTableModel();
	private DefaultTableModel instructionDtm = new DefaultTableModel();
	private DefaultTableModel labelDtm = new DefaultTableModel();

	private JPanel debugAndRun;
	private JButton debug, run;

	private String allString;// For readFile function

	private int[][] tableDecimalInstruction;
	private String[][] tableInstruction;

	private String[][] tableData;

	private int[][] tableDecimalStack;
	private String[][] tableStack;
	private int counterTableStack = 0;

	private int[][] tableDecimalLabel;
	private String[][] tableLabel;

	Parsing parse;

	int debugCounter, howInvisible, tableLabelCounter;

	boolean isNextLine = false, isBinary = true, isHexa = false, isDecimal = false, isHLT = false;

	// IMPORTANT THİNGS THAT YOU MUST REMEMBER.

	// tableInstruction[][0] means - I
	// tableInstruction[][1] means - Opcode
	// tableInstruction[][2] means - D
	// tableInstruction[][3] means - S1
	// tableInstruction[][4] means - S2

	///////////////////////////////////////////

	final String ADD = "1";

	Screen() {


		//	System.out.println(fillZeros("11010", 11));
		debugCounter = 0;
		tableLabelCounter = 0;

		tableInstruction = new String[32][5];
		tableDecimalInstruction = new int[32][5];
		// table[2][1] = "ahmet";

		tableData = new String[16][3];
		// tableData[2][2] = "cabbar";

		tableStack = new String[16][2];
		tableDecimalStack = new int[16][2];
		// tableStack[2][1] = "val";

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

		// Reading a file
		readFile("deneme1.txt");
		InstructionTextArea.setText(allString);
		// parseCode("deneme1.txt");

		try {
			createMif();
		} catch (IOException e) {
			System.out.println("Ahh mif dosyası ahhh...");
			e.printStackTrace();
		}

		setPC();
	}

	private void fillTable() {

		for (int i = 0; i < tableInstruction.length; i++) {

			for (int j = 0; j < tableInstruction[0].length; j++) {

				if (tableInstruction[i][0].substring(0, 1).equalsIgnoreCase("x")) {

					if (Integer.parseInt(tableInstruction[i][0].substring(2)) > -1) {

						instructionDtm.setValueAt(tableInstruction[i][j], i, j + 1);
					} else
						break;
				} else {
					if (Integer.parseInt(tableInstruction[i][0]) > -1) {
						instructionDtm.setValueAt(tableInstruction[i][j], i, j+1);
					} else if (Integer.parseInt(tableInstruction[i][0]) == -5) {


						if (j == 0) instructionDtm.setValueAt(0, i, j+1);
						else instructionDtm.setValueAt(tableInstruction[i][j], i, j+1);

					} else break;
				}
			}
		} // For Instruction Memory

		///////////////////////////////////////////////////////
		for (int i = 0; i < tableData.length; i++) {

			dataDtm.setValueAt(tableData[i][1], i, 1);

		} // For Data Memory

		///////////////////////////////////////////////////////

		for (int i = 0; i < tableStack.length; i++) {

			stackDtm.setValueAt(tableStack[i][1], i, 1);

		} // For Stack Memory

		/////////////////////////////////////////////////////
		for (int i = 0; i < tableLabel.length; i++) {

			labelDtm.setValueAt(tableLabel[i][1], i, 1);
			labelDtm.setValueAt(tableLabel[i][2], i, 2);
			labelDtm.setValueAt(tableLabel[i][3], i, 3);

		} // For Label Memory (La Label ne ara Memory oldu :D)
	}

	private void updateR0() {

		if (textfieldR0.getText().substring(0,1).equalsIgnoreCase("E")) { // E means I have to multiply with minus 1.
			// When I directly multiply with minus 1, the function that convert to binary is add one 28 times, because integer can be 32 bit.

			int butterfly = Integer.parseInt(textfieldR0.getText().substring(1));
			textfieldR0.setText(" ");
			if (isHexa) {


				textfieldR0.setText(Integer.toHexString(butterfly));
			}
			else if (isDecimal) {

				textfieldR0.setText(String.valueOf(butterfly));
			}
			else if (isBinary) {

				butterfly = 15 - butterfly;
				if (butterfly < 2) textfieldR0.setText("111" + Integer.toBinaryString(butterfly));
				else if (butterfly < 4) textfieldR0.setText("11" + Integer.toBinaryString(butterfly));
				else if (butterfly < 8) textfieldR0.setText("1" + Integer.toBinaryString(butterfly));
				else if (butterfly < 16) textfieldR0.setText(Integer.toBinaryString(butterfly));
			}

		} else {

			int butterfly = Integer.parseInt(textfieldR0.getText());

			textfieldR0.setText(" ");
			if (isHexa) {

				textfieldR0.setText(Integer.toHexString(butterfly));
			}
			else if (isDecimal) {

				textfieldR0.setText(String.valueOf(butterfly));
			}
			else if (isBinary) {

				if (butterfly < 2) textfieldR0.setText("000" + Integer.toBinaryString(butterfly));
				else if (butterfly < 4) textfieldR0.setText("00" + Integer.toBinaryString(butterfly));
				else if (butterfly < 8) textfieldR0.setText("0" + Integer.toBinaryString(butterfly));
				else if (butterfly < 16) textfieldR0.setText(Integer.toBinaryString(butterfly));
			}
		}
	}

	private void updateR1() {

		if (textfieldR1.getText().substring(0,1).equalsIgnoreCase("E")) { // E means I have to multiply with minus 1.
			// When I directly multiply with minus 1, the function that convert to binary is add one 28 times, because integer can be 32 bit.

			int butterfly = Integer.parseInt(textfieldR1.getText().substring(1));
			textfieldR1.setText(" ");
			if (isHexa) {


				textfieldR1.setText(Integer.toHexString(butterfly));
			}
			else if (isDecimal) {

				textfieldR1.setText(String.valueOf(butterfly));
			}
			else if (isBinary) {

				butterfly = 15 - butterfly;
				if (butterfly < 2) textfieldR1.setText("111" + Integer.toBinaryString(butterfly));
				else if (butterfly < 4) textfieldR1.setText("11" + Integer.toBinaryString(butterfly));
				else if (butterfly < 8) textfieldR1.setText("1" + Integer.toBinaryString(butterfly));
				else if (butterfly < 16) textfieldR1.setText(Integer.toBinaryString(butterfly));
			}

		} else {

			int butterfly = Integer.parseInt(textfieldR1.getText());

			textfieldR1.setText(" ");
			if (isHexa) {

				textfieldR1.setText(Integer.toHexString(butterfly));
			}
			else if (isDecimal) {

				textfieldR1.setText(String.valueOf(butterfly));
			}
			else if (isBinary) {

				if (butterfly < 2) textfieldR1.setText("000" + Integer.toBinaryString(butterfly));
				else if (butterfly < 4) textfieldR1.setText("00" + Integer.toBinaryString(butterfly));
				else if (butterfly < 8) textfieldR1.setText("0" + Integer.toBinaryString(butterfly));
				else if (butterfly < 16) textfieldR1.setText(Integer.toBinaryString(butterfly));
			}
		}
	}

	private void updateR2() {

		if (textfieldR2.getText().substring(0,1).equalsIgnoreCase("E")) { // E means I have to multiply with minus 1.
			// When I directly multiply with minus 1, the function that convert to binary is add one 28 times, because integer can be 32 bit.

			int butterfly = Integer.parseInt(textfieldR2.getText().substring(1));
			textfieldR2.setText(" ");
			if (isHexa) {


				textfieldR2.setText(Integer.toHexString(butterfly));
			}
			else if (isDecimal) {

				textfieldR2.setText(String.valueOf(butterfly));
			}
			else if (isBinary) {

				butterfly = 15 - butterfly;
				if (butterfly < 2) textfieldR2.setText("111" + Integer.toBinaryString(butterfly));
				else if (butterfly < 4) textfieldR2.setText("11" + Integer.toBinaryString(butterfly));
				else if (butterfly < 8) textfieldR2.setText("1" + Integer.toBinaryString(butterfly));
				else if (butterfly < 16) textfieldR2.setText(Integer.toBinaryString(butterfly));
			}

		} else {

			int butterfly = Integer.parseInt(textfieldR2.getText());
			textfieldR2.setText(" ");
			if (isHexa) {

				textfieldR2.setText(Integer.toHexString(butterfly));
			}
			else if (isDecimal) {

				textfieldR2.setText(String.valueOf(butterfly));
			}
			else if (isBinary) {

				textfieldR2.setText(fillZeros(Integer.toBinaryString(butterfly), 4));

				/*
				if (butterfly < 2) textfieldR2.setText("000" + Integer.toBinaryString(butterfly));
				else if (butterfly < 4) textfieldR2.setText("00" + Integer.toBinaryString(butterfly));
				else if (butterfly < 8) textfieldR2.setText("0" + Integer.toBinaryString(butterfly));
				else if (butterfly < 16) textfieldR2.setText(Integer.toBinaryString(butterfly));*/
			}
		}
	}

	private void updateOutr() {
	
		if (textfieldOutr.getText().substring(0,1).equalsIgnoreCase("E")) { // E means I have to multiply with minus 1.
			// When I directly multiply with minus 1, the function that convert to binary is add one 28 times, because integer can be 32 bit.

			int butterfly = Integer.parseInt(textfieldOutr.getText().substring(1));
			textfieldOutr.setText(" ");
			if (isHexa) {


				textfieldOutr.setText(Integer.toHexString(butterfly));
			}
			else if (isDecimal) {

				textfieldOutr.setText(String.valueOf(butterfly));
			}
			else if (isBinary) {

				butterfly = 15 - butterfly;
				if (butterfly < 2) textfieldOutr.setText("111" + Integer.toBinaryString(butterfly));
				else if (butterfly < 4) textfieldOutr.setText("11" + Integer.toBinaryString(butterfly));
				else if (butterfly < 8) textfieldOutr.setText("1" + Integer.toBinaryString(butterfly));
				else if (butterfly < 16) textfieldOutr.setText(Integer.toBinaryString(butterfly));
			}

		} else {

			int butterfly = Integer.parseInt(textfieldOutr.getText());
			textfieldOutr.setText(" ");
			if (isHexa) {

				textfieldOutr.setText(Integer.toHexString(butterfly));
			}
			else if (isDecimal) {

				textfieldOutr.setText(String.valueOf(butterfly));
			}
			else if (isBinary) {

				textfieldOutr.setText(fillZeros(Integer.toBinaryString(butterfly), 4));
			}
		}
	}
	
	private String[][] toHex (int[][] arr) { // toBinary GIBI YAPMAK LAZIM

		String[][] butterfly = new String[32][5];

		for (int i = 0; i < arr.length; i++) {

			for (int j = 0; j < arr[1].length; j++) {

				if (arr[i][j] > -1) {

					butterfly[i][j] = "xx" + Integer.toHexString(arr[i][j]).toUpperCase(); // 'xx'
					// mean
					// is
					// that
					// the
					// value
					// is
					// Hexadecimal.
				} else {

					butterfly[i][j] = String.valueOf(arr[i][j]);
				}
			}
		}

		return butterfly;
	}

	private String[][] toBinary(int[][] arr) {

		String[][] butterfly = new String[32][5];

		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[1].length; j++) {

				if (arr[i][0] > -6) {


					if (j == 0 && arr[i][j] == -5) { // It means OpCode is "Arithmetic and Logic Operations".

						butterfly[i][0] = "0"; // Q = 0 BY DEFAULT.
						butterfly[i][1] = fillZeros(Integer.toBinaryString(arr[i][1]), 4); // Opcode.length must be equals 4.
						butterfly[i][2] = fillZeros(Integer.toBinaryString(arr[i][2]), 2); // D must be 2 digit.

						if (arr[i][1] > 0 && arr[i][1] < 5) { // INC - DBL - DBT - NOT, WE INTERESTED WITH S1

							butterfly[i][3] = fillZeros(Integer.toBinaryString(arr[i][3]), 2); // S1 must be 2 digit.
							butterfly[i][4] = "00";
						}
						else { // ADD - AND, WE INTERESTED WITH S1 AND S2

							butterfly[i][3] = fillZeros(Integer.toBinaryString(arr[i][3]), 2); // S1 must be 2 digit.
							butterfly[i][4] = fillZeros(Integer.toBinaryString(arr[i][4]), 2); // S2 must be 2 digit.
						}
						break;
					}
					else { // It means OpCode is NOT "Arithmetic and Logic Operations".

						butterfly[i][0] = Integer.toBinaryString(arr[i][0]);
						butterfly[i][1] = fillZeros(Integer.toBinaryString(arr[i][1]), 4); // Opcode.length must be equals 4.
						butterfly[i][2] = fillZeros(Integer.toBinaryString(arr[i][2]), 2); // D must be 2 digit.

						if (arr[i][4] < 2) {

							butterfly[i][3] = "00";
							butterfly[i][4] = "0" + Integer.toBinaryString(arr[i][4]);
						} else if (arr[i][4] < 4) {

							butterfly[i][3] = "00";
							butterfly[i][4] = Integer.toBinaryString(arr[i][4]);

						} else if (arr[i][4] < 8) {

							String butterMıFly = Integer.toBinaryString(arr[i][4]);
							butterfly[i][3] = "0" + butterMıFly.substring(0,1);
							butterfly[i][4] = butterMıFly.substring(2);
						} else if (arr[i][4] < 16) {
							String butterMıFly = Integer.toBinaryString(arr[i][4]);
							butterfly[i][3] = butterMıFly.substring(0, 2);
							butterfly[i][4] = butterMıFly.substring(2);
						}

						break;
					}
				}
				else { // Sadece tek bir yerde arr[i][j] < -1 Oda OpCode kısmında. [][1]

					butterfly[i][0] = String.valueOf(arr[i][0]);
					butterfly[i][1] = String.valueOf(arr[i][1]);
					butterfly[i][2] = String.valueOf(arr[i][2]);
					butterfly[i][3] = String.valueOf(arr[i][3]);
					butterfly[i][4] = String.valueOf(arr[i][4]);
					break;
				}
			}
		}

		return butterfly;
	}
	
	private String[][] toDecimal (int[][] arr) { // toBinary GIBI YAPMAK LAZIM

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
		textfieldStackPointer.setBounds(965, 605, 50, 20); // solsað,
		// aþaðýyukarý, en,
		// boy
		textfieldStackPointer.setEditable(false);
		frame.getContentPane().add(textfieldStackPointer);

		textfieldInsr = new JTextField();
		textfieldInsr.setBounds(965, 570, 85, 20); // solsað, aþaðýyukarý, en,
		// boy
		textfieldInsr.setEditable(false);
		frame.getContentPane().add(textfieldInsr);

		textfieldPc = new JTextField();
		textfieldPc.setBounds(965, 535, 50, 20); // solsað, aþaðýyukarý, en, boy
		textfieldPc.setEditable(false);
		frame.getContentPane().add(textfieldPc);

		textfieldAddr = new JTextField();
		textfieldAddr.setBounds(965, 500, 50, 20); // solsað, aþaðýyukarý, en,
		// boy
		textfieldAddr.setEditable(false);
		frame.getContentPane().add(textfieldAddr);

		textfieldInpr = new JTextField();
		textfieldInpr.setBounds(965, 465, 50, 20); // solsað, aþaðýyukarý, en,
		// boy
		// textfieldInpr.setEditable(false);
		frame.getContentPane().add(textfieldInpr);

		textfieldOutr = new JTextField();
		textfieldOutr.setBounds(965, 430, 50, 20); // solsað, aþaðýyukarý, en,
		// boy
		textfieldOutr.setEditable(false);
		frame.getContentPane().add(textfieldOutr);
		///////////////////////////////////////////

		textfieldT0 = new JTextField();
		textfieldT0.setBounds(427, 430, 130, 20); // solsað, aþaðýyukarý, en,
		// boy
		textfieldT0.setEditable(false);
		frame.getContentPane().add(textfieldT0);

		textfieldT1 = new JTextField();
		textfieldT1.setBounds(427, 465, 80, 20); // solsað, aþaðýyukarý, en, boy
		textfieldT1.setEditable(false);
		frame.getContentPane().add(textfieldT1);

		textfieldT2 = new JTextField();
		textfieldT2.setBounds(427, 500, 230, 20); // solsað, aþaðýyukarý, en,
		// boy
		textfieldT2.setEditable(false);
		frame.getContentPane().add(textfieldT2);

		textfieldT3 = new JTextField();
		textfieldT3.setBounds(427, 535, 120, 20); // solsað, aþaðýyukarý, en,
		// boy
		textfieldT3.setEditable(false);
		frame.getContentPane().add(textfieldT3);

		textfieldT4 = new JTextField();
		textfieldT4.setBounds(427, 570, 120, 20); // solsað, aþaðýyukarý, en,
		// boy
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
		 * radioBinary.setMnemonic(KeyEvent.VK_C);
		 * radioHexa.setMnemonic(KeyEvent.VK_M);
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
				// dataDtm.setValueAt("as", 1, 1);
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
				// dataDtm.setValueAt("as", 1, 1);
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
				// dataDtm.setValueAt("sa", 2, 1);

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
		instructionTable.setBounds(20, 40, 60, 80);

		dataTable = new JTable();
		dataTable.setEnabled(false);
		dataTable.setBounds(20, 40, 60, 80);

		stackTable = new JTable();
		stackTable.setEnabled(false);
		stackTable.setBounds(20, 40, 60, 80);

		labelTable = new JTable();
		labelTable.setEnabled(false);
		labelTable.setBounds(100, 80, 60, 200);

		String[] columnNames = { "Adrs", "I", "Opcode", "D", "S1", "S2" };

		dataMemory = new Memory(16, new String[] { "Adrs", "Data Memory" });
		dataDtm = dataMemory.getButterfly();
		dataTable.setModel(dataDtm);

		stackMemory = new Memory(16, new String[] { "Adrs", "Stack Memory" });
		stackDtm = stackMemory.getButterfly();
		stackTable.setModel(stackDtm);

		instructionMemory = new Memory(32, columnNames);

		labelMemory = new Memory(16, new String[] { "Adrs", "Var", "LabelTable", "Memory" });
		labelDtm = labelMemory.getButterfly();
		labelTable.setModel(labelDtm);

		// instructionDtm.setColumnIdentifiers(columnNames);
		instructionDtm = instructionMemory.getButterfly(); // 32 satýrlýk bir
		// hafýza alaný
		// yaratýyor.
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
		 * 
		 * debugAndRun.setBounds(800, 300, 0, 40);
		 * 
		 * debugAndRun.add(debug); debugAndRun.add(run);
		 */

		debug.setBackground(Color.GRAY);
		run.setBackground(Color.GRAY);

		frame.getContentPane().add(debug);
		frame.getContentPane().add(run);

		JLabel labelOverlow = new JLabel (" - OverFlow - ");
		labelOverlow.setBounds(920, 240, 80, 20);
		frame.getContentPane().add(labelOverlow);

		textfieldOverflow = new JTextField();
		textfieldOverflow.setBounds(941, 265, 30, 20);
		textfieldOverflow.setEditable(false);
		frame.getContentPane().add(textfieldOverflow);

		showCheckBoxDemo(); // INPUT OUTPUT FLAG


		//RUN and DEBUG ClickListener
		run.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				/*
				 * JDialog d = new JDialog(frame, "Hello", true);
				 * d.setLocationRelativeTo(frame); d.setVisible(true);
				 */

				if (isNextLine) {
					debugCounter = 0;
					if (!isHLT) isNextLine = false;
					textBeInvisible(howInvisible);
					textfieldAddr.setText("");
					textfieldInsr.setText("");
					textfieldOverflow.setText("");
				}

				while (!isNextLine && !isHLT) {
					debugOperation();
				}
			}
		});

		debug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 * JDialog d = new JDialog(frame, "Hello", true);
				 * d.setLocationRelativeTo(frame); d.setVisible(true);
				 */
				int i = instructionMemory.getCounter() - 1; // Current
				// ProgramCounter
				int selection = 0;
				if (i > -1)
					selection = tableDecimalInstruction[i][1];


				if (isNextLine) {
					debugCounter = 0;
					isNextLine = false;
					textBeInvisible(howInvisible);
					textfieldAddr.setText("");
					textfieldInsr.setText("");
					textfieldOverflow.setText("");
				}
				if(!isHLT)
					debugOperation();
			}
		});
	}

	public void debugOperation () {

		int i = instructionMemory.getCounter() - 1; // Current ProgramCounter
		int selection = 0;
		if (i > -1) selection = tableDecimalInstruction[ i ][1];



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

	public void setPC() {

		for (int i = 0; i < tableDecimalInstruction.length; i++) {

			if (tableDecimalInstruction[i][1] == -1) {

				System.out.println(tableDecimalInstruction[i][3]);
				instructionMemory.setCounter(tableDecimalInstruction[i][3]);
			}
		}
	}

	public String T0(int arr[][]) {

		String opcode = "";
		///
		for (int j = 0; j < arr[instructionMemory.getCounter()].length; j++) {


			if (j == 0 && arr[instructionMemory.getCounter()][j] == -5) {

				opcode += 0;
			} else opcode += tableInstruction[instructionMemory.getCounter()][j];

		}
		System.out.println("ins length : " + opcode.length());
		return opcode;
	}

	public void T1() {

		instructionMemory.increaseCounter();
	}

	public void T3 (int[][] arr, int i) {

		int R0 = -1, R1 = -1, R2 = -1, Rin = -1;
		int butterfly = Integer.parseInt(textfieldAddr.getText()); // FOR THE VALUE OF PROGRAM COUNTER.
		int value = 0;
		
		switch ( arr[ i ][1] ) {

		case 0: // ADD

			textfieldT3.setText(" D <- S1+S2, SC <- 0");
			textfieldT3.setVisible(true);
			labelT3.setVisible(true);
			isNextLine = true;
			howInvisible = 3;


			writeIntoRegister(); // ALL CONTROL IS IN THIS FUNCTION.

			break;
		case 1: // INC

			if (textfieldR0.getText().length() > 0) R0 = getInteger(textfieldR0.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)

			if (textfieldR1.getText().length() > 0) R1 = getInteger(textfieldR1.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)

			if (textfieldR2.getText().length() > 0) R2 = getInteger(textfieldR2.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)
			
			if (textfieldInpr.getText().length() > 0) Rin = getInteger(textfieldInpr.getText());
			

			if (tableInstruction[ butterfly ][2].equalsIgnoreCase("00")) {

				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) {

					if ( R0 + 1 > 15 ) {

						value = (R0 + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR0.setText("");
					textfieldR0.setText( String.valueOf( value ) );
					updateR0();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("01") && R1 != -1) {

					if ( R1 + 1 > 15 ) {

						value = (R1 + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR0.setText("");
					textfieldR0.setText( String.valueOf( value ) );
					updateR0();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("10") && R2 != -1) {

					if ( R1 + 2 > 15 ) {

						value = (R2 + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR0.setText("");
					textfieldR0.setText( String.valueOf( value ) );
					updateR0();
				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && Rin != -1) {
					
					
					textfieldR1.setText("");
					if ( Rin + 1 > 15 ) {

						value = (Rin + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR0.setText("");
					textfieldR0.setText( String.valueOf( value ) );
					updateR0();
				} else System.out.println("Inc işleminde, boş beleş bir register seçmişsiniz :(");

			} else if (tableInstruction[ butterfly ][2].equalsIgnoreCase("01")) {


				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) {

					if ( R0 + 1 > 15 ) {

						value = (R0 + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR1.setText("");
					textfieldR1.setText( String.valueOf( value ) );
					updateR1();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("01") && R1 != -1) {

					if ( R1 + 1 > 15 ) {

						value = (R1 + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR1.setText("");
					textfieldR1.setText( String.valueOf( value ) );
					updateR1();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("10") && R2 != -1) {

					if ( R2 + 1 > 15 ) {

						value = (R2 + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR1.setText("");
					textfieldR1.setText( String.valueOf( value ) );
					updateR1();
				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && Rin != -1) {
					
					
					textfieldR1.setText("");
					if ( Rin + 1 > 15 ) {

						value = (Rin + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR1.setText("");
					textfieldR1.setText( String.valueOf( value ) );
					updateR1();
				} else System.out.println("Inc işleminde, boş beleş bir register seçmişsiniz :(");

			} else if (tableInstruction[ butterfly ][2].equalsIgnoreCase("10")) {

				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) {

					if ( R0 + 1 > 15 ) {

						value = (R0 + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR2.setText("");
					textfieldR2.setText( String.valueOf( value ) );
					updateR2();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("01") && R1 != -1) {

					if ( R1 + 1 > 15 ) {

						value = (R1 + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR2.setText("");
					textfieldR2.setText( String.valueOf( value ) );
					updateR2();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("10") && R2 != -1) {

					textfieldR2.setText("");
					if ( R2 + 1 > 15 ) {

						value = (R2 + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR2.setText("");
					textfieldR2.setText( String.valueOf( value ) );
					updateR2();
				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && Rin != -1) {
					
					
					textfieldR2.setText("");
					if ( Rin + 1 > 15 ) {

						value = (Rin + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR2.setText("");
					textfieldR2.setText( String.valueOf( value ) );
					updateR2();
				} else System.out.println("Inc işleminde, boş beleş bir register seçmişsiniz :(");
			}
			else if (tableDecimalInstruction[ butterfly ][2] == 3) {
				
				
				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) { // == 0 (R0)

					textfieldOutr.setText("");
					if ( R0 + 1 > 15 ) {

						value = (R0 + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldOutr.setText("");
					textfieldOutr.setText( String.valueOf( value ) );
					updateOutr();
				} else if (tableDecimalInstruction[ butterfly ][3] == 1 && R1 != -1) { // == 1 (R1)

					textfieldOutr.setText("");
					if ( R1 + 1 > 15 ) {

						value = (R1 + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldOutr.setText("");
					textfieldOutr.setText( String.valueOf( value ) );
					updateOutr();
				} else if (tableDecimalInstruction[ butterfly ][3] == 2 && R2 != -1) { // == 2 (R2)

					textfieldOutr.setText("");
					if ( R2 + 1 > 15 ) {

						value = (R2 + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldOutr.setText("");
					textfieldOutr.setText( String.valueOf( value ) );
					updateOutr();
				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && Rin != -1) {
					
					
					textfieldOutr.setText("");
					if ( Rin + 1 > 15 ) {

						value = (Rin + 1) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldOutr.setText("");
					textfieldOutr.setText( String.valueOf( value ) );
					updateOutr();
				} else System.out.println("Inc işleminde, boş beleş bir register seçmişsiniz :(");
			}
			else {
				
				System.out.println("INC işleminde, böyle bir registerı seçemezsin");
			}

			isNextLine = true;
			break;
		case 2: // DBL

			if (textfieldR0.getText().length() > 0) R0 = getInteger(textfieldR0.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)

			if (textfieldR1.getText().length() > 0) R1 = getInteger(textfieldR1.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)

			if (textfieldR2.getText().length() > 0) R2 = getInteger(textfieldR2.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)

			if (tableInstruction[ butterfly ][2].equalsIgnoreCase("00")) {

				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) {

					if ( R0 * 2 > 15 ) {

						value = (R0 * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR0.setText("");
					textfieldR0.setText( String.valueOf( value ) );
					updateR0();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("01") && R1 != -1) {

					if ( R1 * 2 > 15 ) {

						value = (R1 * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR0.setText("");
					textfieldR0.setText( String.valueOf( value ) );
					updateR0();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("10") && R2 != -1) {

					if ( R2 * 2 > 15 ) {

						value = (R2 * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR0.setText("");
					textfieldR0.setText( String.valueOf( value ) );
					updateR0();
				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && Rin != -1) {
					
					
					textfieldOutr.setText("");
					if ( Rin * 2 > 15 ) {

						value = (Rin * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR0.setText("");
					textfieldR0.setText( String.valueOf( value ) );
					updateR0();
				} else System.out.println("Dbl işleminde, boş beleş bir register seçmişsiniz :(");
				
			} else if (tableInstruction[ butterfly ][2].equalsIgnoreCase("01")) { 


				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) {

					if ( R0 * 2 > 15 ) {

						value = (R0 * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR1.setText("");
					textfieldR1.setText( String.valueOf( value ) );
					updateR1();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("01") && R1 != -1) {

					if ( R1 * 2 > 15 ) {

						value = (R1 * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR1.setText("");
					textfieldR1.setText( String.valueOf( value ) );
					updateR1();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("10") && R2 != -1) {

					if ( R2 * 2 > 15 ) {

						value = (R2 * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR1.setText("");
					textfieldR1.setText( String.valueOf( value ) );
					updateR1();
				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && Rin != -1) {
					
					
					textfieldOutr.setText("");
					if ( Rin * 2 > 15 ) {

						value = (Rin * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR1.setText("");
					textfieldR1.setText( String.valueOf( value ) );
					updateR1();
				} else System.out.println("Dbl işleminde, boş beleş bir register seçmişsiniz :(");
				
			} else if (tableInstruction[ butterfly ][2].equalsIgnoreCase("10")) {

				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) {

					if ( R0 * 2 > 15 ) {

						value = (R0 * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR2.setText("");
					textfieldR2.setText( String.valueOf( value ) );
					updateR2();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("01") && R1 != -1) {

					if ( R1 * 2 > 15 ) {

						value = (R1 * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR2.setText("");
					textfieldR2.setText( String.valueOf( value ) );
					updateR2();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("10") && R2 != -1) {

					if ( R2 * 2 > 15 ) {

						value = (R2 * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR2.setText("");
					textfieldR2.setText( String.valueOf( value ) );
					updateR2();
				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && Rin != -1) {
					
					
					textfieldOutr.setText("");
					if ( Rin * 2 > 15 ) {

						value = (Rin * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldR2.setText("");
					textfieldR2.setText( String.valueOf( value ) );
					updateR2();
				} else System.out.println("Dbl işleminde, boş beleş bir register seçmişsiniz :(");
				
			} else if (tableDecimalInstruction[ butterfly ][2] == 3) {
				
				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) { // == 0 (R0)

					textfieldOutr.setText("");
					if ( R0 * 2 > 15 ) {

						value = (R0 * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldOutr.setText("");
					textfieldOutr.setText( String.valueOf( value ) );
					updateOutr();
				} else if (tableDecimalInstruction[ butterfly ][3] == 1 && R1 != -1) { // == 1 (R1)

					textfieldOutr.setText("");
					if ( R1 * 2 > 15 ) {

						value = (R1 * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldOutr.setText("");
					textfieldOutr.setText( String.valueOf( value ) );
					updateOutr();
				} else if (tableDecimalInstruction[ butterfly ][3] == 2 && R2 != -1) { // == 2 (R2)

					textfieldOutr.setText("");
					if ( R2 * 2> 15 ) {

						value = (R2 * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldOutr.setText("");
					textfieldOutr.setText( String.valueOf( value ) );
					updateOutr();
				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && Rin != -1) {
					
					
					textfieldOutr.setText("");
					if ( Rin * 2 > 15 ) {

						value = (Rin * 2) % 16;
						textfieldOverflow.setText("1");
					}
					textfieldOutr.setText("");
					textfieldOutr.setText( String.valueOf( value ) );
					updateOutr();
				} else System.out.println("Dbl işleminde, boş beleş bir register seçmişsiniz :(");
			}
			else {
				
				System.out.println("DBL işleminde, böyle bir registerı seçemezsin");
			}

			isNextLine = true;
			break;
		case 3: // DBT

			if (textfieldR0.getText().length() > 0) R0 = getInteger(textfieldR0.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)

			if (textfieldR1.getText().length() > 0) R1 = getInteger(textfieldR1.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)

			if (textfieldR2.getText().length() > 0) R2 = getInteger(textfieldR2.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)

			if (textfieldInpr.getText().length() > 0) Rin = getInteger(textfieldInpr.getText());
			// it will return integer according to clicked button. (hexa, binary, decimal)

			if (tableInstruction[ butterfly ][2].equalsIgnoreCase("00")) {

				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) {

					textfieldR0.setText("");
					textfieldR0.setText( String.valueOf( R0 / 2 ) );
					updateR0();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("01") && R1 != -1) {

					textfieldR0.setText("");
					textfieldR0.setText( String.valueOf( R1 / 2 ) );
					updateR0();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("10") && R2 != -1) {

					textfieldR0.setText("");
					textfieldR0.setText( String.valueOf( R2 / 2 ) );
					updateR0();
				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && Rin != -1) {

					textfieldR0.setText("");
					textfieldR0.setText( String.valueOf( R2 / 2 ) );
					updateR0();
				}
			} else if (tableInstruction[ butterfly ][2].equalsIgnoreCase("01")) {


				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) {

					textfieldR1.setText("");
					textfieldR1.setText( String.valueOf( R0 / 2 ) );
					updateR1();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("01") && R1 != -1) {

					textfieldR1.setText("");
					textfieldR1.setText( String.valueOf( R1 / 2 ) );
					updateR1();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("10") && R2 != -1) {

					textfieldR1.setText("");
					textfieldR1.setText( String.valueOf( R2 / 2 ) );
					updateR1();
				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && Rin != -1) {

					textfieldR1.setText("");
					textfieldR1.setText( String.valueOf( R2 / 2 ) );
					updateR1();
				}
			} else if (tableInstruction[ butterfly ][2].equalsIgnoreCase("10")) {

				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) {

					textfieldR2.setText("");
					textfieldR2.setText( String.valueOf( R0 / 2 ) );
					updateR2();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("01") && R1 != -1) {

					textfieldR2.setText("");
					textfieldR2.setText( String.valueOf( R1 / 2 ) );
					updateR2();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("10") && R2 != -1) {

					textfieldR2.setText("");
					textfieldR2.setText( String.valueOf( R2 / 2 ) );
					updateR2();
				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && Rin != -1) {

					textfieldR2.setText("");
					textfieldR2.setText( String.valueOf( R2 / 2 ) );
					updateR2();
				}
				else System.out.println("Dbl işleminde, boş beleş bir register seçmişsiniz :(");
			} else if (tableDecimalInstruction[ butterfly ][2] == 3) {
				
				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) {

					textfieldOutr.setText("");
					textfieldOutr.setText( String.valueOf( R0 / 2 ) );
					updateOutr();
				} else if (tableDecimalInstruction[ butterfly ][3] == 1 && R1 != -1) {

					textfieldOutr.setText("");
					textfieldOutr.setText( String.valueOf( R1 / 2 ) );
					updateOutr();
				} else if (tableDecimalInstruction[ butterfly ][3] == 2 && R2 != -1) {

					textfieldOutr.setText("");
					textfieldOutr.setText( String.valueOf( R2 / 2 ) );
					updateOutr();
				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && Rin != -1) {

					textfieldOutr.setText("");
					textfieldOutr.setText( String.valueOf( R2 / 2 ) );
					updateOutr();
				}
				else System.out.println("Dbl işleminde, boş beleş bir register seçmişsiniz :(");
			}
			else {
				
				System.out.println("DBL işleminde, böyle bir registerı seçemezsin");
			}

			isNextLine = true;
			break;
		case 4: // NOT


			if (textfieldR0.getText().length() > 0) R0 = getInteger(textfieldR0.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)

			if (textfieldR1.getText().length() > 0) R1 = getInteger(textfieldR1.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)

			if (textfieldR2.getText().length() > 0) R2 = getInteger(textfieldR2.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)

			if (tableDecimalInstruction[ butterfly ][2] == 0) {

				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) {

					textfieldR0.setText("");
					System.out.println("sasasa : " + String.valueOf( R0 * (-1) ));
					System.out.println(Integer.toBinaryString(R0 * (-1)));
					textfieldR0.setText( "E" + String.valueOf( R0 ) );
					updateR0();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("01") && R1 != -1) {

					textfieldR0.setText("");
					textfieldR0.setText( "E" + String.valueOf( R1 ) );
					updateR0();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("10") && R2 != -1) {

					textfieldR0.setText("");
					textfieldR0.setText( "E" + String.valueOf( R2 ) );
					updateR0();
				} else {

					System.out.println("Boş beleş bir register seçmişsiniz :(");
				}
			} else if (tableDecimalInstruction[ butterfly ][2] == 1) {


				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) {

					textfieldR1.setText("");
					textfieldR1.setText( "E" + String.valueOf( R0 ) );
					updateR1();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("01") && R1 != -1) {

					textfieldR1.setText("");
					textfieldR1.setText( "E" + String.valueOf( R1 ) );
					updateR1();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("10") && R2 != -1) {

					textfieldR1.setText("");
					textfieldR1.setText( "E" + String.valueOf( R2 ) );
					updateR1();
				} else {

					System.out.println("Boş beleş bir register seçmişsiniz :(");
				}
			} else if (tableDecimalInstruction[ butterfly ][2] == 2) {

				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) {

					textfieldR2.setText("");
					textfieldR2.setText( "E" + String.valueOf( R0 ) );
					updateR2();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("01") && R1 != -1) {

					textfieldR2.setText("");
					textfieldR2.setText( "E" + String.valueOf( R1 ) );
					updateR2();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("10") && R2 != -1) {

					textfieldR2.setText("");
					textfieldR2.setText( "E" + String.valueOf( R2 ) );
					updateR2();
				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && R2 != -1) {
					
					textfieldR2.setText("");
					textfieldR2.setText( "E" + String.valueOf( Rin ) );
					updateR2();
				} else System.out.println("Boş beleş bir register seçmişsiniz :(");
			} else if (tableDecimalInstruction[ butterfly ][2] == 3) {

				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) {

					textfieldOutr.setText("");
					textfieldOutr.setText( "E" + String.valueOf( R0 ) );
					updateOutr();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("01") && R1 != -1) {

					textfieldOutr.setText("");
					textfieldOutr.setText( "E" + String.valueOf( R1 ) );
					updateOutr();
				} else if (tableInstruction[ butterfly ][3].equalsIgnoreCase("10") && R2 != -1) {

					textfieldOutr.setText("");
					textfieldOutr.setText( "E" + String.valueOf( R2 ) );
					updateOutr();
				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && R2 != -1) {
					
					textfieldOutr.setText("");
					textfieldOutr.setText( "E" + String.valueOf( Rin ) );
					updateOutr();
				} else System.out.println("Boş beleş bir register seçmişsiniz :(");
			}

			isNextLine = true;
			break;
		case 5: // AND

			if (textfieldR0.getText().length() > 0) R0 = getInteger(textfieldR0.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)

			if (textfieldR1.getText().length() > 0) R1 = getInteger(textfieldR1.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)

			if (textfieldR2.getText().length() > 0) R2 = getInteger(textfieldR2.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)
			
			if (textfieldInpr.getText().length() > 0) Rin = getInteger(textfieldInpr.getText()); 
			// it will return integer according to clicked button. (hexa, binary, decimal)
			

			if (tableDecimalInstruction[ butterfly ][2] == 0) { // D == R2

				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) { // D == R0 && S1 == R0

					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R0 && S1 == R0 && S2 == R0 ise

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( R0 & R0 ) );
						updateR0();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R0 && S1 == R0 && S2 == R1 ise

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( R0 & R1 ) );
						updateR0();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R0 && S1 == R0 && S2 == R2 ise

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( R0 & R2 ) );
						updateR0();
					} else if (tableDecimalInstruction[ butterfly ][4] == 3 && Rin != -1) { // D == R0 && S1 == R2 && S2 == R2

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( R0 & Rin ) );
						updateR0();
					} else System.out.println("Boş beleş bir register seçmişsiniz :(");

				} else if (tableDecimalInstruction[ butterfly ][3] == 1 && R1 != -1) { // D == R0 && S1 == R1


					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R0 && S1 == R1 && S2 == R0 ise

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( R1 & R0 ) );
						updateR0();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R0 && S1 == R1 && S2 == R1 ise

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( R1 & R1 ) );
						updateR0();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R0 && S1 == R1 && S2 == R2 ise

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( R1 & R2 ) );
						updateR0();
					} else if (tableDecimalInstruction[ butterfly ][4] == 3 && Rin != -1) { // D == R0 && S1 == R2 && S2 == R2

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( R1 & Rin ) );
						updateR0();
					} else System.out.println("Boş beleş bir register seçmişsiniz :(");


				} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R0 && S1 == R2

					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R0 && S1 == R2 && S2 == R0

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( R2 & R0 ) );
						updateR0();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R0 && S1 == R2 && S2 == R1

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( R2 & R1 ) );
						updateR0();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R0 && S1 == R2 && S2 == R2

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( R2 & R2 ) );
						updateR0();
					} else if (tableDecimalInstruction[ butterfly ][4] == 3 && Rin != -1) { // D == R0 && S1 == R2 && S2 == R2

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( R2 & Rin ) );
						updateR0();
					} else System.out.println("Boş beleş bir register seçmişsiniz :(");

				} else if (tableDecimalInstruction[ butterfly ][4] == 3 && Rin != -1) { // D == R0 && S1 == Rin

					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R0 && S1 == Rin && S2 == R0

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( Rin & R0 ) );
						updateR0();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R0 && S1 == Rin && S2 == R1

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( Rin & R1 ) );
						updateR0();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R0 && S1 == Rin && S2 == R2

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( Rin & R2 ) );
						updateR0();
					} else if (tableDecimalInstruction[ butterfly ][4] == 3 && Rin != -1) { // D == R0 && S1 == Rin && S2 == R2

						textfieldR0.setText("");
						textfieldR0.setText( String.valueOf( Rin & Rin ) );
						updateR0();
					} else System.out.println("Boş beleş bir register seçmişsiniz :(");

				} else System.out.println("Boş beleş bir register seçmişsiniz :(");
				
			} else if (tableDecimalInstruction[ butterfly ][2] == 1) {


				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) { // D == R0 && S1 == R0

					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R0 && S1 == R0 && S2 == R0 ise

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( R0 & R0 ) );
						updateR1();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R0 && S1 == R0 && S2 == R1 ise

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( R0 & R1 ) );
						updateR1();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R0 && S1 == R0 && S2 == R2 ise

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( R0 & R2 ) );
						updateR1();
					} else if (tableDecimalInstruction[ butterfly ][4] == 3 && Rin != -1) { // D == R0 && S1 == R2 && S2 == R2

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( R0 & Rin ) );
						updateR1();
					} else System.out.println("Boş beleş bir register seçmişsiniz :(");

				} else if (tableDecimalInstruction[ butterfly ][3] == 1 && R1 != -1) { // D == R1 && S1 == R1


					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R1 && S1 == R1 && S2 == R0 ise

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( R1 & R0 ) );
						updateR1();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R1 && S1 == R1 && S2 == R1 ise

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( R1 & R1 ) );
						updateR1();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R1 && S1 == R1 && S2 == R2 ise

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( R1 & R2 ) );
						updateR1();
					} else if (tableDecimalInstruction[ butterfly ][4] == 3 && Rin != -1) { // D == R0 && S1 == R2 && S2 == R2

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( R1 & Rin ) );
						updateR1();
					} else System.out.println("Boş beleş bir register seçmişsiniz :(");


				} else if (tableDecimalInstruction[ butterfly ][3] == 2 && R2 != -1) { // D == R1 && S1 == R2

					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R1 && S1 == R2 && S2 == R0

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( R2 & R0 ) );
						updateR1();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R1 && S1 == R2 && S2 == R1

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( R2 & R1 ) );
						updateR1();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R1 && S1 == R2 && S2 == R2

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( R2 & R2 ) );
						updateR1();
					} else if (tableDecimalInstruction[ butterfly ][4] == 3 && Rin != -1) { // D == R0 && S1 == R2 && S2 == R2

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( R2 & Rin ) );
						updateR1();
					} else System.out.println("S2' yi, boş beleş bir register seçmişsiniz :(");

				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && Rin != -1) { // D == R1 && S1 == R2

					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R1 && S1 == R2 && S2 == R0

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( Rin & R0 ) );
						updateR1();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R1 && S1 == R2 && S2 == R1

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( Rin & R1 ) );
						updateR1();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R1 && S1 == R2 && S2 == R2

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( Rin & R2 ) );
						updateR1();
					} else if (tableDecimalInstruction[ butterfly ][4] == 3 && Rin != -1) { // D == R0 && S1 == R2 && S2 == R2

						textfieldR1.setText("");
						textfieldR1.setText( String.valueOf( Rin & Rin ) );
						updateR1();
					} else System.out.println("S2' yi, boş beleş bir register seçmişsiniz :(");

				} else System.out.println("S1' i, boş beleş bir register seçmişsiniz :(");

			} else if (tableDecimalInstruction[ butterfly ][2] == 2) {


				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) { // D == R2 && S1 == R0

					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R2 && S1 == R0 && S2 == R0 ise

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R0 & R0 ) );
						updateR2();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R2 && S1 == R0 && S2 == R1 ise

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R0 & R1 ) );
						updateR2();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R2 && S1 == R0 && S2 == R2 ise

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R0 & R2 ) );
						updateR2();
					} else if (tableDecimalInstruction[ butterfly ][4] == 3 && Rin != -1) { // D == R0 && S1 == R2 && S2 == R2

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R0 & Rin ) );
						updateR2();
					} else System.out.println("S2' yi, boş beleş bir register seçmişsiniz :(");

				} else if (tableDecimalInstruction[ butterfly ][3] == 1 && R1 != -1) { // D == R2 && S1 == R1


					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R2 && S1 == R1 && S2 == R0 ise

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R1 & R0 ) );
						updateR2();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R2 && S1 == R1 && S2 == R1 ise

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R1 & R1 ) );
						updateR2();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R2 && S1 == R1 && S2 == R2 ise

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R1 & R2 ) );
						updateR2();
					} else if (tableDecimalInstruction[ butterfly ][4] == 3 && Rin != -1) { // D == R0 && S1 == R2 && S2 == R2

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R1 & Rin ) );
						updateR2();
					} else System.out.println("S2' yi, boş beleş bir register seçmişsiniz :(");


				} else if (tableDecimalInstruction[ butterfly ][3] == 2 && R2 != -1) { // D == R2 && S1 == R2

					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R2 && S1 == R2 && S2 == R0

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R2 & R0 ) );
						updateR2();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R2 && S1 == R2 && S2 == R1

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R2 & R1 ) );
						updateR2();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R2 && S1 == R2 && S2 == R2

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R2 & R2 ) );
						updateR2();
					} else if (tableDecimalInstruction[ butterfly ][4] == 3 && Rin != -1) { // D == R0 && S1 == R2 && S2 == R2

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R2 & Rin ) );
						updateR2();
					} else System.out.println("S2' yi, boş beleş bir register seçmişsiniz :(");

				} else if (tableDecimalInstruction[ butterfly ][3] == 3 && Rin != -1) { // D == R2 && S1 == R2

					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R2 && S1 == R2 && S2 == R0

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R2 & R0 ) );
						updateR2();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R2 && S1 == R2 && S2 == R1

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R2 & R1 ) );
						updateR2();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R2 && S1 == R2 && S2 == R2

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R2 & R2 ) );
						updateR2();
					} else if (tableDecimalInstruction[ butterfly ][4] == 3 && Rin != -1) { // D == R0 && S1 == R2 && S2 == R2

						textfieldR2.setText("");
						textfieldR2.setText( String.valueOf( R2 & Rin ) );
						updateR2();
					} else System.out.println("S2' yi, boş beleş bir register seçmişsiniz :(");

				} else System.out.println("S1, i, boş beleş bir register seçmişsiniz :(");
			} else if (tableDecimalInstruction[ butterfly ][2] == 3) {
				
				
				if (tableDecimalInstruction[ butterfly ][3] == 0 && R0 != -1) { // D == R2 && S1 == R0

					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R2 && S1 == R0 && S2 == R0 ise

						textfieldOutr.setText("");
						textfieldOutr.setText( String.valueOf( R0 & R0 ) );
						updateOutr();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R2 && S1 == R0 && S2 == R1 ise

						textfieldOutr.setText("");
						textfieldOutr.setText( String.valueOf( R0 & R1 ) );
						updateOutr();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R2 && S1 == R0 && S2 == R2 ise

						textfieldOutr.setText("");
						textfieldOutr.setText( String.valueOf( R0 & R2 ) );
						updateOutr();
					} else System.out.println("Boş beleş bir register seçmişsiniz :(");

				} else if (tableDecimalInstruction[ butterfly ][3] == 1 && R1 != -1) { // D == R2 && S1 == R1


					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R2 && S1 == R1 && S2 == R0 ise

						textfieldOutr.setText("");
						textfieldOutr.setText( String.valueOf( R1 & R0 ) );
						updateOutr();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R2 && S1 == R1 && S2 == R1 ise

						textfieldOutr.setText("");
						textfieldOutr.setText( String.valueOf( R1 & R1 ) );
						updateOutr();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R2 && S1 == R1 && S2 == R2 ise

						textfieldOutr.setText("");
						textfieldOutr.setText( String.valueOf( R1 & R2 ) );
						updateOutr();
					} else System.out.println("Boş beleş bir register seçmişsiniz :(");


				} else if (tableDecimalInstruction[ butterfly ][3] == 2 && R2 != -1) { // D == R2 && S1 == R2

					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R2 && S1 == R2 && S2 == R0

						textfieldOutr.setText("");
						textfieldOutr.setText( String.valueOf( R2 & R0 ) );
						updateOutr();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R2 && S1 == R2 && S2 == R1

						textfieldOutr.setText("");
						textfieldOutr.setText( String.valueOf( R2 & R1 ) );
						updateOutr();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R2 && S1 == R2 && S2 == R2

						textfieldOutr.setText("");
						textfieldOutr.setText( String.valueOf( R2 & R2 ) );
						updateOutr();
					} else if (tableDecimalInstruction[ butterfly ][4] == 3 && Rin != -1) { // D == R2 && S1 == R2 && S2 == Rin

						textfieldOutr.setText("");
						textfieldOutr.setText( String.valueOf( R2 & Rin ) );
						updateOutr();
					} else System.out.println("Boş beleş bir register seçmişsiniz :(");
					
				}  else if (tableDecimalInstruction[ butterfly ][3] == 3 && R2 != -1) { // D == R2 && S1 == R2

					if (tableDecimalInstruction[ butterfly ][4] == 0 && R0 != -1) { // D == R2 && S1 == R2 && S2 == R0

						textfieldOutr.setText("");
						textfieldOutr.setText( String.valueOf( Rin & R0 ) );
						updateOutr();
					} else if (tableDecimalInstruction[ butterfly ][4] == 1 && R1 != -1) { // D == R2 && S1 == R2 && S2 == R1

						textfieldOutr.setText("");
						textfieldOutr.setText( String.valueOf( Rin & R1 ) );
						updateOutr();
					} else if (tableDecimalInstruction[ butterfly ][4] == 2 && R2 != -1) { // D == R2 && S1 == R2 && S2 == R2

						textfieldOutr.setText("");
						textfieldOutr.setText( String.valueOf( Rin & R2 ) );
						updateOutr();
					} else if (tableDecimalInstruction[ butterfly ][4] == 3 && Rin != -1) { // D == R2 && S1 == R2 && S2 == Rin

						textfieldOutr.setText("");
						textfieldOutr.setText( String.valueOf( Rin & Rin ) );
						updateOutr();
					} else System.out.println("Boş beleş bir register seçmişsiniz :(");
				} else System.out.println("Boş beleş bir register seçmişsiniz :(");
			} else System.out.println("Böyle bir register yok ki yazasın üstüne bir şeyler");

			isNextLine = true;
			break;
		case 6: // LD

			if (arr[i][0] == 0) { // Q == 0 ( @uploadInteger ) I need to find
				// the value of integer.

				textfieldT3.setText(" AR <- S1S2");
				textfieldT3.setVisible(true);
				labelT3.setVisible(true);
				textfieldAddr.setText(tableInstruction[ butterfly ][3] + tableInstruction[ butterfly ][4]);
			}
			else { // Q == 1 ( #directInteger ) Integer value equals S1S2.

				textfieldT3.setText("D <- S1S2, SC <- 0");
				textfieldT3.setVisible(true);
				labelT3.setVisible(true);

				butterfly = Integer.parseInt(textfieldAddr.getText());
				textfieldAddr.setText(tableInstruction[ butterfly ][3] + tableInstruction[ butterfly ][3]);

				if (tableInstruction[butterfly][2].equalsIgnoreCase("00")) {

					textfieldR0.setText("");
					textfieldR0.setText(String.valueOf(tableDecimalInstruction[butterfly][4]));
					updateR0();
				}

				else if (tableInstruction[butterfly][2].equalsIgnoreCase("01")) {

					textfieldR1.setText("");
					textfieldR1.setText(String.valueOf(tableDecimalInstruction[butterfly][4]));
					updateR1();
				}

				else if (tableInstruction[ butterfly ][2].equalsIgnoreCase("10")) {
					textfieldR2.setText("");
					textfieldR2.setText(String.valueOf(tableDecimalInstruction[butterfly][4]));
					updateR2();
				}
			}
			break;
		case 7: // ST


			if (arr[i][0] == 0) { // Q == 0 ( @uploadInteger ) I need to find the value of integer.

				textfieldT3.setText(" AR <- S1S2");
				textfieldT3.setVisible(true);
				labelT3.setVisible(true);
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
				//				else if (tableInstruction[ butterfly ][2].equalsIgnoreCase("10")) {
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
			isHLT = true;
			break;
		case 9: // TSF

			isNextLine = true;
			break;
		case 10: // CAL


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

			textfieldT3.setText("AR <- IR[3..0]");
			textfieldT3.setVisible(true);
			labelT3.setVisible(true);

			break;
		case 15: // POP

			textfieldT3.setText("AR <- IR[3..0]");
			textfieldT3.setVisible(true);
			labelT3.setVisible(true);

			break;
		default:
			break;
		}
	}

	public void T4(int i) {

		int butterfly = Integer.parseInt(textfieldPc.getText()) - 1;

		switch (i) {

		case 6: // LD

			textfieldT4.setText(" D <- DM[AR], SC <- 0");
			textfieldT4.setVisible(true);
			labelT4.setVisible(true);



			if (tableInstruction[butterfly][2].equalsIgnoreCase("00")) {

				textfieldR0.setText("");
				textfieldR0.setText(String.valueOf(tableDecimalInstruction[butterfly][4]));
				updateR0();
			}

			else if (tableInstruction[butterfly][2].equalsIgnoreCase("01")) {

				textfieldR1.setText("");
				textfieldR1.setText(String.valueOf(tableDecimalInstruction[butterfly][4]));
				updateR1();
			}
			else if (tableInstruction[ butterfly ][2].equalsIgnoreCase("10")) {

				textfieldR2.setText("");
				textfieldR2.setText(String.valueOf(tableDecimalInstruction[butterfly][4]));
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

			int but = Integer.parseInt(textfieldAddr.getText());

			textfieldT4.setText("SM[SP] <- DM[AR]");
			textfieldT4.setVisible(true);
			labelT4.setVisible(true);

			tableStack[counterTableStack][1] = tableData[but][1];

			break;
		case 15: // POP

			textfieldT4.setText("SP <- SP-1");
			textfieldT4.setVisible(true);
			labelT4.setVisible(true);

			counterTableStack--;

			break;
		default:
			break;
		}
	}

	public void T5(int i) {

		int butterfly = Integer.parseInt(textfieldAddr.getText());

		switch (i) {

		case 14: // PSH

			textfieldT5.setText("SP <- SP+1");
			textfieldT5.setVisible(true);
			labelT5.setVisible(true);

			counterTableStack++;

			isNextLine = true;
			break;
		case 15: // POP

			textfieldT5.setText("DM[AR] <- SM[SP]");
			textfieldT5.setVisible(true);
			labelT5.setVisible(true);


			tableData[butterfly][1] = tableStack[counterTableStack][1];

			isNextLine = true;
			break;
		default:
			break;
		}

	}

	public int[] determineS1 () { // IT WILL BE RETURN LINE FOR THE CURRENT OPERATION ACCORDING TO ARRAY AND THE VALUE OF 'S1'



		// IMPORTANT AREA

		// lineAndcase[0] = case0 (means which line will be change in instructionArray, like Program Counter)
		// lineAndcase[1] = whichCase (means what is the value of 'S1')

		/////////////////

		int[] lineAndcase = new int[2];
		lineAndcase[0] = Integer.parseInt(textfieldAddr.getText());
		lineAndcase[1] = -1;

		switch (tableDecimalInstruction[ lineAndcase[0] ][3]) { // THE VALUE OF S1

		case 0: // IF S1 EQUALS '00'

			lineAndcase[1] = 0;
			break;
		case 1: // IF S1 EQUALS '01'

			lineAndcase[1] = 1;
			break;
		case 2: // IF S1 EQUALS '10'

			lineAndcase[1] = 2;
			break;
		case 3: // IF S1 EQUALS '11' (INPR)
			lineAndcase[1] = 3;
			break;
		default: // THERE IS NOT REGISTER EXCEPT 00-01-10-11
			System.out.println("Değeri olmayan bir register seçtiniz");
			break;
		}

		return lineAndcase;
	}

	public void writeIntoRegister () {


		int case0, whichCase, R0Value = -1, R1Value = -1, R2Value = -1, RinValue = -1;
		boolean isEverythingOkay = true;
		case0 = determineS1()[0];
		whichCase = determineS1()[1];


		R0Value = getInteger(textfieldR0.getText());// it will return integer according to clicked button. (hexa, binary, decimal)
		R1Value = getInteger(textfieldR1.getText());// it will return integer according to clicked button. (hexa, binary, decimal)
		R2Value = getInteger(textfieldR2.getText()); // it will return integer according to clicked button. (hexa, binary, decimal)
		RinValue = getInteger(textfieldInpr.getText()); // it will return integer according to clicked button. (hexa, binary, decimal)

		System.out.println("0 : " + R0Value + " 1 : " + R1Value + " 2 : " + R2Value + " 3 : " + RinValue);

		if (isEverythingOkay) { // NE ICIN BUNU KOYDUGUNU HATIRLA LUTFEN :(  (SU AN HATIRLAMIYORUM :D)

			int value = 0; // REGISTERA YAZILACAK DEGER

			if (tableDecimalInstruction[ case0 ][2] == 0) { // IF THE VALUE OF D IS EQUALS '00'


				// ALLAHSEN FONKSİYONA AL.

				switch (tableDecimalInstruction[ case0 ][4]) { // I KNOW THE VALUE OF "S1" AND "D", SO SWITCH-CASE FOR THE VALUE OF "S2"

				case 0: // IF S2 EQUALS '00'
					textfieldR0.setText(" ");

					if (whichCase == 0) {

						if (R0Value + R0Value > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + R0Value) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (R0Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + R0Value) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (R0Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + R0Value) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (R0Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + R0Value) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					updateR0();
					break;
				case 1: // IF S2 EQUALS '01'
					textfieldR0.setText(" ");
					if (whichCase == 0) {

						if (R0Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + R1Value) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (R1Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + R1Value) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (R2Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + R1Value) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (R1Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + R1Value) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					updateR0();
					break;
				case 2: // IF S2 EQUALS '10'
					textfieldR0.setText(" ");
					if (whichCase == 0) {

						if (R0Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + R2Value) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (R1Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + R2Value) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (R2Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + R2Value) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (R2Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + R2Value) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					updateR0();
					break;
				case 3: // IF S2 EQUALS '10'
					textfieldR0.setText(" ");
					if (whichCase == 0) {

						if (R0Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + RinValue) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (R1Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + RinValue) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (R2Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + RinValue) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (RinValue + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + RinValue) % 16;
						}
						textfieldR0.setText(String.valueOf(value));
					}
					updateR0();
					break;
				default: // THERE IS NOT REGISTER EXCEPT 00-01-10-11
					System.out.println("Add işleminde yanlış Register galiba");
					break;
				}

			} else if (tableDecimalInstruction[ case0 ][2] == 1) { // IF THE VALUE OF D IS EQUALS '01'


				switch (tableDecimalInstruction[ case0 ][4]) {  // I KNOW THE VALUE OF "S1" AND "D", SO SWITCH-CASE FOR THE VALUE OF "S2"

				///////////////////
				case 0: // IF S2 EQUALS '00'
					textfieldR1.setText(" ");

					if (whichCase == 0) {

						if (R0Value + R0Value > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + R0Value) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (R0Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + R0Value) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (R0Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + R0Value) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (R0Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + R0Value) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					updateR1();
					break;
				case 1: // IF S2 EQUALS '01'
					textfieldR1.setText(" ");
					if (whichCase == 0) {

						if (R0Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + R1Value) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (R1Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + R1Value) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (R2Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + R1Value) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (R1Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + R1Value) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					updateR1();
					break;
				case 2: // IF S2 EQUALS '10'
					textfieldR1.setText(" ");
					if (whichCase == 0) {

						if (R0Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + R2Value) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (R1Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + R2Value) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (R2Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + R2Value) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (R2Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + R2Value) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					updateR1();
					break;
				case 3: // IF S2 EQUALS '10'
					textfieldR1.setText(" ");
					if (whichCase == 0) {

						if (RinValue + R0Value > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + R0Value) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (RinValue + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + R1Value) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (RinValue + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + R2Value) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (RinValue + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + RinValue) % 16;
						}
						textfieldR1.setText(String.valueOf(value));
					}
					updateR1();
					break;
				default:
					System.out.println("Add işleminde yanlış Register galiba");
					break;
				}



			} else if (tableDecimalInstruction[ case0 ][2] == 2) { // IF THE VALUE OF D IS EQUALS '10'

				switch (tableDecimalInstruction[ case0 ][4]) {  // I KNOW THE VALUE OF "S1" AND "D", SO SWITCH-CASE FOR THE VALUE OF "S2"

				case 0: // IF S2 EQUALS '00'
					textfieldR2.setText(" ");

					if (whichCase == 0) {

						if (R0Value + R0Value > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + R0Value) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (R0Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + R0Value) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (R0Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + R0Value) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (R0Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + R0Value) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					updateR2();
					break;
				case 1: // IF S2 EQUALS '01'
					textfieldR2.setText(" ");
					if (whichCase == 0) {

						if (R0Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + R1Value) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (R1Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + R1Value) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (R2Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + R1Value) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (R1Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + R1Value) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					updateR2();
					break;
				case 2: // IF S2 EQUALS '10'
					textfieldR2.setText(" ");
					if (whichCase == 0) {

						if (R0Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + R2Value) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (R1Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + R2Value) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (R2Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + R2Value) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (R2Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + R2Value) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					updateR2();
					break;
				case 3: // IF S2 EQUALS '10'
					textfieldR2.setText(" ");
					if (whichCase == 0) {

						if (R0Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + RinValue) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (R1Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + RinValue) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (R2Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + RinValue) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (RinValue + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + RinValue) % 16;
						}
						textfieldR2.setText(String.valueOf(value));
					}
					updateR2();
					break;
				default:
					System.out.println("Add işleminde yanlış Register galiba");
					break;
				}
			} else if (tableDecimalInstruction[ case0 ][2] == 3) { // IF THE VALUE OF D IS EQUALS '11' (SO OUTPUT REGISTER)

				switch (tableDecimalInstruction[ case0 ][4]) {  // I KNOW THE VALUE OF "S1" AND "D", SO SWITCH-CASE FOR THE VALUE OF "S2"

				case 0: // IF S2 EQUALS '00'
					textfieldOutr.setText(" ");

					if (whichCase == 0) {

						if (R0Value + R0Value > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + R0Value) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (R0Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + R0Value) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (R0Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + R0Value) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (R0Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + RinValue) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					updateOutr();
					break;
				case 1: // IF S2 EQUALS '01'
					textfieldOutr.setText(" ");
					if (whichCase == 0) {

						if (R0Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + R1Value) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (R1Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + R1Value) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (R2Value + R1Value > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + R1Value) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (R1Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + RinValue) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					updateOutr();
					break;
				case 2: // IF S2 EQUALS '10'
					textfieldOutr.setText(" ");
					if (whichCase == 0) {

						if (R0Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + R2Value) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (R1Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + R2Value) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (R2Value + R2Value > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + R2Value) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (R2Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + RinValue) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					updateOutr();
					break;
				case 3: // IF S2 EQUALS '10'
					textfieldOutr.setText(" ");
					if (whichCase == 0) {

						if (R0Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (R0Value + RinValue) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					else if (whichCase == 1) {

						if (R1Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (R1Value + RinValue) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					else if (whichCase == 2) {

						if (R2Value + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (R2Value + RinValue) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					else if (whichCase == 3) {

						if (RinValue + RinValue > 15) {

							textfieldOverflow.setText("1");
							value = (RinValue + RinValue) % 16;
						}
						textfieldOutr.setText(String.valueOf(value));
					}
					updateOutr();
					break;
				default:
					System.out.println("Add işleminde yanlış Register galiba");
					break;
				}
			}
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
		} else if (i == 4) {

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
		} else if (i == 5) {

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

	private void showCheckBoxDemo() {

		checkBoxPanel = new JPanel();
		chkInput = new JCheckBox("Input Flag");
		chkOutput = new JCheckBox("Output Flag");

		// chkInput.setSelected(true);
		chkInput.setBounds(800, 320, 90, 20);
		chkOutput.setBounds(800, 350, 100, 20);

		chkInput.setEnabled(false);
		chkOutput.setEnabled(false);

		chkInput.setSelected(true);

		frame.getContentPane().add(chkInput);
		frame.getContentPane().add(chkOutput);
	}

	private void createMif() throws IOException {

		String sumInstruction = "";
		boolean canWrite = true;

		//Create File
		if (mifFile == null) {
			mifFile = new File(MIF_FILE_NAME);
			mifFile.createNewFile();
			fileWriter = new FileWriter(mifFile);
		} else{
			System.out.println("Mif File already exist !!!");
		}

		//writing File
		fileWriter.write("DEPTH = "+(tableInstruction.length+1)+";				--The size of memory in words\n");
		fileWriter.write("WIDTH = 11;				--The size of data in bits\n");
		fileWriter.write("ADDRESS_RADIX = DEC;	--The radix for address values\n");
		fileWriter.write("DATA_RADIX = BIN;		--The radix for data values\n");
		fileWriter.write("CONTENT					--start of (address : data pairs)\n");
		fileWriter.write("BEGIN\n\n");



		for(int i = 0; i < tableInstruction.length; i++){
			canWrite = true;
			sumInstruction = "";
			for(int j = 0; j < tableInstruction[i].length; j++){
				if(tableInstruction[i][j].equalsIgnoreCase("-9"))
					sumInstruction += "0";
				else if(tableInstruction[i][j].equalsIgnoreCase("-1")){
					canWrite = false;
				}
				else
					sumInstruction += tableInstruction[i][j];
			}
			if(canWrite){
				if(i < 10){
					if(i == 0)
						fileWriter.write(i+"  : "+fillZeros(sumInstruction,11)+"		-- memory address : data\n");
					else fileWriter.write(i+"  : "+fillZeros(sumInstruction,11)+"\n");
				}
				else fileWriter.write(i+" : "+fillZeros(sumInstruction,11)+"\n");
			}
			else{
				if(i < 10)
					fileWriter.write(i+"  : "+fillZeros("0",11)+"\n");
				else fileWriter.write(i+" : "+fillZeros("0",11)+"\n");
			}
		}

		fileWriter.flush();
		fileWriter.close();
	}

	private String fillZeros(String str, int wordLength){

		int length = str.length();

		for(int i = 0; i < wordLength-length; i++){
			str = "0"+str;
		}

		return str;
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