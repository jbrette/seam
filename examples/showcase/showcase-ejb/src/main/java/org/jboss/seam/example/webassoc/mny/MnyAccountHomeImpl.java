package org.jboss.seam.example.webassoc.mny;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.sponsors.Sponsor;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.Query;

@Name("mnyAccountHome")
@Stateful
public class MnyAccountHomeImpl
        extends EntityHome<MnyAccount>
        implements MnyAccountHome
{

   private static final long serialVersionUID = 1968662189025570388L;
   @Logger
   private Log log;

   // Used to create an Money Account for AssocMember
   // without Money account
   @RequestParameter
   private Long assocMemberMissingMyAccountId;
   @RequestParameter
   private Long acctRecordId;

   @Factory("mnyAccount")
   public MnyAccount initMnyAccount()
   {

      return getInstance();

   }

   protected MnyAccount createInstance()
   {

      MnyAccount theAccount = new MnyAccount();
      theAccount.setAcctName("theacctname");
      theAccount.setInitBalance(new Long(0));

      return theAccount;

   }

   @Remove @Destroy
   public void ejbRemove()
   {

   }

   public void createMnyAccount(String accountName)
   {

      Query query =
         getEntityManager().createQuery("SELECT c FROM MnyAccount c "
            + "WHERE c.acctName = :theName ");
      query.setParameter("theName", accountName);

      @SuppressWarnings("unchecked")
      List<MnyAccount> foundAccounts = query.getResultList();
      MnyAccount mnyAccount = null;

      if ((foundAccounts.size() == 0))
      {

         mnyAccount = initMnyAccount();
         mnyAccount.setAcctName(accountName);
         getEntityManager().persist(mnyAccount);

      }
      else
      {

         mnyAccount = foundAccounts.get(0);

      }

   }

   public MnyAccount findAccountByName(String accountName)
   {

      Query query =
         getEntityManager().createQuery("SELECT c FROM MnyAccount c "
            + "WHERE c.acctName = :theName ");
      query.setParameter("theName", accountName);

      @SuppressWarnings("unchecked")
      List<MnyAccount> res = query.getResultList();

      if (res.size() != 0)
      {

         setId(res.get(0).getId());

         return getInstance();

      }
      else
      {

         return null;

      }

   }

   public MnyAccount findAccountByMember(AssocMember assocMember)
   {

      Query query =
         getEntityManager().createQuery("SELECT c FROM MnyAccount c "
            + "WHERE c.owner = :theOwner ");
      query.setParameter("theOwner", assocMember);

      @SuppressWarnings("unchecked")
      List<MnyAccount> res = query.getResultList();

      if (res.size() != 0)
      {

         //         setId(res.get(0).getId());
         //
         //         return getInstance();
         return res.get(0);

      }
      else
      {

         return null;

      }

   }

   public MnyAccount findAccountBySponsor(Sponsor sponsor)
   {

      Query query =
         getEntityManager().createQuery("SELECT c FROM MnyAccount c "
            + "WHERE c.sponsor = :theSponsor ");
      query.setParameter("theSponsor", sponsor);

      @SuppressWarnings("unchecked")
      List<MnyAccount> res = query.getResultList();

      if (res.size() != 0)
      {

         //         setId(res.get(0).getId());
         //
         //         return getInstance();
         return res.get(0);

      }
      else
      {

         return null;

      }

   }

   @Transactional
   public void registerOneAssocMember()
   {

      log.info("registerOneAssocMember with id: "
         + assocMemberMissingMyAccountId);

      if (assocMemberMissingMyAccountId != null)
      {

         Query query1 =
            getEntityManager().createQuery(
               "SELECT c FROM AssocMember c where c.id = :assocMemberMissingMyAccountId");
         query1.setParameter("assocMemberMissingMyAccountId",
            assocMemberMissingMyAccountId);

         AssocMember assocMember = (AssocMember)query1.getSingleResult();
         log.info("registerOneAssocMember found AssocMember: "
            + assocMember.getAssocName());

         MnyAccount mnyAcct = findAccountByMember(assocMember);
         boolean notFound = (mnyAcct == null);

         if (notFound)
         {

            log.info("registerOneAssocMember creating MnyAccount for : "
               + assocMember.getAssocName());
            mnyAcct = createInstance();
            mnyAcct.setAcctName(assocMember.getAssocName());
            mnyAcct.setOwner(assocMember);
            mnyAcct.setInitBalance(new Long(0));
            getEntityManager().persist(mnyAcct);

         }
         else
         {

            log.info("registerOneAssocMember found existing MnyAccount for : "
               + assocMember.getAssocName());

         }

      }

   }

   @Transactional
   public void registerOneSponsor()
   {

      log.info("registerOneSponsor with id: " + assocMemberMissingMyAccountId);

      if (assocMemberMissingMyAccountId != null)
      {

         Query query1 =
            getEntityManager().createQuery(
               "SELECT c FROM Sponsor c where c.id = :assocMemberMissingMyAccountId");
         query1.setParameter("assocMemberMissingMyAccountId",
            assocMemberMissingMyAccountId);

         Sponsor sponsor = (Sponsor)query1.getSingleResult();
         log.info("registerOneSponsor found Sponsor: "
            + sponsor.getSponsorName());

         MnyAccount mnyAcct = findAccountBySponsor(sponsor);
         boolean notFound = (mnyAcct == null);

         if (notFound)
         {

            log.info("registerOneSponsor creating MnyAccount for : "
               + sponsor.getSponsorName());
            mnyAcct = createInstance();
            mnyAcct.setAcctName(sponsor.getSponsorName());
            mnyAcct.setSponsor(sponsor);
            mnyAcct.setInitBalance(new Long(0));
            getEntityManager().persist(mnyAcct);

         }
         else
         {

            log.info("registerOneSponsor found existing MnyAccount for : "
               + sponsor.getSponsorName());

         }

      }

   }

   public void makeDeposit(String accountName, AssocMember assocMember,
      Sponsor sponsor, Long amount, String memo, AcctRecord.TKind tKind,
      Date depositDate)
   {

      try
      {

         String fullAccountName =
            ((accountName != null) ? accountName : assocMember.getAssocName());
         log.info("makeDeposit invoked with [" + fullAccountName + "]");

         MnyAccount mnyAcct =
            ((accountName != null) ? findAccountByName(accountName)
                                   : findAccountByMember(assocMember));
         boolean notFound = (mnyAcct == null);

         if (notFound)
         {

            mnyAcct = createInstance();
            mnyAcct.setAcctName(fullAccountName);
            mnyAcct.setOwner(assocMember);
            mnyAcct.setInitBalance(new Long(0));

         }

         AcctRecord acctRec =
            mnyAcct.deposit(amount, memo, tKind, depositDate);

         if (notFound)
         {

            getEntityManager().persist(mnyAcct);
            getEntityManager().persist(acctRec);

         }
         else
         {

            getEntityManager().persist(acctRec);
            getEntityManager().persist(mnyAcct);

         }

      }
      catch (Exception ex)
      {

         log.error("Exception occured", ex);

      }

   }

   public void makeWithdrawal(String accountName, AssocMember assocMember,
      Sponsor sponsor, Long amount, String memo, AcctRecord.TKind tKind,
      Date withdrawalDate)
   {

      try
      {

         String fullAccountName =
            ((accountName != null) ? accountName : assocMember.getAssocName());
         log.info("makeWithdrawal invoked with [" + fullAccountName + "]");

         MnyAccount mnyAcct =
            ((accountName != null) ? findAccountByName(accountName)
                                   : findAccountByMember(assocMember));
         boolean notFound = (mnyAcct == null);

         if (notFound)
         {

            mnyAcct = createInstance();
            mnyAcct.setAcctName(fullAccountName);
            mnyAcct.setOwner(assocMember);
            mnyAcct.setInitBalance(new Long(0));

         }

         AcctRecord acctRec =
            mnyAcct.withdraw(amount, memo, tKind, withdrawalDate);

         if (notFound)
         {

            getEntityManager().persist(mnyAcct);
            getEntityManager().persist(acctRec);

         }
         else
         {

            getEntityManager().persist(acctRec);
            getEntityManager().persist(mnyAcct);

         }

      }
      catch (Exception ex)
      {

         log.error("Exception occured", ex);

      }

   }

   public String addCheck()
   {

      Calendar newCal = Calendar.getInstance();
      MnyAccount mnyAcct = getInstance();
      AcctRecord acctRec =
         mnyAcct.deposit(new Long(60), "Check XXXX", AcctRecord.TKind.check,
            newCal.getTime());
      getEntityManager().persist(acctRec);
      getEntityManager().persist(mnyAcct);

      return "updated";

   }

   public String addMembership()
   {

      Calendar newCal = Calendar.getInstance();
      int year = newCal.get(Calendar.YEAR);
      newCal.set(year, Calendar.JULY, 1, 1, 1, 1);

      Date firstDay = newCal.getTime();
      MnyAccount mnyAcct = getInstance();
      AcctRecord acctRec =
         mnyAcct.withdraw(new Long(60), year + " Membership",
            AcctRecord.TKind.electronic, firstDay);
      getEntityManager().persist(acctRec);
      getEntityManager().persist(mnyAcct);

      return "updated";

   }

   public String recomputeBalance()
   {

      MnyAccount reg = getInstance();
      List<AcctRecord> records = reg.getAcctRecords();
      Long balance = reg.getInitBalance();

      for (AcctRecord record : records)
      {

         balance += record.getTransAmount();
         record.setBalance(balance);
         getEntityManager().persist(record);

      }

      return "updated";

   }

   public String deleteAcctRecord()
   {

      if ((acctRecordId == null))
      {

         String msg = "No AcctRecord selected";
         FacesMessages.instance().add(msg);
         log.info(msg);

         return "failed";

      }

      MnyAccount reg = getInstance();
      List<AcctRecord> records = reg.getAcctRecords();

      for (AcctRecord record : records)
      {

         if (record.getId().equals(acctRecordId))
         {

            String msg =
               "ActRecord for [" + record.getMnyAccount().getAcctName()
               + "] deleted";
            log.info(msg);
            reg.getAcctRecords().remove(record);
            getEntityManager().remove(record);
            getEntityManager().persist(reg);

            return "updated";

         }

      }

      return "failed";

   }

}