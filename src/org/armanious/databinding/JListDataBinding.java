package org.armanious.databinding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class JListDataBinding<T> extends DataBinding<Collection<T>> {
	
	private final JList<T> list;
	private final Class<?> clazz;
	
	public JListDataBinding(JList<T> list, Class<?> collectionClassToUse){
		this.list = list;
		clazz = collectionClassToUse;
		final ListDataListener ldl = new ListDataListener() {
			@Override
			public void intervalRemoved(ListDataEvent e) {
				update();
			}
			@Override
			public void intervalAdded(ListDataEvent e) {
				update();
			}
			@Override
			public void contentsChanged(ListDataEvent e) {
				update();
			}
		};
		list.getModel().addListDataListener(ldl);
		list.addPropertyChangeListener("model", new PropertyChangeListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				((ListModel<T>)evt.getOldValue()).removeListDataListener(ldl);
				((ListModel<T>)evt.getNewValue()).addListDataListener(ldl);
			}
		});
	
	}
	
	public JListDataBinding(JList<T> list){
		this(list, ArrayList.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> get() {
		final int numElements = list.getModel().getSize();
		Collection<T> collection;
		try {
			collection = (Collection<T>) clazz.getConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			collection = new ArrayList<>();
		}
		for(int i = 0; i < numElements; i++){
			collection.add(list.getModel().getElementAt(i));
		}
		return collection;
	}
}
