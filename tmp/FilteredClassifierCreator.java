package com.pengkong.boatrace.weka.automation;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.common.FileUtil;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;

public class FilteredClassifierCreator {

	Logger logger = LoggerFactory.getLogger(FilteredClassifierCreator.class);
	
	public FilteredClassifierCreator() {
	}
	
	public void create(String dir, String arffFilename, 
			String classifierName, String[] classifierOptions,
			String filterName, String[] filterOptions
			) throws Exception {
		
		String arffFilepath = dir + "/" + arffFilename + ".arff";
		// load train data
		Instances trainDataset = loadDataset(arffFilepath);
		
		// create filtered classifier
        FilteredClassifier fc = new FilteredClassifier();
        
		// create classifier
        Constructor<?> c;
		c = Class.forName(classifierName).getConstructor();
		Classifier classifier = (Classifier) c.newInstance();
		if (classifierOptions.length > 0) {
			((OptionHandler)classifier).setOptions(classifierOptions);
		}
        
		// create filter
		c = Class.forName(filterName).getConstructor();
		Filter filter = (Filter) c.newInstance();
		if (filterOptions.length > 0) {
			((OptionHandler)filter).setOptions(filterOptions);
		}

		logger.info("building model...");
		fc.setClassifier(classifier);
		fc.setFilter(filter);
		
		// train data set
		fc.buildClassifier(trainDataset);
		logger.info("building model complete.");
		
		// save model
		String modelFilepath = dir + "/" + arffFilename + ".model";
		saveModel(fc, modelFilepath);
		logger.info("model saved. " + modelFilepath);

		logger.info("evaluating model...");
		// eavaluation
		String evaluationResult = evaluateModel(fc, trainDataset);
		logger.info(evaluationResult);
		
		// save evaluation result
		String evaluationFilepath = dir + "/" + arffFilename + ".evaluation";
		FileUtil.writeFile(evaluationFilepath, evaluationResult);
		logger.info("evaluation result saved. " + evaluationFilepath);
	}
	
	
	private Instances loadDataset(String path) throws Exception {
        Instances dataset = null;
        dataset = DataSource.read(path);
        if (dataset.classIndex() == -1) {
            dataset.setClassIndex(dataset.numAttributes() - 1);
        }
        
        return dataset;
    }	
	 
    private String evaluateModel(Classifier model, Instances trainDataset) throws Exception {
        // Evaluate classifier with test dataset
        Evaluation eval = new Evaluation(trainDataset);
        eval.evaluateModel(model, trainDataset);
        
        StringBuilder sb = new StringBuilder();
        sb.append(eval.toSummaryString());
        sb.append(eval.toMatrixString());
        sb.append(eval.toClassDetailsString());
        return sb.toString();
    }
    
    public void saveModel(Classifier model, String modelFilepath) throws Exception {
        SerializationHelper.write(modelFilepath, model);
    }
    
    public static void main(String[] args) {
    	if (args.length != 6) {
    		System.out.println("Usage: FilteredClassifierCreator {work directory} {arff file name} {classifier name} {\"classifier options\"} {filter name} {\"filter options\"}");
    		System.exit(1);
    	}
    	
    	FilteredClassifierCreator fcc = new FilteredClassifierCreator();
    	try {
			String dir = args[0];
			String arffFilename = args[1];
			String classifierName = args[2];
			String[] classifierOptions = args[3].split(" ");
			String filterName = args[4];
			String[] filterOptions = args[5].split(" ");
    		
			fcc.create(dir, arffFilename, classifierName, classifierOptions, filterName, filterOptions);
		} catch (Exception e) {
			fcc.logger.error("failed.", e);
		}
	}
    
}
