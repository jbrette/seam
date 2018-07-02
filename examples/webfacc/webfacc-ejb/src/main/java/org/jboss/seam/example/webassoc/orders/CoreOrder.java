package org.jboss.seam.example.webassoc.orders;

import org.hibernate.validator.Digits;
import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;

import org.jboss.seam.annotations.Name;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
@Name("coreOrder")
@Inheritance(strategy = InheritanceType.JOINED)
public class CoreOrder
        implements Serializable
{

   private static final long serialVersionUID = 2462956771857590434L;

   // =================================
   // Members
   // =================================
   private Long m_id;
   private String m_buyerName;

   // =================================
   // Address and Classical Mail
   // =================================
   private String m_address;
   private String m_aptOrSuite;
   private String m_city;
   private String m_state;
   private String m_zip;
   private String m_country;
   private String m_homePhone;
   private String m_email;
   private String m_paymentInfo;
   private String m_price;

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
   @Length(min = 3, max = 40)
   // @Pattern(regex = "[a-zA-Z \\-]+", message = "Last name must only contain letters")
   public String getBuyerName()
   {

      return m_buyerName;

   }

   public void setBuyerName(String buyerName)
   {

      this.m_buyerName = buyerName;

   }

   // =================================
   // Address and Classical Mail
   // =================================
   @Length(max = 250)
   public String getAddress()
   {

      return m_address;

   }

   public void setAddress(String address)
   {

      this.m_address = address;

   }

   public String getAptOrSuite()
   {

      return m_aptOrSuite;

   }

   public void setAptOrSuite(String aptOrSuite)
   {

      this.m_aptOrSuite = aptOrSuite;

   }

   @Length(max = 50)
   public String getCity()
   {

      return m_city;

   }

   public void setCity(String city)
   {

      this.m_city = city;

   }

   @Length(max = 50)
   public String getCountry()
   {

      return m_country;

   }

   public void setCountry(String country)
   {

      this.m_country = country;

   }

   @Length(max = 50)
   @Pattern(
      regex = "[A-Z][A-Z]", message = "#{messages['validator.custom.state']}"
   )
   public String getState()
   {

      return m_state;

   }

   public void setState(String state)
   {

      this.m_state = state;

   }

   @Length(max = 6)
   @Digits(integerDigits = 5, fractionalDigits = 0)
   public String getZip()
   {

      return m_zip;

   }

   public void setZip(String zip)
   {

      this.m_zip = zip;

   }

   // ================================
   // Recompute the registration link
   // ================================
   @Transient
   public String getGMAPAddress()
   {

      String addr =
         getAddress() + ", " + getCity() + ", " + getState() + " " + getZip();

      return addr;

   }

   @Transient
   public String getFullAddress()
   {

      String aptOrSuite = getAptOrSuite();

      if ((aptOrSuite == null) || (aptOrSuite.length() == 0))
      {

         return getAddress();

      }
      else
      {

         return getAddress() + " , " + aptOrSuite;

      }

   }

   @Length(max = 20)
   @Pattern(
      regex = "\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d",
      message = "#{messages['validator.custom.phone']}"
   )
   public String getHomePhone()
   {

      return m_homePhone;

   }

   public void setHomePhone(String homePhone)
   {

      this.m_homePhone = homePhone;

   }

   // =================================
   // EMail
   // =================================
   @Length(max = 100)
   @Email
   public String getEmail()
   {

      return m_email;

   }

   public void setEmail(String email)
   {

      this.m_email = email;

   }

   // =================================
   // Membership
   // =================================
   public String getPaymentInfo()
   {

      return m_paymentInfo;

   }

   public void setPaymentInfo(String paymentInfo)
   {

      this.m_paymentInfo = paymentInfo;

   }

   public String getPrice()
   {

      return m_price;

   }

   public void setPrice(String price)
   {

      this.m_price = price;

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

}