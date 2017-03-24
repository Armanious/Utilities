package org.armanious;

import java.lang.reflect.Array;

public class ArrayUtils {
	
	private ArrayUtils(){}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] filter(Filter<T> filter, T[] arr){
		final T[] tmp = (T[]) Array.newInstance(arr.getClass().getComponentType(), arr.length);
		int idx = 0;
		for(T t : arr){
			if(filter.accept(t)){
				tmp[idx++] = t;
			}
		}
		final T[] dest = (T[]) Array.newInstance(tmp.getClass().getComponentType(), idx);
		System.arraycopy(tmp, 0, dest, 0, idx);
		return dest;
	}

}
