package org.jboss.seam.example.webassoc.mny;

import org.jboss.seam.example.webassoc.mny.AcctRecord;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.sponsors.Sponsor;

import java.util.Date;

import javax.ejb.Local;

@Local
public interface MnyAccountHome
{

   // =================================
   // Methods from MnyAccountHomeImpl
   // =================================
   public MnyAccount initMnyAccount();

   public void ejbRemove();

   // =================================
   // Methods from EntityHome and Home
   // =================================
   public Object getId();

   public void setId(Object id);

   public String persist();

   public String update();

   public String remove();

   public boolean isManaged();

   public void create();

   public Object getInstance();

   // ================================
   // Business operations
   // ================================
   public void registerOneAssocMember();

   public void registerOneSponsor();

   public void createMnyAccount(String accountName);

   public MnyAccount findAccountByName(String accountName);

   public MnyAccount findAccountByMember(AssocMember assocMember);

   public MnyAccount findAccountBySponsor(Sponsor sponsor);

   public void makeDeposit(String accountName, AssocMember assocMember,
      Sponsor sponsor, Long amount, String memo, AcctRecord.TKind tKind,
      Date despositDate);

   public void makeWithdrawal(String accountName, AssocMember assocMember,
      Sponsor sponsor, Long amount, String memo, AcctRecord.TKind tKind,
      Date withdrawalDate);

   public String addCheck();

   public String addMembership();

   public String recomputeBalance();

   public String deleteAcctRecord();

}