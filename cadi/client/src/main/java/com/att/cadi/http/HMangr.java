/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.cadi.http;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.SSLHandshakeException;

import com.att.cadi.Access;
import com.att.cadi.Access.Level;
import com.att.cadi.CadiException;
import com.att.cadi.Locator;
import com.att.cadi.Locator.Item;
import com.att.cadi.LocatorException;
import com.att.cadi.SecuritySetter;
import com.att.cadi.client.Rcli;
import com.att.cadi.client.Retryable;
import com.att.inno.env.APIException;

public class HMangr {
	private String apiVersion;
	private int readTimeout, connectionTimeout;
	public final Locator loc;
	private Access access;
	
	public HMangr(Access access, Locator loc) {
		readTimeout = 10000;
		connectionTimeout=3000;
		this.loc = loc;
		this.access = access;
	}

	/**
	 * Reuse the same service.  This is helpful for multiple calls that change service side cached data so that 
	 * there is not a speed issue.
	 * 
	 * If the service goes down, another service will be substituted, if available.
	 * 
	 * @param access
	 * @param loc
	 * @param ss
	 * @param item
	 * @param retryable
	 * @return
	 * @throws URISyntaxException 
	 * @throws Exception
	 */
	public<RET> RET same(SecuritySetter<HttpURLConnection> ss, Retryable<RET> retryable) throws APIException, CadiException, LocatorException {
		RET ret = null;
		boolean retry = true;
		int retries = 0;
		Rcli<HttpURLConnection> client = retryable.lastClient();
		try {
			do {
				// if no previous state, get the best
				if(retryable.item()==null) {
					retryable.item(loc.best());
					retryable.lastClient = null;
				}
				if(client==null) {
					Item item = retryable.item();
					URI uri=loc.get(item);
					if(uri==null) {
						loc.invalidate(retryable.item());
						retryable.item(loc.next(retryable.item()));
						continue;
					}
					client = new HRcli(this, uri,item,ss)
						.connectionTimeout(connectionTimeout)
						.readTimeout(readTimeout)
						.apiVersion(apiVersion);
				} else {
					client.setSecuritySetter(ss);
				}
				
				retry = false;
				try {
					ret = retryable.code(client);
				} catch (APIException | CadiException e) {
					Item item = retryable.item();
					loc.invalidate(item);
					retryable.item(loc.next(item));
					try {
						Throwable ec = e.getCause();
						if(ec instanceof java.net.ConnectException) {
							if(client!=null && ++retries<2) { 
								access.log(Level.WARN,"Connection refused, trying next available service");
								retry = true;
							} else {
								throw new CadiException("Connection refused, no more available connections to try");
							}
						} else if(ec instanceof SSLHandshakeException) {
							access.log(Level.ERROR,ec.getMessage());
							retry = false;
						} else if(ec instanceof SocketException) {
							if("java.net.SocketException: Connection reset".equals(ec.getMessage())) {
								access.log(Level.ERROR, ec.getMessage(), " can mean Certificate Expiration or TLS Protocol issues");
							}
							retryable.item(null);
							throw e;
						} else {
							retryable.item(null);
							throw e;
						}
					} finally {
						client = null;
					}
				} catch (ConnectException e) {
					Item item = retryable.item();
					loc.invalidate(item);
					retryable.item(loc.next(item));
				}
			} while(retry);
		} finally {
			retryable.lastClient = client;
		}
		return ret;
	}
	
	
	public<RET> RET best(SecuritySetter<HttpURLConnection> ss, Retryable<RET> retryable) throws LocatorException, CadiException, APIException {
		retryable.item(loc.best());
		return same(ss,retryable);
	}
	public<RET> RET all(SecuritySetter<HttpURLConnection> ss, Retryable<RET> retryable) throws LocatorException, CadiException, APIException {
		return oneOf(ss,retryable,true,null);
	}

	public<RET> RET all(SecuritySetter<HttpURLConnection> ss, Retryable<RET> retryable,boolean notify) throws LocatorException, CadiException, APIException {
		return oneOf(ss,retryable,notify,null);
	}
	
	public<RET> RET oneOf(SecuritySetter<HttpURLConnection> ss, Retryable<RET> retryable,boolean notify,String host) throws LocatorException, CadiException, APIException {
		RET ret = null;
		// make sure we have all current references:
		loc.refresh();
		for(Item li=loc.first();li!=null;li=loc.next(li)) {
			URI uri=loc.get(li);
			if(host!=null && !host.equals(uri.getHost())) {
				break;
			}
			try {
				ret = retryable.code(new HRcli(this,uri,li,ss));
				access.log(Level.DEBUG,"Success calling",uri,"during call to all services");
			} catch (APIException | CadiException e) {
				Throwable t = e.getCause();
				if(t!=null && t instanceof ConnectException) {
					loc.invalidate(li);
					access.log(Level.ERROR,"Connection to",uri,"refused during call to all services");
				} else if(t instanceof SSLHandshakeException) {
					access.log(Level.ERROR,t.getMessage());
					loc.invalidate(li);
				} else if(t instanceof SocketException) {
					if("java.net.SocketException: Connection reset".equals(t.getMessage())) {
						access.log(Level.ERROR, t.getMessage(), " can mean Certificate Expiration or TLS Protocol issues");
					}
					retryable.item(null);
					throw e;
				} else {
					throw e;
				}
			} catch (ConnectException e) {
				loc.invalidate(li);
				access.log(Level.ERROR,"Connection to",uri,"refused during call to all services");
			}
		}
			
		if(ret == null && notify) 
			throw new LocatorException("No available clients to call");
		return ret;
	}
	

	public void close() {
		// TODO Anything here?
	}

	public HMangr readTimeout(int timeout) {
		this.readTimeout = timeout;
		return this;
	}

	public int readTimeout() {
		return readTimeout;
	}
	
	public void connectionTimeout(int t) {
		connectionTimeout = t;
	}

	public int connectionTimout() {
		return connectionTimeout;
	}

	public HMangr apiVersion(String version) {
		apiVersion = version;
		return this;
	}

	public String apiVersion() {
		return apiVersion;
	}

}
