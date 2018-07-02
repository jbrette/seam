package org.jboss.seam.example.webassoc.admin;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.example.webassoc.mny.AcctRecord;
import org.jboss.seam.example.webassoc.mny.MnyAccountHome;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.mship.AssocMemberHome;
import org.jboss.seam.example.webassoc.mship.FamilyMember;
import org.jboss.seam.example.webassoc.mship.FamilyMember.FKind;
import org.jboss.seam.example.webassoc.mship.FamilyMember.Gender;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.example.webassoc.util.OccupationCode;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateful
@Name("dbSchemaRemoteOperations")

// @Roles(@Role(name = "jiraIssueListConnector"))
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class DBSchemaRemoteOperationsImpl
        implements RemoteOperations,
           Serializable
{

   private static final long serialVersionUID = 5639536473729214040L;
   @Logger
   private Log log;
   @PersistenceContext
   private EntityManager m_em;
   @In(create = true)
   private AssocMemberHome assocMemberHome;
   @In(create = true)
   private MnyAccountHome mnyAccountHome;
   @In
   private Identity identity;
   @In
   private Credentials credentials;

   // DEBUG FLAGS
   private boolean m_performLoginLogout = false;

   public boolean login(String username, String password)
   {

      credentials.setUsername(username);
      credentials.setPassword(password);
      identity.login();

      return identity.isLoggedIn();

   }

   public void logout()
   {

      identity.logout();

   }

   @Restrict("#{s:hasRole('admin')}")
   public void createAssocMember(String company, String firstName,
      String lastName, String address, String city, String state, String zip,
      String country, String email, String phone, String phoneext, String fax,
      String idstatus, String description, String industry, String title,
      String webSite)
   {

      try
      {

         log.info("createAssocMember invoked with " + firstName + " "
            + lastName);

         AssocMember assocMember = assocMemberHome.initAssocMember();
         assocMember.setAssocName(notNull(company)
            ? company : (notNull(lastName) ? lastName : "theassocname"));
         assocMember.setAddress(notNull(address) ? address.split(",")[0]
                                                 : Cts.Dflt.ADDRESS);
         assocMember.setAptOrSuite(
            (notNull(address) && (address.indexOf(",") != -1))
            ? address.split(",")[1] : Cts.Dflt.APTORNB);
         assocMember.setCity(notNull(city) ? city : Cts.Dflt.CITY);
         assocMember.setState(notNull(state) ? state : Cts.Dflt.STATE);
         assocMember.setZip(notNull(zip) ? zip.split("-")[0] : Cts.Dflt.ZIP);
         assocMember.setAddressValid(notNull(lastName) && notNull(address)
            && notNull(city) && notNull(zip) && notNull(state));
         assocMember.setEmail(notNull(email) ? email : Cts.Dflt.E_MAIL);
         assocMember.setEMailValid(notNull(email));
         assocMember.setHomePage(notNull(webSite) ? webSite : "");
         assocMember.setHomePhone(notNull(phone) ? phone : Cts.Dflt.PHONE);
         assocMember.setHomeFax(notNull(fax) ? fax : Cts.Dflt.PHONE);

         if ("Associate Member".equals(idstatus))
         {

            assocMember.setMKind(AssocMember.MKind.normal);

         }
         else if ("YP Member".equals(idstatus))
         {

            assocMember.setMKind(AssocMember.MKind.youngpro);

         }
         else if ("French Organizations".equals(idstatus))
         {

            assocMember.setMKind(AssocMember.MKind.partnerorg);

         }
         else if ("French Organization".equals(idstatus))
         {

            assocMember.setMKind(AssocMember.MKind.partnerorg);

         }
         else if ("FACC Executive Director".equals(idstatus))
         {

            assocMember.setMKind(AssocMember.MKind.partnerorg);

         }
         else if ("Corporate Member".equals(idstatus))
         {

            assocMember.setMKind(AssocMember.MKind.corporate);

         }
         else if ("Councillor Member".equals(idstatus))
         {

            assocMember.setMKind(AssocMember.MKind.committee);

         }

         FamilyMember fmember = new FamilyMember();
         fmember.setAssocMember(assocMember);
         assocMember.getFamilyMembers().add(fmember);
         fmember.setPosTitle(notNull(title) ? title : "");
         fmember.setLastName((notNull(lastName) ? lastName : "theassocname"));
         fmember.setFirstName(notNull(firstName) ? firstName
                                                 : Cts.Dflt.FIRST_NAME);
         fmember.setDob(Calendar.getInstance().getTime());
         fmember.setGender(Gender.male);
         fmember.setFKind(FKind.main);
         fmember.setCellPhone(Cts.Dflt.PHONE);
         fmember.setPhoneExt(notNull(phoneext) ? phoneext : Cts.Dflt.PHONE);
         fmember.setFaxExt(Cts.Dflt.PHONE);
         fmember.setEmailExt(Cts.Dflt.E_MAIL);
         fmember.setOccupation(OccupationCode.___);
         fmember.setMovedAway(false);
         m_em.persist(assocMember);
         m_em.persist(fmember);

      }
      catch (Exception ex)
      {

         log.error("Exception occured", ex);

      }

   }

   private boolean notNull(String testedString)
   {

      return ((testedString != null) && (testedString.trim().length() != 0));

   }

   @Destroy @Remove
   public void destroy()
   {

      if (m_performLoginLogout)
      {

         identity.logout();

      }

   }

   @Restrict("#{s:hasRole('admin')}")
   public void createMnyAccount(String userName, String password,
      String accountName)
   {

      if (m_performLoginLogout)
      {

         login(userName, password);

      }

      mnyAccountHome.createMnyAccount(accountName);

      if (m_performLoginLogout)
      {

         logout();

      }

   }

   /**
    * JEB: Be very carreful to use the same entityManager everywhere.
    * Don't mix and match the m_em injected into that class and the somehome.getEntityManager()
    * or it will take you hours to figure out the issue.
   */
   @Restrict("#{s:hasRole('admin')}")
   public void makeDeposit(String userName, String password,
      String accountName, Long amount, String memo)
   {

      if (m_performLoginLogout)
      {

         login(userName, password);

      }

      // Are we logged in
      log.info("Is Logged In : " + Identity.instance().isLoggedIn());
      mnyAccountHome.makeDeposit(accountName, null, null, amount, memo,
         AcctRecord.TKind.electronic, new Date());

      if (m_performLoginLogout)
      {

         logout();

      }

   }

}