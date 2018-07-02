package org.jboss.seam.example.webassoc.wbs;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//
//@Name("excelRESTFull")
//@Path("/restfull")
public class ExcelRESTFullImpl
        implements ExcelRESTFull
{

	public String login(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public String logout() {
		// TODO Auto-generated method stub
		return null;
	}

	public String createEntry(int categoryId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void confirmEntry() {
		// TODO Auto-generated method stub
		
	}

//   @Logger
//   private Log log;
//
//   @GET
//   @Produces("text/plain")
//   @Path("/logout")
//   public String logout()
//   {
//
//      log.info("logout");
//
//      return "logout done";
//
//   }
//
//   @GET
//   @Path("/login/{userName}")
//   @Produces("text/plain")
//   public String login(@PathParam("userName") String userName)
//   {
//
//      log.info("login " + userName);
//
//      return "login of " + userName + "done";
//
//   }
//
//   @GET
//   @Path("/confirmEntry")
//   public void confirmEntry()
//   {
//
//      log.info("confirmEntry");
//
//   }
//
//   @GET
//   @Path("/createEntry/{categoryId}")
//   @Produces("text/plain")
//   public String createEntry(@PathParam("categoryId") int categoryId)
//   {
//
//      log.info("createEntry " + categoryId);
//
//      return "createEntry " + categoryId;
//
//   }

}