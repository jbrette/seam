package org.jboss.seam.example.webassoc.wbs;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;

import javax.ejb.Stateless;

import javax.jws.WebMethod;
import javax.jws.WebService;

@Stateless
@Name("excelService")
@WebService(name = "ExcelService", serviceName = "ExcelService")
public class ExcelServiceImpl
        implements ExcelService
{

   @Logger
   private Log log;

   @SuppressWarnings("deprecation")
   @WebMethod
   public boolean login(String username, String password)
   {

      Identity.instance().setUsername(username);
      Identity.instance().setPassword(password);
      Identity.instance().login();

      return Identity.instance().isLoggedIn();

   }

   @WebMethod
   public boolean logout()
   {

      Identity.instance().logout();

      return !Identity.instance().isLoggedIn();

   }

   @WebMethod
   public void createEntry(String title, String description, int categoryId)
   {

      log.info("createEntry invoked with data [" + title + "][" + description
         + "][" + categoryId + "]");

   }

   @WebMethod
   public void confirmEntry()
   {

      log.info("confirmEntry");

   }

}