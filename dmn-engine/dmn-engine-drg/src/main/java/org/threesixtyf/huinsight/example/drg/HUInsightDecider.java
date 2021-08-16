package org.threesixtyf.huinsight.example.drg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

public class HUInsightDecider {
//	  private static final List<String> SEASONS = Arrays.asList("Winter", "Spring", "Summer");

	  public static void printUsage(String errorMessage, int exitCode) {
	    System.err.println("Error: " + errorMessage);
	    System.err.println("Usage: java -jar BeveragesDecider.jar 80 false");
	    System.exit(exitCode);
	  }

	  public static void main(String[] args) {

	    validateInput(args);

	    VariableMap variables = prepareVariableMap(args);

	    // parse decision from resource input stream
	    InputStream inputStream = null;

	    try {
	    	inputStream = HUInsightDecider.class.getResourceAsStream("huInsightTestdrive.dmn");
	    	parseAndEvaluateDecision(variables, inputStream);

	    } finally {
	      try {
	        inputStream.close();
	      } catch (IOException e) {
	      }
	    }
	  }

	  protected static void validateInput(String[] args) {

	    // parse arguments
	    if (args.length != 2) {
	      printUsage("Please specify the HappiU score and if have dependents", 1);
	    }

	    try  {
	     Integer.parseInt(args[0]);

	    }
	    catch (NumberFormatException e) {
	      printUsage("HappiU Score must be a number", 1);
	    }
	    
	    try  {
		 Boolean.parseBoolean(args[1]);

	    }
	    catch (NumberFormatException e) {
	      printUsage("Dependents must be a boolean", 1);
	    }
	  }

	  protected static VariableMap prepareVariableMap(String[] args) {

	    int happiUScore = Integer.parseInt(args[0]);
	    boolean hasDependents = Boolean.parseBoolean(args[1]);

	    // prepare variables for decision evaluation
	    VariableMap variables = Variables
	      .putValue("happiUScore", happiUScore)
	      .putValue("hasDependents", hasDependents);

	    return variables;
	  }

	  protected static void parseAndEvaluateDecision(VariableMap variables, InputStream inputStream) {

	    // create a new default DMN engine
	    DmnEngine dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();

	    DmnDecision decision = dmnEngine.parseDecision("huInsights", inputStream);

	    // evaluate decision
	    DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);

	    // print result
	    List<String> insights = result.collectEntries("comment");
	    System.out.println("Your HappiU Score is: "+variables.get("happiUScore"));
	    System.out.println("Has dependents: "+variables.get("hasDependents"));
	    System.out.println();
	    System.out.println("Your HappiU Insights:");
	    
	    Arrays.stream(insights.toArray()).forEach(System.out::println);
	  }

}
