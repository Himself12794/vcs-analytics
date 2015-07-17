package com.cisco.dft.sdk.vcs.util;

import java.util.Date;

/**
 * Indicates a data type that can fall within a time frame.
 * 
 * @author phwhitin
 *
 */
public interface DateLimitedData {
	
	/**
	 * Check whether or not this data falls in the date range.
	 * 
	 * @param start
	 * @param end
	 * @param inclusive
	 * @return
	 */
	boolean isInDateRange(Date start, Date end, boolean inclusive);
	
}