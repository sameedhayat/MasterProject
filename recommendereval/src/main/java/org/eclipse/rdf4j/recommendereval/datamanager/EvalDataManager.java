/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.datamanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.recommender.config.CrossDomainRecConfig;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommendereval.config.CrossKFoldEvalConfig;
import org.eclipse.rdf4j.recommendereval.config.EvalConfig;
import org.eclipse.rdf4j.recommendereval.config.GenericEvalConfig;
import org.eclipse.rdf4j.recommendereval.config.StandardEvalConfig;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Folds;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.parameter.EvalEntity;
import org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionCriterion;
import org.eclipse.rdf4j.recommendereval.repository.SailRecEvaluatorRepository;
import org.eclipse.rdf4j.recommendereval.util.HashCodeComparator;
import org.eclipse.rdf4j.recommendereval.util.RatingsAlphanumComparator;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Rating;
import org.eclipse.rdf4j.recommendereval.parameter.EvalMetric;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.apache.log4j.Logger;

/**
 * Evaluator Data Manager Class reads, selects and stores data. 
 * 
 * 1) Gets data from the Sparql Repository.
 * 
 * 2) Creates test sets for Evaluator objects by splitting the data in folds.
 * 
 * 3) Capable of removing specific users from the evaluations according to 
 * selection criteria. 
 */
public class EvalDataManager {    
    
    /**
     * Ratings related with only Source Domain
     * 
     * Not needed for the evaluation right now. Can be used in the future.
     * 
     * TODO discuss
     */
    private final ArrayList<Rating> sourceDomainRatings;
    
    /**
     * Ratings stored as 2 Dimensional list. 
     * 
     * Order(Outer to Inner): User-Rating
     * 
     * It is stored as "ratings of each user".
     * 
     * Ratings related with only Target Domain
     */
    private ArrayList<ArrayList<Rating>> usersRatings;  
    private ArrayList<String> users;
    
    /**
     * Boolean Ignore List, at the same size with users list
     * 
     * For users to ignore, boolean value is true.
     * 
     * Users that have less number of ratings than folds, will be ignored.
     * (Their ratings will not be deleted from the repository)
     */
    private final ArrayList<Boolean> ignoreUsers;
    private ArrayList<String> items;
    private ArrayList<ArrayList<String>> itemFeatures;
    
    private final EvalConfig evalConfig;
    
    private final HashMap<EvalEntity, String> evalEntityMap;
    
    final static Logger LOGGER = Logger.getLogger(EvalDataManager.class);
    
    /**
     * Used only for CrossDomain Recommenders
     * 
     * Stores like following:
     * 
     * ItemUri -> Source
     * or
     * ItemUri -> Target
     */
    private final HashMap<String, String> itemDomainMap;
    
    
    public EvalDataManager(EvalConfig evalConfig, HashMap<EvalEntity, String> evalEntityMap) {
        this.usersRatings  = new ArrayList<>();
        this.users         = new ArrayList<>();
        this.items         = new ArrayList<>();
        this.itemFeatures  = new ArrayList<>();
        this.ignoreUsers   = new ArrayList<>();
        this.evalConfig    = evalConfig;
        this.evalEntityMap = evalEntityMap;
        this.itemDomainMap = new HashMap<>();   
        this.sourceDomainRatings = new ArrayList<>();
    }      
    
