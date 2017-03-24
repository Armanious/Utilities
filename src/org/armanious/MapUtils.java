package org.armanious;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MapUtils {
	
	private MapUtils(){}
	
	/*
	 * Coul be *SERIOUSLY* optimized
	 */
	public static <K, V extends Comparable<V>> K[] sortMapLowToHigh(Map<K, V> map, K[] arr){
		final Map<K, V> cloned = new HashMap<>();
		cloned.putAll(map);
		for(int i = 0; i < arr.length; i++){
			K lowest = null;
			for(K k : cloned.keySet()){
				if(lowest == null || cloned.get(k).compareTo(cloned.get(lowest)) < 0){
					lowest = k;
				}
			}
			cloned.remove(lowest);
			arr[i] = lowest;
		}
		return arr;
	}

	public static <K, V> K[] sortMapLowToHigh(Map<K, V> map, Comparator<V> comparator, K[] arr){
		final Map<K, V> cloned = new HashMap<>();
		cloned.putAll(map);
		for(int i = 0; i < arr.length; i++){
			K lowest = null;
			for(K k : cloned.keySet()){
				if(lowest == null || comparator.compare(cloned.get(k), cloned.get(lowest)) < 0){
					lowest = k;
				}
			}
			cloned.remove(lowest);
			arr[i] = lowest;
		}
		return arr;
	}

	public static <K, V extends Comparable<V>> K[] sortMapHighToLow(Map<K, V> map, K[] arr){
		final Map<K, V> cloned = new HashMap<>();
		cloned.putAll(map);
		for(int i = 0; i < arr.length; i++){
			K highest = null;
			for(K k : cloned.keySet()){
				if(highest == null || cloned.get(k).compareTo(cloned.get(highest)) > 0){
					highest = k;
				}
			}
			cloned.remove(highest);
			arr[i] = highest;
		}
		return trimNulls(arr);
	}

	public static <K, V> K[] sortMapHighToLow(Map<K, V> map, Comparator<V> comparator, K[] arr){
		final Map<K, V> cloned = new HashMap<>();
		cloned.putAll(map);
		for(int i = 0; i < arr.length; i++){
			K highest = null;
			for(K k : cloned.keySet()){
				if(highest == null || comparator.compare(cloned.get(k), cloned.get(highest)) > 0){
					highest = k;
				}
			}
			cloned.remove(highest);
			arr[i] = highest;
		}
		return trimNulls(arr);
	}

	@SuppressWarnings("unchecked")
	private static <K> K[] trimNulls(K[] arr){
		int end = arr.length;
		while(--end >= 0 && arr[end] == null);
		int start = -1;
		while(++start < arr.length && arr[start] == null);
		final Object obj = Array.newInstance(arr.getClass().getComponentType(), end - start + 1);
		System.arraycopy(arr, start, obj, 0, end - start + 1);
		return (K[])obj;
	}

	public static <K, V> Map<K, V> filterMap(Filter<K> keyFilter, Map<K, V> src){
		return filterMap(keyFilter, src, new HashMap<K, V>());
	}

	public static <K, V> Map<K, V> filterMap(Filter<K> keyFilter, Map<K, V> src, Map<K, V> dest){
		dest.putAll(src);
		for(K k : src.keySet()){
			if(keyFilter.accept(k)){
				dest.put(k, src.get(k));
			}
		}
		return dest;
	}

}
