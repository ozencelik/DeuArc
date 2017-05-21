import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.sun.org.apache.xerces.internal.impl.dv.xs.DecimalDV;

public class Parsing {


	int[][] instructionDecimal, stackDecimal;
	String[][] dataDecimal;
	int letterCounter, dataCount;
	char letter;

	// IMPORTANT THÝNGS THAT YOU MUST REMEMBER.
	
	// table[0] means - I
	// table[1] means - Opcode
	// table[2] means - D
	// table[3] means - S1
	// table[4] means - S2

	// #define ORG -1
	// table[][1] = ORG (-1)
	// table[][2] = C (0) or D (1)
	// table[][3] = 25 (Hangi satýrdan baþlýyorsa o sayý)
	
	// dataDecimal[][0] = Value of the name.
	// dataDecimal[][1] = Value of the decimal.
	
	///////////////////////////////////////////


	Parsing (String str) {

		instructionDecimal = new int[32][5];
		stackDecimal = new int [16][5];
		dataDecimal = new String[16][2];
		
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
				while ((butterflyLine = butterflyReader.readLine()) != null) {
					
					
					butterflyLine = butterflyLine.trim(); // BAYA IMPORTANT
					dataArray = butterflyLine.split(" ");
					
					if (dataArray.length == 3) {
						
						if (dataArray[0].equalsIgnoreCase("ORG") && dataArray[1].equalsIgnoreCase("D")) {
							
							// dataArray[2] integer or something else control ????
							
							
							
							
							while ((butterflyLine = butterflyReader.readLine()) != null) {
								
								if (butterflyLine.equalsIgnoreCase("END")) break;
									
								String[] str = butterflyLine.trim().split(":");
								if (str.length == 2) {

									
									dataDecimal[dataCount][0] = str[0];
									
									str = str[1].trim().split(" "); // Again split operation according to space.
									
									System.out.println("str[0] : " + str[0] + " - str[1] : " + str[1] + " dataCount : " + dataCount);
									if (str.length == 2) {
										
										
										if (str[0].equalsIgnoreCase("HEX")) {
											
											dataDecimal[dataCount][1] = String.valueOf(Integer.parseInt(str[1], 16));
											
										} else if (str[0].equalsIgnoreCase("DEC")) {
											
											dataDecimal[dataCount][1] = str[1];
											
										} else if (str[0].equalsIgnoreCase("BIN")) {
											
											dataDecimal[dataCount][1] = String.valueOf(Integer.parseInt(str[1], 2));
											
										} else System.out.println("HEX, DEC, BIN dýþýnda bilinmeyen bir argüman girdiniz.");
										
									}
									else System.out.println(str[1] + " : , Hem türü, hem sayýsý, hemde bir þey daha var ? O bir þey ne");
									dataCount++;
								}
								else {
									System.out.println("ORG D'nin içinde 1 den fazla ':' var");
								}
								
							}
							
						}
					}
				}
				int lineCounter = 0;
				while ((line = reader.readLine()) != null) { // satýr satýr cebimizde.
					
					boolean exist = false;
					splitSpace = line.split(" ");
					
					if (splitSpace.length > 1) {
						
						splitComma = splitSpace[1].split(",");
						
						
							switch(splitSpace[0].trim().toUpperCase()) {
							
							case "ORG":
								
								
								if (splitSpace.length == 3) {

									
									if (splitSpace[1].trim().equalsIgnoreCase("C")) {
										
										lineCounter = Integer.parseInt(splitSpace[2]) - 1;
										instructionDecimal[lineCounter][0] = 0; // Q = 0 (default)
										instructionDecimal[lineCounter][1] = -1; // -1 mean ORG
										instructionDecimal[lineCounter][2] = 1; // 1 mean D
										instructionDecimal[lineCounter][3] = Integer.parseInt(splitSpace[2]); // near integer of ORG D (int)
									}
								}
								break;
							case "HLT":
								
								break;
							case "INC":
								
								break;
							case "DBL":
								
								break;
							case "DBT":
								
								break;
							case "NOT":
								
								break;
							case "AND":
								
								break;
							case "TSF":
								
								break;
							case "CAL":
								
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
								
								Load(splitComma, lineCounter); // lineCounter = According to ORG
								break;
							case "ADD": // ADD operation.
								
								Add(splitComma, lineCounter); // lineCounter = According to ORG
								break;
							case "ST": // STORAGE operation.
								
								Storage(splitComma, lineCounter); // lineCounter = According to ORG
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
	
	public void Storage(String[] str, int line) {
		
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
				if ((butterfly = whereisData(dataDecimal,dataCount, str[1].substring(1))) != -1) {

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
	
	public void Add (String[] str, int line) {
		
		
		
		if (str.length == 3) {
			
			System.out.println("lineCounter : " + line);
			System.out.println("ADD ~ 0 : " + str[0] + " - 1 : " + str[1] + " - 2 : " + str[2]);
			instructionDecimal[line][0] = 0; // Q = 0 (default)
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
	
	public void Load (String[] str, int line) {
		
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
				if ((butterfly = whereisData(dataDecimal,dataCount, str[1].substring(1))) != -1) {

					instructionDecimal[line][0] = 0; // Q = 0
					instructionDecimal[line][1] = 6;
					
					if (str[0].trim().equalsIgnoreCase("R0")) instructionDecimal[line][2] = 0;
					else if (str[0].trim().equalsIgnoreCase("R1")) instructionDecimal[line][2] = 1;
					else if (str[0].trim().equalsIgnoreCase("R2")) instructionDecimal[line][2] = 2;
					
					instructionDecimal[line][3] = 0; // 0 attým, çünkü decimal, binary çevirirken içini güncellicem.
					System.out.println("bFly : " + butterfly + " " + dataDecimal[butterfly][1]);
					instructionDecimal[line][4] = Integer.parseInt(dataDecimal[butterfly][1]); // decimali s1s2 diye bölmeden attým.
				}
				else {
					
					System.out.println(str[1].substring(1) + " diye bir deðer yok.");
				}
			}
		}
	}
	
	public void fillTableWithMinus9 () {
		
		for (int i = 0; i < instructionDecimal.length; i++) {
			
			for (int j = 0; j < instructionDecimal[1].length; j++) {
			
				instructionDecimal[i][j] = -9;
			}
		}
	}
}