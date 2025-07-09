package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.ExpenseType;

public interface ExpenseTypeService {

	public ExpenseType addEditExpenseType(ExpenseType request);

	public ExpenseType getExpenseType(String expenseTypeId);

	public Boolean deleteExpenseType(String expenseTypeId, Boolean discarded);

	public List<ExpenseType> getExpenseType(int page, int size, String searchTerm, Boolean discarded);

}
