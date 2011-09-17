package com.lkc.constraints;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Textbox;

public class NoEmptyTextConstraint implements Constraint {
	private String fieldName;
	private Constraint constraint;

	public NoEmptyTextConstraint(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public void validate(Component arg0, Object arg1) throws WrongValueException {
		WrongValueException exception = new WrongValueException(arg0, Labels.getLabel("noEmpty", new Object[] { fieldName }));
		if (arg0 instanceof Textbox || arg0 instanceof Combobox) {
			String value = arg1.toString().trim();
			if (value.isEmpty()) {
				throw exception;
			}
			if (constraint != null) {
				constraint.validate(arg0, arg1);
			}
		}
	}

	public Constraint getConstraint() {
		return constraint;
	}

	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}

}
