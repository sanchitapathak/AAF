/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
/**
 * 
 */
package com.att.inno.env.impl;

import com.att.inno.env.APIException;
import com.att.inno.env.Env;
import com.att.inno.env.LifeCycle;



/**
 * <h1>NullLifeCycle</h1>
 * 
 * This is a convenience class for those Objects which should
 * implement LifeCycle, but don't have anything to do in any of the 
 * LifeCycle methods defined. Extending
 * NullLifeCycle reduces the required methods for the class by 5.  
 * Any one or two of them can be overloaded.<p>
 * 
 * If more are overloaded, it is
 * recommended just to implement LifeCycle.
 * <p>
 * 
 * This only works, though, if the Object doesn't need to extend something
 * else, due to Java's Single Extension policy.  In other cases, just
 * implement LifeCycle, and leave them empty.
 * 
 *
 */
public class NullLifeCycle implements LifeCycle {
	public void servicePrestart(Env env) throws APIException {}
	public void threadPrestart(Env env) throws APIException {}
	public void refresh(Env env) throws APIException {}
	public void threadDestroy(Env env) throws APIException {}
	public void serviceDestroy(Env env) throws APIException {}
}