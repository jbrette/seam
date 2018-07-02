package org.jboss.seam.example.webassoc.sponsors;

import org.hibernate.validator.Digits;
import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;

import org.jboss.seam.annotations.Name;

import java.io.Serializable;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
@Name("sponsor")
public class Sponsor
        implements Serializable
{

   private static final long serialVersionUID = -160359063107758536L;

   // =================================
   // Kind Definition
   // =================================
   public enum SKind
   {

      standard(0), premium(1), bronze(2), silver(3), gold(4), platinum(5);

      private int id;

      private SKind(int id)
      {

         this.id = id;

      }

      public int getId()
      {

         return id;

      }

   }
   ;

   // Front page space
   public enum FPState
   {

      invisible(0), visible(1), url(2);

      private int id;

      private FPState(int id)
      {

         this.id = id;

      }

      public int getId()
      {

         return id;

      }

   }
   ;

   // =================================
   // Sponsors
   // =================================
   private Long m_id;
   private String m_sponsorName;

   // =================================
   // Sponsorship
   // =================================
   private SKind m_skind = SKind.standard;
   private Date m_sponsorSince;
   private Date m_sponsorUntil;
   private Integer m_sponsorshipAmount;
   private Boolean m_sponsorshipValid;
   private Date m_lastPaymentDate;
   private FPState m_frontPage = FPState.invisible;

   // =================================
   // Address and Classical Mail
   // =================================
   private String m_address;
   private String m_city;
   private String m_state;
   private String m_zip;
   private String m_country;
   private String m_phone;

   // =================================
   // EMail
   // =================================
   private String m_email;
   private String m_webSite;
   private SponsorImage m_picture;
   private String m_content;

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
   // @Pattern(regex = "[a-zA-Z \\-]+", message =
   // "Last name must only contain letters")
   public String getSponsorName()
   {

      return m_sponsorName;

   }

   public void setSponsorName(String sponsorName)
   {

      this.m_sponsorName = sponsorName;

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
   public String getState()
   {

      return m_state;

   }

   public void setState(String state)
   {

      this.m_state = state;

   }

   @Length(max = 6)
   @Digits(
      integerDigits = 5, fractionalDigits = 0,
      message = "#{messages['validator.custom.zip']}"
   )
   public String getZip()
   {

      return m_zip;

   }

   public void setZip(String zip)
   {

      this.m_zip = zip;

   }

   @Length(max = 20)
   @Pattern(
      regex = "\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d",
      message = "#{messages['validator.custom.phone']}"
   )
   public String getPhone()
   {

      return m_phone;

   }

   public void setPhone(String homePhone)
   {

      this.m_phone = homePhone;

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

      m_email = email;

   }

   @Length(max = 100)
   public String getWebSite()
   {

      return m_webSite;

   }

   public void setWebSite(String webSite)
   {

      m_webSite = webSite;

   }

   // =================================
   // Sponsorship
   // =================================
   @NotNull
   public SKind getSKind()
   {

      return m_skind;

   }

   public void setSKind(SKind skind)
   {

      this.m_skind = skind;

   }

   public void setSponsorshipValid(Boolean sponsorshipValid)
   {

      this.m_sponsorshipValid = sponsorshipValid;

   }

   public Boolean getSponsorshipValid()
   {

      return m_sponsorshipValid;

   }

   public Date getSponsorSince()
   {

      return m_sponsorSince;

   }

   public void setSponsorSince(Date sponsorSince)
   {

      this.m_sponsorSince = sponsorSince;

   }

   public Date getSponsorUntil()
   {

      return m_sponsorUntil;

   }

   public void setSponsorUntil(Date sponsorUntil)
   {

      this.m_sponsorUntil = sponsorUntil;

   }

   public void setSponsorshipAmount(Integer sponsorshipdues)
   {

      this.m_sponsorshipAmount = sponsorshipdues;

   }

   public Integer getSponsorshipAmount()
   {

      return m_sponsorshipAmount;

   }

   public Date getLastPaymentDate()
   {

      return m_lastPaymentDate;

   }

   public void setLastPaymentDate(Date lastPaymentDate)
   {

      this.m_lastPaymentDate = lastPaymentDate;

   }

   @NotNull
   public FPState getFPState()
   {

      return m_frontPage;

   }

   public void setFPState(FPState fpState)
   {

      this.m_frontPage = fpState;

   }

   @Transient
   public boolean isWithFPLink()
   {

      return (m_frontPage == FPState.url);

   }

   // =================================
   // Relationship
   // =================================
   // =================================
   @OneToOne(fetch = FetchType.LAZY)
   public SponsorImage getPicture()
   {

      return m_picture;

   }

   public void setPicture(SponsorImage picture)
   {

      m_picture = picture;

   }

   @Lob
   public String getContent()
   {

      return m_content;

   }

   public void setContent(String description)
   {

      if ((description != null) && (description.length() == 0))
      {

         // JEB: Protect against HIBERNATE-MYSQL CLOB issue
         m_content = " ";

      }
      else
      {

         m_content = description;

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

}