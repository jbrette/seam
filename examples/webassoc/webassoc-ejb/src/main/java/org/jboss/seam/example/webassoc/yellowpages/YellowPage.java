package org.jboss.seam.example.webassoc.yellowpages;

import org.hibernate.validator.Digits;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.mship.AssocMember;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
@Name("yellowPage")
public class YellowPage
        implements Serializable
{

   private static final long serialVersionUID = -5403144828776641388L;

   // =================================
   // Sponsors
   // =================================
   private Long m_id;
   private String m_yellowPageName;

   // =================================
   // Address and Classical Mail
   // =================================
   private String m_address;
   private String m_aptOrSuite;
   private String m_city;
   private String m_state;
   private String m_zip;
   private String m_country;

   // =================================
   // EMail
   // =================================
   private String m_comment;
   private String m_cat1;
   private String m_cat2;
   private String m_webSite1;
   private String m_webSite2;
   private String m_phone1;
   private String m_phone2;
   private YellowPageImage m_picture;
   private Double m_gmapLat;
   private Double m_gmapLng;

   // =================================
   // Referral
   // =================================
   private AssocMember m_referredBy;

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
   @Length(min = 3, max = 255)
   // @Pattern(regex = "[a-zA-Z \\-]+", message =
   // "Last name must only contain letters")
   public String getName()
   {

      return m_yellowPageName;

   }

   public void setName(String yellowPageName)
   {

      this.m_yellowPageName = yellowPageName;

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

   public Double getGMAPLat()
   {

      return m_gmapLat;

   }

   public void setGMAPLat(Double lat)
   {

      this.m_gmapLat = lat;

   }

   public Double getGMAPLng()
   {

      return m_gmapLng;

   }

   public void setGMAPLng(Double lng)
   {

      this.m_gmapLng = lng;

   }

   // =================================
   // EMail
   // =================================
   @Length(max = 100)
   public String getCat1()
   {

      return m_cat1;

   }

   public void setCat1(String cat)
   {

      m_cat1 = cat;

   }

   @Length(max = 100)
   public String getCat2()
   {

      return m_cat2;

   }

   public void setCat2(String cat)
   {

      m_cat2 = cat;

   }

   @Length(max = 100)
   public String getWebSite1()
   {

      return m_webSite1;

   }

   public void setWebSite1(String webSite)
   {

      m_webSite1 = webSite;

   }

   @Length(max = 100)
   public String getWebSite2()
   {

      return m_webSite2;

   }

   public void setWebSite2(String webSite)
   {

      m_webSite2 = webSite;

   }

   @Length(max = 20)
   // @Pattern(regex="\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d",message="#{messages['validator.custom.phone']}")
   public String getPhone1()
   {

      return m_phone1;

   }

   public void setPhone1(String homePhone)
   {

      this.m_phone1 = homePhone;

   }

   @Length(max = 20)
   // @Pattern(regex="\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d",message="#{messages['validator.custom.phone']}")
   public String getPhone2()
   {

      return m_phone2;

   }

   public void setPhone2(String homePhone)
   {

      this.m_phone2 = homePhone;

   }

   @Length(max = 255)
   public String getComment()
   {

      return m_comment;

   }

   public void setComment(String comment)
   {

      m_comment = comment;

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

         return getAddress() + ", " + aptOrSuite;

      }

   }

   // =================================
   // Relationship
   // =================================
   // =================================
   @OneToOne(fetch = FetchType.LAZY)
   public YellowPageImage getPicture()
   {

      return m_picture;

   }

   public void setPicture(YellowPageImage picture)
   {

      m_picture = picture;

   }

   // @NotNull
   @OneToOne
   public AssocMember getReferredBy()
   {

      return m_referredBy;

   }

   public void setReferredBy(AssocMember referredBy)
   {

      this.m_referredBy = referredBy;

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