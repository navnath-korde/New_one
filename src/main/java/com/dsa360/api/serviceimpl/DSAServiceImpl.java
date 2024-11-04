package com.dsa360.api.serviceimpl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsa360.api.dao.DSADao;
import com.dsa360.api.dto.DSAApplicationDTO;
import com.dsa360.api.dto.DSA_KYC_DTO;
import com.dsa360.api.entity.DSAApplicationEntity;
import com.dsa360.api.entity.DSA_KYC_Entity;
import com.dsa360.api.exceptions.ResourceNotFoundException;
import com.dsa360.api.service.DSAService;
import com.dsa360.api.utility.DynamicID;
import com.dsa360.api.utility.FileStorageUtility;
import com.dsa360.api.utility.MailAsyncServices;
import com.dsa360.api.utility.ObjectConverter;

/**
 * @author RAM
 *
 */
@Service
@Transactional(readOnly = true)
public class DSAServiceImpl implements DSAService {

	@Autowired
	private DSADao dao;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	MailAsyncServices mailAsyncServices;

	@Autowired
	private FileStorageUtility fileStorageUtility;

	@Autowired
	private ObjectConverter converter;

	@Value("${file.upload}")
	private String uploadDir;

	@Override
	public DSAApplicationDTO dsaApplication(DSAApplicationDTO dsaApplicationDTO) {
		dsaApplicationDTO.setDsaApplicationId(DynamicID.getDynamicId());

		DSAApplicationEntity dsaApplicationEntity = mapper.map(dsaApplicationDTO, DSAApplicationEntity.class);

		DSAApplicationEntity registerDSA = dao.dsaApplication(dsaApplicationEntity);
		if (registerDSA != null) {

			mailAsyncServices.sendApplicationConfirmationEmail(dsaApplicationDTO);

			return dsaApplicationDTO;

		}
		return null;

	}

	@Override
	public DSAApplicationDTO getDSAById(String dsaRegId) {

		DSAApplicationEntity dsaEntity = dao.getDSAById(dsaRegId);
		DSAApplicationDTO dsaDto = mapper.map(dsaEntity, DSAApplicationDTO.class);

		return dsaDto;
	}

	@Override
	public String notifyReview(String applicationId, String approvalStatus, String type) {
		DSAApplicationEntity entity = dao.notifyReview(applicationId, approvalStatus, type);

		mailAsyncServices.dsaReviewMail(entity.getEmailAddress(), entity.getFirstName() + " " + entity.getLastName(),
				approvalStatus, type);

		return approvalStatus;
	}

	@Override
	public String systemUserKyc(DSA_KYC_DTO kyc_DTO) {

		DSAApplicationDTO dsaRegDTO = getDSAById(kyc_DTO.getDsaApplicationId());

		if (dsaRegDTO != null) {
			String kycId = DynamicID.getDynamicId();
			kyc_DTO.setDsaKycId(kycId);

			List<Path> storedFilePaths = fileStorageUtility.storeFiles(kyc_DTO.getDsaApplicationId(),
					kyc_DTO.getPassportFile(), kyc_DTO.getDrivingLicenceFile(), kyc_DTO.getAadharCardFile(),
					kyc_DTO.getPanCardFile(), kyc_DTO.getPhotographFile(), kyc_DTO.getAddressProofFile(),
					kyc_DTO.getBankPassbookFile());

			DSA_KYC_Entity entity = (DSA_KYC_Entity) converter.dtoToEntity(kyc_DTO);

			DSAApplicationEntity dsaById = dao.systemUserKyc(entity, storedFilePaths);

			List<String> docs = new ArrayList<String>();
			docs.add(entity.getAadharCard());
			docs.add(entity.getAddressProof());
			docs.add(entity.getBankPassbook());
			docs.add(entity.getDrivingLicence());
			docs.add(entity.getPanCard());
			docs.add(entity.getPassport());

			mailAsyncServices.sendKycSubmittedEmail(dsaById.getEmailAddress(), kycId, dsaById.getDsaApplicationId(),
					dsaById.getFirstName() + " " + dsaById.getLastName(), dsaById.getContactNumber(),
					dsaById.getStreetAddress(), docs);
		} else {
			throw new ResourceNotFoundException("Invalid DSA Application Id = " + kyc_DTO.getDsaApplicationId());
		}

		return "KYC Submitted";
	}

	@Override
	public List<DSAApplicationDTO> getAllDsaApplication() {
		List<DSAApplicationEntity> list = dao.getAllDsaApplication();
		if (!list.isEmpty()) {
			return list.stream().map(entity -> mapper.map(entity, DSAApplicationDTO.class))
					.collect(Collectors.toList());
		} else {
			throw new ResourceNotFoundException("DSA Application data not found");
		}
	}

	@Override
	public DSA_KYC_Entity getDsaKycByDsaId(String dsaApplicationId) {
		DSA_KYC_Entity dsaKyc = dao.getDsaKycByDsaId(dsaApplicationId);
		dsaKyc.setPassport("assets/images/kyc-docs/" + dsaApplicationId + "/" + dsaKyc.getPassport());
		dsaKyc.setDrivingLicence("assets/images/kyc-docs/" + dsaApplicationId + "/" + dsaKyc.getDrivingLicence());
		dsaKyc.setAadharCard("assets/images/kyc-docs/" + dsaApplicationId + "/" + dsaKyc.getAadharCard());
		dsaKyc.setPanCard("assets/images/kyc-docs/" + dsaApplicationId + "/" + dsaKyc.getPanCard());
		dsaKyc.setAddressProof("assets/images/kyc-docs/" + dsaApplicationId + "/" + dsaKyc.getAddressProof());
		dsaKyc.setBankPassbook("assets/images/kyc-docs/" + dsaApplicationId + "/" + dsaKyc.getBankPassbook());
dsaKyc.setPhotograph("assets/images/kyc-docs/" + dsaApplicationId + "/" + dsaKyc.getPhotograph());
		return dsaKyc;
	}

	@Override
	public void emailVerificationRequest(String dsaId) {

		String token = java.util.UUID.randomUUID().toString();
		DSAApplicationEntity dsaEntity = dao.emailVerificationRequest(dsaId, token);

		String dsaName = dsaEntity.getFirstName() + " " + dsaEntity.getLastName();
		String emailTo = dsaEntity.getEmailAddress();

		mailAsyncServices.emailVerificationRequestMail(dsaId, dsaName, emailTo, token);

	}

	@Override
	public void verifyEmail(String dsaId, String token) {

		dao.verifyEmail(dsaId, token);
	}

	@Override
	public List<String> getAllApprovedDsa() {

		return dao.getAllApprovedDsa();
	}

}
