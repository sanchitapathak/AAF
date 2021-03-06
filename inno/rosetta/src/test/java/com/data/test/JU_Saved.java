/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.data.test;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

import com.att.inno.env.TimeTaken;
import com.att.inno.env.Trans;
import com.att.inno.env.impl.EnvFactory;
import com.att.inno.env.util.StringBuilderWriter;
import com.att.rosetta.InJson;
import com.att.rosetta.JaxInfo;
import com.att.rosetta.OutJson;
import com.att.rosetta.OutXML;
import com.att.rosetta.Saved;

import s.xsd.LargerData;

public class JU_Saved<b> {
	private static int ITERATIONS = 100000;

	@Test
	public void test() throws Exception {
		InJson inJSON = new InJson();
		OutDump dump = new OutDump();
		JaxInfo ji = JaxInfo.build(LargerData.class);
		OutXML xml = new OutXML(ji);;
		OutJson json = new OutJson();
		
		Saved saved = new Saved();
		
		StringBuilderWriter sbw = new StringBuilderWriter(1024);
		
		Trans trans;
		Report report = new Report(ITERATIONS,"Save","Dump","XML ","JSON");
		do {
			sbw.reset();
			trans = EnvFactory.newTrans();
			Reader sr = new StringReader(JU_FromJSON.str);
			TimeTaken tt = trans.start("Parse Text, and Save", 1);
			try {
				saved.load(sr, inJSON);
			} finally {
				tt.done();
			}

//			sbw.append("==== Start Direct Raw =====\n");
//			new OutRaw().extract(new StringReader(JU_FromJSON.str), sbw, inJSON);
//			
//			sbw.append("==== Start Raw from Saved =====\n");
//			new OutRaw().extract(null,sbw,saved);

			sbw.append("==== Start Dump from Saved =====\n");
			tt = trans.start("Dump", 2);
			try {
				dump.extract(null,sbw,saved);
			} finally {
				tt.done();
			}
			
			sbw.append("\n==== Start XML =====\n");
			tt = trans.start("XML", 3);
			try {
				xml.extract(null,sbw,saved);
			} finally {
				tt.done();
			}
			
			sbw.append("\n==== Start JSON =====\n");
			tt = trans.start("JSON", 4);
			try {
				json.extract(null,sbw,saved);
			} finally {
				tt.done();
			}
			report.glean(trans,1,2,3,4);
		} while(report.go());
		
		report.report(sbw);
		System.out.println(sbw.toString());

	}
}
