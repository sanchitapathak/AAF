/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.rosetta;

import java.util.Iterator;

import com.att.inno.env.Env;
import com.att.inno.env.TimeTaken;

public abstract class Marshal<T> implements Parse<T, Marshal.State> {

	/* (non-Javadoc)
	 * @see com.att.rosetta.Parse#newParsed()
	 */
	@Override
	public Parsed<State> newParsed() throws ParseException {
		return new Parsed<State>(new State());
	}

	@Override
	public TimeTaken start(Env env) {
		//TODO is a way to mark not-JSON?
		return env.start("Rosetta Marshal", Env.JSON);
	};

	public static class State {
		// Note:  Need a STATEFUL stack... one that will remain stateful until marked as finished
		// "finished" is know by Iterators with no more to do/null
		// Thus the concept of "Ladder", which one ascends and decends
		public Ladder<Iterator<?>> ladder = new Ladder<Iterator<?>>();
		public boolean smallest = true;
	}

	public static final Iterator<Void> DONE_ITERATOR = new Iterator<Void>() {
		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public Void next() {
			return null;
		}

		@Override
		public void remove() {
		}
	};

	/**
	 * Typical definition of Done is when Iterator in Ladder is "DONE_ITERATOR"
	 * 
	 * It is important, however, that the "Ladder Rung" is set to the right level.
	 * 
	 * @param state
	 * @return
	 */
	public boolean amFinished(State state) {
		return DONE_ITERATOR.equals(state.ladder.peek());
	}

}
