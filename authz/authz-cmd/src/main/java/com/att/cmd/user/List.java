/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.cmd.user;

import java.util.Collections;
import java.util.Comparator;

import com.att.cmd.BaseCmd;
import com.att.inno.env.util.Chrono;

import aaf.v2_0.Approval;
import aaf.v2_0.Approvals;
import aaf.v2_0.Delg;
import aaf.v2_0.Delgs;
import aaf.v2_0.Users;

public class List extends BaseCmd<User> {

	public List(User parent) {
		super(parent,"list");
		cmds.add(new ListForRoles(this));
		cmds.add(new ListForPermission(this));
		cmds.add(new ListForCreds(this));
		cmds.add(new ListDelegates(this));
		cmds.add(new ListApprovals(this));
		cmds.add(new ListActivity(this));
	}

	 
	void report(Users users, boolean count, String ... str) {
		reportHead(str);
		String format = reportColHead("%-50s %-30s\n","User","Expires");
		String date = "XXXX-XX-XX";
		int idx = 0;
		java.util.List<aaf.v2_0.Users.User> sorted = users.getUser();
		Collections.sort(sorted, new Comparator<aaf.v2_0.Users.User>() {
			@Override
			public int compare(aaf.v2_0.Users.User u1, aaf.v2_0.Users.User u2) {
				if(u2==null || u2 == null) {
					return -1;
				}
				return u1.getId().compareTo(u2.getId());
			}
		});
		for(aaf.v2_0.Users.User user : sorted) {
			if(!aafcli.isTest()) 
				date = Chrono.dateOnlyStamp(user.getExpires());
			
			pw().format(format, 
					count? (Integer.valueOf(++idx) + ") " + user.getId()): user.getId(), 
					date);
		}
		pw().println();
	}

	public void report(Approvals approvals, String title, String id) {
		reportHead(title,id);
		String format = reportColHead("  %-20s %-20s %-11s %-6s %12s\n","User","Approver","Type","Status","Updated");
		java.util.List<Approval> lapp = approvals.getApprovals();
		Collections.sort(lapp, new Comparator<Approval>() {
			@Override
			public int compare(Approval a1, Approval a2) {
				return a1.getTicket().compareTo(a2.getTicket());
			}
		} );
		String ticket = null, prev = null;
		for(Approval app : lapp ) {
			ticket = app.getTicket();
			if(!ticket.equals(prev)) {
				pw().print("Ticket: ");
				pw().println(ticket);
			}
			prev = ticket;

			pw().format(format,
					app.getUser(),
					app.getApprover(),
					app.getType(),
					app.getStatus(),
					Chrono.niceDateStamp(app.getUpdated())
					);
		}
	}

	public void report(Delgs delgs, String title, String id) {
		reportHead(title,id);
		String format = reportColHead(" %-25s %-25s  %-10s\n","User","Delegate","Expires");
		String date = "XXXX-XX-XX";
		for(Delg delg : delgs.getDelgs()) {
			if(!this.aafcli.isTest()) 
				date = Chrono.dateOnlyStamp(delg.getExpires());
			pw().printf(format, 
						delg.getUser(),
						delg.getDelegate(),
						date
						);
		}
	}


}
