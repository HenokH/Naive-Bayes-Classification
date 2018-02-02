package NaiveBayes;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author henok
 *Trains the native Bayes Classifier  
 *Testes the native Bayes Classifier
 */

public class Trainer{
	
	List<List<String>> cars;             // training data
	int trainingDataSize,testingDataSize,totalDataSize;
	
	Map<String,Map<String,Map<String, Double>>> probablitiesOverClassification = new HashMap<String,Map<String,Map<String, Double>>>();//probablit
															//ies of attributes given a classification
	Map<String,Double> classificationProbablity=new HashMap<String,Double>();//probablitz of a classification
	FeatureNameAndValues2 featurenamesandvalues= new FeatureNameAndValues2();//containtg the valid attribute
															//values, and Classification(output) lists and mapping data column to attribute
	Map<String,Integer> trueCount= new HashMap<String,Integer>();//actual true classification as provided by the trainer
	Map<String,Integer> predictedforPrecision=new HashMap<String,Integer>();
	Map<String,Map<String,Integer>> confusionMatrix= new HashMap<String,Map<String,Integer>>();//for a particular sample

	public Trainer(CarData cardata){
		cars= cardata.getInstanceData();
		totalDataSize=cardata.getNumberOfRow();
		trainingDataSize=Math.round(totalDataSize*2/3);
		testingDataSize=cardata.getNumberOfRow()-trainingDataSize;
	}
	
	

	/**
	 * Trains naive classifier 
	 * Counts attribute value occurances and classification on instances
	 * instances are extracted from data file which is already stored using a list of a list where inner list discribes
	 * >>>shuffles data to select random instances 
	 *  a particular instance(car attributes and its classification) (2/3 of data)
	 *  the counting result is then used to caluclate probablity of attribute value given class and probablity of classification
	 *  saved on this classes variables
	 *  @result variable probablitiesOverClassification && classificationProbablity
	 */
	public void counter(){
		Collections.shuffle(cars);//randomize data 
		Map<String,Integer> classificationCount=new HashMap<String,Integer>();
		Map<String,Map<String,Map<String, Integer>>> countOverClassification = new HashMap<String,Map<String,Map<String, Integer>>>();

		
		
		//--------------Set count recorder to 0
		//>>initialize countOverClassification and ClassificationCount to 0
		for(String outputvalues:featurenamesandvalues.output){
			
			classificationCount.put(outputvalues,0);
			Map<String,Map<String, Integer>> countOverAttribute = new HashMap<String,Map<String, Integer>>();
			Map<String,Map<String, Double>> probablitiesOverAttribute = new HashMap<String,Map<String, Double>>();

			for(Map.Entry<String,List<String>> entry:featurenamesandvalues.AttributeValues.entrySet()){
				
				List<String> possibleValuesList =entry.getValue();
				Map<String, Integer> countOverAttributeValue = new HashMap<String,Integer>();
				Map<String, Double> probablitiesOverAttributeValue = new HashMap<String,Double>();

				for(String possibleValues:possibleValuesList){
					
					countOverAttributeValue.put(possibleValues,0);
					probablitiesOverAttributeValue.put(possibleValues,0.0);
				}
				
				countOverAttribute.put(entry.getKey(), countOverAttributeValue);
				probablitiesOverAttribute.put(entry.getKey(), probablitiesOverAttributeValue);

			}
			countOverClassification.put(outputvalues, countOverAttribute);
			probablitiesOverClassification.put(outputvalues, probablitiesOverAttribute);

		}// end of 	 //initialize countOverClassification and ClassificationCount to 0


		//Start of counting 
		//counts the number of attribute values given a training claassification
		//stores the count on multi level hash map with a structure Classification/AttributeName/Attribute value << count calue
		for(int i=0;i<trainingDataSize;i++){
			
			String classification=cars.get(i).get(6);
			for(int j=0;j<6;j++){
				
				String thisColumnName=featurenamesandvalues.columnname.get(j);
				String thisColumnValue=cars.get(i).get(j);
				
				int count=countOverClassification.get(classification).get(thisColumnName).get(thisColumnValue);
				count=count+1;
				countOverClassification.get(classification).get(thisColumnName).put(thisColumnValue,count);
				
			}
			
			int tempClassificationCount=classificationCount.get(classification);
			tempClassificationCount=tempClassificationCount+1;
			
			double classProbablity=(double)tempClassificationCount/trainingDataSize;
			
			classificationProbablity.put(classification, classProbablity);
			classificationCount.put(classification,tempClassificationCount);	
		}// end of counting
		

		
		//calculate probablity of atribute value using the result of the counting process
		//uses laplace smooting m estimate(Source book: MAcine Learning Tom mitchell page 179 >>(count+mp)/(classcount+m)
		
		for(Map.Entry<String,Map<String,Map<String, Integer>>> tempClassification:countOverClassification.entrySet()){
			
			for(Map.Entry<String,Map<String, Integer>> temp2ColumnName:tempClassification.getValue().entrySet()){
				
				for(Map.Entry<String, Integer> temp3ColumnValue:temp2ColumnName.getValue().entrySet()){
					
					int count=temp3ColumnValue.getValue();
					int m=temp2ColumnName.getValue().size();
					
					double p=(double)1/m;
					
					String thisClassification=tempClassification.getKey();
					int thisClassificationCount=classificationCount.get(thisClassification);
					double probality=(count+(m*p))/(thisClassificationCount+m);
					
					probablitiesOverClassification.get(thisClassification).get(temp2ColumnName.getKey()).put(temp3ColumnValue.getKey(), probality);
				}
			}
		}//end of probablity calculation
	}
	
	
	
