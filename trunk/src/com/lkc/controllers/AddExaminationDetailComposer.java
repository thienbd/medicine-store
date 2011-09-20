package com.lkc.controllers;

import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;

import com.lkc.dao.MedicineDAO;
import com.lkc.entities.Examination;
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
			refreshData();
		}
	}

	private void refreshData() {
		List<String> listMedicine = medicineDAO.loadMedicineName();
		medicineCombobox.setModel(new SimpleListModel(listMedicine));
	}
}