    /**
     * Gets ratings from repository and stores it as 2D list. 
     * 
     * Also stores users and items.
     * 
     * @param evalRepository Evaluator Repository
     * @param recommenderConfigurations
     */
    public void fetchRatings(SailRecEvaluatorRepository evalRepository,
            List<RecConfig> recommenderConfigurations ) {
        
        SailRepositoryConnection conn;
        
                  
            conn = evalRepository.getConnection();
            
            /**
             * Considering first Recommender Configuration is enough since all 
             * configurations must have same type.
             */            
            if( recommenderConfigurations.get(0) instanceof CrossDomainRecConfig ) {
            
                String sourceDomain = ((CrossDomainRecConfig)recommenderConfigurations.get(0)).getSourceDomain();
                String targetDomain = ((CrossDomainRecConfig)recommenderConfigurations.get(0)).getTargetDomain();
                String[] partsSource = sourceDomain.split("\\s+");
                String[] partsTarget = targetDomain.split("\\s+");                
                String sourceItemName = partsSource[0];
                String targetItemName = partsTarget[0];
                
                // Get source items
                String preprocessingSPARQLCDSourceQuery = 
                    "SELECT " + sourceItemName + "\n"
                    + "WHERE {\n"
                    +   sourceDomain + " \n"
                    + "}";                
                
                TupleQuery tupleQuerySource = conn.prepareTupleQuery(QueryLanguage.SPARQL, preprocessingSPARQLCDSourceQuery);
                TupleQueryResult resultSource = tupleQuerySource.evaluate();

                while (resultSource.hasNext()) {

                    BindingSet bs = resultSource.next();                    
                    // store source items
                    Value sourceItem = bs.getValue(sourceItemName.replace("?", ""));
                    itemDomainMap.put(sourceItem.stringValue(), "source");
                }          
            
                // get target items
                String preprocessingSPARQLCDTargetQuery = 
                    "SELECT " + targetItemName + "\n"
                    + "WHERE {\n"
                    +   targetDomain + " \n"
                    + "}";
                
                TupleQuery tupleQueryTarget = conn.prepareTupleQuery(QueryLanguage.SPARQL, preprocessingSPARQLCDTargetQuery);
                TupleQueryResult resultTarget = tupleQueryTarget.evaluate();

                while (resultTarget.hasNext()) {

                    BindingSet bs = resultTarget.next();
                    // store target items
                    Value targetItem = bs.getValue(targetItemName.replace("?", ""));
                    itemDomainMap.put(targetItem.stringValue(), "target");
                    
                }          
            }
            StringBuilder sb = new StringBuilder();
            HashSet<String> otherEntities = new HashSet<>();
            
            // Rating scale is used (e.g. 5 stars)
            if( evalEntityMap.get(EvalEntity.RATING) != null ) {
                
                // If entities different than item,user and rating exist, get them
                otherEntities = getOtherEntities(); 
                
                sb.append("SELECT ").append(evalEntityMap.get(EvalEntity.USER)).append("\n");
                sb.append(evalEntityMap.get(EvalEntity.RAT_ITEM)).append("\n");
                sb.append(evalEntityMap.get(EvalEntity.RATING)).append("\n");
                
                for( String ent : otherEntities ) {
                    sb.append(ent).append("\n");                
                }
                
                sb.append("WHERE {\n");
                sb.append(evalConfig.getGraphPattern());
                sb.append("}");
            }
            // likes case)
            else  { 
                sb.append("SELECT ").append(evalEntityMap.get(EvalEntity.USER)).append("\n");
                sb.append(evalEntityMap.get(EvalEntity.POS_ITEM)).append("\n");
                sb.append("WHERE {\n");
                sb.append(evalConfig.getGraphPattern());
                sb.append("}");
            }          
            
            TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, sb.toString());
            TupleQueryResult result = tupleQuery.evaluate();

            while (result.hasNext()) {

                Rating rating = new Rating();
                
                BindingSet bs = result.next();
                
                Value userVal = bs.getValue(evalEntityMap.get(EvalEntity.USER).replace("?", ""));
                Value itemVal = null;
                if( evalEntityMap.get(EvalEntity.RAT_ITEM) != null ) {
                    itemVal = bs.getValue(evalEntityMap.get(EvalEntity.RAT_ITEM).replace("?", ""));
                }
                else if( evalEntityMap.get(EvalEntity.POS_ITEM) != null && 
                        evalEntityMap.get(EvalEntity.NEG_ITEM) == null ) {
                    itemVal = bs.getValue(evalEntityMap.get(EvalEntity.POS_ITEM).replace("?", ""));
                }
                
                rating.setUserURI(userVal.stringValue());
                rating.setItemURI(itemVal.stringValue());
                
                if( evalEntityMap.get(EvalEntity.RATING) != null ) {                 
                    Value ratingVal = bs.getValue(evalEntityMap.get(EvalEntity.RATING).replace("?", ""));
                    rating.setRatingValue(Double.parseDouble(ratingVal.stringValue()));
                
                    for( String ent : otherEntities ) {    
                        Value otherVal = bs.getValue(ent.replace("?", "")); 
                        rating.addEntity(ent, otherVal.stringValue());              
                    }
                }
                else {
                    rating.setRatingValue(1.0);
                }    
                
                if( recommenderConfigurations.get(0) instanceof CrossDomainRecConfig ) { 
                    // item from source domain
                	String value = itemDomainMap.get(rating.getItemURI());
                	if(value != null) {
	                    if( itemDomainMap.get(rating.getItemURI()).equals("source") ) {
	                        sourceDomainRatings.add(rating);
	                        continue;
	                    }// item from target domain
	                    else {
	                    }
                	}else {
                		System.out.println("Skipped" + rating.getItemURI());
                	}
                    
                	
                }
                
                addItem(itemVal.stringValue());
                int indexOfUser = addUser(userVal.stringValue());
                usersRatings.get(indexOfUser).add(rating);                           
            }            
            
