package org.jboss.seam.example.webassoc.clubs;

import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.util.Attachment;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
@Name("clubEventImage")
public class ClubEventImage
        extends Attachment
{

   private static final long serialVersionUID = -4311006491329448797L;
   private ClubEvent m_event;
   private Boolean m_visible;
   private Boolean m_archived;
   private AssocMember m_photograph;

   @NotNull @ManyToOne
   public AssocMember getPhotograph()
   {

      return m_photograph;

   }

   public void setPhotograph(AssocMember photograph)
   {

      this.m_photograph = photograph;

   }

   // Image is approved
   public void setVisible(Boolean visible)
   {

      this.m_visible = visible;

   }

   public Boolean getVisible()
   {

      return m_visible;

   }

   // Image is archived
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

   @NotNull @ManyToOne
   public ClubEvent getEvent()
   {

      return m_event;

   }

   public void setEvent(ClubEvent event)
   {

      this.m_event = event;

   }

}