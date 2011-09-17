package com.lkc.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import com.lkc.dao.PatientDAO;
import com.lkc.entities.Patient;

public class PatientLazyList implements List<Patient> {

	private ArrayList<Patient> data = new ArrayList<Patient>();
	private PatientDAO patientDAO;
	private int count = 0;

	public PatientLazyList() {
		DelegatingVariableResolver resolver = Util.getSpringDelegatingVariableResolver();
		patientDAO = (PatientDAO) resolver.resolveVariable("patientDAO");
	}

	@Override
	public int size() {
		count = (int) patientDAO.count();
		return count;
	}

	@Override
	public boolean isEmpty() {
		return patientDAO.count() <= 0;
	}

	@Override
	public boolean contains(Object o) {
		return patientDAO.countByFullName(((Patient) o).getFullName()) > 0;
	}

	@Override
	public Iterator<Patient> iterator() {
		return data.iterator();
	}

	@Override
	public Object[] toArray() {
		return data.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return data.toArray(a);
	}

	@Override
	public boolean add(Patient e) {
		return false;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends Patient> c) {
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Patient> c) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {
		data.clear();
	}

	@Override
	public Patient get(int index) {
		List<Patient> listPatients = patientDAO.load(index, index + 1);
		return listPatients.get(0);
	}

	@Override
	public Patient set(int index, Patient element) {
		return element;
	}

	@Override
	public void add(int index, Patient element) {
		// TODO Auto-generated method stub
	}

	@Override
	public Patient remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<Patient> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<Patient> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Patient> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

}
