import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.sun.org.apache.xerces.internal.impl.dv.xs.DecimalDV;

public class Parsing {

	int[][] instructionDecimal, stackDecimal;
	String[][] dataDecimal;
	int letterCounter, dataCount, dataStart;
	char letter;

	// IMPORTANT THINGS THAT YOU MUST REMEMBER.

	// IF (table[i] == -5) it means this value don't care.
	// table[0] means - I
	// table[1] means - Opcode
	// table[2] means - D
	// table[3] means - S1
	// table[4] means - S2

	// #define ORG -1
	// table[][0] = 0 - Q = 0 (default)
	// table[][1] = ORG - (-1)
	// table[][2] = C (0) or D (1)
	// table[][3] = 25 (The number that program counter start with this value.)

	// dataDecimal[][0] = Value of the name.
	// dataDecimal[][1] = Value of the decimal.
	// dataDecimal[][2] = Adress of the value.

	///////////////////////////////////////////


	Parsing (String str) {

		instructionDecimal = new int[32][5];
		stackDecimal = new int [16][5];
		dataDecimal = new String[16][3];

		fillTableWithMinus9();

		parseCode(str);
	}

	public int[][] getInstructionDecimal () {

		return instructionDecimal;
	}

	public String[][] getDataDecimal () {

		return dataDecimal;
	}

	private static int whereisData (String[][] arr, int size, String str) { // Aranacak array, kaça kadar gideyim, aranan kelime

		boolean isOkay = false;
		int q;
		for (q = 0; q < size; q++) {

			if (str.equalsIgnoreCase(arr[q][0])) { // [][1] de
				isOkay = true;
				break;
			}
		}
		return (isOkay == true) ? q:-1;
	}

