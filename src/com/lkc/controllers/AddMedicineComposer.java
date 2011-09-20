package com.lkc.controllers;

import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

import com.lkc.constraints.NoEmptyTextConstraint;
import com.lkc.constraints.PatternConstraint;
import com.lkc.dao.MedicineDAO;
import com.lkc.entities.Medicine;
import com.lkc.utils.MessageUtil;
import com.lkc.utils.Util;

public class AddMedicineComposer extends GenericAutowireComposer {

	private static final long serialVersionUID = 4095913778619814352L;

	private Textbox nameTextbox;
	private Textbox priceTextbox;
	private Button saveButton;
	private Button closeButton;
	private Component component;

	private DelegatingVariableResolver resolver;
	private MedicineDAO medicineDAO;
	private MessageUtil messageUtil;

	public AddMedicineComposer() {
		resolver = (DelegatingVariableResolver) Util.getSpringDelegatingVariableResolver();
		medicineDAO = (MedicineDAO) resolver.resolveVariable("medicineDAO");
		messageUtil = (MessageUtil) resolver.resolveVariable("messageUtil");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		this.component = comp;
		// Constraint
		addConstraint();
		// Listner
		addListner();
	}

	private void addConstraint() {
		PatternConstraint priceConstraint = new PatternConstraint(Labels.getLabel("price"), PatternConstraint.doubleLargerThan0Pattern,
				Labels.getLabel("mustBeNumberLargerThan0"));
		priceTextbox.setConstraint(priceConstraint);
		NoEmptyTextConstraint nameConstraint = new NoEmptyTextConstraint(Labels.getLabel("name"));
		nameTextbox.setConstraint(nameConstraint);
	}

	private void addListner() {
		closeButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = 6796475098299233079L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				component.detach();
			}
		});
		saveButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = 3848860906471297927L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				List<Medicine> listMedicines = medicineDAO.loadByField("name", nameTextbox.getValue());
				if (listMedicines != null && listMedicines.size() > 0) {
					final Medicine medicine = listMedicines.get(0);
					ActionTrigger replaceActionTrigger = new ActionTrigger() {
						private static final long serialVersionUID = 6958947119122796916L;

						@Override
						public void doAction() throws InterruptedException {
							try {
								medicine.setPrice(Double.valueOf(priceTextbox.getValue()));
								medicineDAO.update(medicine);
								messageUtil.showMessage(Labels.getLabel("message"),
										Labels.getLabel("save") + " " + Labels.getLabel("medicine-lower") + " \"" + medicine.getName()
												+ "\" " + Labels.getLabel("success-lower"));
							} catch (Exception e) {
								messageUtil.showError(Labels.getLabel("error"),
										Labels.getLabel("save") + " " + Labels.getLabel("medicine-lower") + " \"" + medicine.getName()
												+ "\" " + Labels.getLabel("fail-lower"));
							}
						}

						@Override
						public void doAction(Object data) throws Throwable {
							throw new UnsupportedOperationException();
						}

					};
					messageUtil.showConfirm(Labels.getLabel("confirm"),
							Labels.getLabel("confirm-replace-medicine", new Object[] { medicine.getName() }), replaceActionTrigger);
				} else {
					Medicine medicine = new Medicine(System.currentTimeMillis(), nameTextbox.getValue(), Double.valueOf(priceTextbox
							.getValue()));
					try {
						medicineDAO.save(medicine);
						messageUtil.showMessage(Labels.getLabel("message"),
								Labels.getLabel("save") + " " + Labels.getLabel("medicine-lower") + " \"" + medicine.getName() + "\" "
										+ Labels.getLabel("success-lower"));
					} catch (Exception e) {
						messageUtil.showError(Labels.getLabel("error"), Labels.getLabel("save") + " " + Labels.getLabel("medicine-lower")
								+ " \"" + medicine.getName() + "\" " + Labels.getLabel("fail-lower"));
					}
				}
			}
		});
	}
}