            // for DIVERSITY and NOVELTY metrics, get features
            if( ((GenericEvalConfig) evalConfig).evalMetricsContain(EvalMetric.DIVERSITY)
                || ((GenericEvalConfig) evalConfig).evalMetricsContain(EvalMetric.NOVELTY)) {
                sb = new StringBuilder();
                
                if( evalEntityMap.get(EvalEntity.RAT_ITEM) != null ) {
                    sb.append("SELECT ").append(evalEntityMap.get(EvalEntity.RAT_ITEM)).append("\n");
                }
                else if( evalEntityMap.get(EvalEntity.POS_ITEM) != null && 
                        evalEntityMap.get(EvalEntity.NEG_ITEM) == null ) {
                    sb.append("SELECT ").append(evalEntityMap.get(EvalEntity.POS_ITEM)).append("\n");
                }
                
                sb.append(evalEntityMap.get(EvalEntity.FEATURE)).append("\n");
                sb.append("WHERE {\n");
                sb.append(evalConfig.getFeatureGraphPattern());
                sb.append("}");
                
                TupleQuery featureTupleQuery    = conn.prepareTupleQuery(QueryLanguage.SPARQL, sb.toString());
                TupleQueryResult featureResult  = featureTupleQuery.evaluate();
                
                items.stream().forEach((_item) -> {
                    itemFeatures.add( new ArrayList<>() );
                });
                
                while (featureResult.hasNext()) {
                    
                    BindingSet bs = featureResult.next();

                    Value featureVal = bs.getValue(evalEntityMap.get(EvalEntity.FEATURE).replace("?", ""));
                    Value itemVal = null;
                    if( evalEntityMap.get(EvalEntity.RAT_ITEM) != null ) {
                        itemVal = bs.getValue(evalEntityMap.get(EvalEntity.RAT_ITEM).replace("?", ""));
                    }
                    else if( evalEntityMap.get(EvalEntity.POS_ITEM) != null && 
                            evalEntityMap.get(EvalEntity.NEG_ITEM) == null ) {
                        itemVal = bs.getValue(evalEntityMap.get(EvalEntity.POS_ITEM).replace("?", ""));
                    }
                    int itemIndex = items.indexOf(itemVal.stringValue());
                    if( itemIndex > -1 ) {
                        itemFeatures.get(itemIndex).add(featureVal.stringValue());  
                    }
                }
                
            }
//        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException 
//                | NullPointerException ex) {
//            LOGGER.error("Cannot get input data from repository, please check input parameters(Graph pattern & entities)!");
//            LOGGER.error(ex.getMessage());
//            throw new EvaluatorException("Cannot get input data from repository, please check input parameters(Graph pattern & entities)!");
//        }
         
        selectUsers();
       
