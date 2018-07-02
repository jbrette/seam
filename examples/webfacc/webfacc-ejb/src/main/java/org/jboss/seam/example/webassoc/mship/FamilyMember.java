package org.jboss.seam.example.webassoc.mship;

import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.clubs.Club;
import org.jboss.seam.example.webassoc.clubs.ClubEvent;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.example.webassoc.util.OccupationCode;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Transient;

@Entity
@Name("familyMember")
public class FamilyMember
        implements Serializable
{

   private static final long serialVersionUID = 3327306429678829583L;

   // =================================
   // Gender Definition
   // =================================
   public enum Gender
   {

      male(0), female(1);

      private int id;

      private Gender(int id)
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
   // Kind Definition
   // =================================
   public enum FKind
   {

      spouse(0), kid(1), contact(2), main(3);

      private int id;

      private FKind(int id)
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
   private AssocMember m_assocMember;
   private String m_posTitle;
   private String m_lastName;
   private String m_firstName;
   private Date m_dob;
   private String m_cellPhone;
   private String m_phoneExt;
   private String m_faxExt;
   private String m_emailExt;
   private Gender m_gender = Gender.male;
   private FKind m_fkind = FKind.main;
   private Boolean m_movedAway;
   private OccupationCode m_occupation = OccupationCode.___;
   private List<ClubMembership> m_memberships =
      new ArrayList<ClubMembership>();
   private List<ClubEvent> m_eventCoordinations = new ArrayList<ClubEvent>();

   // =================================
   // Constructor
   // =================================
   public FamilyMember()
   {

   }

   public FamilyMember(AssocMember assocMember)
   {

      this.m_assocMember = assocMember;
      assocMember.getFamilyMembers().add(this);

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

   @Length(max = 50)
   public String getPosTitle()
   {

      return m_posTitle;

   }

   public void setPosTitle(String posTitle)
   {

      this.m_posTitle = posTitle;

   }

   @NotNull
   @Length(min = 2, max = 40)
   // @Pattern(regex = "[a-zA-Z \\-]+", message =
   // "Last Name must only contain letters")
   public String getLastName()
   {

      return m_lastName;

   }

   public void setLastName(String lastName)
   {

      this.m_lastName = lastName;

   }

   @NotNull
   @Length(min = 1, max = 40)
   // @Pattern(regex = "[a-zA-Z \\-]+", message =
   // "First Name must only contain letters")
   public String getFirstName()
   {

      return m_firstName;

   }

   public void setFirstName(String firstName)
   {

      this.m_firstName = firstName;

   }

   @NotNull
   public Gender getGender()
   {

      return m_gender;

   }

   public void setGender(Gender gender)
   {

      this.m_gender = gender;

   }

   @NotNull
   public FKind getFKind()
   {

      return m_fkind;

   }

   public void setFKind(FKind fkind)
   {

      this.m_fkind = fkind;

   }

   @NotNull
   public Date getDob()
   {

      return m_dob;

   }

   public void setDob(Date dob)
   {

      this.m_dob = dob;

   }

   @Transient
   public boolean isAdult()
   {

      return (m_fkind == FKind.spouse) || (m_fkind == FKind.contact)
         || (m_fkind == FKind.main);

   }

   @Transient
   public String getAge()
   {

      if (isAdult())
      {

         return "";

      }
      else
      {

         Calendar birthday = new GregorianCalendar();
         birthday.setTime(m_dob);

         int by = birthday.get(Calendar.YEAR);
         int bm = birthday.get(Calendar.MONTH);
         int bd = birthday.get(Calendar.DATE);
         Calendar now = new GregorianCalendar();
         now.setTimeInMillis(System.currentTimeMillis());

         int ny = now.get(Calendar.YEAR);
         int nm = now.get(Calendar.MONTH);
         int nd = now.get(Calendar.DATE);
         int age =
            ny - by + (((nm > bm) || ((nm == bm) && (nd >= bd))) ? 0 : -1);

         return String.format("%d years old", age);

      }

   }

   @Length(max = 20)
   @Pattern(
      regex = "\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d",
      message = "#{messages['validator.custom.phone']}"
   )
   public String getCellPhone()
   {

      return m_cellPhone;

   }

   public void setCellPhone(String cellPhone)
   {

      this.m_cellPhone = cellPhone;

   }

   @Length(max = 20)
   @Pattern(
      regex = "\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d",
      message = "#{messages['validator.custom.phone']}"
   )
   public String getPhoneExt()
   {

      return m_phoneExt;

   }

   public void setPhoneExt(String phoneExt)
   {

      this.m_phoneExt = phoneExt;

   }

   @Transient
   public String getPhone()
   {

      return (((m_phoneExt != null) && !Cts.Dflt.PHONE.equals(m_phoneExt))
            ? m_phoneExt : getAssocMember().getHomePhone());

   }

   @Length(max = 20)
   @Pattern(
      regex = "\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d",
      message = "#{messages['validator.custom.phone']}"
   )
   public String getFaxExt()
   {

      return m_faxExt;

   }

   public void setFaxExt(String faxExt)
   {

      this.m_faxExt = faxExt;

   }

   @Transient
   public String getFax()
   {

      return (((m_faxExt != null) && !Cts.Dflt.PHONE.equals(m_faxExt))
            ? m_faxExt : getAssocMember().getHomeFax());

   }

   @Length(max = 100)
   @Email
   public String getEmailExt()
   {

      return m_emailExt;

   }

   public void setEmailExt(String email)
   {

      this.m_emailExt = email;

   }

   @Transient
   public String getEmail()
   {

      return (((m_emailExt != null) && !Cts.Dflt.PHONE.equals(m_emailExt))
            ? m_emailExt : getAssocMember().getEmail());

   }

   public OccupationCode getOccupation()
   {

      return m_occupation;

   }

   public void setOccupation(OccupationCode occupation)
   {

      this.m_occupation = occupation;

   }

   public Boolean getMovedAway()
   {

      return m_movedAway;

   }

   public void setMovedAway(Boolean movedAway)
   {

      this.m_movedAway = movedAway;

   }

   // =================================
   // Relationship
   // =================================
   @NotNull @ManyToOne
   public AssocMember getAssocMember()
   {

      return m_assocMember;

   }

   public void setAssocMember(AssocMember assocMember)
   {

      this.m_assocMember = assocMember;

   }

   @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
   public List<ClubMembership> getMemberships()
   {

      return m_memberships;

   }

   public void setMemberships(List<ClubMembership> memberships)
   {

      this.m_memberships = memberships;

   }

   @OneToMany(mappedBy = "eventContact", cascade = CascadeType.REMOVE)
   public List<ClubEvent> getEventCoordinations()
   {

      return m_eventCoordinations;

   }

   public void setEventCoordinations(List<ClubEvent> eventCoordinations)
   {

      this.m_eventCoordinations = eventCoordinations;

   }

   // =================================
   // Other
   // =================================
   @SuppressWarnings("unchecked")
   public void addDefaultMemberships(EntityManager em)
   {

      Query clubQuery =
         em.createQuery(
            "select c from Club c where c.automaticMembership = true");
      List<Club> clubs = clubQuery.getResultList();

      for (Club club : clubs)
      {

         ClubMembership membership = new ClubMembership();
         membership.setMember(this);
         membership.setClub(club);
         membership.setFKind(ClubMembership.FKind.member);
         membership.setActive(Boolean.TRUE);
         getMemberships().add(membership);
         club.getClubMembers().add(membership);
         em.persist(membership);

      }

   }

}