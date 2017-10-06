/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.evaluator.standard;

import java.util.List;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommendereval.evaluator.AbstractEvaluator;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.repository.SailRecEvaluatorRepository;

public class StandardEvaluator extends AbstractEvaluator {

    public StandardEvaluator(SailRecEvaluatorRepository evaluatorRepository) {
        super(evaluatorRepository);
    }

    @Override
    public void evaluate(List<RecConfig> configuration) throws EvaluatorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
