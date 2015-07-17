package com.cisco.dft.sdk.vcs.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Represents a data structure whose data entries can be restricted to a certain
 * time frame. This class is recursive, so if {@code T} is also an instance of
 * DateLimitedDataContainer, it will check the date limit on that as well, and
 * so on. If desired, this can also work similarly to a list as long as all of
 * its entries implement {@link DateLimitedData}
 * 
 * @author phwhitin
 *
 * @param <T>
 */
public class DateLimitedDataContainer<T extends DateLimitedData> {

	protected final List<T> data;

	protected List<T> limitedData = Lists.newArrayList();

	protected DateRange dateRange = new DateRange();

	public DateLimitedDataContainer() {
		this(new ArrayList<T>());
	}

	public DateLimitedDataContainer(final List<T> data) {
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
				((DateLimitedDataContainer) dld).limitToDateRange(null, null,
						true);
			}
		}
		limitedData = Lists.newArrayList();
		dateRange.setDefault();
		return this;
	}

	/**
	 * Determines if the data is limited.
	 * 
	 * @return whether or not the data is limited
	 */
	public boolean isLimited() {
		return !dateRange.hasAll();
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
	 * Limits returned data to a specific range. If you need to override this,
	 * don't forget to call super.{@link #limitToDateRange(DateRange)} or it
	 * won't work correctly.
	 * 
	 * @param dateRange
	 *            the date range to use
	 */
	public void limitToDateRange(DateRange dateRange) {
		limitToDateRange(dateRange.start, dateRange.end, dateRange.inclusive);
	}

	/**
	 * Limits returned data to a specific range. If you need to override this,
	 * don't forget to call super.{@link #limitToDateRange(Date, Date, boolean)}
	 * or it won't work correctly.
	 * 
	 * @param start
	 *            the start date
	 * @param end
	 *            the end data
	 * @param inclusive
	 *            whether or not the range is inclusive
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void limitToDateRange(Date start, Date end, boolean inclusive) {

		includeAll();
		dateRange.setRange(start, end, inclusive);

		for (DateLimitedData dld : data) {
			if (dld.isInDateRange(dateRange)) {
				if (dld instanceof DateLimitedDataContainer) {
					((DateLimitedDataContainer) dld).limitToDateRange(start,
							end, inclusive);
				}
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
	public boolean add(T t) {
		return dateRange.isInRange(t) ? data.add(t) && limitedData.add(t) : data
				.add(t);
	}

	/**
	 * Delegate for {@link List#get(int)}. Works appropriately if the data is
	 * limited.
	 * 
	 * @param index
	 * @return
	 */
	public T get(int index) {
		return isLimited() ? limitedData.get(index) : data.get(index);
	}

	public DateLimitedDataContainer<T> copy() {

		DateLimitedDataContainer<T> theCopy = new DateLimitedDataContainer<T>(this.data);
		theCopy.dateRange = this.dateRange;
		theCopy.limitedData = this.limitedData;

		return theCopy;
	}
	
	public DateRange getDateRange() {
		return this.dateRange;
	}
	
	/**
	 * Convenience sub class of {@link DateLimitedDataContainer} that also provides an implementation of 
	 * {@link DateLimitedData} for ease of recursion.
	 * 
	 * @author phwhitin
	 *
	 * @param <T>
	 */
	public static class RecursiveDateLimitedDataContainer<T extends DateLimitedData> extends
			DateLimitedDataContainer<T> implements DateLimitedData {


		public RecursiveDateLimitedDataContainer(List<T> data) {
			super(data);
		}
		
		/**
		 * Checks to see if this particular piece of data falls within a time range.
		 */
		@Override
		public boolean isInDateRange(DateRange dateRange) {
			boolean flag = false;

			for (T t : data) {
				flag |= t.isInDateRange(dateRange);
			}

			return flag;
		}

	}
	
	/**
	 * Wrapper class for a date range.
	 * 
	 * @author phwhitin
	 *
	 */
	public static class DateRange {

		/** The epoch start date */
		public static final Date DEFAULT_START = new Date(0L);

		/** Nov of 5138. An nice thrown out there date for a good end point. */
		public static final Date DEFAULT_END = new Date(99999999999999L);

		public static final boolean DEFAULT_INCLUSION = true;

		private Date start, end;

		private boolean inclusive;

		public DateRange() {
			setDefault();
		}

		public DateRange(Date start, Date end, boolean inclusive) {
			setRange(start, end, inclusive);
		}

		public void setRange(Date start, Date end, boolean inclusive) {
			setStart(start);
			setEnd(end);
			setInclusive(inclusive);
		}

		/**
		 * Sets range open-ended.
		 */
		public void setDefault() {
			setRange(DEFAULT_START, DEFAULT_END, DEFAULT_INCLUSION);
		}

		/**
		 * Checks whether or not range is open-ended.
		 * 
		 * @return
		 */
		public boolean hasAll() {
			return this.start.equals(DEFAULT_START)
					&& this.end.equals(DEFAULT_END);
		}

		public Date getStart() {
			return new Date(start.getTime());
		}

		public void setStart(Date start) {
			this.start = start != null ? start : DEFAULT_START;
		}

		public Date getEnd() {
			return new Date(end.getTime());
		}

		public void setEnd(Date end) {
			this.end = end != null ? end : DEFAULT_END;
		}

		public boolean isInclusive() {
			return inclusive;
		}

		public void setInclusive(boolean inclusive) {
			this.inclusive = inclusive;
		}

		/**
		 * Checks if the specified data falls in the range this object currently
		 * has. 
		 * 
		 * @param dld
		 * @return
		 */
		public boolean isInRange(DateLimitedData dld) {
			return dld.isInDateRange(this);
		}

	}

}
