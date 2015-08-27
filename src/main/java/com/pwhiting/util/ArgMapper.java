package com.pwhiting.util;

public interface ArgMapper<T> {
	
	/**
	 * Constract a config object and map with the given parser.
	 * 
	 * @param parser
	 * @return
	 */
	T mapArguments(ArgParser parser);
	
}
