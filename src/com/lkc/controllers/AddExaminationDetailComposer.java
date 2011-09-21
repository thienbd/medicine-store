package com.lkc.controllers;

import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Textbox;

import com.lkc.constraints.NoEmptyTextConstraint;
import com.lkc.constraints.PatternConstraint;
import com.lkc.dao.MedicineDAO;
import com.lkc.entities.Examination;
import com.lkc.entities.ExaminationDetail;
import com.lkc.entities.Medicine;
import com.lkc.utils.ComposerUtil;
import com.lkc.utils.MessageUtil;
import com.lkc.utils.Util;

@SuppressWarnings("unchecked")
public class AddExaminationDetailComposer extends GenericAutowireComposer {
	private static final long serialVersionUID = 8914061444109678764L;
	public static String DETAIL_KEY = "detail";
	public static String SAVE_KEY = "save";

	private Component component;
	private Combobox medicineCombobox;
	private Textbox quantityTextbox;
	private Textbox usingTextbox;

	private Button saveButton;
	private Button closeButton;

	private DelegatingVariableResolver resolver;
	private ActionTrigger actionTrigger;
	private Examination examination;
	private MessageUtil messageUtil;
	private MedicineDAO medicineDAO;

	public AddExaminationDetailComposer() {
		resolver = Util.getSpringDelegatingVariableResolver();
		messageUtil = (MessageUtil) resolver.resolveVariable("messageUtil");
		medicineDAO = (MedicineDAO) resolver.resolveVariable("medicineDAO");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		this.component = comp;
		Map<String, Object> args = execution.getArg();
		examination = (Examination) args.get(ComposerUtil.EXAMINATION_KEY);
		actionTrigger = (ActionTrigger) args.get(ComposerUtil.ACTION_KEY);
		if (examination == null || actionTrigger == null) {
			messageUtil.showError(Labels.getLabel("error"),
					Labels.getLabel("pleaseForwardParam", new Object[] { ComposerUtil.EXAMINATION_KEY }) + " " + Labels.getLabel("and")
							+ " \"" + ComposerUtil.ACTION_KEY + "\"");
			comp.detach();
		} else {
			// Constraint
			addConstraint();
			// Refresh
			refreshData();
			// Listner
			addListener();
		}
	}

	private void refreshData() {
		ComposerUtil composerUtil = (ComposerUtil) resolver.resolveVariable("composerUtil");
		composerUtil.removeAllChilds(medicineCombobox);
		List<String> listMedicine = medicineDAO.loadMedicineName();
		for (String medicine : listMedicine) {
			Comboitem comboitem = new Comboitem(medicine);
			medicineCombobox.appendChild(comboitem);
		}
	}

	private void addConstraint() {
		PatternConstraint quantityConstraint = new PatternConstraint(Labels.getLabel("quantity"),
				PatternConstraint.integerLargerThan0Pattern, Labels.getLabel("mustBeIntegerLargerThan0"));
		quantityTextbox.setConstraint(quantityConstraint);
		NoEmptyTextConstraint medicineConstraint = new NoEmptyTextConstraint(Labels.getLabel("medicine-uper"));
		medicineConstraint.setConstraint(new Constraint() {
			@Override
			public void validate(Component arg0, Object arg1) throws WrongValueException {
				String value = arg1.toString().trim();
				if (!medicineDAO.checkExistByField("name", value)) {
					throw new WrongValueException(arg0, Labels.getLabel("medicine-name-must-exist"));
				}
			}
		});
		medicineCombobox.setConstraint(medicineConstraint);
	}

	private void addListener() {
		closeButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = 8730612242691566930L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				component.detach();
			}
		});
		saveButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = -1747005506811601027L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				try {
					Medicine medicine = medicineDAO.loadByField("name", medicineCombobox.getValue()).get(0);
					ExaminationDetail examinationDetail = new ExaminationDetail(System.currentTimeMillis(), examination, medicine, Integer
							.parseInt(quantityTextbox.getValue()), usingTextbox.getValue());
					component.setAttribute(DETAIL_KEY, examinationDetail);
					component.setAttribute(SAVE_KEY, true);
					component.detach();
					if (actionTrigger != null) {
						actionTrigger.doAction(component);
					}
					messageUtil.showMessage(Labels.getLabel("message"),
							Labels.getLabel("add-examination-detail") + " " + Labels.getLabel("success-lower"));
				} catch (Throwable e) {
					e.printStackTrace();
					messageUtil.showError(Labels.getLabel("error"),
							Labels.getLabel("add-examination-detail") + " " + Labels.getLabel("fail-lower"));
				}
			}
		});
	}
}
