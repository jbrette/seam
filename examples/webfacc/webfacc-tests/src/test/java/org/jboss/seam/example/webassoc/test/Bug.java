package org.jboss.seam.example.webassoc.test;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import org.jboss.seam.annotations.Name;

import java.io.Serializable;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * This is a simple entity bean used to track the enhancement requests
 * and bugs found by the beta testers.
 */
@Entity
@Name("bug")
public class Bug
        implements Serializable
{

   private static final long serialVersionUID = -8691112945813424852L;

   // =================================
   // Kind Definition
   // =================================
   public enum BState
   {

      created(0), open(1), fixed(2), rejected(3);

      private int id;

      private BState(int id)
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
   private String m_title;
   private String m_createdBy;
   private Date m_commentDate;
   private String m_comment;
   private Date m_resolutionDate;
   private String m_resolution;
   private BState m_bstate;

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

   @Length(max = 50)
   public String getCreatedBy()
   {

      return m_createdBy;

   }

   public void setCreatedBy(String name)
   {

      this.m_createdBy = name;

   }

   @NotNull
   @Length(max = 5000)
   public String getComment()
   {

      return m_comment;

   }

   public void setComment(String comment)
   {

      this.m_comment = comment;

   }

   @NotNull
   public Date getCommentDate()
   {

      return m_commentDate;

   }

   public void setCommentDate(Date commentDate)
   {

      this.m_commentDate = commentDate;

   }

   @Length(max = 5000)
   public String getResolution()
   {

      return m_resolution;

   }

   public void setResolution(String resolution)
   {

      this.m_resolution = resolution;

   }

   public Date getResolutionDate()
   {

      return m_resolutionDate;

   }

   public void setResolutionDate(Date resolutionDate)
   {

      this.m_resolutionDate = resolutionDate;

   }

   @NotNull
   public BState getBState()
   {

      return m_bstate;

   }

   public void setBState(BState bstate)
   {

      this.m_bstate = bstate;

   }

}