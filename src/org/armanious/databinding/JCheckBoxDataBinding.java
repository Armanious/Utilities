package org.armanious.databinding;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class JCheckBoxDataBinding extends DataBinding<Boolean> {
	
	private final JCheckBox checkBox;
	
	public JCheckBoxDataBinding(JCheckBox checkBox){
		this.checkBox = checkBox;
		checkBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
		});
	}

	@Override
	public Boolean get() {
		return checkBox.isSelected();
	}

}
