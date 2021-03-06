/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.rosetta.marshal;

import java.util.Iterator;

import com.att.rosetta.Ladder;
import com.att.rosetta.Marshal;
import com.att.rosetta.ParseException;
import com.att.rosetta.Parsed;

/**
 * Object Marshal
 * Assumes has Fields and other Objects
 * s
 *
 * @param <T>
 */
public abstract class ObjMarshal<T> extends Marshal<T> {
	// Note: Not Using List or ArrayList, because there is no "Peek" concept in their iterator.
	private Marshal<T>[] pml;
	private int end=0;
	
	/**
	 * @param pm
	 */
	@SuppressWarnings("unchecked")
	protected void add(Marshal<T> pm) {
		if(pml==null) {
			pml = new Marshal[Ladder.DEFAULT_INIT_SIZE]; 
		} else if(end>pml.length) {
			Object temp[] = pml; 
			pml = new Marshal[pml.length+Ladder.DEFAULT_INIT_SIZE];
			System.arraycopy(temp, 0, pml, 0, pml.length);
		}
		pml[end]=pm;
		++end;
	}
	
	/* (non-Javadoc)
	 * @see com.att.rosetta.Parse#parse(java.lang.Object, com.att.rosetta.Parsed)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Parsed<State> parse(T in, Parsed<State> parsed) throws ParseException {
		Ladder<Iterator<?>> ladder = parsed.state.ladder;
		Iterator<Marshal<T>> iter = (Iterator<Marshal<T>>)ladder.peek();
		if(iter==null) {
			if(pml.length>0) {
				ladder.push(new FieldsIterator());
				parsed.event = START_OBJ;
			} else {
				ladder.push(DONE_ITERATOR);
			}
		} else if (DONE_ITERATOR.equals(iter)) {
		} else {
			FieldsIterator fields = (FieldsIterator)iter;
			ladder.ascend(); // look at field info
				Iterator<?> currFieldIter = ladder.peek();
				Marshal<T> marshal;
				if(currFieldIter==null) {
					marshal=fields.next();
				} else if(!DONE_ITERATOR.equals(currFieldIter)) {
					marshal=fields.peek();
					if(marshal==null && fields.hasNext())marshal=fields.next();
				} else if(fields.hasNext()) {
					marshal=fields.next();
					ladder.push(null);
				} else {
					marshal=null;
				}
				
				if(marshal!=null)
					parsed = marshal.parse(in, parsed);
			ladder.descend();
			if(marshal==null || parsed.event==NONE) {
				parsed.event = END_OBJ;
				ladder.push(DONE_ITERATOR);
			}
		}
		return parsed; // if unchanged, then it will end process
	}

	private class FieldsIterator implements Iterator<Marshal<T>> {
		private int idx = -1;

		@Override
		public boolean hasNext() {
			return idx<end;
		}

		@Override
		public Marshal<T> next() {
			return pml[++idx];
		}

		public Marshal<T> peek() {
			return idx<0?null:pml[idx];
		}
		
		@Override
		public void remove() {
			pml[idx]=null;
		}
		
	}

}
