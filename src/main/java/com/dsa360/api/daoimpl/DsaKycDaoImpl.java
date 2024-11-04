package com.dsa360.api.daoimpl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dsa360.api.dao.DsaKycDao;
import com.dsa360.api.entity.DSA_KYC_Entity;
import com.dsa360.api.exceptions.SomethingWentWrongException;

@Repository
public class DsaKycDaoImpl implements DsaKycDao {

	private static final Logger logger = LoggerFactory.getLogger(DsaKycDaoImpl.class);
	@Autowired
	private SessionFactory factory;

	@Override
	public List<DSA_KYC_Entity> getAllKycs() {
		List<DSA_KYC_Entity> list = null;
		try (Session session = factory.openSession()) {
			Criteria criteria = session.createCriteria(DSA_KYC_Entity.class);
			list = criteria.list();

		} catch (Exception e) {
			logger.error("Exception occurred during retrive All Kycs :{}", e);
			throw new SomethingWentWrongException("Something went wrong during retrive all kycs");

		}
		return list;
	}

}
