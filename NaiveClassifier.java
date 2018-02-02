package NaiveBayes;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.SwingUtilities;

public class NaiveClassifier {

	public static void main(String[] args)   {
		
		DataRead datafile=new DataRead();
		CarData cardata=datafile.dataRead();
		
	    Random random = new Random();

FeatureNameAndValues2 features= new FeatureNameAndValues2();
for(int noise=0; noise<4;noise++){//for loop to introduce noise
	if(noise>0){

		System.out.println("\n\nintroducing noise level :  "+noise);
		int oneInTenNoise=random.nextInt(3)+1;

		for(int dataline=0;dataline<cardata.getInstanceData().size();dataline+=oneInTenNoise){
			int randomColumn= random.nextInt(6);
			String columnForNoiseAddition=features.columnname.get(randomColumn);
			int columnValuesSize=features.AttributeValues.get(columnForNoiseAddition).size();
			int randomAttributeValueIndex= random.nextInt(columnValuesSize);
			String randomAttributeValue=features.AttributeValues.get(columnForNoiseAddition).get(randomAttributeValueIndex);
			cardata.getInstanceData().get(dataline).set(randomColumn, randomAttributeValue);
oneInTenNoise=random.nextInt(3)+1;
		}
	}		

		Trainer trainer=new Trainer(cardata);
		
		List<Double> ErrorList=new ArrayList<Double>();
		double errorRate;
		double sumErrorRate=0;
		double averageErrorRate;
		double maxErrorRate=0.0;
		
	    int randomconfusionmatrix=random.nextInt(100);
	    
		for(int i=0;i<100;i++){//for 100 times the naive classifier is tested
			trainer.counter();
			errorRate=trainer.tester();
			if(i==randomconfusionmatrix){
				trainer.drawConfusionMatrix();
			}
			//System.out.println("Iteration : "+i);
			if(errorRate>maxErrorRate){
				maxErrorRate=errorRate;
			}
			sumErrorRate=sumErrorRate+errorRate;
			//	percentError=(errorRate*100+0.5);
			//System.out.println("Error : "+errorRate+"\n");

			ErrorList.add(errorRate);		
		}
		
		averageErrorRate=sumErrorRate/100;
		//System.out.println("error ratelist " + ErrorList );
		System.out.println("\n----------------------------------------------------------------------------------\n\n" );

		System.out.println("Average error rate over 100 iterations :" + averageErrorRate );
		System.out.println("Maximum error rate over 100 iterations :" + maxErrorRate );

		
		graphMaker test = new graphMaker(ErrorList);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
		    	 test.createAndShowGui();
			}
		});
		 
	  }
}
}
