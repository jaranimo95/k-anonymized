import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

public class p3 
{	
	private static final int dataSize = 23065;

	public static void main(String[] args) 
	{
		int k = 2; // For two quasi-identifiers, each combination must match to atleast 2 rows to achieve 2-anonymity

		Scanner kb = null;
		try { kb = new Scanner(new File("dog-data.csv")); }
		catch (FileNotFoundException e) {
			System.out.println("Specified file not found.");
			System.exit(0);
		}

		// Entry Value Format: LicenseType,Breed,Color,DogName,OwnerZip,ExpYear,ValidDate
		//   LicenseType contains owner age group & disability classification
		String[][] values = new String[dataSize][7];	// csv = comma separated values
		int index = 0;
		while(kb.hasNextLine() && index < dataSize) {
			values[index++] = kb.nextLine().split(",");
		}

		// Place all entries into clusters respective to breed
		ArrayList<ArrayList<String>> clusters = new ArrayList<ArrayList<String>>();
		boolean breedExists = false;
		for(int i = 0; i < dataSize; i++) {	 // For each entry in dog-data.csv
			if(clusters.size() != 0) {		 // If cluster list already initialized with first breed
				for(int j = 0; j < clusters.size(); j++) {			// For all breeds in cluster list
					if(clusters.get(j).contains(values[i][1])) {	// If breed exists in cluster list
						breedExists = true;							// Set flag 
						break;										// Stop iteration
					}
				}
				if(breedExists) {			// If breed exists
					breedExists = false;	// Reset flag
					continue;				// Continue to next entry
				}
				else {	// Else, add breed to cluster list
					ArrayList<String> temp = new ArrayList<String>();	
					temp.add(values[i][1]);															// Add name of breed as bin
					//System.out.println(values[i][1] + " added to cluster list.");
					clusters.add(temp);																// Add bin to cluster list
					for(int l = 0; l < dataSize; l++)	{											// For each entry in dog-data.csv, add all breed owners to bin
						if(values[i][1].equals(values[l][1])) {										// If breed of entry matches breed we just added as a bin
							clusters.get(clusters.size() - 1).add(values[l][0]+","+values[l][4].substring(0,4));	// Add owner licensetype and zipcode to bin
							//System.out.println("    "+values[l][0]+","+values[l][4]+" added.");
						}
					}
				}
			}
			else {	// Initial Case
				ArrayList<String> temp = new ArrayList<String>();	
				temp.add(values[0][1]);													// Add name of breed as bin
				//System.out.println(values[0][1] + " added to initial cluster list.");
				clusters.add(temp);														// Add bin to cluster list
				for(int l = 0; l < dataSize; l++) {										// For each entry in dog-data.csv, add all breed owners to bin
					if(values[0][1].equals(values[l][1])) {								// If breed of entry matches breed we just added as a bin
						clusters.get(0).add(values[l][0]+","+values[l][4].substring(0,4));				// Add owner licensetype and zipcode to bin
						//System.out.println("    "+values[l][0]+","+values[l][4]+" added.");
					}
				}
			}
		}

		// Anonymize clusters by matching each combination of entries to k rows in new table
		//
		//	 2-anonymous Table will be of the format:
		//
		// | DISABILITY |     ZIP     |    BREED   |
		// |    True    | 15000-15100 |  Auckland  |
		// |    True    | 15100-15200 |  St. Bern  |
		// |     ...    |     ...     |     ...    |
		//
		//	Zip Ranges:
		//	  - 15000-15100
		//	  - 15100-15200
		//	  - 15200-15300
		//	  - 15300-16100
		//
		//  Disability status can be either true or false
		//  
		//	246 Breeds listed in dog-data.csc
		//
		//  Total number of rows = 2*4*246 = 1968
		//

		System.out.println("------------\n2-ANON TABLE\n------------");
		int numOwners = 0;
		ArrayList<kAnonymizedData> kAnonTable = new ArrayList<kAnonymizedData>();
		
		for(int i = 0; i < 246; i++) { // should be 1968, but put it to 1 for testing purposes			
			// Create 8 rows for each breed (# zip ranges (4) * # disability statuses (2) = 8)
			kAnonTable.add(new kAnonymizedData("15000","15100",clusters.get(i).get(0),true,0));
			for(int j = 1; j < clusters.get(i).size(); j++) {
				String[] data = clusters.get(i).get(j).split(",");
				if(data[0].contains("Disability") && Integer.parseInt(data[1]) < 15100)
					numOwners++;
			}

			kAnonTable.get(kAnonTable.size() - 1).setNumOwners(numOwners);
			numOwners = 0;
				
			kAnonTable.add(new kAnonymizedData("15000","15100",clusters.get(i).get(0),false,0));
			for(int j = 1; j < clusters.get(i).size(); j++) {
				String[] data = clusters.get(i).get(j).split(",");
				if(!data[0].contains("Disability") && Integer.parseInt(data[1]) < 15100)
					numOwners++;
			}

			kAnonTable.get(kAnonTable.size() - 1).setNumOwners(numOwners);
			numOwners = 0;

			kAnonTable.add(new kAnonymizedData("15100","15200",clusters.get(i).get(0),true,0));
			for(int j = 1; j < clusters.get(i).size(); j++) {
				String[] data = clusters.get(i).get(j).split(",");
				if(data[0].contains("Disability") && Integer.parseInt(data[1]) >= 15100 && Integer.parseInt(data[1]) <= 15200)
					numOwners++;
			}

			kAnonTable.get(kAnonTable.size() - 1).setNumOwners(numOwners);
			numOwners = 0;

			kAnonTable.add(new kAnonymizedData("15100","15200",clusters.get(i).get(0),false,0));
			for(int j = 1; j < clusters.get(i).size(); j++) {
				String[] data = clusters.get(i).get(j).split(",");
				if(!data[0].contains("Disability") && Integer.parseInt(data[1]) >= 15100 && Integer.parseInt(data[1]) < 15200)
					numOwners++; 
			}

			kAnonTable.get(kAnonTable.size() - 1).setNumOwners(numOwners);
			numOwners = 0;

			kAnonTable.add(new kAnonymizedData("15200","15300",clusters.get(i).get(0),true,0));
			for(int j = 1; j < clusters.get(i).size(); j++) {
				String[] data = clusters.get(i).get(j).split(",");
				if(data[0].contains("Disability") && Integer.parseInt(data[1]) >= 15200 && Integer.parseInt(data[1]) < 15300)
					numOwners++; 
			}

			kAnonTable.get(kAnonTable.size() - 1).setNumOwners(numOwners);
			numOwners = 0;

			kAnonTable.add(new kAnonymizedData("15200","15300",clusters.get(i).get(0),false,0));
			for(int j = 1; j < clusters.get(i).size(); j++) {
				String[] data = clusters.get(i).get(j).split(",");
				if(!data[0].contains("Disability") && Integer.parseInt(data[1]) >= 15200 && Integer.parseInt(data[1]) < 15300)
					numOwners++; 
			}

			kAnonTable.get(kAnonTable.size() - 1).setNumOwners(numOwners);
			numOwners = 0;

			kAnonTable.add(new kAnonymizedData("15300","16100",clusters.get(i).get(0),true,0));
			for(int j = 1; j < clusters.get(i).size(); j++) {
				String[] data = clusters.get(i).get(j).split(",");
				if(data[0].contains("Disability") && Integer.parseInt(data[1]) >= 15300)
					numOwners++; 
			}

			kAnonTable.get(kAnonTable.size() - 1).setNumOwners(numOwners);
			numOwners = 0;

			kAnonTable.add(new kAnonymizedData("15300","16100",clusters.get(i).get(0),false,0));
			for(int j = 1; j < clusters.get(i).size(); j++) {
				String[] data = clusters.get(i).get(j).split(",");
				if(!data[0].contains("Disability") && Integer.parseInt(data[1]) >= 15300)
					numOwners++;
			}

			kAnonTable.get(kAnonTable.size() - 1).setNumOwners(numOwners);
			numOwners = 0;
		}

		for(kAnonymizedData ka : kAnonTable)
			if(ka.getNumOwners() > 0) System.out.println("Entries: " + ka.getNumOwners() + ", Disability: " + ka.getDisability() + ", (" + ka.getZipLo() + "-" + ka.getZipHi() + "), Breed: " + ka.getBreed());
	
		System.out.print("\nINSIGHT: The number of users who aren't disabled, live in a zip code between [15000,15100], and own a Mini Schnauzer: ");
		for(kAnonymizedData ka : kAnonTable) {
			if(ka.getBreed().contains("SCHNAUZER MIN") && ka.getZipLo().equals("15000") && ka.getZipHi().equals("15100") && !ka.getDisability()) {
				System.out.println(ka.getNumOwners()+"\n\n\n\n\n");
				break;
			}
		}


		// Satisfying Differential Privacy
		// Anonymize clusters by matching each combination of entries to k rows in new table
		//
		//	 2-anonymous Table satisfying differential privacy will be of the format:
		//
		// | DISABILITY |     ZIP     |    BREED   			   |
		// |    True    | 15000-15100 |  (Auckland, St. Bern)  |
		// |    False   | 15000-15100 |  (Auckland, St. Bern)  |
		// |    True    | 15100-15200 |  (Auckland, St. Bern)  |
		// |    False   | 15100-15200 |  (Auckland, St. Bern)  |
		// |    True    | 15200-15300 |  (Auckland, St. Bern)  |
		// |    False   | 15200-15300 |  (Auckland, St. Bern)  |
		// |     ...    |     ...     |     ...    			   |
		//
		//	Zip Ranges:
		//	  - 15000-15100
		//	  - 15100-15200
		//	  - 15200-15300
		//	  - 15300-16100
		//
		//  Disability status can be either true or false
		//  
		//	246 Breeds listed in dog-data.csc
		//	  -  # of breeds per cluster = 2
		//
		//  Total number of rows = 2*4*[246/(# breeds per cluster)] = 984
		//
		System.out.println("------------\nE DIFF TABLE\n------------");

		String temp = null;
		ArrayList<kAnonymizedData> eDiffTable = new ArrayList<kAnonymizedData>();

		for(int i = 0; i < kAnonTable.size() - 15; i+=8) {
			temp = "(" + kAnonTable.get(i).getBreed() + "," + kAnonTable.get(i+8).getBreed() + ")";
			eDiffTable.add(new kAnonymizedData(kAnonTable.get(i).getZipLo(),kAnonTable.get(i).getZipHi(),temp,true,kAnonTable.get(i).getNumOwners() + kAnonTable.get(i+8).getNumOwners()));
		
			temp = "(" + kAnonTable.get(i+1).getBreed() + "," + kAnonTable.get(i+9).getBreed() + ")";
			eDiffTable.add(new kAnonymizedData(kAnonTable.get(i+1).getZipLo(),kAnonTable.get(i+1).getZipHi(),temp,false,kAnonTable.get(i+1).getNumOwners() + kAnonTable.get(i+9).getNumOwners()));

			temp = "(" + kAnonTable.get(i+2).getBreed() + "," + kAnonTable.get(i+10).getBreed() + ")";
			eDiffTable.add(new kAnonymizedData(kAnonTable.get(i+2).getZipLo(),kAnonTable.get(i+2).getZipHi(),temp,true,kAnonTable.get(i+2).getNumOwners() + kAnonTable.get(i+10).getNumOwners()));

			temp = "(" + kAnonTable.get(i+3).getBreed() + "," + kAnonTable.get(i+11).getBreed() + ")";
			eDiffTable.add(new kAnonymizedData(kAnonTable.get(i+3).getZipLo(),kAnonTable.get(i+3).getZipHi(),temp,false,kAnonTable.get(i+3).getNumOwners() + kAnonTable.get(i+11).getNumOwners()));

			temp = "(" + kAnonTable.get(i+4).getBreed() + "," + kAnonTable.get(i+12).getBreed() + ")";
			eDiffTable.add(new kAnonymizedData(kAnonTable.get(i+4).getZipLo(),kAnonTable.get(i+4).getZipHi(),temp,true,kAnonTable.get(i+4).getNumOwners() + kAnonTable.get(i+12).getNumOwners()));

			temp = "(" + kAnonTable.get(i+5).getBreed() + "," + kAnonTable.get(i+13).getBreed() + ")";
			eDiffTable.add(new kAnonymizedData(kAnonTable.get(i+5).getZipLo(),kAnonTable.get(i+5).getZipHi(),temp,false,kAnonTable.get(i+5).getNumOwners() + kAnonTable.get(i+13).getNumOwners()));

			temp = "(" + kAnonTable.get(i+6).getBreed() + "," + kAnonTable.get(i+14).getBreed() + ")";
			eDiffTable.add(new kAnonymizedData(kAnonTable.get(i+6).getZipLo(),kAnonTable.get(i+6).getZipHi(),temp,true,kAnonTable.get(i+6).getNumOwners() + kAnonTable.get(i+14).getNumOwners()));

			temp = "(" + kAnonTable.get(i+7).getBreed() + "," + kAnonTable.get(i+8).getBreed() + ")";
			eDiffTable.add(new kAnonymizedData(kAnonTable.get(i+7).getZipLo(),kAnonTable.get(i+7).getZipHi(),temp,false,kAnonTable.get(i+7).getNumOwners() + kAnonTable.get(i+15).getNumOwners()));

		}

		for(kAnonymizedData ka : eDiffTable)
			if(ka.getNumOwners() > 0) System.out.println("Entries: " + ka.getNumOwners() + ", Disability: " + ka.getDisability() + ", (" + ka.getZipLo() + "-" + ka.getZipHi() + "), Breed: " + ka.getBreed());
	

	}
}