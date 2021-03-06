/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.dao.aaf.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.att.authz.layer.Result;
import com.att.dao.aaf.cass.HistoryDAO;

public class JU_HistoryDAO  extends AbsJUCass {
	
	@Test
	public void testCreate() throws Exception {
		HistoryDAO historyDAO = new HistoryDAO(trans, cluster, AUTHZ);
		HistoryDAO.Data data = createHistoryData();
		
		try {
			historyDAO.create(trans,data);			
			Thread.sleep(200);// History Create is Async
			Result<List<HistoryDAO.Data>> records = historyDAO.readByUser(trans,data.user,data.yr_mon);
			assertTrue(records.isOKhasData());
			for(HistoryDAO.Data d : records.value) {
				assertHistory(data, d);
			}
		} finally {
			historyDAO.close(trans);
		}
	}
	
	@Test
	public void tesReadByUser() throws Exception {
		HistoryDAO historyDAO = new HistoryDAO(trans,cluster, AUTHZ);
		HistoryDAO.Data data = createHistoryData();
		
		try {
			historyDAO.create(trans,data);
			Thread.sleep(200);// History Create is Async
			Result<List<HistoryDAO.Data>> records = historyDAO.readByUser(trans, data.user,data.yr_mon);
			assertTrue(records.isOKhasData());
			for(HistoryDAO.Data d : records.value) {
				assertHistory(data, d);
			}
		} finally {
			historyDAO.close(trans);
		}
	}
	
/*
	@Test
	public void readByUserAndMonth() throws Exception {
		HistoryDAO historyDAO = new HistoryDAO(trans,cluster, AUTHZ);
		HistoryDAO.Data data = createHistoryData();
		
		try {
			historyDAO.create(trans,data);			
			Thread.sleep(200);// History Create is Async
			Result<List<HistoryDAO.Data>> records = historyDAO.readByUserAndMonth(trans,
					data.user, Integer.valueOf(String.valueOf(data.yr_mon).substring(0, 4)),
					Integer.valueOf(String.valueOf(data.yr_mon).substring(4, 6)));
			assertTrue(records.isOKhasData());
			for(HistoryDAO.Data d : records.value) {
				assertHistory(data, d);
			}
		} finally {
			historyDAO.close(trans);
		}
	}
*/	
	//TODO readadd this
//	@Test
//	public void readByUserAndDay() throws Exception {
//		HistoryDAO historyDAO = new HistoryDAO(trans, cluster, AUTHZ);
//		HistoryDAO.Data data = createHistoryData();
//		
//		try {
//			historyDAO.create(trans, data);		
//			Thread.sleep(200);// History Create is Async
//			
//			String dayTime = String.valueOf(data.day_time);
//			String day = null;
//			if (dayTime.length() < 8)
//				day = dayTime.substring(0, 1);
//			else 
//				day = dayTime.substring(0, 2);
//			
//			List<HistoryDAO.Data> records = historyDAO.readByUserBetweenDates(trans,
//							data.user, Integer.valueOf(String.valueOf(data.yr_mon).substring(0, 4)),
//							Integer.valueOf(String.valueOf(data.yr_mon).substring(4, 6)),
//							Integer.valueOf(day), 0);
//			assertEquals(1,records.size());
//			for(HistoryDAO.Data d : records) {
//				assertHistory(data, d);
//			}
//		} finally {
//			historyDAO.close(trans);
//		}
//	}
	private HistoryDAO.Data createHistoryData() {
		HistoryDAO.Data data = HistoryDAO.newInitedData();
		Random random = new Random();
		data.user = "test" + random.nextInt();
		data.action = "add";
		data.target = "history";
		data.memo = "adding a row into history table";
//		data.detail().put("id", "test");
//		data.detail().put("name", "test");
		//String temp = "Test Blob Message";
		data.reconstruct = ByteBuffer.wrap("Temp Blob Message".getBytes());		
		return data;
	}
	
	private void assertHistory(HistoryDAO.Data ip, HistoryDAO.Data op) {
		assertEquals(ip.yr_mon, op.yr_mon);		
//		assertEquals(ip.day_time, op.day_time);		
		assertEquals(ip.user, op.user);		
		assertEquals(ip.action, op.action);
		assertEquals(ip.target, op.target);
		assertEquals(ip.memo, op.memo);
		//TODO : have to see if third party assert utility can be used
//		assertTrue(CollectionUtils.isEqualCollection(ip.detail, op.detail));
//		for (String key : ip.detail().keySet()) {
//			assertNotNull(op.detail().get(key));
//		}
		assertNotNull(op.reconstruct);
	}
	
}
