package NaiveBayes;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class DataRead {





	public  CarData dataRead()  {
			List<List<String>> instanceList = new ArrayList<List<String>>();
			
			try {
				File file = new File("resources"+File.separator+"car.data");
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					List<String> instancetemp = Arrays.asList(line.split(","));
					List<String> instance=new ArrayList<String>();
					for(String str: instancetemp){
						instance.add(str);
					}
					instanceList.add(instance);//before arraylist
				}
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			CarData cardata =new CarData(instanceList);
					
	//DecisionTree decsiontree=new DecisionTree(cardata);
			
			return cardata;

	}

}
