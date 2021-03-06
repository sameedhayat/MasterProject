/*******************************************************************************
 * Copyright (c) 2015 Eclipse RDF4J contributors, Aduna, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
/* Generated By:JJTree&JavaCC: Do not edit this line. SyntaxTreeBuilderConstants.java */
package org.eclipse.rdf4j.query.parser.serql.ast;

/**
 * Token literal values and constants. Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface SyntaxTreeBuilderConstants {

	/** End of File. */
	int EOF = 0;

	/** RegularExpression Id. */
	int SINGLE_LINE_COMMENT = 5;

	/** RegularExpression Id. */
	int EQ = 6;

	/** RegularExpression Id. */
	int NE = 7;

	/** RegularExpression Id. */
	int LT = 8;

	/** RegularExpression Id. */
	int LE = 9;

	/** RegularExpression Id. */
	int GE = 10;

	/** RegularExpression Id. */
	int GT = 11;

	/** RegularExpression Id. */
	int STAR = 12;

	/** RegularExpression Id. */
	int COMMA = 13;

	/** RegularExpression Id. */
	int SEMICOLON = 14;

	/** RegularExpression Id. */
	int LBRACE = 15;

	/** RegularExpression Id. */
	int RBRACE = 16;

	/** RegularExpression Id. */
	int LPAREN = 17;

	/** RegularExpression Id. */
	int RPAREN = 18;

	/** RegularExpression Id. */
	int LBRACK = 19;

	/** RegularExpression Id. */
	int RBRACK = 20;

	/** RegularExpression Id. */
	int USING = 21;

	/** RegularExpression Id. */
	int NAMESPACE = 22;

	/** RegularExpression Id. */
	int SELECT = 23;

	/** RegularExpression Id. */
	int CONSTRUCT = 24;

	/** RegularExpression Id. */
	int DISTINCT = 25;

	/** RegularExpression Id. */
	int REDUCED = 26;

	/** RegularExpression Id. */
	int FROM = 27;

	/** RegularExpression Id. */
	int CONTEXT = 28;

	/** RegularExpression Id. */
	int WHERE = 29;

	/** RegularExpression Id. */
	int ORDER = 30;

	/** RegularExpression Id. */
	int BY = 31;

	/** RegularExpression Id. */
	int ASC = 32;

	/** RegularExpression Id. */
	int DESC = 33;

	/** RegularExpression Id. */
	int LIMIT = 34;

	/** RegularExpression Id. */
	int OFFSET = 35;

	/** RegularExpression Id. */
	int TRUE = 36;

	/** RegularExpression Id. */
	int FALSE = 37;

	/** RegularExpression Id. */
	int NOT = 38;

	/** RegularExpression Id. */
	int AND = 39;

	/** RegularExpression Id. */
	int OR = 40;

	/** RegularExpression Id. */
	int SAMETERM = 41;

	/** RegularExpression Id. */
	int LIKE = 42;

	/** RegularExpression Id. */
	int IGNORE = 43;

	/** RegularExpression Id. */
	int CASE = 44;

	/** RegularExpression Id. */
	int REGEX = 45;

	/** RegularExpression Id. */
	int LABEL = 46;

	/** RegularExpression Id. */
	int LANG = 47;

	/** RegularExpression Id. */
	int LANGMATCHES = 48;

	/** RegularExpression Id. */
	int DATATYPE = 49;

	/** RegularExpression Id. */
	int LOCALNAME = 50;

	/** RegularExpression Id. */
	int STR = 51;

	/** RegularExpression Id. */
	int BOUND = 52;

	/** RegularExpression Id. */
	int NULL = 53;

	/** RegularExpression Id. */
	int ISRESOURCE = 54;

	/** RegularExpression Id. */
	int ISBNODE = 55;

	/** RegularExpression Id. */
	int ISURI = 56;

	/** RegularExpression Id. */
	int ISLITERAL = 57;

	/** RegularExpression Id. */
	int AS = 58;

	/** RegularExpression Id. */
	int UNION = 59;

	/** RegularExpression Id. */
	int MINUS = 60;

	/** RegularExpression Id. */
	int INTERSECT = 61;

	/** RegularExpression Id. */
	int ANY = 62;

	/** RegularExpression Id. */
	int ALL = 63;

	/** RegularExpression Id. */
	int IN = 64;

	/** RegularExpression Id. */
	int EXISTS = 65;

	/** RegularExpression Id. */
	int LANG_LITERAL = 66;

	/** RegularExpression Id. */
	int DATATYPED_LITERAL = 67;

	/** RegularExpression Id. */
	int STRING = 68;

	/** RegularExpression Id. */
	int SAFE_CHAR = 69;

	/** RegularExpression Id. */
	int ESCAPED_CHAR = 70;

	/** RegularExpression Id. */
	int UNICODE_ESC = 71;

	/** RegularExpression Id. */
	int LANG_TAG = 72;

	/** RegularExpression Id. */
	int URI = 73;

	/** RegularExpression Id. */
	int SCHEME = 74;

	/** RegularExpression Id. */
	int QNAME = 75;

	/** RegularExpression Id. */
	int BNODE = 76;

	/** RegularExpression Id. */
	int PREFIX_NAME = 77;

	/** RegularExpression Id. */
	int POS_INTEGER = 78;

	/** RegularExpression Id. */
	int NEG_INTEGER = 79;

	/** RegularExpression Id. */
	int DECIMAL = 80;

	/** RegularExpression Id. */
	int HEX = 81;

	/** RegularExpression Id. */
	int ALPHA = 82;

	/** RegularExpression Id. */
	int NUM = 83;

	/** RegularExpression Id. */
	int NCNAME = 84;

	/** RegularExpression Id. */
	int NCNAME_CHAR = 85;

	/** RegularExpression Id. */
	int LETTER = 86;

	/** RegularExpression Id. */
	int BASECHAR = 87;

	/** RegularExpression Id. */
	int IDEOGRAPHIC = 88;

	/** RegularExpression Id. */
	int COMBINING_CHAR = 89;

	/** RegularExpression Id. */
	int DIGIT = 90;

	/** RegularExpression Id. */
	int EXTENDER = 91;

	/** Lexical state. */
	int DEFAULT = 0;

	/** Literal token values. */
	String[] tokenImage = {
			"<EOF>",
			"\" \"",
			"\"\\t\"",
			"\"\\n\"",
			"\"\\r\"",
			"<SINGLE_LINE_COMMENT>",
			"\"=\"",
			"\"!=\"",
			"\"<\"",
			"\"<=\"",
			"\">=\"",
			"\">\"",
			"\"*\"",
			"\",\"",
			"\";\"",
			"\"{\"",
			"\"}\"",
			"\"(\"",
			"\")\"",
			"\"[\"",
			"\"]\"",
			"\"using\"",
			"\"namespace\"",
			"\"select\"",
			"\"construct\"",
			"\"distinct\"",
			"\"reduced\"",
			"\"from\"",
			"\"context\"",
			"\"where\"",
			"\"order\"",
			"\"by\"",
			"\"asc\"",
			"\"desc\"",
			"\"limit\"",
			"\"offset\"",
			"\"true\"",
			"\"false\"",
			"\"not\"",
			"\"and\"",
			"\"or\"",
			"\"sameTerm\"",
			"\"like\"",
			"\"ignore\"",
			"\"case\"",
			"\"regex\"",
			"\"label\"",
			"\"lang\"",
			"\"langMatches\"",
			"\"datatype\"",
			"\"localname\"",
			"\"str\"",
			"\"bound\"",
			"\"null\"",
			"\"isResource\"",
			"\"isBNode\"",
			"\"isURI\"",
			"\"isLiteral\"",
			"\"as\"",
			"\"union\"",
			"\"minus\"",
			"\"intersect\"",
			"\"any\"",
			"\"all\"",
			"\"in\"",
			"\"exists\"",
			"<LANG_LITERAL>",
			"<DATATYPED_LITERAL>",
			"<STRING>",
			"<SAFE_CHAR>",
			"<ESCAPED_CHAR>",
			"<UNICODE_ESC>",
			"<LANG_TAG>",
			"<URI>",
			"<SCHEME>",
			"<QNAME>",
			"<BNODE>",
			"<PREFIX_NAME>",
			"<POS_INTEGER>",
			"<NEG_INTEGER>",
			"<DECIMAL>",
			"<HEX>",
			"<ALPHA>",
			"<NUM>",
			"<NCNAME>",
			"<NCNAME_CHAR>",
			"<LETTER>",
			"<BASECHAR>",
			"<IDEOGRAPHIC>",
			"<COMBINING_CHAR>",
			"<DIGIT>",
			"<EXTENDER>", };

}
