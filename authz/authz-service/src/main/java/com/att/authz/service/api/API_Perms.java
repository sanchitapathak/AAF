/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.authz.service.api;

import static com.att.authz.layer.Result.OK;
import static com.att.cssa.rserv.HttpMethods.DELETE;
import static com.att.cssa.rserv.HttpMethods.GET;
import static com.att.cssa.rserv.HttpMethods.POST;
import static com.att.cssa.rserv.HttpMethods.PUT;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.att.aft.dme2.internal.jetty.http.HttpStatus;
import com.att.authz.env.AuthzTrans;
import com.att.authz.facade.AuthzFacade;
import com.att.authz.layer.Result;
import com.att.authz.service.AuthAPI;
import com.att.authz.service.Code;
import com.att.authz.service.mapper.Mapper.API;
import com.att.cadi.config.Config;

public class API_Perms {
	public static void timeSensitiveInit(AuthAPI authzAPI, AuthzFacade facade) throws Exception {
		/** 
		 *  gets all permissions by user name
		 */
		authzAPI.route(GET, "/authz/perms/user/:user", API.PERMS, new Code(facade,"Get Permissions by User",true) {
			public void handle(
					AuthzTrans trans, 
					HttpServletRequest req,
					HttpServletResponse resp) throws Exception {
				
				Result<Void> r = context.getPermsByUser(trans, resp, pathParam(req, "user"));
				switch(r.status) {
					case OK: 
						resp.setStatus(HttpStatus.OK_200); 
						break;
					default:
						context.error(trans,resp,r);
				}
			}

		});
		
		/** 
		 *  gets all permissions by user name
		 */
		authzAPI.route(POST, "/authz/perms/user/:user", API.PERMS, new Code(facade,"Get Permissions by User, Query AAF Perms",true) {
			public void handle(
					AuthzTrans trans, 
					HttpServletRequest req,
					HttpServletResponse resp) throws Exception {
				
				Result<Void> r = context.getPermsByUserWithAAFQuery(trans, req, resp, pathParam(req, "user"));
				switch(r.status) {
					case OK: 
						resp.setStatus(HttpStatus.OK_200); 
						break;
					default:
						context.error(trans,resp,r);
				}
			}

		});


	} // end timeSensitiveInit

