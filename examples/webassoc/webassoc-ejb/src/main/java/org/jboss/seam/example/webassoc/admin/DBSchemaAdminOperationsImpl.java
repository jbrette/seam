package org.jboss.seam.example.webassoc.admin;

import static org.jboss.seam.ScopeType.SESSION;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.example.webassoc.clubs.ClubEvent;
import org.jboss.seam.example.webassoc.clubs.ClubEventRegistration;
import org.jboss.seam.example.webassoc.mail.Mailing;
import org.jboss.seam.example.webassoc.mny.AcctRecord;
import org.jboss.seam.example.webassoc.mny.MnyAccount;
import org.jboss.seam.example.webassoc.mny.MnyAccountHome;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.vote.Ballot;
import org.jboss.seam.log.Log;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateful
@Scope(SESSION)
@Name("dbSchemaAdminOperations")
public class DBSchemaAdminOperationsImpl
        implements AdminOperations,
           Serializable
{

   private static final long serialVersionUID = -5619764497879111930L;
   @Logger
   private Log log;
   @PersistenceContext
   private EntityManager m_em;
   @In(create = true)
   private MnyAccountHome mnyAccountHome;

   @Restrict("#{s:hasRole('admin')}")
   public void invokedByMenuButton1()
   {

      log.info("DBSchemaAdminOperationsImpl:invokedByMenuButton1 invoked");
      makeDeposit();

   }

   @Restrict("#{s:hasRole('admin')}")
   public void invokedByMenuButton2()
   {

      log.info("DBSchemaAdminOperationsImpl:invokedByMenuButton2 invoked");
      makeWithdrawal();

   }

   @Restrict("#{s:hasRole('admin')}")
   public void invokedByMenuButton3()
   {

      log.info("DBSchemaAdminOperationsImpl:invokedByMenuButton3 invoked");
      createAccounts();

   }

   protected void updateMailingStates()
   {

      String ejbqlUpdate = "";
      int updatedEntities = 0;
      ejbqlUpdate = "update Mailing set state = :state where sent = true";
      updatedEntities =
         m_em.createQuery(ejbqlUpdate).setParameter("state",
            Mailing.MState.sent).executeUpdate();
      log.info(" " + updatedEntities + " Mailing updated");
      ejbqlUpdate = "update Mailing set state = :state where sent = false";
      updatedEntities =
         m_em.createQuery(ejbqlUpdate).setParameter("state",
            Mailing.MState.notSent).executeUpdate();
      log.info(" " + updatedEntities + " Mailing updated");
      ejbqlUpdate = "update Ballot set state = :state";
      updatedEntities =
         m_em.createQuery(ejbqlUpdate).setParameter("state",
            Ballot.MState.sent).executeUpdate();
      log.info(" " + updatedEntities + " Ballot updated");

   }

   protected void updateClubEvent()
   {

      String ejbqlUpdate = "";
      int updatedEntities = 0;
      ejbqlUpdate =
         "update ClubEvent set status = :status where openRegistration = true";
      updatedEntities =
         m_em.createQuery(ejbqlUpdate).setParameter("status",
            ClubEvent.CEState.open).executeUpdate();
      log.info(" " + updatedEntities + " updated");
      ejbqlUpdate =
         "update ClubEvent set status = :status where openRegistration = false";
      updatedEntities =
         m_em.createQuery(ejbqlUpdate).setParameter("status",
            ClubEvent.CEState.closed).executeUpdate();
      log.info(" " + updatedEntities + " updated");
      ejbqlUpdate =
         "update ClubEventRegistration set status = :status where cancelled = true";
      updatedEntities =
         m_em.createQuery(ejbqlUpdate).setParameter("status",
            ClubEventRegistration.CERState.cancelled).executeUpdate();
      log.info(" " + updatedEntities + " updated");
      ejbqlUpdate =
         "update ClubEventRegistration set status = :status where ((cancelled = false) and (confirmed = true))";
      updatedEntities =
         m_em.createQuery(ejbqlUpdate).setParameter("status",
            ClubEventRegistration.CERState.confirmed).executeUpdate();
      log.info(" " + updatedEntities + " updated");
      ejbqlUpdate =
         "update ClubEventRegistration set status = :status where ((cancelled = false) and (confirmed = false))";
      updatedEntities =
         m_em.createQuery(ejbqlUpdate).setParameter("status",
            ClubEventRegistration.CERState.registered).executeUpdate();
      log.info(" " + updatedEntities + " updated");

   }

   protected void initIsValidFields()
   {

      Boolean trueValue = Boolean.TRUE;
      String ejbqlUpdate = "";
      int updatedEntities = 0;
      ejbqlUpdate =
         "update AssocMember set membershipValid = :membershipValid";
      updatedEntities =
         m_em.createQuery(ejbqlUpdate).setParameter("membershipValid",
            trueValue).executeUpdate();

      if (updatedEntities != 0)
      {

         // Nothing special to do
      }

   }

   @Restrict("#{s:hasRole('admin')}")
   public void makeDeposit()
   {

      log.info("DBSchemaAdminOperationsImpl:invokedByMenuButton1 invoked");
      mnyAccountHome.makeDeposit("fromweb", null, null, new Long(1000),
         "a deposit", AcctRecord.TKind.electronic, new Date());

   }

   @Restrict("#{s:hasRole('admin')}")
   public void makeWithdrawal()
   {

      log.info("DBSchemaAdminOperationsImpl:invokedByMenuButton2 invoked");
      mnyAccountHome.makeWithdrawal("fromweb", null, null, new Long(500),
         "a withdrawal", AcctRecord.TKind.electronic, new Date());

   }

   @Restrict("#{s:hasRole('admin')}")
   public void createAccounts()
   {

      AssocMember.MKind[] todos =
         new AssocMember.MKind[]
         {
            AssocMember.MKind.committee, AssocMember.MKind.corporate,
            AssocMember.MKind.normal, AssocMember.MKind.youngpro
         };
      boolean add2011 = true;

      if (add2011)
      {

         for (int i = 0; i < todos.length; i++)
         {

            Query query =
               m_em.createQuery("SELECT c FROM AssocMember c "
                  + "WHERE (c.MKind = :theKind1) " + "ORDER BY c.assocName");
            query.setParameter("theKind1", todos[i]);

            @SuppressWarnings("unchecked")
            List<AssocMember> assocMembers = query.getResultList();
            Calendar newCal = Calendar.getInstance();
            int year = newCal.get(Calendar.YEAR);
            newCal.set(year, Calendar.JULY, 1, 1, 1, 1);

            Date firstDay = newCal.getTime();

            // newCal.set(2010, Calendar.SEPTEMBER, 1, 1, 1, 1);
            // Date payDay = newCal.getTime();
            for (AssocMember assocMember : assocMembers)
            {

               mnyAccountHome.makeWithdrawal(null, assocMember, null,
                  new Long(assocMember.getMembershipDues()),
                  year + " Membership", AcctRecord.TKind.electronic, firstDay);
               // mnyAccountHome.makeDeposit(null, assocMember, new Long(assocMember.getMembershipDues()),
               //   assocMember.getPaymentInfo(), AcctRecord.TKind.check, payDay);

            }

         }

      }

      Query query =
         m_em.createQuery("SELECT c FROM AssocMember c "
            + "WHERE NOT ((c.paymentInfo = :emptyString) OR (c.paymentInfo IS NULL))");
      query.setParameter("emptyString", "");

      @SuppressWarnings("unchecked")
      List<AssocMember> assocMembers = query.getResultList();
      Calendar newCal = Calendar.getInstance();
      newCal.set(2009, Calendar.JULY, 1, 1, 1, 1);

      for (AssocMember assocMember : assocMembers)
      {

         MnyAccount myAcct = mnyAccountHome.findAccountByMember(assocMember);

         if (myAcct == null)
         {

            log.info("Should create account to transfer paymentInfo ["
               + assocMember.getPaymentInfo() + "] ["
               + assocMember.getAssocName() + "]");
            mnyAccountHome.makeDeposit(null, assocMember, null, new Long(0),
               assocMember.getPaymentInfo(), AcctRecord.TKind.electronic,
               newCal.getTime());

         }

      }

   }

   @Remove
   public void destroy()
   {

      // TODO Auto-generated method stub
   }

}