package org.armanious.databinding;

import java.util.ArrayList;

public abstract class DataBinding<T> {

	private ArrayList<ObjectData<T>> objectDataList;

	public abstract T get();

	//public abstract void set(T value);

	public final void bind(ObjectData<T> objectData){
		if(objectDataList == null){
			objectDataList = new ArrayList<>();
		}
		objectDataList.add(objectData);
		objectData.set(get());
	}
	
	public final void unbind(ObjectData<T> objectData){
		objectDataList.remove(objectData);
	}

	public final void update(){
		if(objectDataList != null){
			for(ObjectData<T> od : objectDataList){
				od.set(get());
			}
		}
	}

}
