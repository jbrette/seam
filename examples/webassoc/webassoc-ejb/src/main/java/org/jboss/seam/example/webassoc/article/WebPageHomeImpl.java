package org.jboss.seam.example.webassoc.article;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.Query;
import javax.persistence.Transient;

@Name("webPageHome")
@Stateful
public class WebPageHomeImpl
        extends EntityHome<WebPage>
        implements WebPageHome
{

   private static final long serialVersionUID = 4858807229987024844L;
   private String m_pageName;

   @Factory("webPage")
   public WebPage initWebPage()
   {

      return getInstance();

   }

   @Transient
   public WebPage getHomePage()
   {

      Query query =
         getEntityManager().createQuery(
            "SELECT c FROM WebPage c where c.name = :theName");
      query.setParameter("theName", "home");

      WebPage page = (WebPage)(query.getSingleResult());

      return page;

   }

   @Transient
   public WebPage getLatestNewsPage()
   {

      Query query =
         getEntityManager().createQuery(
            "SELECT c FROM WebPage c where c.name = :theName");
      query.setParameter("theName", "latestNews");

      WebPage page = (WebPage)(query.getSingleResult());

      return page;

   }

   /**
    */
   public String getPageName()
   {

      return m_pageName;

   }

   /**
    */
   public void setPageName(String pageName)
   {

      m_pageName = pageName;

      Query query =
         getEntityManager().createQuery(
            "SELECT c FROM WebPage c where c.name = :theName");
      query.setParameter("theName", m_pageName);

      WebPage page = (WebPage)(query.getSingleResult());
      setId(page.getId());

   }

   protected WebPage createInstance()
   {

      WebPage res = new WebPage();
      res.setName("TheWebPageName");
      res.setLayout("buildPageLayout1.xhtml");
      res.setMenuPosition("99.99.99");
      res.setLeftColumnSize(15);
      res.setCenterColumnSize(15);
      res.setRightColumnSize(15);
      res.setCKind(WebPage.CKind.forEverybody);
      res.setHeaderText("<hr width=\"100%\">");
      res.setFooterText("<hr width=\"100%\">");

      return res;

   }

   @Transient
   @SuppressWarnings("unchecked")
   public List<WebPage> getAllWebPages()
   {

      Query query =
         getEntityManager().createQuery("SELECT c FROM WebPage c "
            + "where c.discontinued := theFilter");
      query.setParameter("theFilter", Boolean.FALSE);

      return query.getResultList();

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

}