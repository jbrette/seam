package org.jboss.seam.example.webassoc.util;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
@Name("htext")
@Inheritance(strategy = InheritanceType.JOINED)
public class HText
        implements Serializable
{

   private static final long serialVersionUID = 3894629235611460215L;

   // =================================
   // Members
   // =================================
   private Long m_id;
   private String m_title;
   private String m_description;
   private String m_altDescription;
   private Boolean m_visible;
   private Boolean m_archived;
   private Date m_textDate;
   private AssocMember m_author;
   private List<HTextAttachment> m_attachments =
      new ArrayList<HTextAttachment>();

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

   @Lob
   public String getAltDescription()
   {

      return m_altDescription;

   }

   public void setAltDescription(String alternateDescription)
   {

      if ((alternateDescription != null)
         && (alternateDescription.length() == 0))
      {

         // JEB: Protect against HIBERNATE-MYSQL CLOB issue
         m_altDescription = " ";

      }
      else
      {

         m_altDescription = alternateDescription;

      }

   }

   public Date getTextDate()
   {

      return m_textDate;

   }

   public void setTextDate(Date textDate)
   {

      this.m_textDate = textDate;

   }

   // Text is approved
   public void setVisible(Boolean visible)
   {

      this.m_visible = visible;

   }

   public Boolean getVisible()
   {

      return m_visible;

   }

   // Text is archived
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

   // =================================
   // Relationship
   // =================================
   @OneToMany(mappedBy = "HText", cascade = CascadeType.REMOVE)
   public List<HTextAttachment> getAttachments()
   {

      return m_attachments;

   }

   public void setAttachments(List<HTextAttachment> attachments)
   {

      m_attachments = attachments;

   }

   @NotNull @ManyToOne
   public AssocMember getAuthor()
   {

      return m_author;

   }

   public void setAuthor(AssocMember author)
   {

      this.m_author = author;

   }

}