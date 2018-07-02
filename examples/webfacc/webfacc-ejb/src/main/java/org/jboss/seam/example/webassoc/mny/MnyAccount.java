package org.jboss.seam.example.webassoc.mny;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.mny.AcctRecord.ARKind;
import org.jboss.seam.example.webassoc.mny.AcctRecord.TKind;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.sponsors.Sponsor;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
@Name("mnyAccount")
public class MnyAccount
        implements Serializable
{

   private static final long serialVersionUID = -3628180604888692997L;

   // =================================
   // Members
   // =================================
   private Long m_id;
   private String m_acctName;
   private Long m_initBalance;
   private AssocMember m_assocMember;
   private Sponsor m_sponsor;
   private List<AcctRecord> m_acctRecords = new ArrayList<AcctRecord>();

   // =================================
   // Optimistic locking
   // =================================
   private int m_versionNum;

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

   @NotNull
   @Length(min = 3, max = 100)
   public String getAcctName()
   {

      return m_acctName;

   }

   public void setAcctName(String assocName)
   {

      this.m_acctName = assocName;

   }

   @NotNull
   public Long getInitBalance()
   {

      return m_initBalance;

   }

   public void setInitBalance(Long lng)
   {

      this.m_initBalance = lng;

   }

   // =================================
   // Relationship
   // =================================
   // @NotNull
   @OneToOne
   public AssocMember getOwner()
   {

      return m_assocMember;

   }

   public void setOwner(AssocMember owner)
   {

      this.m_assocMember = owner;

   }

   // @NotNull
   @OneToOne
   public Sponsor getSponsor()
   {

      return m_sponsor;

   }

   public void setSponsor(Sponsor sponsor)
   {

      this.m_sponsor = sponsor;

   }

   @OneToMany(mappedBy = "mnyAccount", cascade = CascadeType.REMOVE)
   @OrderBy("date ASC")
   public List<AcctRecord> getAcctRecords()
   {

      return m_acctRecords;

   }

   public void setAcctRecords(List<AcctRecord> acctRecords)
   {

      this.m_acctRecords = acctRecords;

   }

   @Transient
   public Long getAcctBalance()
   {

      // get the balance from the last record
      List<AcctRecord> tmpList = getAcctRecords();

      if (!tmpList.isEmpty())
      {

         AcctRecord rec = tmpList.get(tmpList.size() - 1);

         return rec.getBalance();

      }
      else
      {

         return getInitBalance();

      }

   }

   // ===========================================
   // DON'T REMOVE. Used for optimistic locking
   // ===========================================
   @Version
   public int getVersionNum()
   {

      return m_versionNum;

   }

   public void setVersionNum(int versionNum)
   {

      this.m_versionNum = versionNum;

   }

   // =================================
   // account methods
   // =================================
   public AcctRecord deposit(Long amount, String memo, TKind tKind, Date date)
   {

      return addRecord(amount, memo, ARKind.credit, tKind, date);

   }

   public AcctRecord withdraw(Long amount, String memo, TKind tKind, Date date)
   {

      return addRecord((-1) * amount, memo, ARKind.debit, tKind, date);

   }

   public AcctRecord addRecord(Long amount, String memo, ARKind arKind,
      TKind tKind, Date date)
   {

      AcctRecord record = new AcctRecord();
      record.setDate(date);
      record.setTransAmount(amount);
      record.setTransMemo(memo);
      record.setARKind(arKind);
      record.setTKind(tKind);

      Long oldBalance = getAcctBalance();
      record.setBalance(oldBalance + amount);
      record.setMnyAccount(this);
      getAcctRecords().add(record);

      return record;

   }

   @Override
   public String toString()
   {

      String res =
         "MnyAccount [m_id=" + m_id + ", m_acctName=" + m_acctName
         + ", m_acctRecords=" + "]";

      for (AcctRecord i : m_acctRecords)
      {

         res += i.toString();

      }

      return res;

   }

}