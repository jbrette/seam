package org.jboss.seam.example.webassoc.admin;

import static org.jboss.seam.ScopeType.SESSION;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.security.AccountMember;
import org.jboss.seam.example.webassoc.security.AccountMemberRole;
import org.jboss.seam.example.webassoc.util.Attachment;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.log.Log;

import java.io.Serializable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateful
@Scope(SESSION)
@Name("adminOperations")
public class AdminOperationsImpl
        implements AdminOperations,
           Serializable
{

   private static final long serialVersionUID = -2456315436827058871L;
   @Logger
   private Log log;
   @PersistenceContext
   private EntityManager m_em;

   @Restrict("#{s:hasRole('admin')}")
   public void invokedByMenuButton1()
   {

      log.info("AdminOperationsImpl:checkWebAccount invoked");
      checkWebAccount();
      // giveProperPermissions();

   }

   @Restrict("#{s:hasRole('admin')}")
   public void invokedByMenuButton2()
   {

      log.info("AdminOperationsImpl:buildThumbnails invoked");
      buildThumbnails();

   }

   @Restrict("#{s:hasRole('admin')}")
   public void invokedByMenuButton3()
   {

      log.info("AdminOperationsImpl:checkAssocMemberFields invoked");
      checkAssocMemberFields();

   }

   @Restrict("#{s:hasRole('admin')}")
   @SuppressWarnings("unchecked")
   public void checkAssocMemberFields()
   {

      List<AssocMember> assocMembers =
         m_em.createQuery("from AssocMember a").getResultList();
      Pattern emailPattern =
         Pattern.compile(Cts.Rgx.E_MAIL, Pattern.CASE_INSENSITIVE);
      Pattern phonePattern = Pattern.compile(Cts.Rgx.PHONE_STRICT);
      Pattern zipPattern = Pattern.compile(Cts.Rgx.ZIP_STRICT);
      Pattern statePattern = Pattern.compile(Cts.Rgx.STATE_STRICT);

      for (AssocMember assocMember : assocMembers)
      {

         // Check the e-mail
         CharSequence emailStr = assocMember.getEmail();
         Matcher emailMatcher = emailPattern.matcher(emailStr);

         if (!emailMatcher.matches())
         {

            log.info(assocMember.getAssocName() + " has wrong email format: ["
               + emailStr + "]");
            // assocMember.setEmail(Cts.Dflt.E_MAIL);
            // assocMember.setEMailValid(Boolean.FALSE);
            // m_em.persist(assocMember);

         }

         // Phone
         CharSequence phoneStr = assocMember.getHomePhone();
         Matcher phoneMatcher = phonePattern.matcher(phoneStr);

         if (!phoneMatcher.matches() || (phoneStr.length() != 12))
         {

            log.info(assocMember.getAssocName() + " has wrong phone format: ["
               + phoneStr + "]");

         }

         // Zip
         CharSequence zipStr = assocMember.getZip();
         Matcher zipMatcher = zipPattern.matcher(zipStr);

         if (!zipMatcher.matches() || (zipStr.length() != 5))
         {

            log.info(assocMember.getAssocName() + " has wrong zip format: ["
               + zipStr + "]");

         }

         // state
         CharSequence stateStr = assocMember.getState();
         Matcher stateMatcher = statePattern.matcher(stateStr);

         if (!stateMatcher.matches() || (stateStr.length() != 2))
         {

            log.info(assocMember.getAssocName() + " has wrong stateS format: ["
               + stateStr + "]");

         }

         if ((assocMember.getUseEMail()) || (assocMember.getUseUSPS()))
         {

            if ((assocMember.getMKind().equals(AssocMember.MKind.guest))
               || (assocMember.getMKind().equals(AssocMember.MKind.sponsor))
               || (assocMember.getMKind().equals(AssocMember.MKind.advertiser))
               || (assocMember.getMKind().equals(AssocMember.MKind.pastmember)))
            {

               log.info(assocMember.getAssocName()
                  + " should not recieve bulletin ");

            }

         }

      }

   }

   @Restrict("#{s:hasRole('admin')}")
   @SuppressWarnings("unchecked")
   public void checkWebAccount()
   {

      Query query1 =
         m_em.createQuery("SELECT c FROM AssocMember c "
            + "WHERE (c.membershipValid = true) "
            + "AND (c.EMailValid = true) "
            + "AND ((c.MKind = :theKind1) or ((c.MKind = :theKind2a) or (c.MKind = :theKind2b) or (c.MKind = :theKind2c)) or (c.MKind = :theKind3)) "
            + "ORDER BY c.assocName");
      query1.setParameter("theKind1", AssocMember.MKind.normal);
      query1.setParameter("theKind2a", AssocMember.MKind.committee);
      query1.setParameter("theKind2b", AssocMember.MKind.corporate);
      query1.setParameter("theKind2c", AssocMember.MKind.youngpro);
      query1.setParameter("theKind3", AssocMember.MKind.guest);

      List<AssocMember> assocMembers = query1.getResultList();
      Query query2 =
         m_em.createQuery(
            "SELECT c FROM AccountMember c where c.member = :assocMember");

      for (AssocMember assocMember : assocMembers)
      {

         query2.setParameter("assocMember", assocMember);

         List<AccountMember> accountMembers = query2.getResultList();

         if (accountMembers.size() == 0)
         {

            if (assocMember.getMKind() == AssocMember.MKind.normal)
            {

               log.info("Can not find accountMember for normal member "
                  + assocMember.getAssocName());

            }
            else if (assocMember.getMKind() == AssocMember.MKind.committee)
            {

               log.info("Can not find accountMember for committee member "
                  + assocMember.getAssocName());

            }
            else if (assocMember.getMKind() == AssocMember.MKind.corporate)
            {

               log.info("Can not find accountMember for corporate member "
                  + assocMember.getAssocName());

            }
            else if (assocMember.getMKind() == AssocMember.MKind.youngpro)
            {

               log.info("Can not find accountMember for councillor member "
                  + assocMember.getAssocName());

            }
            else if (assocMember.getMKind() == AssocMember.MKind.guest)
            {

               log.info("Can not find accountMember for guest member "
                  + assocMember.getAssocName());

            }

         }
         else if (accountMembers.size() == 1)
         {

            AccountMember accountMember = accountMembers.get(0);
            String assocMemberEMail = assocMember.getEmail();
            String accountMemberEMail = accountMember.getEmail();

            if (!assocMemberEMail.equals(accountMemberEMail))
            {

               log.info(assocMember.getAssocName() + " has e-mail ["
                  + assocMemberEMail + "]but " + accountMember.getUserName()
                  + " has e-mail [" + accountMemberEMail + "]");

            }

         }

      }

   }

   @Restrict("#{s:hasRole('admin')}")
   @SuppressWarnings("unchecked")
   public void giveProperPermissions()
   {

      Query query1 =
         m_em.createQuery(
            "SELECT c FROM AccountMemberRole c where c.name = :roleName");
      query1.setParameter("roleName", "webuser");

      AccountMemberRole webUserRole =
         (AccountMemberRole)query1.getSingleResult();
      Query query2 =
         m_em.createQuery(
            "SELECT c FROM AccountMemberRole c where c.name = :roleName");
      query2.setParameter("roleName", "member");

      AccountMemberRole realMemberRole =
         (AccountMemberRole)query2.getSingleResult();
      Query query = m_em.createQuery("SELECT c FROM AccountMember c ");
      List<AccountMember> accountMembers = query.getResultList();

      for (AccountMember accountMember : accountMembers)
      {

         AssocMember assocMember = accountMember.getMember();

         if (assocMember.isClassic())
         {

            if (accountMember.getRoles().contains(webUserRole))
            {

               accountMember.removeRole(webUserRole);

            }

            accountMember.addRole(realMemberRole);
            m_em.persist(accountMember);

         }

      }

   }

   @Restrict("#{s:hasRole('admin')}")
   @SuppressWarnings("unchecked")
   public void buildThumbnails()
   {

      String[] tableNames = new String[] {"Attachment"};

      for (int i = 0; i < tableNames.length; i++)
      {

         Query query =
            m_em.createQuery("SELECT c FROM " + tableNames[i]
               + " c WHERE c.thumbnail IS NULL");
         List<Attachment> images = query.getResultList();

         for (Attachment image : images)
         {

            log.info("Updating thumbnail" + image.getFileName());
            image.updateThumbnail();
            m_em.persist(image);

         }

      }

   }

   @Remove
   public void destroy()
   {

   }

}