	private void parseCode (String FileName) {


		BufferedReader reader = null;
		BufferedReader butterflyReader = null;
		String[] splitSpace;
		String[] splitComma;
		String[] splitOrg;
		String[][] data = new String[30][3]; // Þimdilik 30 yaptým, istersek arttýrabiliriz.

		data[29][1] = "5";
		File file = new File(FileName);

		if (file != null) {
			try {

				reader = new BufferedReader(new FileReader(file));
				butterflyReader = new BufferedReader(new FileReader(file));

				String line = null;
				String butterflyLine = null;
				int lineCount = 0;

				String[] dataArray;

				dataCount = 0;
				dataStart = 0;
				while ((butterflyLine = butterflyReader.readLine()) != null) {


					butterflyLine = butterflyLine.trim(); // BAYA IMPORTANT
					dataArray = butterflyLine.split(" ");

					if (dataArray.length == 3) {

						if (dataArray[0].equalsIgnoreCase("ORG") && dataArray[1].equalsIgnoreCase("D")) {

							
							
							// dataArray[2] integer or something else control ????
							dataStart = Integer.parseInt(dataArray[2]);
							dataCount = Integer.parseInt(dataArray[2]);

							
							while ((butterflyLine = butterflyReader.readLine()) != null) {
								
								
								if (butterflyLine.trim().equalsIgnoreCase("END")) break;
								
								String[] str = butterflyLine.trim().split(":");
								
								if (str.length == 2) {

									String butterfly = str[0];
									str = str[1].trim().split(" "); // Again split operation according to space.

									
									if (str.length == 2) {

										System.out.println("Parse ta str[1].split( )[0] : " + str[0] + " str[1].split( )[1] : " + str[1]);
										
										if (str[0].equalsIgnoreCase("HEX")) {

											dataDecimal[ dataCount ][0] = butterfly;
											dataDecimal[ dataCount ][1] = String.valueOf(Integer.parseInt(str[1], 16));
											dataDecimal[ dataCount ][2] = String.valueOf(dataStart);

										} else if (str[0].equalsIgnoreCase("DEC")) {

											dataDecimal[ dataCount ][0] = butterfly;
											dataDecimal[ dataCount ][1] = str[1];
											dataDecimal[ dataCount ][2] = String.valueOf(dataStart);

										} else if (str[0].equalsIgnoreCase("BIN")) {

											dataDecimal[ dataCount ][0] = butterfly;
											dataDecimal[ dataCount ][1] = String.valueOf(Integer.parseInt(str[1], 2));
											dataDecimal[ dataCount ][2] = String.valueOf(dataStart);

										} else System.out.println("HEX, DEC, BIN dýþýnda bilinmeyen bir argüman girdiniz.");

									}
									else System.out.println(str[1] + " : , Hem türü, hem sayýsý, hemde bir þey daha var ? O bir þey ne");
									dataCount++;
									dataStart++;
								}
								else {
									System.out.println("ORG D'nin içinde 1 den fazla ':' var");
								}

							}

						}
					}
				}
				int lineCounter = 0;
				boolean isCodeFinish = false;
				while ( (line = reader.readLine()) != null ) { // satýr satýr cebimizde.

					boolean exist = false;
					line = line.replace("\t", " "); // Replace all tabs with 1 space.
					line = line.replace("\\s+", " "); // Replace all space with one space.
					splitSpace = line.trim().split(" ");


					if (splitSpace.length > 1 || splitSpace[0].trim().equalsIgnoreCase("HLT")) {

						if (splitSpace[0].trim().equalsIgnoreCase("HLT")) splitComma = null;
						else splitComma = splitSpace[1].split(",");

						String selection;
						if (line.split(":").length > 1) {

							splitSpace[1] = splitSpace[1].replace("\t", " "); // Replace all tabs with 1 space.
							splitSpace[1] = splitSpace[1].replace("\\s+", " "); // Replace all space with one space.
							selection = splitSpace[1].trim();
						} else {

							selection = splitSpace[0].trim();
						}
						switch(selection.toUpperCase()) {

						case "ORG":


							if (splitSpace.length == 3) {


								if (splitSpace[1].trim().equalsIgnoreCase("C")) {

									lineCounter = Integer.parseInt(splitSpace[2]) - 1;
									//										instructionDecimal[lineCounter][0] = 0; // Q = 0 (default)
									instructionDecimal[lineCounter][1] = -1; // -1 mean ORG
									instructionDecimal[lineCounter][2] = 1; // 1 mean D
									instructionDecimal[lineCounter][3] = Integer.parseInt(splitSpace[2]); // near integer of ORG D (int)
								}
							}
							break;
						case "HLT":

							instructionDecimal[lineCounter][0] = 0; // Q = 0 - (DEFAULT)
							instructionDecimal[lineCounter][1] = 8; // OPCODE = 1000
							instructionDecimal[lineCounter][2] = 0; // D = 00 - (DEFAULT)
							instructionDecimal[lineCounter][3] = 0; // S1 = 00 - (DEFAULT)
							instructionDecimal[lineCounter][4] = 0; // S2 = 00 - (DEFAULT)
							isCodeFinish = true;
							break;
						case "INC":

							Inc (splitComma, lineCounter); // lineCounter = According to ORG
							break;
						case "DBL":

							Dbl (splitComma, lineCounter); // lineCounter = According to ORG
							break;
						case "DBT":

							Dbt (splitComma, lineCounter); // lineCounter = According to ORG
							break;
						case "NOT": // NOT OPERATION

							Not (splitComma, lineCounter); // lineCounter = According to ORG
							break;
						case "AND":

							And (splitComma, lineCounter); // lineCounter = According to ORG
							break;
						case "TSF":

							break;
						case "CAL":

							System.out.println("CAL'A giriyor.");
							Cal (splitSpace, lineCounter, file); // lineCounter = According to ORG
							break;
						case "RET":

							break;
						case "JMP":

							break;
						case "JMR":

							break;
						case "PSH":

							break;
						case "POP":

							break;
						case "LD": // LOAD operation.

							Load (splitComma, lineCounter); // lineCounter = According to ORG
							break;
						case "ADD": // ADD operation.

							Add (splitComma, lineCounter); // lineCounter = According to ORG
							break;
						case "ST": // STORAGE operation.

							Storage (splitComma, lineCounter); // lineCounter = According to ORG
							break;
						default:
						}
						lineCounter++;
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void Add (String[] str, int line) { // (EXAMPLE) - "ADD R2,R0,R1"



		if (str.length == 3) {

			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 0;

			if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
			else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
			else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;

			if (str[1].equalsIgnoreCase("R0")) instructionDecimal[line][3] = 0;
			else if (str[1].equalsIgnoreCase("R1")) instructionDecimal[line][3] = 1;
			else if (str[1].equalsIgnoreCase("R2")) instructionDecimal[line][3] = 2;

			if (str[2].equalsIgnoreCase("R0")) instructionDecimal[line][4] = 0;
			else if (str[2].equalsIgnoreCase("R1")) instructionDecimal[line][4] = 1;
			else if (str[2].equalsIgnoreCase("R2")) instructionDecimal[line][4] = 2;
		}
		else {

			System.out.println("Hangi registerý hangi registera atayým usta, toplamda 3 tane girecen..");
		}
	}

	private void Inc (String[] str, int line) { // (EXAMPLE) - "INC R2,R0"

		if (str.length > 1) {

			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 1; // opCode = ..-0001-..

			if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
			else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
			else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;

			if (str[1].equalsIgnoreCase("R0")) instructionDecimal[line][3] = 0;
			else if (str[1].equalsIgnoreCase("R1")) instructionDecimal[line][3] = 1;
			else if (str[1].equalsIgnoreCase("R2")) instructionDecimal[line][3] = 2;

			instructionDecimal[line][4] = 0; // S2 - DON'T CARE = 0 (default)
		}
	}

	private void Dbl (String[] str, int line) { // (EXAMPLE) - "DBL R0,R2"

		if (str.length > 1) {

			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 2; // opCode = ..-0010-..

			if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
			else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
			else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;

			if (str[1].equalsIgnoreCase("R0")) instructionDecimal[line][3] = 0;
			else if (str[1].equalsIgnoreCase("R1")) instructionDecimal[line][3] = 1;
			else if (str[1].equalsIgnoreCase("R2")) instructionDecimal[line][3] = 2;

			instructionDecimal[line][4] = 0; // S2 - DON'T CARE = 0 (default)
		}
	}

	private void Dbt (String[] str, int line) { // (EXAMPLE) - "DBT R2,R0"

		if (str.length > 1) {

			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 3; // opCode = ..-0011-..

			if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
			else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
			else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;

			if (str[1].equalsIgnoreCase("R0")) instructionDecimal[line][3] = 0;
			else if (str[1].equalsIgnoreCase("R1")) instructionDecimal[line][3] = 1;
			else if (str[1].equalsIgnoreCase("R2")) instructionDecimal[line][3] = 2;

			instructionDecimal[line][4] = 0; // S2 - DON'T CARE = 0 (default)
		}
	}

	private void Not (String[] str, int line) { // (EXAMPLE) - "NOT R0,R2"

		if (str.length > 1) {

			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 4; // opCode = ..-0100-..

			if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
			else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
			else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;

			if (str[1].equalsIgnoreCase("R0")) instructionDecimal[line][3] = 0;
			else if (str[1].equalsIgnoreCase("R1")) instructionDecimal[line][3] = 1;
			else if (str[1].equalsIgnoreCase("R2")) instructionDecimal[line][3] = 2;

			instructionDecimal[line][4] = 0; // S2 - DON'T CARE = 0 (default)
		}
	}

	private void And (String[] str, int line) { // (EXAMPLE) - "AND R1,R0,R2"
		
		if (str.length == 3) {

			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 5; // // opCode = ..-0101-..

			if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
			else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
			else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;

			if (str[1].equalsIgnoreCase("R0")) instructionDecimal[line][3] = 0;
			else if (str[1].equalsIgnoreCase("R1")) instructionDecimal[line][3] = 1;
			else if (str[1].equalsIgnoreCase("R2")) instructionDecimal[line][3] = 2;

			if (str[2].equalsIgnoreCase("R0")) instructionDecimal[line][4] = 0;
			else if (str[2].equalsIgnoreCase("R1")) instructionDecimal[line][4] = 1;
			else if (str[2].equalsIgnoreCase("R2")) instructionDecimal[line][4] = 2;
		}
		else {

			System.out.println("Hangi registerý hangi registera atayým usta, toplamda 3 tane girecen..");
		}
	}
	
	private void Cal (String[] str, int line,File f) {


		if (str.length > 1) {
			instructionDecimal[line][0] = 0; // Q = 0 (default)
			instructionDecimal[line][1] = 10; // Opcode of 'CAL' operation

			BufferedReader subProgramFinder;
			try {

				subProgramFinder =  new BufferedReader(new FileReader(f));

				String butterflyLine;
				String[] splitColon;
				String[] splitSpace;
				str[1] = str[1].replace("\t", " "); // Replace all tabs with 1 space.
				str[1] = str[1].trim().replace("\\s+", " "); // Replace all space with one space.
				String[] expression = str[1].trim().split(" "); // Split by (" ");

				int whichLine = 0;
				while ((butterflyLine = subProgramFinder.readLine()) != null) {

					splitColon = butterflyLine.trim().split(":"); // split by colon (:)
					splitSpace = butterflyLine.trim().split(" ");

					if (splitSpace.length > 1) {

						if (splitSpace.length == 3) {


							if (splitSpace[1].trim().equalsIgnoreCase("C")) {

								whichLine = Integer.parseInt(splitSpace[2]) - 1;
							}
						}
						if (splitColon.length == 2) {

							if (splitColon[0].equalsIgnoreCase(expression[0].trim())) { // Ahada found demektir.

								System.out.println("a:" + whichLine + " b:" + line);
							}
						}
						whichLine++;
					}

				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	private void Storage(String[] str, int line) {

		if (str.length == 2) {

			if (str[1].substring(0, 1).equals("#")) {

				instructionDecimal[line][0] = 1; // Q = 1;
				instructionDecimal[line][1] = 7;

				if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
				else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
				else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;

				int butterfly = Integer.parseInt(str[1].substring(1));

				instructionDecimal[line][3] = 0; // 0 attým, çünkü decimal, binary çevirirken içini güncellicem.
				instructionDecimal[line][4] = butterfly; // decimali s1s2 diye bölmeden attým.
			}
			else if (str[1].substring(0, 1).equals("@")) {


				int butterfly = 0;
				if ((butterfly = whereisData(dataDecimal,16, str[1].substring(1))) != -1) {

					instructionDecimal[line][0] = 0; // Q = 0;
					instructionDecimal[line][1] = 7;

					if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
					else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
					else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;

					instructionDecimal[line][3] = 0; // 0 attým, çünkü decimal, binary çevirirken içini güncellicem.
					instructionDecimal[line][4] = Integer.parseInt(dataDecimal[butterfly][1]); // decimali s1s2 diye bölmeden attým.
				}
				else {

					System.out.println(str[1].substring(1) + " diye bir deðer yok.");
				}
			}
		}

	}

	private void Load (String[] str, int line) {

		if (str.length == 2) {

			if (str[1].substring(0, 1).equals("#")) {

				instructionDecimal[line][0] = 1; // Q = 1
				instructionDecimal[line][1] = 6;

				if (str[0].trim().equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
				else if (str[0].trim().equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
				else if (str[0].trim().equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;

				int butterfly = Integer.parseInt(str[1].substring(1));

				instructionDecimal[line][3] = 0; // 0 attým, çünkü decimal, binary çevirirken içini güncellicem.
				instructionDecimal[line][4] = butterfly; // decimali s1s2 diye bölmeden attým.
			}
			else if (str[1].substring(0, 1).equals("@")) {


				int butterfly = 0;
				if ((butterfly = whereisData(dataDecimal,16, str[1].substring(1))) != -1) {

					instructionDecimal[line][0] = 0; // Q = 0
					instructionDecimal[line][1] = 6;

					if (str[0].trim().equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
					else if (str[0].trim().equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
					else if (str[0].trim().equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;

					instructionDecimal[line][3] = 0; // 0 attým, çünkü decimal, binary çevirirken içini güncellicem.
					instructionDecimal[line][4] = Integer.parseInt(dataDecimal[butterfly][1]); // decimali s1s2 diye bölmeden attým.
				}
				else {


					System.out.println(str[1].substring(1) + " diye bir deðer yok.");
				}
			}
		}
	}

	private void fillTableWithMinus9 () {


		for (int i = 0; i < instructionDecimal.length; i++) {

			for (int j = 0; j < instructionDecimal[1].length; j++) {

				instructionDecimal[i][j] = -9;
			}
		}
	}

}