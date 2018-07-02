package org.jboss.seam.example.webassoc.util;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.example.webassoc.classified.Classified;
import org.jboss.seam.example.webassoc.clubs.Club;
import org.jboss.seam.example.webassoc.mail.MailingController;
import org.jboss.seam.example.webassoc.sponsors.Sponsor;
import org.jboss.seam.international.LocaleSelector;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

@Name("globals")
@Scope(ScopeType.SESSION)
@AutoCreate
public class Globals
        implements Serializable
{

   private static final long serialVersionUID = -779306770270396319L;
   @In(create = true)
   private LocaleSelector localeSelector;

   // =================================
   // Members
   // =================================
   private Long m_id;
   private String m_searchedString;
   private String m_searchedComboBox = "2";
   private int m_searchedInt;
   private String m_currentPage = "home";
   private Long m_currentPageInt = new Long(0);
   private String m_skin = Cts.DFLT_SKIN;
   private String m_theme = Cts.DFLT_THEME;

   // =================================
   // Members
   // =================================
   private final boolean m_trueHack = true;
   private final boolean m_falseHack = false;

   // =================================
   // Constructor
   // =================================
   public Globals()
   {

   }

   // =================================
   // Getter/Setter
   // =================================
   @Id @GeneratedValue
   public Long getId()
   {

      return m_id;

   }

   public void setId(Long id)
   {

      this.m_id = id;

   }

   //-------------------------------------
   // Used to save the skin
   //-------------------------------------
   public String getSearchedString()
   {

      return m_searchedString;

   }

   public void setSearchedString(String searchedString)
   {

      this.m_searchedString = searchedString;

   }

   //-------------------------------------
   // Used to save the skin
   //-------------------------------------
   public String getSearchedComboBox()
   {

      return m_searchedComboBox;

   }

   public void setSearchedComboBox(String searchedComboBox)
   {

      this.m_searchedComboBox = searchedComboBox;

   }

   //-------------------------------------
   // Used to save the skin
   //-------------------------------------
   public int getSearchedInt()
   {

      return m_searchedInt;

   }

   public void setSearchedInt(int searchedInt)
   {

      this.m_searchedInt = searchedInt;

   }

   //-------------------------------------
   // Used to save the skin
   //-------------------------------------
   public String getCurrentPage()
   {

      return m_currentPage;

   }

   public void setCurrentPage(String currentPage)
   {

      this.m_currentPage = currentPage;

   }

   //-------------------------------------
   // Used in WebAssocEntityEntry.
   // This is a temporary dirty hack until I can fix it
   //-------------------------------------
   public boolean getTrueHack()
   {

      return m_trueHack;

   }

   public void setTrueHack(boolean foo)
   {

   }

   public boolean getFalseHack()
   {

      return m_falseHack;

   }

   public void setFalseHack(boolean foo)
   {

   }

   //-------------------------------------
   // Used to save the skin
   //-------------------------------------
   public String getHomePage()
   {

      return "home";

   }

   public void setHomePage(String currentPage)
   {

   }

   //-------------------------------------
   // Used to save the skin
   //-------------------------------------
   public Long getCurrentPageInt()
   {

      return m_currentPageInt;

   }

   public void setCurrentPageInt(Long currentPageInt)
   {

      this.m_currentPageInt = currentPageInt;

   }

   @Transient
   public Club.CKind getClubCKind()
   {

      for (Club.CKind value : Club.CKind.values())
      {

         if (value.getId() == m_currentPageInt)
         {

            return value;

         }

      }

      return Club.CKind.values()[0];

   }

   @Transient
   public Classified.CKind getClassifiedCKind()
   {

      for (Classified.CKind value : Classified.CKind.values())
      {

         if (value.getId() == m_currentPageInt)
         {

            return value;

         }

      }

      return Classified.CKind.values()[0];

   }

   @Transient
   public Sponsor.SKind getSponsorSKind()
   {

      for (Sponsor.SKind value : Sponsor.SKind.values())
      {

         if (value.getId() == m_currentPageInt)
         {

            return value;

         }

      }

      return Sponsor.SKind.values()[0];

   }

   @Transient
   public MailingController.NKind getNewsLetterNKind()
   {

      for (MailingController.NKind value : MailingController.NKind.values())
      {

         if (value.getId() == m_currentPageInt)
         {

            return value;

         }

      }

      return MailingController.NKind.values()[0];

   }

   //-------------------------------------
   // Used to save the skin
   //-------------------------------------
   public void setSkin(String skin)
   {

      this.m_skin = skin;

   }

   public String getSkin()
   {

      return m_skin;

   }

   //-------------------------------------
   // Used to save the theme
   //-------------------------------------
   public void setTheme(String theme)
   {

      this.m_theme = theme;

   }

   public String getTheme()
   {

      return m_theme;

   }

   //-------------------------------------
   // Used to save the skin
   //-------------------------------------
   public void selectLanguage(String lang)
   {

      localeSelector.setLanguage(lang);
      localeSelector.select();

   }

   //-------------------------------------
   // Used for GMAP
   //-------------------------------------
   public Double getDfltLat()
   {

      return Cts.GMAP.LAT;

   }

   public Double getDfltLng()
   {

      return Cts.GMAP.LNG;

   }

   public String getKey()
   {

      return Cts.GMAP.KEY;

   }

   public String getDoNotReply()
   {

      return Cts.DO_NOT_REPLY;

   }

   public String getSecretary()
   {

      return Cts.SECRETARY;

   }

   public String getContactUs()
   {

      return Cts.CONTACT_US;

   }

   public String getMainHomePage()
   {

      return Cts.HOME_PAGE;

   }

   public String getProjectName()
   {

      return Cts.PROJECT_NAME;

   }

}