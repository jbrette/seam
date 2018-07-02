package org.jboss.seam.example.webassoc.mny;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;

import java.io.Serializable;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Name("acctRecord")
public class AcctRecord
        implements Serializable
{

   private static final long serialVersionUID = 1198019286976329456L;

   // =================================
   // ARKind Definition
   // credit: Credit operation
   // debit: Debit operation
   // transfertIn: Transfer to this account from another account
   // transfertOut: Transfer from this account to another account
   // =================================
   public enum ARKind
   {

      credit(0), debit(1), transfertIn(2), transfertOut(3);

      private int id;

      private ARKind(int id)
      {

         this.id = id;

      }

      public int getId()
      {

         return id;

      }

   }

   // =================================
   // TKind Definition
   // cash: Cash
   // check: Check
   // moneyOrder: money order
   // wired: wired
   // =================================
   public enum TKind
   {

      cash(0), check(1), moneyOrder(2), electronic(3);

      private int id;

      private TKind(int id)
      {

         this.id = id;

      }

      public int getId()
      {

         return id;

      }

   }

   // =================================
   // Members
   // =================================
   private Long m_id;
   private MnyAccount m_mnyAccount;
   private String m_transMemo;
   private Long m_balance;
   private Long m_transAmount;
   private Date m_date;
   private ARKind m_ARKind;
   private TKind m_TKind;

   // =================================
   // Constructor
   // =================================
   public AcctRecord()
   {

   }

   public AcctRecord(MnyAccount mnyAccount)
   {

      this.m_mnyAccount = mnyAccount;
      mnyAccount.getAcctRecords().add(this);

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

   // JEB: Need a limit of 255
   // @Length(max = 50)
   public String getTransMemo()
   {

      return m_transMemo;

   }

   public void setTransMemo(String transMemo)
   {

      this.m_transMemo = transMemo;

   }

   @NotNull
   public Long getBalance()
   {

      return m_balance;

   }

   public void setBalance(Long balance)
   {

      this.m_balance = balance;

   }

   @NotNull
   public Long getTransAmount()
   {

      return m_transAmount;

   }

   public void setTransAmount(Long transAmount)
   {

      this.m_transAmount = transAmount;

   }

   @NotNull
   public Date getDate()
   {

      return m_date;

   }

   public void setDate(Date date)
   {

      this.m_date = date;

   }

   @NotNull
   public ARKind getARKind()
   {

      return m_ARKind;

   }

   public void setARKind(ARKind theKind)
   {

      this.m_ARKind = theKind;

   }

   @NotNull
   public TKind getTKind()
   {

      return m_TKind;

   }

   public void setTKind(TKind theKind)
   {

      this.m_TKind = theKind;

   }

   // =================================
   // Relationship
   // =================================
   @NotNull @ManyToOne
   public MnyAccount getMnyAccount()
   {

      return m_mnyAccount;

   }

   public void setMnyAccount(MnyAccount mnyAccount)
   {

      this.m_mnyAccount = mnyAccount;

   }

   @Override
   public String toString()
   {

      return "AcctRecord [m_id=" + m_id + ", m_date=" + m_date
         + ", m_transMemo=" + m_transMemo + ", m_transAmount=" + m_transAmount
         + ", m_balance=" + m_balance + "]";

   }

}