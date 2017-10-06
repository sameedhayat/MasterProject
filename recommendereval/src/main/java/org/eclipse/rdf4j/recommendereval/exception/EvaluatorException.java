/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.exception;

import org.eclipse.rdf4j.RDF4JException;

/**
 * An exception thrown by classes from the Recommender Evaluator Repository API  
 * to indicate an error.
 */
public class EvaluatorException extends RDF4JException {

	public EvaluatorException() {
		super();
	}

	public EvaluatorException(String msg) {
		super(msg);
	}

	public EvaluatorException(Throwable t) {
		super(t);
	}

	public EvaluatorException(String msg, Throwable t) {
		super(msg, t);
	}
}
