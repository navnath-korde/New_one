package com.dsa360.api.service;

import java.util.List;

import com.dsa360.api.dto.DSAApplicationDTO;
import com.dsa360.api.dto.DSA_KYC_DTO;
import com.dsa360.api.entity.DSA_KYC_Entity;

/**
 * @author RAM
 *
 */
public interface DSAService {

	public abstract DSAApplicationDTO getDSAById(String dsaID); //public

	public abstract DSAApplicationDTO dsaApplication(DSAApplicationDTO dsaRegistrationDTO); //public

	public abstract List<DSAApplicationDTO> getAllDsaApplication(); //admin, subadmin
	
	public abstract List<String> getAllApprovedDsa();

	public abstract String notifyReview(String registrationId, String approvalStatus, String type);// subadmin

	public abstract String systemUserKyc(DSA_KYC_DTO kyc_DTO); //public
	
	public abstract DSA_KYC_Entity getDsaKycByDsaId(String dsaRegistrationId);// subadmin
	
	public abstract void emailVerificationRequest(String dsaId);
	
	public abstract void verifyEmail(String dsaId,String token);
	
	
}
