package org.armanious.databinding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public class JTextComponentDataBinding extends DataBinding<String> {

	private final JTextComponent textComponent;

	public JTextComponentDataBinding(JTextComponent textComponent){
		this.textComponent = textComponent;
		final DocumentListener dl = new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}
		}; 
		textComponent.getDocument().addDocumentListener(dl);
		textComponent.addPropertyChangeListener("document", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				((Document)evt.getOldValue()).removeDocumentListener(dl);
				((Document)evt.getNewValue()).addDocumentListener(dl);
				update();
			}
		});
	}

	@Override
	public String get() {
		return textComponent.getText();
	}

	/*@Override
	public void set(String value) {
		textComponent.setText(value);
	}*/

}