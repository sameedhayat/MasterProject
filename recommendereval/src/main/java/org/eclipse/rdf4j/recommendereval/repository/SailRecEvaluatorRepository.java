/* 
 * Victor Anthony Arrascue Ayala
 * & Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.repository;

import java.io.File;
import java.util.ArrayList;
import org.eclipse.rdf4j.recommendereval.config.EvalConfig;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommendereval.config.CrossKFoldEvalConfig;
import org.eclipse.rdf4j.recommendereval.config.StandardEvalConfig;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.evaluator.Evaluator;
import org.eclipse.rdf4j.recommendereval.evaluator.crosskfold.CrossKFoldEvaluator;
import org.eclipse.rdf4j.recommendereval.evaluator.standard.StandardEvaluator;
import org.eclipse.rdf4j.recommendereval.parameter.EvalEntity;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.Sail;

/**
 * An implementation of the {@link Repository} interface that operates on a
 * (stack of) {@link Sail Sail} object(s). The behaviour of the repository is
 * determined by the Sail stack that it operates on; for example, the repository
 * will only support RDF Schema or OWL semantics if the Sail stack includes an
 * inferencer for this.
 * <p>
 * Creating a repository object of this type is very easy. For example, the
 * following code creates and initializes a main-memory store with RDF Schema
 * semantics:
 *
 * <pre>
 * Repository repository = new SailRepository(new ForwardChainingRDFSInferencer(new MemoryStore()));
 * repository.initialize();
 * </pre>
 *
 * Or, alternatively:
 *
 * <pre>
 * Sail sailStack = new MemoryStore();
 * sailStack = new ForwardChainingRDFSInferencer(sailStack);
 *
 * Repository repository = new SailRepository(sailStack);
 * repository.initialize();
 * </pre>
 *
 */
public class SailRecEvaluatorRepository extends SailRepository {

    /*--------*
     * Fields *
     *--------*/
    
    /**
     * Configuration of the evaluation.
     */
    private EvalConfig evalConfig = null;

    private Evaluator evaluator = null;

    /**
     * Set of recommendation configurations. Allows multiple recommenders to be 
     * evaluated at once.
     */
    private ArrayList<RecConfig> recConfigList = null;

    /*--------------*
     * Constructors *
     *--------------*/
    
    /**
     * Creates a new repository object that operates on the supplied Sail.
     *
     * @param sail A Sail object.
     * @param configList
     */
    public SailRecEvaluatorRepository(Sail sail, ArrayList<RecConfig> configList) {
        super(sail);
        this.recConfigList = configList;
    }

    /*---------*
     * Methods *
     *---------*/
    
    public void loadEvalConfiguration(EvalConfig evalConfig)
            throws EvaluatorException {

        System.out.println("Loading configuration...");
        long startTime = System.currentTimeMillis();
        
        if (evalConfig == null) {
            throw new EvaluatorException("CANNOT LOAD A NULL CONFIGURATION");
        }        
        if ( recConfigList.isEmpty() ) {
            throw new EvaluatorException("RECOMMENDER CONFIGURATION LIST EMPTY");
        }
        evalConfig.setRecommenderConfigurations(recConfigList);
                
        evalConfig.addEvalEntity(EvalEntity.USER, recConfigList.get(0).getRecEntity(RecEntity.USER));
        // if you change checkGraphPattern(), change below too
        //Getting the graph pattern. Depending on the kind of feedback different
        //cases have to be considered:
        if (recConfigList.get(0).getRatGraphPattern() != null) {
                evalConfig.setGraphPattern(recConfigList.get(0).getRatGraphPattern());
                evalConfig.addEvalEntity(EvalEntity.RAT_ITEM, recConfigList.get(0).getRecEntity(RecEntity.RAT_ITEM));
        }        
        else if (recConfigList.get(0).getPosGraphPattern() != null 
                        && recConfigList.get(0).getNegGraphPattern() == null) {
                evalConfig.setGraphPattern(recConfigList.get(0).getPosGraphPattern());
                evalConfig.addEvalEntity(EvalEntity.POS_ITEM, recConfigList.get(0).getRecEntity(RecEntity.POS_ITEM));
        }        
        else if (recConfigList.get(0).getNegGraphPattern() != null 
                        && recConfigList.get(0).getPosGraphPattern() == null)
                //TODO
                throw new EvaluatorException("EVALUATION OF ONLY NEGATIVE FEEDBACK CURRENTLY NOT SUPPORTED");
        else if (recConfigList.get(0).getNegGraphPattern() != null 
                        && recConfigList.get(0).getPosGraphPattern() != null)
                //TODO
                throw new EvaluatorException("EVALUATION OF POSITIVE AND NEGATIVE FEEDBACK CURRENTLY NOT SUPPORTED");
        
        evalConfig.addEvalEntity(EvalEntity.RATING, recConfigList.get(0).getRecEntity(RecEntity.RATING));
                                
        boolean success = evalConfig.validateConfiguration();

        if (success) {

            this.evalConfig = evalConfig;

            if( evalConfig instanceof CrossKFoldEvalConfig ) {
                evaluator = new CrossKFoldEvaluator(this);
            }
            else if( evalConfig instanceof StandardEvalConfig ) {
                evaluator = new StandardEvaluator(this);
            }
            else {
                throw new EvaluatorException("EVALUATOR CONFIGURATION ERROR. PLEASE USE ONE OF THE VALID CONFIGURATION CLASSES");
            }

        } else {
            throw new EvaluatorException("EVALUATOR CONFIGURATION VALIDATION UNSUCCESSFUL");
        }
        
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("LOADING... COMPLETED");
        System.out.println("Total time spent for loading configuration: " + (elapsedTime) 
                + "ms (ca. " + (elapsedTime / 1000) + " secs).");
    }

    /**
     * Evaluates all Recommender Configurations and handles output.
     * @throws EvaluatorException 
     */
    public void evaluate()
            throws EvaluatorException {
        
        System.out.println("Evaluator starting...");
        long startTime = System.currentTimeMillis();
        
        evaluator.preprocess(recConfigList);
        evaluator.evaluate(recConfigList);
        evaluator.writeOutput(evalConfig.getOutputMethod(),evalConfig.getEvalMetrics());
        // remove files in native store case
        removeFiles(evaluator.getNativeStoreCounter());
        
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("EVALUATION... COMPLETED");
        System.out.println("Total time spent for evaluation: " + (elapsedTime) 
                + "ms (ca. " + (elapsedTime / 1000) + " secs).");
        System.out.println("");
    }

    /**
     * Gets the inner configuration.
     *
     * @return
     */
    public EvalConfig getCurrentLoadedConfiguration() {
        return this.evalConfig;
    }
    
    /**
     * For testing
     * @return evaluator
     */
    public Evaluator getEvaluator() {
        return this.evaluator;
    }
    
    /**
     * Removes native store files after evaluation operation.
     * @param nativeStoreCounter 
     */
    public void removeFiles(int nativeStoreCounter) {
        if( nativeStoreCounter > 1) {
            while( nativeStoreCounter != 0) {
                File myFile = new File(Thread.currentThread()
                            .getContextClassLoader().getResource("").getPath() + nativeStoreCounter + "/"); 
                deleteDirectory(myFile);
                nativeStoreCounter--;
            }
        }
    }
    
    /**
     * Deletes directory and its content.
     * 
     * @param directory to be deleted
     * @return true if successful, false otw
     */
    private boolean deleteDirectory(File directory) {
        
        if(directory.exists()){
            
            File[] files = directory.listFiles();
            
            if( files != null ) {
                
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return(directory.delete());
    }
}
