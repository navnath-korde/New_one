package com.dsa360.api.serviceimpl;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsa360.api.dao.DsaKycDao;
import com.dsa360.api.entity.DSA_KYC_Entity;
import com.dsa360.api.service.DsaKycService;

/**
 * @author Nath
 *
 */
@Service
@Transactional(readOnly = true)
public class DsaKycServiceImpl implements DsaKycService {
	
	private static final Logger lOGGER = LoggerFactory.getLogger(DsaKycServiceImpl.class);
	
	
	
	@Autowired
	private DsaKycDao kycDao;

	@Override
	public List<DSA_KYC_Entity> getAllKycs() {
		lOGGER.info("In get all kyc");
			List<DSA_KYC_Entity> allKycs = kycDao.getAllKycs();
			if(!allKycs.isEmpty()) {
//				 return allKycs.stream()
//		                  .map(kyc -> mapper.map(kyc, DSA_KYC_DTO.class))
//		                  .collect(Collectors.toList());
			}
			
		return allKycs;
	}

}
