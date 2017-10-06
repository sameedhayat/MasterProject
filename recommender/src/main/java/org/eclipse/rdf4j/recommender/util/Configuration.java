package org.eclipse.rdf4j.recommender.util;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;


/**
 * The configuration class is used to get the paths to all data resources
 */
public class Configuration {
	
	//Default namespace
//	private final static String NAMESPACE = "http://project.ss2017/";       
	private final static String NAMESPACE = "http://example.org/data#"; 
	//Path to user likes data(books, movies and music)
	private final static String USER_BOOKS_PATH = 
			"./src/main/resources/input/U-training_likes_books.ttl";
	private final static String USER_MOVIES_PATH = 
			"./src/main/resources/input/U-training_likes_movies.ttl";
	private final static String USER_MUSIC_PATH = 
			"./src/main/resources/input/U-training_likes_music.ttl";
	
		
	//Path to dbpedia data for books, movies and music (Level 1)
	private final static String BOOKS_LEVEL_1_PATH =
			"./src/main/resources/input/L1-training_likes_books_level-1.ttl";
	private final static String MOVIES_LEVEL_1_PATH =
			"./src/main/resources/input/L1-training_likes_movies_level-1.ttl";
	private final static String MUSIC_LEVEL_1_PATH =
			"./src/main/resources/input/L1-training_likes_music_level-1.ttl";
	
	//Path to dbpedia data for books, movies and music (Level 2)
	private final static String BOOKS_LEVEL_2_PATH =
			"./src/main/resources/input/L2-training_likes_books_level-2.ttl";
	private final static String MOVIES_LEVEL_2_PATH =
			"./src/main/resources/input/L2-training_likes_movies_level-2.ttl";
	private final static String MUSIC_LEVEL_2_PATH =
			"./src/main/resources/input/L2-training_likes_music_level-2.ttl";
		
	//Path to dbpedia data for books, movies and music (Level 1)
	private final static String BOOKS_LEVEL_3_PATH =
			"./src/main/resources/input/L3-training_likes_books_level-3.ttl";
	private final static String MOVIES_LEVEL_3_PATH =
			"./src/main/resources/input/L3-training_likes_movies_level-3.ttl";
	private final static String MUSIC_LEVEL_3_PATH =
			"./src/main/resources/input/L3-training_likes_music_level-3.ttl";
	
	private static ValueFactory factory = SimpleValueFactory.getInstance();
	
    //Getters and Setters
    public static ResourceInfo getUserBooks() {
    	IRI iri = factory.createIRI(getNamespace(),FilenameUtils.getBaseName(USER_BOOKS_PATH));
    	ResourceInfo ret = new ResourceInfo(iri, USER_BOOKS_PATH);
		return ret;
	}
    
    public static ResourceInfo getUserMovies() {
    	IRI iri = factory.createIRI(getNamespace(),FilenameUtils.getBaseName(USER_MOVIES_PATH));
    	ResourceInfo ret = new ResourceInfo(iri, USER_MOVIES_PATH);
		return ret;
	}
    
    public static ResourceInfo getUserMusic() {
    	IRI iri = factory.createIRI(getNamespace(),FilenameUtils.getBaseName(USER_MUSIC_PATH));
    	ResourceInfo ret = new ResourceInfo(iri, USER_MUSIC_PATH);
		return ret;
	}
    
    public static ResourceInfo getBooksLevel1() {
    	IRI iri = factory.createIRI(getNamespace(),FilenameUtils.getBaseName(BOOKS_LEVEL_1_PATH));
    	ResourceInfo ret = new ResourceInfo(iri, BOOKS_LEVEL_1_PATH);
		return ret;
	}

    public static ResourceInfo getMoviesLevel1() {
    	IRI iri = factory.createIRI(getNamespace(),FilenameUtils.getBaseName(MOVIES_LEVEL_1_PATH));
    	ResourceInfo ret = new ResourceInfo(iri, MOVIES_LEVEL_1_PATH);
		return ret;
	}

    public static ResourceInfo getMusicLevel1() {
    	IRI iri = factory.createIRI(getNamespace(),FilenameUtils.getBaseName(MUSIC_LEVEL_1_PATH));
    	ResourceInfo ret = new ResourceInfo(iri, MUSIC_LEVEL_1_PATH);
		return ret;
	}
    
    public static ResourceInfo getBooksLevel2() {
    	IRI iri = factory.createIRI(getNamespace(),FilenameUtils.getBaseName(BOOKS_LEVEL_2_PATH));
    	ResourceInfo ret = new ResourceInfo(iri, BOOKS_LEVEL_2_PATH);
		return ret;
	}
    
    public static ResourceInfo getMoviesLevel2() {
    	IRI iri = factory.createIRI(getNamespace(),FilenameUtils.getBaseName(MOVIES_LEVEL_2_PATH));
    	ResourceInfo ret = new ResourceInfo(iri, MOVIES_LEVEL_2_PATH);
		return ret;
	}

    public static ResourceInfo getMusicLevel2() {
    	IRI iri = factory.createIRI(getNamespace(),FilenameUtils.getBaseName(MUSIC_LEVEL_2_PATH));
    	ResourceInfo ret = new ResourceInfo(iri, MUSIC_LEVEL_2_PATH);
		return ret;
	}
    
    public static ResourceInfo getBooksLevel3() {
    	IRI iri = factory.createIRI(getNamespace(),FilenameUtils.getBaseName(BOOKS_LEVEL_3_PATH));
    	ResourceInfo ret = new ResourceInfo(iri, BOOKS_LEVEL_3_PATH);
		return ret;
	}
    
    public static ResourceInfo getMoviesLevel3() {
    	IRI iri = factory.createIRI(getNamespace(),FilenameUtils.getBaseName(MOVIES_LEVEL_3_PATH));
    	ResourceInfo ret = new ResourceInfo(iri, MOVIES_LEVEL_3_PATH);
		return ret;
	}
    
    public static ResourceInfo getMusicLevel3() {
    	IRI iri = factory.createIRI(getNamespace(),FilenameUtils.getBaseName(MUSIC_LEVEL_3_PATH));
    	ResourceInfo ret = new ResourceInfo(iri, MUSIC_LEVEL_3_PATH);
		return ret;
	}

	public static String getNamespace() {
		return NAMESPACE;
	}
	
}

