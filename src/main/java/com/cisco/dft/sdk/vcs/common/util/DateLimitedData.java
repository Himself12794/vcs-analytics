package com.cisco.dft.sdk.vcs.common.util;

import java.util.Date;

import com.google.common.collect.Range;

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
	boolean isInDateRange(Range<Date> dateRange);

}
