package org.jboss.seam.example.webassoc.classified;

import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.mship.AssocMember;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
@Name("classified")
public class Classified
        implements Serializable
{

   private static final long serialVersionUID = -1844917087283799068L;

   // =================================
   // Kind Definition
   // =================================
   public enum CKind
   {

      sell(0), rent(1), buy(2), service(3);

      private int id;

      private CKind(int id)
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
   private CKind m_kind;
   private String m_title;
   private String m_description;
   private Date m_date;
   private List<ClassifiedImage> m_images = new ArrayList<ClassifiedImage>();
   private Boolean m_visible;
   private Boolean m_archived;
   private AssocMember m_publisher;
   private String m_homePhone;
   private String m_cellPhone;
   private String m_email;

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

   public CKind getCKind()
   {

      return m_kind;

   }

   public void setCKind(CKind kind)
   {

      this.m_kind = kind;

   }

   @Transient
   public boolean isSellKind()
   {

      return m_kind == CKind.sell;

   }

   @Transient
   public boolean isRentKind()
   {

      return m_kind == CKind.rent;

   }

   @Transient
   public boolean isBuyKind()
   {

      return m_kind == CKind.buy;

   }

   @Transient
   public boolean isServiceKind()
   {

      return m_kind == CKind.service;

   }

   @Length(max = 50)
   public String getTitle()
   {

      return m_title;

   }

   public void setTitle(String name)
   {

      this.m_title = name;

   }

   @Lob
   public String getDescription()
   {

      return m_description;

   }

   public void setDescription(String description)
   {

      if ((description != null) && (description.length() == 0))
      {

         // JEB: Protect against HIBERNATE-MYSQL CLOB issue
         m_description = " ";

      }
      else
      {

         m_description = description;

      }

   }

   @NotNull
   public Date getDate()
   {

      return m_date;

   }

   public void setDate(Date commentDate)
   {

      this.m_date = commentDate;

   }

   @NotNull @ManyToOne
   public AssocMember getPublisher()
   {

      return m_publisher;

   }

   public void setPublisher(AssocMember seller)
   {

      this.m_publisher = seller;

   }

   // Classified is approved
   public void setVisible(Boolean visible)
   {

      this.m_visible = visible;

   }

   public Boolean getVisible()
   {

      return m_visible;

   }

   // Classified is archived
   public void setArchived(Boolean archived)
   {

      this.m_archived = archived;

   }

   public Boolean getArchived()
   {

      return m_archived;

   }

   // Text is displayable
   @Transient
   public Boolean getDisplayed()
   {

      return m_visible && !m_archived;

   }

   @Pattern(
      regex = "\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d",
      message = "#{messages['validator.custom.phone']}"
   )
   public String getHomePhone()
   {

      return m_homePhone;

   }

   public void setHomePhone(String m_homePhone)
   {

      this.m_homePhone = m_homePhone;

   }

   @Pattern(
      regex = "\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d",
      message = "#{messages['validator.custom.phone']}"
   )
   public String getCellPhone()
   {

      return m_cellPhone;

   }

   public void setCellPhone(String m_cellPhone)
   {

      this.m_cellPhone = m_cellPhone;

   }

   @Email
   public String getEmail()
   {

      return m_email;

   }

   public void setEmail(String m_email)
   {

      this.m_email = m_email;

   }

   // =================================
   // Relationship
   // =================================
   @OneToMany(mappedBy = "classified", cascade = CascadeType.REMOVE)
   public List<ClassifiedImage> getImages()
   {

      return m_images;

   }

   public void setImages(List<ClassifiedImage> images)
   {

      m_images = images;

   }

}