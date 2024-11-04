package com.dsa360.api.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dsa360.api.annotatios.ValidFile;
import com.dsa360.api.dto.DSAApplicationDTO;
import com.dsa360.api.dto.DSA_KYC_DTO;
import com.dsa360.api.dto.DSA_KYC_JSON_BODY;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.service.DSAService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author RAM
 *
 */
@RestController
@RequestMapping("/public")
@Validated
public class PublicApiController {

	@Autowired
	private DSAService dsaService;

	@PostMapping("/dsa-application")
	public ResponseEntity<DSAApplicationDTO> dsaApplication(@RequestBody @Valid DSAApplicationDTO dsaRegistrationDTO) {
		DSAApplicationDTO dsaRegistration = dsaService.dsaApplication(dsaRegistrationDTO);
		return new ResponseEntity<>(dsaRegistration, HttpStatus.CREATED);

	}

	@GetMapping("/get-dsa-application/{dsaId}")
	public ResponseEntity<DSAApplicationDTO> getDsaApplicationData(@PathVariable String dsaId) {

		DSAApplicationDTO dsaById = dsaService.getDSAById(dsaId);

		return new ResponseEntity<>(dsaById, HttpStatus.OK);

	}

	@PostMapping("/syatem-user-kyc")
	public ResponseEntity<String> systemUserKyc(@RequestParam @ValidFile MultipartFile passport,
			@RequestParam @ValidFile MultipartFile drivingLicence, @RequestParam @ValidFile MultipartFile aadharCard,
			@RequestParam @ValidFile MultipartFile panCard, @RequestParam @ValidFile MultipartFile photograph,
			@RequestParam @ValidFile MultipartFile addressProof, @RequestParam @ValidFile MultipartFile bannkPassbook,

			@RequestParam String jsonBody) {

		ObjectMapper objectMapper = new ObjectMapper();
		DSA_KYC_DTO dsa_KYC_DTO = new DSA_KYC_DTO();
		DSA_KYC_JSON_BODY obj = null;
		String message = null;
		try {
			obj = objectMapper.readValue(jsonBody, DSA_KYC_JSON_BODY.class);
			if (obj != null) {
				dsa_KYC_DTO.setDsaApplicationId(obj.getDsaApplicationId());
				dsa_KYC_DTO.setBankName(obj.getBankName());
				dsa_KYC_DTO.setAccountNumber(obj.getAccountNumber());
				dsa_KYC_DTO.setIfscCode(obj.getIfscCode());

				dsa_KYC_DTO.setPassportFile(passport);
				dsa_KYC_DTO.setDrivingLicenceFile(drivingLicence);
				dsa_KYC_DTO.setAadharCardFile(aadharCard);
				dsa_KYC_DTO.setPanCardFile(panCard);
				dsa_KYC_DTO.setPhotographFile(photograph);
				dsa_KYC_DTO.setAddressProofFile(addressProof);
				dsa_KYC_DTO.setBankPassbookFile(bannkPassbook);
				message = dsaService.systemUserKyc(dsa_KYC_DTO);
				return ResponseEntity.ok(message);
			} else {
				throw new SomethingWentWrongException("JSON Parsing Issue");
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(message, HttpStatus.CREATED);

	}

	@GetMapping("/verify-email/{dsaId}")
	public ResponseEntity<String> verifyEmail(@PathVariable String dsaId, @RequestParam String token) {

		dsaService.verifyEmail(dsaId, token);
		try {
			ClassPathResource htmlFile = new ClassPathResource("static/email-verified.html");
			String htmlContent = StreamUtils.copyToString(htmlFile.getInputStream(), StandardCharsets.UTF_8);
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
			return new ResponseEntity<>(htmlContent, headers, HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>("Email Verification Successfully Completed !!", HttpStatus.NOT_FOUND);
		}

	}

}
