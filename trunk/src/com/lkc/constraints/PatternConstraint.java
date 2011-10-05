package com.lkc.constraints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Textbox;

public class PatternConstraint implements Constraint {
	public static final String emailPattern = "[a-zA-Z0-9_]+[\\.[a-zA-Z0-9]+]*@[a-zA-Z0-9_]+[.[a-zA-Z]+]+";
	public static final String integerPattern = "[-]*[\\d]+";
	public static final String integerLargerThanOrEqual0Pattern = "[\\d]+";
	public static final String integerLargerThan0Pattern = "[1-9]+[0-9]*";
	public static final String doublePattern = "[-]*[\\d]+[.]{0,1}[\\d]*";
	public static final String doubleLargerThanOrEqual0Pattern = "[-]*[\\d]+[.]{0,1}[\\d]*";
	public static final String doubleLargerThan0Pattern = "[\\d]+[.]{0,1}[\\d]*";

	private String fieldName;
	private String pattern;
	private String message;
	private Constraint constraint;

	public PatternConstraint(String fieldName, String pattern, String message) {
		this.fieldName = fieldName;
		this.pattern = pattern;
		this.message = message;
	}

	@Override
	public void validate(Component comp, Object valueInput) throws WrongValueException {
		if (comp instanceof Textbox) {
			String value = valueInput.toString().trim();
			Pattern pattern = Pattern.compile(this.pattern);
			Matcher matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				throw new WrongValueException(comp, fieldName + " " + message);
			}
			if (constraint != null) {
				constraint.validate(comp, valueInput);
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