	/**
	 * tests naive classifier over traing samples
	 * predicts a classification based on attributes and probablity calculated in trainer method
	 * checks if predicted is similar to actual classifcation
	 * @return returns an error rate over a particular test(set of samples)
	 * error rate is the proportion of wrong prediction over the total test instances(total cars tested)  
	 *
	 */
	public double tester(){
		
		//initialize temporary counting result storage(which is a hashmap of actual outcome vs predicted ones) MAP<Actualclassification,<predicted classification,count>>
		//all to 0
		for(String outputvalues:featurenamesandvalues.output){
			Map<String,Integer> predicted= new HashMap<String,Integer>();
			for(String predictedOutputvalues:featurenamesandvalues.output){
				predicted.put(predictedOutputvalues, 0);
			}
			confusionMatrix.put(outputvalues, predicted);
			trueCount.put(outputvalues, 0);
predictedforPrecision.put(outputvalues, 0);
		}// end of intialize
		
		
		//predicts classification using naive algorithm argmax { p(V/x)= p(x/V)*p(V)}
		int error=0;//this is for error counting
		
		for(int i=trainingDataSize;i<trainingDataSize+testingDataSize;i++){
			
			Map<String,Double> classificationProbablityGivenInstance=new HashMap<String,Double>();//p(V/x) in argmax
																									//{ p(V/x)= ||p(x/V)*p(V)}
			double maxprob=0;
			String predictedClass=null; 
			for(String classification:probablitiesOverClassification.keySet()){
				double probablityClassGivenInstance=1;

				for(int column=0;column<6;column++){//for each column(attribute)
					String attrValue=cars.get(i).get(column);
					String columnName=featurenamesandvalues.columnname.get(column);
					double probablityAtrGivenClass=probablitiesOverClassification.get(classification).get(columnName).get(attrValue);					
					probablityClassGivenInstance=probablityClassGivenInstance*probablityAtrGivenClass;
				}
				
				probablityClassGivenInstance=probablityClassGivenInstance*classificationProbablity.get(classification);
				classificationProbablityGivenInstance.put(classification, probablityClassGivenInstance);
				
				if(probablityClassGivenInstance>maxprob){//finding max liklihood classification
					maxprob=probablityClassGivenInstance;
					predictedClass=classification;
				}
			}
			
			//store count of predicted class vs true class in confusion matrix
			String trueClassification=cars.get(i).get(6);
			int predictedcount=confusionMatrix.get(trueClassification).get(predictedClass);
			predictedcount=predictedcount+1;
int totalforPrecision=predictedforPrecision.get(predictedClass);
totalforPrecision=totalforPrecision+1;
predictedforPrecision.put(predictedClass, totalforPrecision);
			int tempTrueCount=trueCount.get(trueClassification)+1;
		
			trueCount.put(trueClassification,tempTrueCount);
			confusionMatrix.get(trueClassification).put(predictedClass,predictedcount);

			//check and count if predicted classification is wrong
			if(!predictedClass.equals(trueClassification)){
				error=error+1;
			}
		}
		//drawConfusionMatrix();
		double errorrate=((double)error)/testingDataSize;
		//System.out.println("Errorrate>> "+errorrate);
		return(errorrate);
	}

	/**
	 * Format and draws confusion Matrix
	 */
	public void drawConfusionMatrix(){
		int tableFormater=0;
		List<String> order=Arrays.asList("unacc", "acc", "good","vgood");
		for(String name:order){
			if(tableFormater==0){
				
				System.out.println("****************************      Confusion Matrix     *******************************");
				System.out.format("%-20s%-15s%-15s%-15s%-15s\n"," ","Classified ","Classified","Classified","Classified");
				System.out.format("%-20s","");

				for(String predictedname:order){
					System.out.format("%-15s","As>> "+predictedname);
				}
				
				System.out.format("%-15s\n","Total");
				tableFormater++;
			}
			System.out.format("%-20s","Actual "+name);
			for(String predictedname:order){
				System.out.format("%-15s",confusionMatrix.get(name).get(predictedname));
			}
			System.out.format("%-15s%.2f\n",trueCount.get(name)+" && Recall >> ",(double)confusionMatrix.get(name).get(name)/trueCount.get(name));
		}
System.out.format("%-20s","Precision ");

for(String name:order){
	System.out.format("%.2f%-11s",(double)confusionMatrix.get(name).get(name)/predictedforPrecision.get(name),"");

}
		
		
	}
	
}




