package org.jboss.seam.example.webassoc.mship;

import org.hibernate.validator.Digits;
import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.clubs.ClubEventRegistration;
import org.jboss.seam.example.webassoc.mail.Mailing;
import org.jboss.seam.example.webassoc.slideshow.Album;
import org.jboss.seam.example.webassoc.util.FormEntry;
import org.jboss.seam.example.webassoc.util.UNCountryCode;
import org.jboss.seam.example.webassoc.vote.Ballot;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
@Name("assocMember")
public class AssocMember
        implements Serializable
{

   private static final long serialVersionUID = 1459813778756054081L;

   // =================================
   // Kind Definition
   // =================================
   public enum MKind
   {

      normal(0), committee(1), guest(2), sponsor(3), advertiser(4),
      partnerorg(5), pastmember(6), corporate(7), youngpro(8);

      private int id;

      private MKind(int id)
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
   // Members
   // =================================
   private Long m_id;
   private String m_assocName;

   // =================================
   // Membership
   // =================================
   private Date m_memberSince;
   private Date m_memberUntil;
   private Integer m_membershipDues;
   private Boolean m_membershipValid;
   private MKind m_memberKind;
   private Boolean m_freeMembership;
   private Date m_lastPaymentDate;
   private String m_paymentInfo;

   // =================================
   // Address and Classical Mail
   // =================================
   private Boolean m_useUSPS;
   private Boolean m_addressValid;
   private String m_address;
   private String m_aptOrSuite;
   private String m_city;
   private String m_state;
   private String m_zip;
   private String m_country;
   private String m_homePhone;
   private String m_homeFax;
   private Date m_inTheCitySince;
   private Double m_gmapLat;
   private Double m_gmapLng;

   // =================================
   // More Personal information
   // =================================
   private UNCountryCode m_nation1;
   private UNCountryCode m_nation2;

   // =================================
   // EMail
   // =================================
   private Boolean m_useEMail;
   private Boolean m_eMailValid;
   private String m_email;
   private String m_homePage;

   // Fields to remove
   private String m_formulaire;

   // Relationships fields
   private List<FamilyMember> m_familyMembers = new ArrayList<FamilyMember>();
   private List<ClubEventRegistration> m_events =
      new ArrayList<ClubEventRegistration>();
   private List<Mailing> m_mailings = new ArrayList<Mailing>();
   private List<Ballot> m_ballots = new ArrayList<Ballot>();
   private List<ClubEventHosting> m_eventHostings =
      new ArrayList<ClubEventHosting>();
   private List<Album> m_albums = new ArrayList<Album>();

   // =================================
   // Transient fields
   // =================================
   private transient String m_firstNames = null;
   private transient String m_kidFirstNames = null;
   private transient String m_spouseFirstNames = null;
   private transient String m_exportNameSuffix = null;
   private transient String m_attentionTo = null;

   // =================================
   // Optimistic locking
   // =================================
   private int m_versionNum;
   private AssocMemberImage m_picture;

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
   // @Pattern(regex = "[a-zA-Z \\-]+", message = "Last name must only contain letters")
   public String getAssocName()
   {

      return m_assocName;

   }

   public void setAssocName(String assocName)
   {

      this.m_assocName = assocName;

   }

   // =================================
   // Address and Classical Mail
   // =================================
   public Boolean getUseUSPS()
   {

      return m_useUSPS;

   }

   public void setUseUSPS(Boolean useUSPS)
   {

      this.m_useUSPS = useUSPS;

   }

   public Boolean getAddressValid()
   {

      return m_addressValid;

   }

   public void setAddressValid(Boolean addressValid)
   {

      this.m_addressValid = addressValid;

   }

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

   @Length(max = 20)
   @Pattern(
      regex = "\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d",
      message = "#{messages['validator.custom.phone']}"
   )
   public String getHomeFax()
   {

      return m_homeFax;

   }

   public void setHomeFax(String homeFax)
   {

      this.m_homeFax = homeFax;

   }

   public Date getInTheCitySince()
   {

      return m_inTheCitySince;

   }

   public void setInTheCitySince(Date inTheCitySince)
   {

      this.m_inTheCitySince = inTheCitySince;

   }

   // =================================
   // More Personal information
   // =================================
   public UNCountryCode getNation1()
   {

      return m_nation1;

   }

   public void setNation1(UNCountryCode nation1)
   {

      this.m_nation1 = nation1;

   }

   public UNCountryCode getNation2()
   {

      return m_nation2;

   }

   public void setNation2(UNCountryCode nation2)
   {

      this.m_nation2 = nation2;

   }

   // =================================
   // EMail
   // =================================
   public Boolean getUseEMail()
   {

      return m_useEMail;

   }

   public void setUseEMail(Boolean useEMail)
   {

      this.m_useEMail = useEMail;

   }

   public Boolean getEMailValid()
   {

      return m_eMailValid;

   }

   public void setEMailValid(Boolean eMailValid)
   {

      this.m_eMailValid = eMailValid;

   }

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

   @Length(max = 200)
   public String getHomePage()
   {

      return m_homePage;

   }

   public void setHomePage(String homePage)
   {

      this.m_homePage = homePage;

   }

   // =================================
   // Membership
   // =================================
   public Boolean getMembershipValid()
   {

      return m_membershipValid;

   }

   public void setMembershipValid(Boolean membershipValid)
   {

      this.m_membershipValid = membershipValid;

   }

   public Date getMemberSince()
   {

      return m_memberSince;

   }

   public void setMemberSince(Date memberSince)
   {

      this.m_memberSince = memberSince;

   }

   public Date getMemberUntil()
   {

      return m_memberUntil;

   }

   public void setMemberUntil(Date memberUntil)
   {

      this.m_memberUntil = memberUntil;

   }

   public Integer getMembershipDues()
   {

      return m_membershipDues;

   }

   public void setMembershipDues(Integer membershipdues)
   {

      this.m_membershipDues = membershipdues;

   }

   public Date getLastPaymentDate()
   {

      return m_lastPaymentDate;

   }

   public void setLastPaymentDate(Date lastPaymentDate)
   {

      this.m_lastPaymentDate = lastPaymentDate;

   }

   public Boolean getFreeMembership()
   {

      return m_freeMembership;

   }

   public void setFreeMembership(Boolean freeMembership)
   {

      this.m_freeMembership = freeMembership;

   }

   public String getPaymentInfo()
   {

      return m_paymentInfo;

   }

   public void setPaymentInfo(String paymentInfo)
   {

      this.m_paymentInfo = paymentInfo;

   }

   public MKind getMKind()
   {

      return m_memberKind;

   }

   public void setMKind(MKind memberKind)
   {

      this.m_memberKind = memberKind;

   }

   @Transient
   public boolean isClassic()
   {

      return (m_memberKind == AssocMember.MKind.normal)
         || (m_memberKind == AssocMember.MKind.committee)
         || (m_memberKind == AssocMember.MKind.corporate)
         || (m_memberKind == AssocMember.MKind.youngpro);

   }

   @Transient
   public String getExportNameSuffix()
   {

      if (m_exportNameSuffix != null)
      {

         return m_exportNameSuffix;

      }

      if ((m_memberKind == MKind.normal) || (m_memberKind == MKind.committee))
      {

         m_exportNameSuffix = "Family";

      }
      else if ((m_memberKind == MKind.corporate)
         || (m_memberKind == MKind.youngpro))
      {

         m_exportNameSuffix = "";

      }
      else if ((m_memberKind == MKind.sponsor)
         || (m_memberKind == MKind.advertiser)
         || (m_memberKind == MKind.partnerorg))
      {

         m_exportNameSuffix = "";

      }
      else if ((m_memberKind == MKind.guest)
         || (m_memberKind == MKind.pastmember))
      {

         m_exportNameSuffix = "";

      }
      else
      {

         m_exportNameSuffix = "Dallas Accueil Member";

      }

      return m_exportNameSuffix;

   }

   @Transient
   public String getAttentionTo()
   {

      if (m_attentionTo != null)
      {

         return m_attentionTo;

      }

      if (isClassic())
      {

         m_attentionTo = "";

      }
      else if ((m_memberKind == MKind.sponsor)
         || (m_memberKind == MKind.advertiser)
         || (m_memberKind == MKind.partnerorg))
      {

         List<FamilyMember> fmembers = getFamilyMembers();

         if (fmembers.size() > 0)
         {

            m_attentionTo =
               "To " + fmembers.get(0).getFirstName() + " "
               + fmembers.get(0).getLastName();

         }
         else
         {

            m_attentionTo = "";

         }

      }
      else if ((m_memberKind == MKind.guest)
         || (m_memberKind == MKind.pastmember))
      {

         m_attentionTo = "";

      }
      else
      {

         m_attentionTo = "";

      }

      return m_attentionTo;

   }

   @Transient
   public String getFirstNames()
   {

      if (m_firstNames != null)
      {

         return m_firstNames;

      }

      if ((m_memberKind == MKind.normal) || (m_memberKind == MKind.committee))
      {

         Calendar cdar = Calendar.getInstance();
         List<FamilyMember> fmembers = getFamilyMembers();
         List<FamilyMember> tmpf = new ArrayList<FamilyMember>();
         m_firstNames = "";

         for (FamilyMember fmember : fmembers)
         {

            if (fmember.isAdult())
            {

               m_firstNames += fmember.getFirstName() + " ";

            }
            else if (fmember.getFKind() == FamilyMember.FKind.kid)
            {

               tmpf.add(fmember);

            }

         }

         sortFamilyMembers(tmpf);

         for (FamilyMember fmember : tmpf)
         {

            cdar.setTime(fmember.getDob());
            m_firstNames +=
               fmember.getFirstName() + "(" + cdar.get(Calendar.YEAR) + ") ";

         }

      }
      else if ((m_memberKind == MKind.corporate)
         || (m_memberKind == MKind.youngpro))
      {

         m_firstNames = "";

      }
      else if ((m_memberKind == MKind.sponsor)
         || (m_memberKind == MKind.advertiser)
         || (m_memberKind == MKind.partnerorg))
      {

         m_firstNames = "";

      }
      else if ((m_memberKind == MKind.guest)
         || (m_memberKind == MKind.pastmember))
      {

         m_firstNames = "";

      }
      else
      {

         m_firstNames = "";

      }

      return m_firstNames;

   }

   @Transient
   public String getSpouseFirstNames()
   {

      if (m_spouseFirstNames != null)
      {

         return m_spouseFirstNames;

      }

      if ((m_memberKind == MKind.normal) || (m_memberKind == MKind.committee))
      {

         List<FamilyMember> fmembers = getFamilyMembers();
         m_spouseFirstNames = "";

         for (FamilyMember fmember : fmembers)
         {

            if (fmember.isAdult())
            {

               if (m_spouseFirstNames.equals(""))
               {

                  m_spouseFirstNames = fmember.getFirstName();

               }
               else
               {

                  m_spouseFirstNames += ", " + fmember.getFirstName();

               }

            }

         }

      }
      else if ((m_memberKind == MKind.sponsor)
         || (m_memberKind == MKind.advertiser)
         || (m_memberKind == MKind.partnerorg))
      {

         m_spouseFirstNames = "";

      }
      else if ((m_memberKind == MKind.guest)
         || (m_memberKind == MKind.pastmember))
      {

         m_spouseFirstNames = "";

      }
      else
      {

         m_spouseFirstNames = "";

      }

      return m_spouseFirstNames;

   }

   @Transient
   public String getKidFirstNames()
   {

      if (m_kidFirstNames != null)
      {

         return m_kidFirstNames;

      }

      if ((m_memberKind == MKind.normal) || (m_memberKind == MKind.committee))
      {

         Calendar cdar = Calendar.getInstance();
         List<FamilyMember> fmembers = getFamilyMembers();
         List<FamilyMember> tmpf = new ArrayList<FamilyMember>();
         m_kidFirstNames = "";

         for (FamilyMember fmember : fmembers)
         {

            if (fmember.getFKind() == FamilyMember.FKind.kid)
            {

               tmpf.add(fmember);

            }

         }

         sortFamilyMembers(tmpf);

         for (FamilyMember fmember : tmpf)
         {

            cdar.setTime(fmember.getDob());
            m_kidFirstNames +=
               fmember.getFirstName() + "(" + cdar.get(Calendar.YEAR) + ") ";

         }

      }
      else if ((m_memberKind == MKind.sponsor)
         || (m_memberKind == MKind.advertiser)
         || (m_memberKind == MKind.partnerorg))
      {

         m_kidFirstNames = "";

      }
      else if ((m_memberKind == MKind.guest)
         || (m_memberKind == MKind.pastmember))
      {

         m_kidFirstNames = "";

      }
      else
      {

         m_kidFirstNames = "";

      }

      return m_kidFirstNames;

   }

   private void sortFamilyMembers(List<FamilyMember> tmpf)
   {

      Collections.sort(tmpf, new Comparator<FamilyMember>()
         {

            public int compare(FamilyMember a, FamilyMember b)
            {

               if (a.getDob().equals(b.getDob()))
               {

                  return (a.getFirstName().compareTo(b.getFirstName()));

               }
               else
               {

                  return (a.getDob().compareTo(b.getDob()));

               }

            }

         });

   }

   // =================================
   // Relationship
   // =================================
   @OneToMany(mappedBy = "assocMember", cascade = CascadeType.REMOVE)
   public List<FamilyMember> getFamilyMembers()
   {

      return m_familyMembers;

   }

   public void setFamilyMembers(List<FamilyMember> familyMembers)
   {

      this.m_familyMembers = familyMembers;

   }

   @OneToOne(fetch = FetchType.LAZY)
   public AssocMemberImage getPicture()
   {

      return m_picture;

   }

   public void setPicture(AssocMemberImage picture)
   {

      m_picture = picture;

   }

   @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
   public List<ClubEventRegistration> getEvents()
   {

      return m_events;

   }

   public void setEvents(List<ClubEventRegistration> events)
   {

      this.m_events = events;

   }

   @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
   public List<Mailing> getMailings()
   {

      return m_mailings;

   }

   public void setMailings(List<Mailing> mailings)
   {

      this.m_mailings = mailings;

   }

   @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
   public List<Ballot> getBallots()
   {

      return m_ballots;

   }

   public void setBallots(List<Ballot> ballots)
   {

      this.m_ballots = ballots;

   }

   @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
   public List<ClubEventHosting> getEventHostings()
   {

      return m_eventHostings;

   }

   public void setEventHostings(List<ClubEventHosting> eventHostings)
   {

      this.m_eventHostings = eventHostings;

   }

   @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
   public List<Album> getAlbums()
   {

      return m_albums;

   }

   public void setAlbums(List<Album> eventHostings)
   {

      this.m_albums = eventHostings;

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

   // ============================
   // Legacy registration form
   // ============================
   @Length(max = 10000)
   public String getFormulaire()
   {

      return m_formulaire;

   }

   public void setFormulaire(String formulaire)
   {

      this.m_formulaire = formulaire;

   }

   /**
    * Functions to remove once the "email form" is not used anymore.
    * @return
    */
   @Transient
   public boolean getParEMail()
   {

      if ((m_formulaire == null) || (m_formulaire.length() == 0))
      {

         return false;

      }

      return (m_formulaire.indexOf("Par e-mail") != -1);

   }

   @Transient
   public boolean getParPoste()
   {

      if ((m_formulaire == null) || (m_formulaire.length() == 0))
      {

         return false;

      }

      return (m_formulaire.indexOf("Par voie postale") != -1);

   }

   @Transient
   public List<FormEntry> getFormulaireParts()
   {

      ArrayList<FormEntry> res = new ArrayList<FormEntry>();

      if ((m_formulaire == null) || (m_formulaire.length() == 0))
      {

         return res;

      }

      String tmp = m_formulaire;
      tmp = tmp.replaceAll("'", "|");
      tmp = tmp.replaceAll("\n", "");
      tmp = tmp.replaceAll("> ", "");
      tmp = tmp.replaceAll("\\|enfant", "'enfant");
      tmp = tmp.replaceAll("\\|enf", "'enf");
      tmp = tmp.replaceAll("\\|s first name", "'s first name");
      tmp = tmp.replaceAll("\\|anne", "'anne");

      String[] parts = tmp.split("\\|");
      String nb = "";

      for (int i = 0; i < parts.length; i = i + 2)
      {

         String[] subparts = parts[i].split("\\.");

         if ((subparts[0].trim().length() != 0) && (!nb.equals(subparts[0])))
         {

            nb = subparts[0].trim();

         }

         if (i < (parts.length - 1))
         {

            res.add(new FormEntry(nb, parts[i].trim(), parts[i + 1].trim()));

         }
         else
         {

            res.add(new FormEntry(nb, parts[i].trim(), ""));

         }

      }

      return res;

   }

}