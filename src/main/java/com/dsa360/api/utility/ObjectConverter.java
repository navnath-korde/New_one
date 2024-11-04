package com.dsa360.api.utility;

import org.springframework.stereotype.Component;

import com.dsa360.api.dto.DSA_KYC_DTO;
import com.dsa360.api.entity.DSAApplicationEntity;
import com.dsa360.api.entity.DSA_KYC_Entity;

@Component
public class ObjectConverter {


	public Object dtoToEntity(Object sourceObject) {
		DSA_KYC_Entity kycEntity = null;
		if (sourceObject instanceof DSA_KYC_DTO) {
			DSA_KYC_DTO dto = (DSA_KYC_DTO) sourceObject;
			DSAApplicationEntity dsaRegistrationEntity = new DSAApplicationEntity();
			dsaRegistrationEntity.setDsaApplicationId(dto.getDsaApplicationId());
			kycEntity = new DSA_KYC_Entity();
			kycEntity.setDsaKycId(dto.getDsaKycId());
			kycEntity.setDsaApplicationId(dsaRegistrationEntity);
			kycEntity.setBankName(dto.getBankName());
			kycEntity.setAccountNumber(dto.getAccountNumber());
			kycEntity.setIfscCode(dto.getIfscCode());

			kycEntity.setPassport(dto.getPassportFile().getOriginalFilename());
			kycEntity.setDrivingLicence(dto.getDrivingLicenceFile().getOriginalFilename());
			kycEntity.setAadharCard(dto.getAadharCardFile().getOriginalFilename());
			kycEntity.setPanCard(dto.getPanCardFile().getOriginalFilename());
			kycEntity.setPhotograph(dto.getPhotographFile().getOriginalFilename());
			kycEntity.setAddressProof(dto.getAddressProofFile().getOriginalFilename());
			kycEntity.setBankPassbook(dto.getBankPassbookFile().getOriginalFilename());

			kycEntity.setApprovalStatus(dto.getApprovalStatus());
			
			return kycEntity;

		}
		
	
		return null;

		

	}

}
