package org.jboss.seam.example.webassoc.wbs;

public interface ExcelRESTFull
{

   String login(String username);

   String logout();

   String createEntry(int categoryId);

   void confirmEntry();

}