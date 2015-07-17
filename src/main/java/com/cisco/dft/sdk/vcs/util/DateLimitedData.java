package com.cisco.dft.sdk.vcs.util;

import com.cisco.dft.sdk.vcs.util.DateLimitedDataContainer.DateRange;

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
	boolean isInDateRange(DateRange dateRange);
	
}