	public static void init(AuthAPI authzAPI, AuthzFacade facade) throws Exception {
		/**
		 * Create a Permission
		 */
		authzAPI.route(POST,"/authz/perm",API.PERM_REQ,new Code(facade,"Create a Permission",true) {
			public void handle(
					AuthzTrans trans, 
					HttpServletRequest req,
					HttpServletResponse resp) throws Exception {
				
				Result<Void> r = context.createPerm(trans, req, resp);
				switch(r.status) {
					case OK: 
						resp.setStatus(HttpStatus.CREATED_201); 
						break;
					default:
						context.error(trans,resp,r);
				}
			}
		});

		/** 
		 *  get details of Permission
		 */
		authzAPI.route(GET, "/authz/perms/:type/:instance/:action", API.PERMS, new Code(facade,"Get Permissions by Key",true) {
			public void handle(
					AuthzTrans trans, 
					HttpServletRequest req,
					HttpServletResponse resp) throws Exception {
				
				Result<Void> r = context.getPermsByName(trans, resp, 
						pathParam(req, "type"),
						URLDecoder.decode(pathParam(req, "instance"),Config.UTF_8),
						pathParam(req, "action"));
				switch(r.status) {
					case OK: 
						resp.setStatus(HttpStatus.OK_200); 
						break;
					default:
						context.error(trans,resp,r);
				}
			}

		});
		
		/** 
		 *  get children of Permission
		 */
		authzAPI.route(GET, "/authz/perms/:type", API.PERMS, new Code(facade,"Get Permissions by Type",true) {
			public void handle(
					AuthzTrans trans, 
					HttpServletRequest req,
					HttpServletResponse resp) throws Exception {
				
				Result<Void> r = context.getPermsByType(trans, resp, pathParam(req, "type"));
				switch(r.status) {
					case OK: 
						resp.setStatus(HttpStatus.OK_200); 
						break;
					default:
						context.error(trans,resp,r);
				}
			}

		});

		
		/**
		 * gets all permissions by role name
		 */
		authzAPI.route(GET,"/authz/perms/role/:role",API.PERMS,new Code(facade,"Get Permissions by Role",true) {
			public void handle(
					AuthzTrans trans, 
					HttpServletRequest req,
					HttpServletResponse resp) throws Exception {
				
				Result<Void> r = context.getPermsForRole(trans, resp, pathParam(req, "role"));
				switch(r.status) {
					case OK: 
						resp.setStatus(HttpStatus.OK_200); 
						break;
					default:
						context.error(trans,resp,r);
				}
			}
		});

		/**
		 * gets all permissions by Namespace
		 */
		authzAPI.route(GET,"/authz/perms/ns/:ns",API.PERMS,new Code(facade,"Get PermsByNS",true) {
			public void handle(
					AuthzTrans trans, 
					HttpServletRequest req,
					HttpServletResponse resp) throws Exception {
				
				Result<Void> r = context.getPermsByNS(trans, resp, pathParam(req, "ns"));
				switch(r.status) {
					case OK: 
						resp.setStatus(HttpStatus.OK_200); 
						break;
					default:
						context.error(trans,resp,r);
				}
			}
		});
		
		/**
		 * Set a perm's description
		 */
		authzAPI.route(PUT,"/authz/perm",API.PERM_REQ,new Code(facade,"Set Description for Permission",true) {
			@Override
			public void handle(
					AuthzTrans trans, 
					HttpServletRequest req,
					HttpServletResponse resp) throws Exception {
				
				Result<Void> r = context.updatePermDescription(trans, req, resp);
				switch(r.status) {
					case OK: 
						resp.setStatus(HttpStatus.OK_200); 
						break;
					default:
						context.error(trans,resp,r);
				}
			}
		});	
		
		/**
		 * Update a permission with a rename
		 */
		authzAPI.route(PUT,"/authz/perm/:type/:instance/:action",API.PERM_REQ,new Code(facade,"Update a Permission",true) {
			public void handle(
					AuthzTrans trans, 
					HttpServletRequest req,
					HttpServletResponse resp) throws Exception {
				
				Result<Void> r = context.renamePerm(trans, req, resp, pathParam(req, "type"), 
						pathParam(req, "instance"), pathParam(req, "action"));
				switch(r.status) {
					case OK: 
						resp.setStatus(HttpStatus.OK_200); 
						break;
					default:
						context.error(trans,resp,r);
				}
			}
		});	
		
		/**
		 * Delete a Permission
		 */
		authzAPI.route(DELETE,"/authz/perm",API.PERM_REQ,new Code(facade,"Delete a Permission",true) {
			public void handle(
					AuthzTrans trans, 
					HttpServletRequest req,
					HttpServletResponse resp) throws Exception {
				
				Result<Void> r = context.deletePerm(trans,req, resp);
				switch(r.status) {
					case OK: 
						resp.setStatus(HttpStatus.OK_200); 
						break;
					default:
						context.error(trans,resp,r);
				}
			}
		});

		
		

		/**
		 * Delete a Permission
		 */
		authzAPI.route(DELETE,"/authz/perm/:name/:type/:action",API.PERM_KEY,new Code(facade,"Delete a Permission",true) {
			public void handle(
					AuthzTrans trans, 
					HttpServletRequest req,
					HttpServletResponse resp) throws Exception {
				
				Result<Void> r = context.deletePerm(trans, resp,
						pathParam(req, ":name"),
						pathParam(req, ":type"),
						pathParam(req, ":action"));
				switch(r.status) {
					case OK: 
						resp.setStatus(HttpStatus.OK_200); 
						break;
					default:
						context.error(trans,resp,r);
				}
			}
		});

	} // end init
}



