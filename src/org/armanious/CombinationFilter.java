package org.armanious;

public class CombinationFilter<T> implements Filter<T> {

	public enum Policy {
		ALL_FILTERS,
		ANY_FILTER,
		NO_FILTER;
	}

	private final Policy policy;
	private final Filter<T>[] filters;

	/**
	 * 
	 * @param policy CombinationFilter.Policy value<br/><code><b>ALL_FILTER</b></code>: All given filters must return true for this
	 * CombinationFilter instance to return true.<br/><code><b>ANY_FILTER</b></code>: Any one of the given filters must
	 * return true for this CombinationFilter instance to return true.<br/><code><b>NO_FILTER</b></code>: All given filters
	 * must return false for this CombinationFilter instance to return true.
	 * @param filters The list of filters to combine
	 */
	@SafeVarargs
	public CombinationFilter(Policy policy, Filter<T>...filters){
		this.policy = policy;
		this.filters = filters;
		if(filters == null){
			throw new IllegalArgumentException("Must provide at least one filter.");
		}
	}

	@Override
	public boolean accept(final T t) {
		switch(policy){
		case ALL_FILTERS:
			for(Filter<T> filter : filters){
				if(!filter.accept(t)){
					return false;
				}
			}
			return true;
		case ANY_FILTER:
			for(Filter<T> filter : filters){
				if(filter.accept(t)){
					return true;
				}
			}
			return false;
		case NO_FILTER:
			for(Filter<T> filter : filters){
				if(filter.accept(t)){
					return false;
				}
			}
			return true;
		default:
			return false;
		}
	}

}