        /**
         * Sort each user's ratings alpha-numerically so that evaluation becomes
         * repeatable.
         * 
         * Check RatingsAlphanumComparator Class for better understanding.
         */
        for( int i = 0 ; i < usersRatings.size() ; i++ ) {
            Collections.sort(usersRatings.get(i), new RatingsAlphanumComparator());
        }
    }
    
    /**
     * For each user, creates K different folds and fills them with ratings. 
     * 
     * @param kFoldSize
     * @param isReproducible if it is true, ratings are sorted according to 
     * their hashcodes, if it is false, ratings are picked randomly
     * 
     * @return 3 Dimensional ratings array list. Order(Outer to Inner): User-KFold-Rating
     * 
     * @throws EvaluatorException 
     */
    public Folds splitRatingsKFold(int kFoldSize, boolean isReproducible)
            throws EvaluatorException {

        int totalNumberOfRatings = 0;
        
        for( int i = 0 ; i < usersRatings.size() ; i++ ) {
            totalNumberOfRatings += usersRatings.get(i).size();
        }
        
        System.out.println("Total number of Ratings " + totalNumberOfRatings + " " + kFoldSize);
        
        if( totalNumberOfRatings < kFoldSize ) {
            throw new EvaluatorException("K VALUE(KFOLD) SHOULD BE LESS THAN NUMBER OF RATINGS");
        }
        
        ArrayList<ArrayList<ArrayList<Rating>>> usersFoldsRatings = new ArrayList<>();
        
        int nextFoldToUse = 0;
        
        // for every user
        for( int i = 0 ; i < usersRatings.size() ; i++ ) {
        
            ArrayList<ArrayList<Rating>> foldsRatings = new ArrayList<>();

            // create new empty arraylists for each folds
            for( int j = 0 ; j < kFoldSize ; j++ ) {
                ArrayList<Rating> ratings = new ArrayList<>();
                foldsRatings.add(ratings);
            }                        
            
            ArrayList<Rating> tempRatings = new ArrayList<>();
            tempRatings.addAll(usersRatings.get(i));
            
            int ratingsForUserSize = tempRatings.size();
            
            if( isReproducible ) { 
                Collections.sort(tempRatings,new HashCodeComparator());
            }
            else {
                Collections.shuffle(tempRatings);
            }
            
            for (int j = 0; j < ratingsForUserSize; j++) {

                // reset index
                if (nextFoldToUse == kFoldSize) {
                    nextFoldToUse = 0;
                }

                foldsRatings.get(nextFoldToUse++).add(tempRatings.get(0));
                tempRatings.remove(0);
            }

            usersFoldsRatings.add(foldsRatings);
        } 
        
        //printUsersFoldsRatingsList(usersFoldsRatings);
        return new Folds(usersFoldsRatings);
    }
    
    
    /**
     * Removes test set ratings from Recommender Repository
     * 
     * @param sRecommender Recommender Repository
     * @param testSet ratings
     * @return 
     * 
     * @throws org.eclipse.rdf4j.repository.RepositoryException
     */
    public SailRecommenderRepository removeStatements(SailRecommenderRepository sRecommender, 
            ArrayList<Rating> testSet) throws RepositoryException {
        
        RepositoryConnection conn = sRecommender.getConnection();
                
        if( evalConfig.getEvalEntityMap().get(EvalEntity.RATING) != null ) {
            
            for (Rating tempRating : testSet) { 
                
                HashMap<String,String> otherEntities = tempRating.getOtherEntities();
                
                String[] graphParts = getGraphPatternParts();       
                String itemEntityString = getItemEntity();
                
                // remove triples with respect to graph pattern
                int loopCount = graphParts.length / 3;                
                for( int i = 0 ; i < loopCount ; i++ ) {
                    
                    if( graphParts[i*3].equals(evalEntityMap.get(EvalEntity.USER)) ) {
                        if( !graphParts[i*3+2].equals(itemEntityString) ) {
                            for( Map.Entry<String, String> entry : otherEntities.entrySet() ) {
                                conn.remove(new URIImpl(tempRating.getUserURI()), null , new URIImpl(entry.getValue()));                                 
                            }
                        }
                    }
                    if( graphParts[i*3].equals(itemEntityString) ) {
                        if( !graphParts[i*3+2].equals(evalEntityMap.get(EvalEntity.USER)) ) {
                            for( Map.Entry<String, String> entry : otherEntities.entrySet() ) {
                                conn.remove(new URIImpl(tempRating.getItemURI()), null , new URIImpl(entry.getValue()));                                 
                            }
                        }
                    }
                    if( graphParts[i*3+2].equals(evalEntityMap.get(EvalEntity.USER)) ) {
                        if( !graphParts[i*3].equals(itemEntityString) ) {
                            for( Map.Entry<String, String> entry : otherEntities.entrySet() ) {
                                conn.remove(new URIImpl(entry.getValue()), null , new URIImpl(tempRating.getUserURI()));                                 
                            }
                        }
                    }
                    if( graphParts[i*3+2].equals(itemEntityString) ) {
                        if( !graphParts[i*3].equals(evalEntityMap.get(EvalEntity.USER)) ) {
                            for( Map.Entry<String, String> entry : otherEntities.entrySet() ) {
                                conn.remove(new URIImpl(entry.getValue()), null , new URIImpl(tempRating.getItemURI()));                                 
                            }
                        }
                    }                   
                }
            }
        }
        else {

            for (Rating tempRating : testSet) {      
                conn.remove(new URIImpl(tempRating.getUserURI()), null, new URIImpl(tempRating.getItemURI()));
            }
        }  
        
        return sRecommender;
    }
    
    /**
     * Calculates rating averages for each user considering given folds.
     * 
     * @param folds 
     * @param foldIndex 
     * @return 
     */
    public ArrayList<Double> calculateAllUsersAverages(Folds folds, int foldIndex) {
        
        long startTime = System.currentTimeMillis();     
                
        ArrayList<Double> averages = new ArrayList<>();
        
        // iterating over users
        for( int usersIndex = 0 ; usersIndex < users.size() ; usersIndex++ ) {
            
            ArrayList<Rating> ratingsOfUser = new ArrayList<>();
            double userTotal = 0.0;
            
            // iterating over folds
            for( int foldsIndex = 0 ; foldsIndex < folds.getNumberOfFolds() ; foldsIndex++ ) {
                if( foldsIndex == foldIndex ) 
                    continue;
                ratingsOfUser.addAll(folds.getRatingsOfFold(usersIndex,foldsIndex));
            }
            
            for( int j = 0 ; j < ratingsOfUser.size() ; j++ ) {
                userTotal += ratingsOfUser.get(j).getRatingValue();
            }
            
            if( ratingsOfUser.isEmpty() ) {
                averages.add(userTotal);
                continue;
            }
            Double avg = userTotal/ratingsOfUser.size();
            averages.add(avg);
        }
        
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time spent for calculating User Averages: " + (elapsedTime) 
                + "ms (ca. " + (elapsedTime / 1000) + " secs).");
                
        return averages;
    }

    /**
     * Selects Users with respect to User Selection Criterion.
     * 
     * Removes Users if needed.
     */
    private void selectUsers() {
        
        if( evalConfig.getUserSelectionCriterion().getNumberOfUsers() >= users.size() )  {
            LOGGER.warn("Number of users for the user selection is greater than (or equals to) the size of the users list. No users will be removed.");
        }
        // in case of invalid Number of Users Selection, no users will be removed
        else if( evalConfig.getUserSelectionCriterion().getNumberOfUsers() < 1 )  {
            LOGGER.warn("Number of users is INVALID. No users will be removed.");
        }
        else {
            removeUsers();
        }        
        
        /**
         * After user removal, if we still have users with number of ratings  
         * less than number of folds, ignore them. Do not remove from the 
         * repository.
         */
        if( evalConfig instanceof CrossKFoldEvalConfig ) {

            int numberOfFolds = ( (CrossKFoldEvalConfig) evalConfig ).getNumberOfFolds();      

            for( int i = 0 ; i < usersRatings.size() ; i++ ) {            

                ArrayList<Rating> tempRatingsList = usersRatings.get(i);               

                // this user is not going to be evaluated anyways, so remove it
                if( tempRatingsList.size() < numberOfFolds ) {
                    ignoreUsers.add(Boolean.TRUE);
                }
                else {
                    ignoreUsers.add(Boolean.FALSE);
                }
            }
        }
        else if( evalConfig instanceof StandardEvalConfig ) {
            // TODO fill when StandardEvalConfig is implemented
            throw new UnsupportedOperationException("STANDARDEVALCONFIG IS NOT IMPLEMENTED!");
        }
    }

    /**
     * Removes Users from the Users list and UsersFoldsRatings list with respect to User Selection Criterion.
     */
    private void removeUsers() {        
        
        int numberOfUsersToRemove = users.size() - evalConfig.getUserSelectionCriterion().getNumberOfUsers();
        
        if( evalConfig.getUserSelectionCriterion().getSelection() == EvalUserSelectionCriterion.RANDOM ) {
                   
            // remove users who has number of ratings less than number of folds
            if( evalConfig instanceof CrossKFoldEvalConfig ) {

                int numberOfFolds = ( (CrossKFoldEvalConfig) evalConfig ).getNumberOfFolds();        

                Iterator<String> usersListIterator = users.iterator();
                Iterator<ArrayList<Rating>> usersRatingsListIterator = usersRatings.iterator();

                while( usersListIterator.hasNext() && usersRatingsListIterator.hasNext() ) {

                    String tempString = usersListIterator.next(); 
                    ArrayList<Rating> tempList = usersRatingsListIterator.next();               

                    // this user is not going to be evaluated anyways, so remove it
                    if( tempList.size() < numberOfFolds ) {
                        usersListIterator.remove();
                        usersRatingsListIterator.remove();
                        numberOfUsersToRemove--;
                    }
                }
            }
            else if( evalConfig instanceof StandardEvalConfig ) {
                // TODO fill when StandardEvalConfig is implemented
            }
            
            
            while( numberOfUsersToRemove > 0 ) {            

                Random rand = new Random(); 
                int randomIndex = rand.nextInt(users.size());
                users.remove(randomIndex);
                usersRatings.remove(randomIndex);                

                numberOfUsersToRemove--;
            }            
        }
        else {
            throw new UnsupportedOperationException("Not supported yet."); 
        }
    }  
    
    /**
     * If entities different than item,user and rating exist, get them
     * 
     * @return 
     * @throws org.eclipse.rdf4j.recommendereval.exception.EvaluatorException 
     */
    public HashSet<String> getOtherEntities() throws EvaluatorException {
    
        String[] graphParts = getGraphPatternParts();          
        
        if( (graphParts.length % 3) != 0 ) {
            throw new EvaluatorException("GRAPH PATTERN MUST BE IN TRIPLES!");
        }
        
        HashSet<String> otherEntities = new HashSet<>();
                
        int loopCount = graphParts.length / 3 ;
        String itemEntityString = getItemEntity();
                
        // check triples
        for( int i = 0 ; i < loopCount ; i++ ) {
            if( graphParts[i*3].equals(evalEntityMap.get(EvalEntity.USER)) ) {
                if( !graphParts[i*3+2].equals(itemEntityString) ) {
                    otherEntities.add(graphParts[i*3+2]);
                }
            }
            if( graphParts[i*3].equals(itemEntityString) ) {
                if( !graphParts[i*3+2].equals(evalEntityMap.get(EvalEntity.USER)) ) {
                    otherEntities.add(graphParts[i*3+2]);
                }
            }
            if( graphParts[i*3+2].equals(evalEntityMap.get(EvalEntity.USER)) ) {
                if( !graphParts[i*3].equals(itemEntityString) ) {
                    otherEntities.add(graphParts[i*3]);
                }
            }
            if( graphParts[i*3+2].equals(itemEntityString) ) {
                if( !graphParts[i*3].equals(evalEntityMap.get(EvalEntity.USER)) ) {
                    otherEntities.add(graphParts[i*3]);
                }
            }
        }        
        
        return otherEntities;
    }
    
    
    /**
     * Gets graph pattern parts, splitted
     * 
     * @return 
     */
    public String[] getGraphPatternParts() {
    
        String graphPattern = evalConfig.getGraphPattern();

        // replace dots with spaces
        graphPattern = graphPattern.replaceAll("\\s+[.]", " ");

        // replace new line characters with spaces
        graphPattern = graphPattern.replaceAll("\\r|\\n", "");

        // trim start and end of the string and shrink spaces
        graphPattern = graphPattern.trim().replaceAll(" +", " ");
        
        return graphPattern.split("\\s+");
    }
    
    /**
     * Getter method for usersRatings 2D List.
     * @return 
     */
    public ArrayList<ArrayList<Rating>> getUsersRatings() {
        return this.usersRatings;
    } 
    
    /**
     * Adds User URI to Users ArrayList if it does not exist. 
     * 
     * @param stringValue User URI
     * @return index of User URI in the Users ArrayList
     */
    private int addUser(String stringValue) {
        if( !users.contains(stringValue) ) {
            users.add(stringValue);
            ArrayList<Rating> tempRatings = new ArrayList<>();
            usersRatings.add(tempRatings);
        }
        return users.indexOf(stringValue);
    }
    
    /**
     * Getter method for Users List.
     * 
     * @return users
     */
    public ArrayList<String> getUsers() {
        return this.users;
    }
    
    /**
     * Getter method for Users to Ignore List.
     * 
     * @return ignoreUsers
     */
    public ArrayList<Boolean> getUsersToIgnore() {
        return this.ignoreUsers;
    }

    /**
     * Adds Item URI to Items ArrayList if it does not exist. 
     * 
     * @param stringValue Item URI
     */
    private void addItem(String stringValue) {
        if( !items.contains(stringValue) ) {
            items.add(stringValue);
        }
    }
    
    /**
     * Getter method for Items List.
     * 
     * @return items
     */
    public ArrayList<String> getItems() {
        return this.items;
    }
    
    /**
     * Getter method for ItemFeatures List.
     * 
     * @return itemFeatures
     */
    public ArrayList<ArrayList<String>> getItemFeatures() {
        return this.itemFeatures;
    }
    
    /**
     * Getter method for Source Domain Ratings List.
     * 
     * @return sourceDomainRatings
     */
    public ArrayList<Rating> getSourceDomainRatings() {
        return this.sourceDomainRatings;
    }
    
    /**
     * For testing.
     * 
     * @param users 
     */
    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }
    
    /**
     * For testing.
     * 
     * @param items 
     */
    public void setItems(ArrayList<String> items) {
        this.items = items;
    }
    
    /**
     * For testing.
     * 
     * @param itemFeatures 
     */
    public void setItemFeatures(ArrayList<ArrayList<String>> itemFeatures) {
        this.itemFeatures = itemFeatures;
    }
    
    /**
     * For testing. 
     * 
     * @param usersRatings 
     */
    public void setUsersRatings(ArrayList<ArrayList<Rating>> usersRatings) {
        this.usersRatings = usersRatings;
    } 

    /**
     * For testing.
     * 
     * @return graphPattern
     */
    public String getGraphPattern() {
        return this.evalConfig.getGraphPattern();
    }
    
    /**
     * For testing.
     * 
     * @return graphPattern
     */
    public HashMap<EvalEntity,String> getEntityMap() {
        return this.evalEntityMap;
    } 
    
    /**
     * For development & testing. Prints usersFoldsRatings list.
     * 
     * @param usersFoldsRatings 
     */
    public void printUsersFoldsRatingsList(ArrayList<ArrayList<ArrayList<Rating>>> usersFoldsRatings) {
        
        for (int i = 0; i < usersFoldsRatings.size(); i++) {

            ArrayList<ArrayList<Rating>> nestedRatingsList = usersFoldsRatings.get(i);
            System.out.println("User " + i);

            for (int j = 0; j < nestedRatingsList.size(); j++) {

                System.out.println("Fold " + j);

                ArrayList<Rating> innerRatings = nestedRatingsList.get(j);

                for (int k = 0; k < innerRatings.size(); k++) {
                    System.out.println("Rating: User: " + innerRatings.get(k).getUserURI() 
                            + " Item: " + innerRatings.get(k).getItemURI() 
                            + " RatingValue: " + innerRatings.get(k).getRatingValue() );
                }
            }
        }
    }

    /**
     * Finds the item entity. 
     * 
     * TODO should use other mechanisms for 4 case scenario
     * 
     * @return 
     */
    private String getItemEntity() {
        
        if( evalEntityMap.get(EvalEntity.RAT_ITEM) != null ) {
            return evalEntityMap.get(EvalEntity.RAT_ITEM);
        }
        else if( evalEntityMap.get(EvalEntity.POS_ITEM) != null && 
                evalEntityMap.get(EvalEntity.NEG_ITEM) == null ) {
            return evalEntityMap.get(EvalEntity.POS_ITEM);
        }        
        
        // TODO neg_item case and pos&neg case missing
        return null;
    }
}