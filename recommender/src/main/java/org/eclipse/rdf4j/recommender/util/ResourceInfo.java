package org.eclipse.rdf4j.recommender.util;

import org.eclipse.rdf4j.model.IRI;

/**
 * ResourceInfo class that will contain IRI for graph and path information for one resource
 */
public class ResourceInfo {
	//IRI to be used as name for graph
	private IRI iri;
	//path of the file to be used
	private String path;

 public ResourceInfo(IRI iri, String path) {
    this.iri = iri;
    this.path = path;
 }

 public IRI getResourceIRI(){ return this.iri; }
 public String getResourcePath(){ return this.path; }

}