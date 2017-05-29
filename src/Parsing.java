import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Parsing {

	int[][] instructionDecimal, stackDecimal;
	String[][] dataDecimal;
	int letterCounter, dataCount, dataStart, cmCounter = 0;
	char letter;
	boolean isOrgC = false; // if false, org in D

	private String[][] codeMemory;//CAL, JMP gibi işlemler gideceğimiz yerin adını tutacak. 


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

		codeMemory = new String[32][2];

		fillTableWithMinus9();

		parseCode(str);

		//		for (int i = 0; i < codeMemory.length; i++) {
		//			
		//			if (codeMemory[i][0] != null)
		//			System.out.println(i + " : " + codeMemory[i][0]);
		//			System.out.println(i + " : " + codeMemory[i][1]);
		//		}
	}

	public String[][] getCodeMemory() {
		return codeMemory;
	}

	public int[][] getInstructionDecimal () {

		return instructionDecimal;
	}

	public String[][] getDataDecimal () {

		return dataDecimal;
	}

	private static int whereisData (String[][] arr, int size, String str,int ara, boolean isString) { // Aranacak array, kaça kadar gideyim, aranan kelime, aranan integer, String mi int mi

		boolean isOkay = false;
		int q;
		if(isString){
			for (q = 0; q < size; q++) {

				if (str.equalsIgnoreCase(arr[q][0])) { // [][1] de
					isOkay = true;
					break;
				}
			}
			return (isOkay == true) ? q:-1;
		}
		else{
			for (q = 0; q < size; q++) {
				if(q == ara){
					if ((arr[q][0] != null) || (arr[q][0].length() != 0)) { // [][1] de
						isOkay = true;
						break;
					}
				}
			}
			return (isOkay == true) ? q:-1;
		}

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
				while ( (line = reader.readLine()) != null) { // satýr satýr cebimizde.

					boolean exist = false;
					int w = 0;
					int lenLine = line.length();
					while (w + 1 < lenLine) {

						if (line.substring(w, w+1).equalsIgnoreCase("%")) {

							line = line.substring(0,w);
							break;
						}
						w++;
					}
					line = line.replace("\t", " "); // Replace all tabs with 1 space.
					line = line.replace("\\s+", " "); // Replace all space with one space.
					splitSpace = line.trim().split(" ");


					if (splitSpace.length > 1 || splitSpace[0].trim().equalsIgnoreCase("HLT") || splitSpace[0].trim().equalsIgnoreCase("RET")) {


						if (splitSpace[0].trim().equalsIgnoreCase("HLT")) splitComma = null;
						else if (splitSpace[0].trim().equalsIgnoreCase("RET")) splitComma = null;
						else splitComma = splitSpace[1].split(",");

						String selection;
						if (line.split(":").length > 1) {

							splitSpace[1] = splitSpace[1].replace("\t", " "); // Replace all tabs with 1 space.
							splitSpace[1] = splitSpace[1].replace("( )+", " "); // Replace all space with one space.
							selection = splitSpace[0].trim();
						} else {

							selection = splitSpace[0].trim();
						}
						switch(selection.trim().toUpperCase()) {

						case "ORG":


							if (splitSpace.length == 3) {

								if (splitSpace[1].trim().equalsIgnoreCase("C")) {

									System.out.println("true oldumm");
									isOrgC = true;
									lineCounter = Integer.parseInt(splitSpace[2]) - 1;
									//										instructionDecimal[lineCounter][0] = 0; // Q = 0 (default)
									instructionDecimal[lineCounter][1] = -1; // -1 mean ORG
									instructionDecimal[lineCounter][2] = 1; // 1 mean D
									instructionDecimal[lineCounter][3] = Integer.parseInt(splitSpace[2]); // near integer of ORG D (int)
								}
								else if (splitSpace[1].trim().equalsIgnoreCase("D")) {
									System.out.println("false oldummm  " + lineCounter);
									isOrgC = false;
									break;
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
						case "ADD": // ADD operation.

							Add (splitComma, lineCounter); // lineCounter = According to ORG
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
						case "LD": // LOAD operation.

							Load (splitComma, lineCounter); // lineCounter = According to ORG
							break;

						case "ST": // STORAGE operation.

							Storage (splitComma, lineCounter); // lineCounter = According to ORG
							break;
						case "TSF":
							Transfer(splitComma, lineCounter); // lineCounter = According to ORG
							break; 
						case "CAL":

							System.out.println("CAL'A giriyor.");
							Cal (splitSpace, lineCounter, file); // lineCounter = According to ORG
							break;
						case "RET":

							Return(splitSpace, lineCounter);

							break;
						case "JMP":

							Jump(splitSpace, lineCounter);

							break;
						case "JMR":

							Jmr(splitSpace, lineCounter);
							break;
						case "PSH":

							break;
						case "POP":

							break;
						default:

							if (isOrgC) {

								String[] str = selection.trim().split(":");
								if (str.length == 2) {
									codeMemory[cmCounter][0] = str[0];
									codeMemory[cmCounter][1] = String.valueOf(lineCounter);
									cmCounter++;

									if (str[1].trim().length() > 1) {

										line = line.replace("\t", " "); // Replace all tabs with 1 space.
										line = line.replaceAll("( )+", " "); // Replace all space with one space.

										// COK CIRKIN OLDU FARKINDAYIM AMA IDARE EDECEZ.
										int i = 0;
										int len = line.length();
										while (i + 1 < len) {

											if (line.substring(i, i+1).equalsIgnoreCase("%")) {

												line = line.substring(0,i);
												break;
											}
											i++;
										}
										System.out.println("işte bu : " + line);
										String[] butter = line.trim().split(":");
										if (butter.length > 0) {

											butter[1] = butter[1].replace("\t", " "); // Replace all tabs with 1 space.
											butter[1] = butter[1].replace("( )+", " "); // Replace all space with one space.
											splitSpace = butter[1].trim().split(" ");
											if (splitSpace.length > 1) {

												splitComma = splitSpace[1].split(",");
											}
											System.out.println("len : " + splitComma.length);
										}


										switch (str[1].trim()) {
										case "ADD": // ADD operation.

											Add (splitComma, lineCounter); // lineCounter = According to ORG
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
										case "LD": // LOAD operation.

											Load (splitComma, lineCounter); // lineCounter = According to ORG
											break;

										case "ST": // STORAGE operation.

											Storage (splitComma, lineCounter); // lineCounter = According to ORG
											break;
										case "TSF":
											Transfer(splitComma, lineCounter); // lineCounter = According to ORG
											break; 
										case "CAL":

											System.out.println("CAL'A giriyor.");
											Cal (splitSpace, lineCounter, file); // lineCounter = According to ORG
											break;
										case "RET":

											Return(splitSpace, lineCounter);

											break;
										case "JMP":

											Jump(splitSpace, lineCounter);

											break;
										case "JMR":

											Jmr(splitSpace, lineCounter);
											break;
										case "PSH":

											break;
										case "POP":

											break;
										}
									}

								}
							}
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

			if(str[0].equalsIgnoreCase("INPR")) System.out.println("DESTINATION'A INPR'Yİ VEREMEZSİNNNNNN ADD İŞLEMİİ !!!!");
			if(str[1].equalsIgnoreCase("OUTR") || str[2].equalsIgnoreCase("OUTR")) System.out.println("DESTINATION'A INPR'Yİ VEREMEZSİNNNNNN ADD İŞLEMİİ !!!!");

			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 0;

			if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
			else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
			else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;
			else if(str[0].equalsIgnoreCase("OUTR")) instructionDecimal[line][2] = 3;

			if (str[1].equalsIgnoreCase("R0")) instructionDecimal[line][3] = 0;
			else if (str[1].equalsIgnoreCase("R1")) instructionDecimal[line][3] = 1;
			else if (str[1].equalsIgnoreCase("R2")) instructionDecimal[line][3] = 2;
			else if(str[1].equalsIgnoreCase("INPR")) instructionDecimal[line][3] = 3;

			if (str[2].equalsIgnoreCase("R0")) instructionDecimal[line][4] = 0;
			else if (str[2].equalsIgnoreCase("R1")) instructionDecimal[line][4] = 1;
			else if (str[2].equalsIgnoreCase("R2")) instructionDecimal[line][4] = 2;
			else if(str[2].equalsIgnoreCase("INPR")) instructionDecimal[line][4] = 3;

		}
		else {

			System.out.println("Hangi registerý hangi registera atayým usta, toplamda 3 tane girecen..");
		}
	}

	private void Inc (String[] str, int line) { // (EXAMPLE) - "INC R2,R0"

		if(str[0].equalsIgnoreCase("INPR")) System.out.println("DESTINATION'A INPR'Yİ VEREMEZSİNNNNNN !!!!");

		if (str.length > 1) {

			if(str[1].equalsIgnoreCase("OUTR") || str[1].equalsIgnoreCase("OUTR")) System.out.println("DESTINATION'A INPR'Yİ VEREMEZSİNNNNNN !!!!");

			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 1; // opCode = ..-0001-..

			if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
			else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
			else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;
			else if (str[0].equalsIgnoreCase("OUTR")) instructionDecimal[line][2] = 3;

			if (str[1].equalsIgnoreCase("R0")) instructionDecimal[line][3] = 0;
			else if (str[1].equalsIgnoreCase("R1")) instructionDecimal[line][3] = 1;
			else if (str[1].equalsIgnoreCase("R2")) instructionDecimal[line][3] = 2;
			//////////////////////////////////////////////
			//Burası önemli.Instruction Set paylaşımından sonra eklendi
			else if(str[1].equalsIgnoreCase("INPR")) instructionDecimal[line][2] = 3;
			///////////////////////////////////////////////
			instructionDecimal[line][4] = 0; // S2 - DON'T CARE = 0 (default)
		}
		else if (str.length == 1){
			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 1; // opCode = ..-0001-..
			if (str[0].equalsIgnoreCase("R0")){
				instructionDecimal[line][2] = 0;
				instructionDecimal[line][3] = 0;
			}
			else if (str[0].equalsIgnoreCase("R1")){
				instructionDecimal[line][2] = 1;
				instructionDecimal[line][3] = 1;
			}
			else if (str[0].equalsIgnoreCase("R2")){
				instructionDecimal[line][2] = 2;
				instructionDecimal[line][3] = 2;
			}     
			else if(str[0].equalsIgnoreCase("OUTR")){
				System.out.println("SOURCE'A OUTR'Yİ VEREMEZSİNNNNNN !!!!");
			}
			instructionDecimal[line][4] = 0; // S2 - DON'T CARE = 0 (default)
		}
		else System.out.println("Nasıl INc e gelen array sıfır oluyor arkadaşşşş !!!");
	}

	private void Dbl (String[] str, int line) { // (EXAMPLE) - "DBL R0,R2"

		if(str[0].equalsIgnoreCase("INPR")) System.out.println("DESTINATION'A INPR'Yİ VEREMEZSİNNNNNN !!!!");

		if (str.length > 1) {
			if(str[1].equalsIgnoreCase("OUTR") || str[1].equalsIgnoreCase("OUTR")) System.out.println("DESTINATION'A INPR'Yİ VEREMEZSİNNNNNN !!!!");

			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 2; // opCode = ..-0010-..

			if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
			else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
			else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;
			else if (str[0].equalsIgnoreCase("OUTR")) instructionDecimal[line][2] = 3;

			if (str[1].equalsIgnoreCase("R0")) instructionDecimal[line][3] = 0;
			else if (str[1].equalsIgnoreCase("R1")) instructionDecimal[line][3] = 1;
			else if (str[1].equalsIgnoreCase("R2")) instructionDecimal[line][3] = 2;
			else if (str[1].equalsIgnoreCase("INPR")) instructionDecimal[line][3] = 3;

			instructionDecimal[line][4] = 0; // S2 - DON'T CARE = 0 (default)
		}
		else if(str.length == 1){//Yani sadece bir tane şey girerse EXAMPLE : DBL OUTR

			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 2; // opCode = ..-0010-..

			if (str[0].equalsIgnoreCase("R0")){
				instructionDecimal[line][2] = 0;
				instructionDecimal[line][2] = 0;
			}
			else if (str[0].equalsIgnoreCase("R1")){
				instructionDecimal[line][2] = 1;
				instructionDecimal[line][2] = 1;
			}
			else if (str[0].equalsIgnoreCase("R2")){
				instructionDecimal[line][2] = 2;
				instructionDecimal[line][2] = 2;
			}
			else if (str[0].equalsIgnoreCase("OUTR")){
				System.out.println("SOURCE'A OUTR'Yİ VEREMEZSİNNNNNN !!!!");
			}
			else if (str[0].equalsIgnoreCase("INPR")){
				System.out.println("DESTINATION 'A INPR'Yİ VEREMEZSİNNNNNN !!!!");
			}
		}
	}

	private void Dbt (String[] str, int line) { // (EXAMPLE) - "DBT R2,R0"

		if(str[0].equalsIgnoreCase("INPR")) System.out.println("DESTINATION'A INPR'Yİ VEREMEZSİNNNNNN !!!!");

		if (str.length > 1) {
			if(str[1].equalsIgnoreCase("OUTR") || str[1].equalsIgnoreCase("OUTR")) System.out.println("DESTINATION'A INPR'Yİ VEREMEZSİNNNNNN !!!!");

			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 3; // opCode = ..-0010-..

			if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
			else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
			else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;
			else if (str[0].equalsIgnoreCase("OUTR")) instructionDecimal[line][2] = 3;

			if (str[1].equalsIgnoreCase("R0")) instructionDecimal[line][3] = 0;
			else if (str[1].equalsIgnoreCase("R1")) instructionDecimal[line][3] = 1;
			else if (str[1].equalsIgnoreCase("R2")) instructionDecimal[line][3] = 2;
			else if (str[1].equalsIgnoreCase("INPR")) instructionDecimal[line][3] = 3;

			instructionDecimal[line][4] = 0; // S2 - DON'T CARE = 0 (default)
		}
		else if(str.length == 1){//Yani sadece bir tane şey girerse EXAMPLE : DBL OUTR

			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 3; // opCode = ..-0010-..

			if (str[0].equalsIgnoreCase("R0")){
				instructionDecimal[line][2] = 0;
				instructionDecimal[line][2] = 0;
			}
			else if (str[0].equalsIgnoreCase("R1")){
				instructionDecimal[line][2] = 1;
				instructionDecimal[line][2] = 1;
			}
			else if (str[0].equalsIgnoreCase("R2")){
				instructionDecimal[line][2] = 2;
				instructionDecimal[line][2] = 2;
			}
			else if (str[0].equalsIgnoreCase("OUTR")){
				System.out.println("SOURCE'A OUTR'Yİ VEREMEZSİNNNNNN !!!!");
			}
			else if (str[0].equalsIgnoreCase("INPR")){
				System.out.println("DESTINATION 'A INPR'Yİ VEREMEZSİNNNNNN !!!!");
			}
		}
	}

	private void Not (String[] str, int line) { // (EXAMPLE) - "NOT R0,R2"

		if(str[0].equalsIgnoreCase("INPR")) System.out.println("DESTINATION'A INPR'Yİ VEREMEZSİNNNNNN !!!!");

		if (str.length > 1) {
			if(str[1].equalsIgnoreCase("OUTR") || str[1].equalsIgnoreCase("OUTR")) System.out.println("DESTINATION'A INPR'Yİ VEREMEZSİNNNNNN !!!!");

			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 4; // opCode = ..-0010-..

			if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
			else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
			else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;
			else if (str[0].equalsIgnoreCase("OUTR")) instructionDecimal[line][2] = 3;

			if (str[1].equalsIgnoreCase("R0")) instructionDecimal[line][3] = 0;
			else if (str[1].equalsIgnoreCase("R1")) instructionDecimal[line][3] = 1;
			else if (str[1].equalsIgnoreCase("R2")) instructionDecimal[line][3] = 2;
			else if (str[1].equalsIgnoreCase("INPR")) instructionDecimal[line][3] = 3;

			instructionDecimal[line][4] = 0; // S2 - DON'T CARE = 0 (default)
		}
		else if(str.length == 1){//Yani sadece bir tane şey girerse EXAMPLE : NOT R1

			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 4; // opCode = ..-0010-..

			if (str[0].equalsIgnoreCase("R0")){
				instructionDecimal[line][2] = 0;
				instructionDecimal[line][3] = 0;
			}
			else if (str[0].equalsIgnoreCase("R1")){
				instructionDecimal[line][2] = 1;
				instructionDecimal[line][3] = 1;
			}
			else if (str[0].equalsIgnoreCase("R2")){
				instructionDecimal[line][2] = 2;
				instructionDecimal[line][3] = 2;
			}
			else if (str[0].equalsIgnoreCase("OUTR")){
				System.out.println("SOURCE'A OUTR'Yİ VEREMEZSİNNNNNN !!!!");
			}
			/* OZENC KANKA BUNE, USTTEKI ILE AYNI? BIRI YANLISTIR.
			else if (str[0].equalsIgnoreCase("INPR")){
				System.out.println("DESTINATION 'A INPR'Yİ VEREMEZSİNNNNNN !!!!");
			}
			 */
		}
	}

	private void And (String[] str, int line) { // (EXAMPLE) - "AND R1,R0,R2"

		if (str.length == 3) {
			instructionDecimal[line][0] = -5; // Q = X - (DON'T CARE)
			instructionDecimal[line][1] = 5; // // opCode = ..-0101-..

			if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
			else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
			else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;
			else if (str[0].equalsIgnoreCase("OUTR")) instructionDecimal[line][2] = 3;

			if (str[1].equalsIgnoreCase("R0")) instructionDecimal[line][3] = 0;
			else if (str[1].equalsIgnoreCase("R1")) instructionDecimal[line][3] = 1;
			else if (str[1].equalsIgnoreCase("R2")) instructionDecimal[line][3] = 2;
			else if (str[1].equalsIgnoreCase("INPR")) instructionDecimal[line][2] = 3;

			if (str[2].equalsIgnoreCase("R0")) instructionDecimal[line][4] = 0;
			else if (str[2].equalsIgnoreCase("R1")) instructionDecimal[line][4] = 1;
			else if (str[2].equalsIgnoreCase("R2")) instructionDecimal[line][4] = 2;
			else if (str[2].equalsIgnoreCase("INPR")) instructionDecimal[line][2] = 3;
		}
		else {
			System.out.println("Hangi registerý hangi registera atayým usta, toplamda 3 tane girecen..");
		}
	}

	private void Load (String[] str, int line) {

		if (str.length == 2) {

			if (str[0].trim().equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
			else if (str[0].trim().equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
			else if (str[0].trim().equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;
			else if (str[0].trim().equalsIgnoreCase("OUTR")) instructionDecimal[line][2] = 3;

			if (str[1].substring(0, 1).equals("#")) {

				instructionDecimal[line][0] = 1; // Q = 1
				instructionDecimal[line][1] = 6;

				int butterfly = Integer.parseInt(str[1].substring(1));

				instructionDecimal[line][3] = 0; // 0 attým, çünkü decimal, binary çevirirken içini güncellicem.
				instructionDecimal[line][4] = butterfly; // decimali s1s2 diye bölmeden attým.
			}
			else if (str[1].substring(0, 1).equals("@")) {

				instructionDecimal[line][0] = 0; // Q = 0
				instructionDecimal[line][1] = 6;

				int butterfly = 0;
				if ((butterfly = whereisData(dataDecimal,16, str[1].substring(1), 0, true)) != -1) {

					instructionDecimal[line][3] = 0; // 0 attým, çünkü decimal, binary çevirirken içini güncellicem.
					instructionDecimal[line][4] = Integer.parseInt(dataDecimal[butterfly][1]); // decimali s1s2 diye bölmeden attým.
				}
				else if (isNumber(str[1].substring(1)) &&(butterfly = whereisData(dataDecimal,16, str[1].substring(1), Integer.parseInt(str[1].substring(1)), false)) != -1) {

					instructionDecimal[line][3] = 0; // 0 attým, çünkü decimal, binary çevirirken içini güncellicem.
					instructionDecimal[line][4] = Integer.parseInt(dataDecimal[butterfly][1]); // decimali s1s2 diye bölmeden attým.
				}
				else {
					System.out.println(str[1].substring(1) + " diye bir deðer yok.");
				}
			}
		}
	}

	private void Storage(String[] str, int line) {

		if (str.length == 2) {

			//Burada işler biraz daha farklı
			//Bana gelen destination u source a atıyorum
			//Source u ise destinatinationa atıyorum.
			//Biraz bak anlarsın :))

			/*
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
			 */
			if (str[1].substring(0, 1).equals("@")) {

				instructionDecimal[line][0] = 0; // Q = 0;Address varrr
				instructionDecimal[line][1] = 7;

				if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
				else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
				else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;
				else if (str[0].equalsIgnoreCase("INPR")) instructionDecimal[line][2] = 3;


				int butterfly = 0;
				if ((butterfly = whereisData(dataDecimal,16, str[1].substring(1), 0, true)) != -1) {
					instructionDecimal[line][3] = 0; // 0 attým, çünkü decimal, binary çevirirken içini güncellicem.
					instructionDecimal[line][4] = butterfly;//Integer.parseInt(dataDecimal[butterfly][1]); // decimali s1s2 diye bölmeden attým.
				}
				else if(isNumber(str[1].substring(1)) && (butterfly = whereisData(dataDecimal,16, str[1].substring(1), Integer.parseInt(str[1].substring(1)), false)) != -1){
					System.out.println("bakkkkkkkkkkkkkkkkkkkkkkkk : "+Integer.parseInt(str[1].substring(1))+" line  : "+line);
					instructionDecimal[line][3] = 0; // 0 attým, çünkü decimal, binary çevirirken içini güncellicem.
					instructionDecimal[line][4] = butterfly;//Integer.parseInt(dataDecimal[butterfly][1]); // decimali s1s2 diye bölmeden attým
				}
				else {
					System.out.println(str[1].substring(1) + " diye bir deðer yok.");
				}
			} 
			else{//iki tarafında register olma durumu.EXAMPLE : "ST INPR,OUTR"

				instructionDecimal[line][0] = 1; // Q = 1;Address li birşeyler yok.
				instructionDecimal[line][1] = 7;

				if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
				else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
				else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;
				else if (str[0].equalsIgnoreCase("INPR")) instructionDecimal[line][2] = 3;

				if (str[1].equalsIgnoreCase("R0")) instructionDecimal[line][3] = 0;
				else if (str[1].equalsIgnoreCase("R1")) instructionDecimal[line][3] = 1;
				else if (str[1].equalsIgnoreCase("R2")) instructionDecimal[line][3] = 2;
				else if (str[1].equalsIgnoreCase("OUTR")) instructionDecimal[line][3] = 3;

				instructionDecimal[line][4] = 0;
			}
		}

	}

	private void Transfer(String[] str, int line){// (EXAMPLE) - "TSF OUTR,INPR"

		if(str.length > 1){
			instructionDecimal[line][0] = 0; // Q = 0;
			instructionDecimal[line][1] = 9;

			if (str[0].equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
			else if (str[0].equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
			else if (str[0].equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;
			else if (str[0].equalsIgnoreCase("OUTR")) instructionDecimal[line][2] = 3;

			if (str[1].equalsIgnoreCase("R0")) instructionDecimal[line][3] = 0;
			else if (str[1].equalsIgnoreCase("R1")) instructionDecimal[line][3] = 1;
			else if (str[1].equalsIgnoreCase("R2")) instructionDecimal[line][3] = 2;
			else if (str[1].equalsIgnoreCase("OUTR")) instructionDecimal[line][3] = 3;

			instructionDecimal[line][4] = 0;
		}
		else{
			System.out.println("Transferde 2 den fazla eleman olmalı");
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

								whichLine = Integer.parseInt(splitSpace[2]);
							}
						}
						if (splitColon.length == 2) {

							if (splitColon[0].equalsIgnoreCase(expression[0].trim())) { // Ahada found demektir.

								System.out.println("a:" + whichLine + " b:" + line);
								instructionDecimal[line][0] = 0; // Q = 0 (default)
								instructionDecimal[line][1] = 10; // Opcode of 'RET' operation
								instructionDecimal[line][2] = 0; 
								instructionDecimal[line][3] = 0; 
								instructionDecimal[line][4] = whichLine; //Where is SUB = whichLine
								System.out.println("wLine : " + whichLine);
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

	private void Return(String[] str, int line){

		if(str.length == 1){


			instructionDecimal[line][0] = 0; // Q = 0 (default)
			instructionDecimal[line][1] = 11; // Opcode of 'RET' operation
			instructionDecimal[line][2] = 0; 
			instructionDecimal[line][3] = 0; 
			instructionDecimal[line][4] = 0; 
		}
		else{
			System.out.println("RET Array i nasıl 1 den uzun olabilir !!!");
		}
	}

	private void Jump(String[] str, int line){

		if(str.length == 2){// "JMP 23" ya da "JMP MUL" -- Q=0, DIREK ZIPLA

			if(isNumber(str[1])){// "JMP 23"

				System.out.println("JMP.len = 2 GIRDI : " + str[0]);
				instructionDecimal[line][0] = 0; // Q = 0 (default)
				instructionDecimal[line][1] = 12; // Opcode of 'RET' operation
				instructionDecimal[line][2] = 0; 
				instructionDecimal[line][3] = 0; 
				instructionDecimal[line][4] = Integer.parseInt(str[1]);

//				codeMemory[line][0] = "JMP"; // BUNU NEDEN ATIYORSUN KI KANKI? BU SAYLANMAZ. SADECE CAL FALAN OLUNCA GITTIGIMIZ YERLERIN DEGERINI ATSAK ENOUGH.
//				codeMemory[line][1] = String.valueOf(Integer.parseInt(str[0]));
			}
			else{//"JMP MUL"

				int butterfly = whereisData(dataDecimal, 16, str[1], 0, true);
				instructionDecimal[line][0] = 0; // Q = 0 (default)
				instructionDecimal[line][1] = 12; // Opcode of 'RET' operation
				instructionDecimal[line][2] = 0; 
				instructionDecimal[line][3] = 0;
				instructionDecimal[line][4] = Integer.parseInt(dataDecimal[butterfly][1]); // S4 TEKI ADRESS' E ZIPLA
			}
		}
		else if(str.length == 3){// "JMP 23 V"
			if(isNumber(str[1]) && str[2].equalsIgnoreCase("V")){//str[0] must be integer.str[1] must be "V" means OverFlow.

				instructionDecimal[line][0] = 1; // Q = 0 (default)
				instructionDecimal[line][1] = 12; // Opcode of 'RET' operation
				instructionDecimal[line][2] = 0; 
				instructionDecimal[line][3] = 0;
				instructionDecimal[line][4] = Integer.parseInt(str[1]);
				System.out.println("JMP.len = 3 GIRDI : " + str[1]);
			} else if (!isNumber(str[1]) && str[2].equalsIgnoreCase("V")) { // BUNU YAPTIGI YAZILMAMIS, OPSIYON OLARAK EKLEDIM.

				int butterfly = whereisData(dataDecimal, 16, str[1], 0, true);
				instructionDecimal[line][0] = 1; // Q = 0 (default)
				instructionDecimal[line][1] = 12; // Opcode of 'RET' operation
				instructionDecimal[line][2] = 0; 
				instructionDecimal[line][3] = 0;
				instructionDecimal[line][4] = Integer.parseInt(dataDecimal[butterfly][1]);
			}
		}

	}

	private void Jmr (String[] str, int line) {

		
		int butterfly;
		
		int a = Integer.parseInt("1011",2);

		if(str.length == 2) { // JMR 2, JMR -2, ...

//			if (isNumber(str[1])) { // negatif sayıları almıyor almıyor almıyor :( :( :(
				
				butterfly = Integer.parseInt(str[1]);
				instructionDecimal[line][0] = 0; // Q = 0 (default)
				instructionDecimal[line][1] = 13; // Opcode of 'RET' operation
				instructionDecimal[line][2] = 0; 
				instructionDecimal[line][3] = 0;
				instructionDecimal[line][4] = butterfly;
//			}

		} else if (str.length == 3) { // JMR A HEX, JMR 0110 BIN, ...

			if (str[2].equalsIgnoreCase("HEX")) {

				butterfly = Integer.parseInt(str[3],16);
				instructionDecimal[line][0] = 0; // Q = 0 (default)
				instructionDecimal[line][1] = 13; // Opcode of 'RET' operation
				instructionDecimal[line][2] = 0; 
				instructionDecimal[line][3] = 0;
				instructionDecimal[line][4] = butterfly;
				
			} else if (str[2].equalsIgnoreCase("BIN")) {

				if (str[1].substring(0,1).equalsIgnoreCase("1")) { // NEGATIVE ?


					butterfly = Integer.parseInt(str[3],2);
					instructionDecimal[line][0] = 0; // Q = 0 (default)
					instructionDecimal[line][1] = 13; // Opcode of 'RET' operation
					instructionDecimal[line][2] = 0; 
					instructionDecimal[line][3] = 0;
					instructionDecimal[line][4] = 16 - butterfly;
				} else { // POSITIVE ?
					
					butterfly = Integer.parseInt(str[3],2);
					instructionDecimal[line][0] = 0; // Q = 0 (default)
					instructionDecimal[line][1] = 13; // Opcode of 'RET' operation
					instructionDecimal[line][2] = 0; 
					instructionDecimal[line][3] = 0;
					instructionDecimal[line][4] = butterfly;

				}
			} else if (str[2].equalsIgnoreCase("DEC")) {
				
				if (isNumber(str[1])) {
					
					instructionDecimal[line][0] = 0; // Q = 0 (default)
					instructionDecimal[line][1] = 13; // Opcode of 'RET' operation
					instructionDecimal[line][2] = 0; 
					instructionDecimal[line][3] = 0;
					instructionDecimal[line][4] = Integer.parseInt(str[3]);
				} else {
					
					System.out.println("Please enter a digit for JMR operation.");
				}
			}
			//			int a = Integer.toBinaryString(i)
		}

	}

	private void fillTableWithMinus9 () {


		for (int i = 0; i < instructionDecimal.length; i++) {

			for (int j = 0; j < instructionDecimal[1].length; j++) {

				instructionDecimal[i][j] = -9;
			}
		}
	}

	public boolean isNumber(String string)
	{
		boolean isno=false;
		for(int i=0; i<string.length();i++) // GELEN STRING'IN HER ELEMANINI TEK TEK (CHAR CHAR) DONECEK FOR LOOP.
		{
			isno = false;
			char ch = string.charAt(i);
			int x = (int) ch;
			for(int j=48; j<=57;j++) // STRING'IN CEBE ATTIGIMIZ TEK CHARACTERI 0 - 9 ARASINDA MI?
			{
				if(x==j) // 0 - 9 ARASINDA HER HANGI BIR SAYIYA ESITSE TRUE.
				{
					isno=true;
				}
			}
			if(isno==false) // 0 - 9 ARASINDAKİ HICBIR SAYIYA ESIT DEGILSE, DEMEK KI ICINDE INTEGER HARICI BIR SEY VAR.
			{
				break;
			}
		}
		return isno;
	}

}