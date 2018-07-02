package org.jboss.seam.example.webassoc.wbs;

import javax.ejb.Remote;

@Remote
public interface ExcelService
{

   boolean login(String username, String password);

   boolean logout();

   void createEntry(String title, String description, int categoryId);

   void confirmEntry();

}