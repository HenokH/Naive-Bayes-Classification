package NaiveBayes;

import java.util.List;

/**
 * @author henok
 *CarData class used to store the data read from file line by line 
 *Structured using a list of a list 
 *Each inner List contains the attribute of each cars
 *Outer List contains all the cars
 */

public class CarData {
	private List<List<String>> instanceData;
	
	private int numberOfRow;
	private int numberOfColumns;

	
	/**
	 * @param data read from file
	 */
	public CarData(List<List<String>> data) {
		
		instanceData = data;
		numberOfRow = instanceData.size();
		numberOfColumns = 7;
	}

	public List<List<String>> getInstanceData() {
		return instanceData;
	}

	public int getNumberOfRow() {
		return numberOfRow;
	}

	public int getNumberOfColumns() {
		return numberOfColumns;
	}

}


