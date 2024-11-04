package com.dsa360.api.service;

import com.dsa360.api.dto.CustomerDTO;

public interface CustomerService {

	public abstract void createCustomerRecord(CustomerDTO customerDto);
	
	public abstract String checkLoanEligibility(String customerId);
	
}
