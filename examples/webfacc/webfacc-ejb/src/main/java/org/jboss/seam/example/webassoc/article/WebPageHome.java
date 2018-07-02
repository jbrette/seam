package org.jboss.seam.example.webassoc.article;

import javax.ejb.Local;

@Local
public interface WebPageHome
{

   // Methods from WebPageHomeImpl
   public WebPage initWebPage();

   public void ejbRemove();

   // Override the getId() and setId(Object id)
   // to be able to use the name instead of the id
   public String getPageName();

   public void setPageName(String pageName);

   //
   public WebPage getHomePage();

   public WebPage getLatestNewsPage();

   // Methods from EntityHome and Home
   public Object getId();

   public void setId(Object id);

   public String persist();

   public String update();

   public String remove();

   public boolean isManaged();

   public void create();

   public Object getInstance();

}