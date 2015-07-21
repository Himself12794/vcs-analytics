package com.cisco.dft.sdk.vcs.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;

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
		limitToDateRange(dateRange.endA, dateRange.endB, dateRange.inclusive);
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
		return dateRange.isInRange(t) ? data.add(t) && limitedData.add(t)
				: data.add(t);
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
	
	/**
	 * The current date range used for this repo. 
	 * 
	 * @return
	 */
	public DateRange getDateRange() {
		return this.dateRange;
	}

	/**
	 * Convenience sub class of {@link DateLimitedDataContainer} that also
	 * provides an implementation of {@link DateLimitedData} for ease of
	 * recursion.
	 * 
	 * @author phwhitin
	 *
	 * @param <T>
	 */
	public static class RecursiveDateLimitedDataContainer<T extends DateLimitedData>
			extends DateLimitedDataContainer<T> implements DateLimitedData {

		public RecursiveDateLimitedDataContainer(List<T> data) {
			super(data);
		}

		/**
		 * Checks to see if this particular piece of data falls within a time
		 * range.
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
		public static final Date DEFAULT_END_A = new Date(0L);

		/** Nov of 5138. An nice thrown out there date for a good end point. */
		public static final Date DEFAULT_END_B = new Date(99999999999999L);

		public static final boolean DEFAULT_INCLUSION = true;

		private Date endA, endB;

		private boolean inclusive;

		public DateRange() {
			setDefault();
		}

		public DateRange(Date endA, Date endB, boolean inclusive) {
			setRange(endA, endB, inclusive);
		}

		public DateRange setRange(Date endA, Date endB, boolean inclusive) {
			setStart(endA);
			setEnd(endB);
			setInclusive(inclusive);
			return this;
		}

		/**
		 * Sets range open-ended.
		 */
		public void setDefault() {
			setRange(DEFAULT_END_A, DEFAULT_END_B, DEFAULT_INCLUSION);
		}

		/**
		 * Checks whether or not range is open-ended.
		 * 
		 * @return
		 */
		public boolean hasAll() {
			return this.endA.equals(DEFAULT_END_A)
					&& this.endB.equals(DEFAULT_END_B);
		}

		public Date getStart() {
			return new Date(endA.getTime());
		}

		public void setStart(Date start) {
			this.endA = start != null ? start : DEFAULT_END_A;
		}

		public Date getEnd() {
			return new Date(endB.getTime());
		}

		public void setEnd(Date end) {
			this.endB = end != null ? end : DEFAULT_END_B;
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
		
		/**
		 * Checks if the date is in the range.
		 * 
		 * @param date
		 * @return
		 */
		public boolean isInRange(Date date) {
			Range<Long> rangeInclusive = Range.closed(endA.getTime(), endB.getTime());
			Range<Long> rangeNonInclusive = Range.open(endA.getTime(), endB.getTime());
			long c = date.getTime();

			return inclusive ? rangeInclusive.contains(c) : rangeNonInclusive.contains(c);

		}

	}

}
