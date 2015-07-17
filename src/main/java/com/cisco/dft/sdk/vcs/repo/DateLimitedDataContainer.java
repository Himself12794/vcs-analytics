package com.cisco.dft.sdk.vcs.repo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Represents a data structure whose data entries can be restricted to a certain time frame.
 * This class is recursive, so if {@code T} is also an instance of DateLimitedDataContainer, it will
 * check the date limit on that as well, and so on. If desired, this can also work similarly to a list
 * as long as all of its entries implement {@link DateLimitedData}
 * 
 * @author phwhitin
 *
 * @param <T>
 */
public class DateLimitedDataContainer<T extends DateLimitedData> {
	
	/**The epoch start date*/
	public static final Date DEFAULT_START = new Date(0L);
	
	/**Nov of 5138. An nice thrown out there date for a good end point. */
	public static final Date DEFAULT_END = new Date(99999999999999L);
	
	public static final boolean DEFAULT_INCLUSION = true;
	
	protected final List<T> data;
	
	protected List<T> limitedData = Lists.newArrayList();
	
	protected Date start, end;
	
	protected boolean inclusive = DEFAULT_INCLUSION;
	
	protected DateLimitedDataContainer() {
		this(new ArrayList<T>());
	}
	
	protected DateLimitedDataContainer(final List<T> data) {
		this.data = data;
		includeAll();
	}

	/**
	 * Removes any date limits imposed.
	 * 
	 * @return the limited object
	 */
	@SuppressWarnings("rawtypes")
	public DateLimitedDataContainer<T> includeAll() {
		for (DateLimitedData dld : data) {
			if (dld instanceof DateLimitedDataContainer) {
				((DateLimitedDataContainer)dld).limitToDateRange(DEFAULT_START, DEFAULT_END, DEFAULT_INCLUSION);
			}
		}
		limitedData = Lists.newArrayList();
		setRange(DEFAULT_START, DEFAULT_END, DEFAULT_INCLUSION);
		return this;
	}
	
	protected void setRange(Date start, Date end, boolean inclusive) {
		this.start = start != null ? start : DEFAULT_START;
		this.end = end != null ? end : DEFAULT_END;
		this.inclusive = inclusive;
	}	
	
	/**
	 * Determines if the data is limited.
	 * 
	 * @return whether or not the data is limited
	 */
	public boolean isLimited() {
		return !start.equals(DEFAULT_START) && !end.equals(DEFAULT_END);
	}
	
	/**
	 * Gets the list associated with the data. 
	 * 
	 * @return if limited, the limited data, else the full data
	 */
	public List<T> getData() {
		return isLimited() ? limitedData : data;
	}
	
	/**
	 * Limits returned data to a specific range.
	 * If you need to override this, don't forget to call super.{@link #limitToDateRange(Date, Date, boolean)}
	 * or it won't work correctly.
	 * 
	 * @param start
	 *            the start date
	 * @param end
	 *            the end data
	 * @param inclusive
	 *            whether or not the range is inclusive
	 * @return the object instance for convenience
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void limitToDateRange(Date start, Date end, boolean inclusive) {
		
		includeAll();
		setRange(start, end, inclusive);
		
		for (DateLimitedData dld : data) {
			if (dld.isInDateRange(start, end, inclusive)) {
				if (dld instanceof DateLimitedDataContainer) { ((DateLimitedDataContainer)dld).limitToDateRange(start, end, inclusive); }
				limitedData.add((T) dld);
			}
		}
		
	}
	
	/**
	 * Delegate for {@link List#add(Object)}.
	 * 
	 * @param t
	 * @return
	 */
	boolean add(T t) {
		return t.isInDateRange(start, end, inclusive) ? data.add(t) && limitedData.add(t) : data.add(t);
	}
	
	/**
	 * Delegate for {@link List#get(int)}.
	 * Works appropriately if the data is limited.
	 * 
	 * @param index
	 * @return
	 */
	public T get(int index) {
		return isLimited() ? limitedData.get(index) : data.get(index);
	}
	
	public DateLimitedDataContainer<T> copy() {
		
		DateLimitedDataContainer<T> theCopy = new DateLimitedDataContainer<T>(this.data);
		theCopy.end = this.end;
		theCopy.inclusive = this.inclusive;
		theCopy.start = this.start;
		theCopy.limitedData = this.limitedData;
		
		return theCopy;
	}
	
}
