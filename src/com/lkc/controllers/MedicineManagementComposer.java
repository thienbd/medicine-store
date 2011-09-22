package com.lkc.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.lkc.dao.MedicineDAO;
import com.lkc.entities.Medicine;
import com.lkc.utils.ComposerUtil;
import com.lkc.utils.MessageUtil;
import com.lkc.utils.Util;

public class MedicineManagementComposer extends GenericAutowireComposer {

	private static final long serialVersionUID = -7763636671952856478L;
	private Component component;

	private Textbox searchMedicineTextbox;
	private Button searchMedicineButton;
	private boolean searched = false;
	private String searchData = "";

	private Button addMedicineButton;
	private Rows medicineRows;
	private Paging medicinePaging;
	private int currentPage = 1;
	private int numOfPage = 1;
	private int medicinePerPage = 10;

	private DelegatingVariableResolver resolver;
	private MedicineDAO medicineDAO;
	private ComposerUtil composerUtil;
	private MessageUtil messageUtil;

	public MedicineManagementComposer() {
		resolver = Util.getSpringDelegatingVariableResolver();
		medicineDAO = (MedicineDAO) resolver.resolveVariable("medicineDAO");
		composerUtil = (ComposerUtil) resolver.resolveVariable("composerUtil");
		messageUtil = (MessageUtil) resolver.resolveVariable("messageUtil");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		this.component = comp;
		// Listener
		addListener();
		// Refresh
		refreshMedicines();
	}

	private void addListener() {
		addMedicineButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {

			private static final long serialVersionUID = 1416055834052387634L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(ComposerUtil.ACTION_KEY, refreshActionTrigger);
				Window window = (Window) execution.createComponents("/addMedicine.zul", component, params);
				try {
					window.setClosable(true);
					window.doModal();
				} catch (Exception e) {
					e.printStackTrace();
					try {
						window.detach();
					} catch (Exception ex) {
					}
				}
			}
		});
		SerializableEventListener searchEvent = new SerializableEventListener() {
			private static final long serialVersionUID = 3212992361144848710L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				searchData = searchMedicineTextbox.getValue().trim();
				searched = (!searchData.isEmpty());
				refreshMedicines();
			}

		};
		searchMedicineTextbox.addEventListener(Events.ON_OK, searchEvent);
		searchMedicineButton.addEventListener(Events.ON_CLICK, searchEvent);
	}

	private void refreshMedicines() {
		composerUtil.removeAllChilds(medicineRows);
		List<Medicine> listMedicines = null;
		if (searched) {
			Map<String, String> searchMapData = new HashMap<String, String>();
			searchMapData.put("name", searchData);
			loadMedicinePage(searchMapData);
			listMedicines = medicineDAO.loadSearch((currentPage - 1) * medicinePerPage, medicinePerPage, searchMapData);
		} else {
			loadMedicinePage(null);
			listMedicines = medicineDAO.load((currentPage - 1) * medicinePerPage, medicinePerPage);
		}
		int i = 0;
		for (final Medicine medicine : listMedicines) {
			Row row = new Row();
			medicineRows.appendChild(row);
			Label nobLabel = new Label(String.valueOf(++i));
			row.appendChild(nobLabel);
			Label nameLabel = new Label(medicine.getName());
			row.appendChild(nameLabel);
			Label priceLabel = new Label(String.valueOf(medicine.getPrice()));
			row.appendChild(priceLabel);
			Hbox actionHbox = new Hbox();
			row.appendChild(actionHbox);
			Button editButton = new Button(Labels.getLabel("edit"));
			actionHbox.appendChild(editButton);
			editButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
				private static final long serialVersionUID = -6933017148519067335L;

				@Override
				public void onEvent(Event arg0) throws Exception {
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(ComposerUtil.ACTION_KEY, refreshActionTrigger);
					params.put(ComposerUtil.MEDICINE_KEY, medicine);
					Window window = (Window) execution.createComponents("/addMedicine.zul", component, params);
					try {
						window.setClosable(true);
						window.doModal();
					} catch (Exception e) {
						e.printStackTrace();
						try {
							window.detach();
						} catch (Exception ex) {
						}
					}
				}
			});
			Button deleteButton = new Button(Labels.getLabel("delete"));
			actionHbox.appendChild(deleteButton);
			deleteButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
				private static final long serialVersionUID = -2172969624015500817L;

				@Override
				public void onEvent(Event arg0) throws Exception {
					messageUtil.showConfirm(Labels.getLabel("confirm"),
							Labels.getLabel("confirm-delete-medicine", new Object[] { medicine.getName() }), new ActionTrigger() {
								private static final long serialVersionUID = 3481165408946064159L;

								@Override
								public void doAction(Object data) throws Throwable {
									throw new UnsupportedOperationException();
								}

								@Override
								public void doAction() throws Throwable {
									medicineDAO.delete(medicine);
									refreshMedicines();
								}
							});
				}
			});
		}
	}

	private void loadMedicinePage(Map<String, String> searchMapData) {
		long count = 0;
		if (searchMapData != null) {
			count = medicineDAO.countSearch(searchMapData);
		} else {
			count = medicineDAO.count();
		}
		numOfPage = (int) (count / medicinePerPage + (count % medicinePerPage > 0 ? 1 : 0));
		if (currentPage > numOfPage) {
			currentPage = numOfPage;
		} else {
			if (currentPage < 1) {
				currentPage = 1;
			}
		}
		medicinePaging.setTotalSize(numOfPage);
		medicinePaging.setPageSize(1);
		medicinePaging.setActivePage(currentPage - 1);
		medicinePaging.setVisible(numOfPage > 1);
	}

	private ActionTrigger refreshActionTrigger = new ActionTrigger() {
		private static final long serialVersionUID = 421837411356308912L;

		@Override
		public void doAction(Object data) throws Throwable {
			throw new UnsupportedOperationException();
		}

		@Override
		public void doAction() throws Throwable {
			refreshMedicines();
		}
	};
}
