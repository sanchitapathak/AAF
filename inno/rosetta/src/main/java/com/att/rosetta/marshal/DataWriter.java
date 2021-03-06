/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.rosetta.marshal;

import javax.xml.datatype.XMLGregorianCalendar;

import com.att.inno.env.util.Chrono;

/**
 * We make these objects instead of static functions so they can be passed into 
 * FieldArray.
 * 
 *
 * @param <T>
 */
public abstract class DataWriter<T> {
	public abstract boolean write(T t, StringBuilder sb);
	
	public final static DataWriter<String> STRING = new DataWriter<String>() {
		@Override
		public boolean write(String s, StringBuilder sb) {
			sb.append(s);
			return true;
		}		
	};
	
	public final static DataWriter<Integer> INTEGER = new DataWriter<Integer>() {
		@Override
		public boolean write(Integer i, StringBuilder sb) {
			sb.append(i);
			return false;
		}		
	};
	
	public final static DataWriter<Long> LONG = new DataWriter<Long>() {
		@Override
		public boolean write(Long t, StringBuilder sb) {
			sb.append(t);
			return false;
		}		
	};

	public final static DataWriter<Byte> BYTE = new DataWriter<Byte>() {
		@Override
		public boolean write(Byte t, StringBuilder sb) {
			sb.append(t);
			return false;
		}		
	};

	public final static DataWriter<Character> CHAR = new DataWriter<Character>() {
		@Override
		public boolean write(Character t, StringBuilder sb) {
			sb.append(t);
			return true;
		}		
	};

	public final static DataWriter<Boolean> BOOL = new DataWriter<Boolean>() {
		@Override
		public boolean write(Boolean t, StringBuilder sb) {
			sb.append(t);
			return true;
		}		
	};


	/*
	public final static DataWriter<byte[]> BYTE_ARRAY = new DataWriter<byte[]>() {
		@Override
		public boolean write(byte[] ba, StringBuilder sb) {
			ByteArrayInputStream bais = new ByteArrayInputStream(ba);
			StringBuilderOutputStream sbos = new StringBuilderOutputStream(sb);
//			try {
				//TODO find Base64
//				Symm.base64noSplit().encode(bais, sbos);
//			} catch (IOException e) {
//				// leave blank
//			}
			return true;
		}
		
	};
	*/

	public final static DataWriter<XMLGregorianCalendar> DATE = new DataWriter<XMLGregorianCalendar>() {
		@Override
		public boolean write(XMLGregorianCalendar t, StringBuilder sb) {
			sb.append(Chrono.dateOnlyStamp(t));
			return true;
		}
	};
	
	public final static DataWriter<XMLGregorianCalendar> DATE_TIME = new DataWriter<XMLGregorianCalendar>() {
		@Override
		public boolean write(XMLGregorianCalendar t, StringBuilder sb) {
			sb.append(Chrono.dateTime(t));
			return true;
		}
	};

	private static final char[] chars="0123456789ABCDEF".toCharArray();
	public final static DataWriter<byte[]> HEX_BINARY = new DataWriter<byte[]>() {
		@Override
		public boolean write(byte[] ba, StringBuilder sb) {
			// FYI, doing this because don't want intermediate 
			// String in "HexString" or the processing in
			// "String.format"
			//sb.append("0x");
			for(int i=0;i<ba.length;++i) {
				byte b = ba[i];
				sb.append(chars[((b&0xF0)>>4)]);
				sb.append(chars[b&0xF]);
			}
			return true;
		}
	};

}
