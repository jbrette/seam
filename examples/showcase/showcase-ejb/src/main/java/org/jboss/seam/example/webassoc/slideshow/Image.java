/**
 * License Agreement.
 *
 *  JBoss RichFaces - Ajax4jsf Component Library
 *
 * Copyright (C) 2007  Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */
/*
 * Image.java
 * Last modified by: $Author: amarkhel $
 * $Revision: 16727 $   $Date: 2010-04-06 08:20:31 -0700 (Tue, 06 Apr 2010) $
 */
package org.jboss.seam.example.webassoc.slideshow;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.example.webassoc.mship.AssocMember;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

//@NamedQueries(
//   {
//
//      @NamedQuery(
//         name = "image-exist",
//         query =
//            "select i from Image i where i.path = :path and i.album = :album"
//      ),
//      @NamedQuery(
//         name = "image-countIdenticalImages",
//         query =
//            "select count(i) from Image i where i.path like :path and i.album = :album"
//      )
//   }
//)
/**
 * Class for representing Image Entity
 *  EJB3 Entity Bean
 *
 * @author Andrey Markhel
 */
@Entity
@Name("image")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class Image
        implements Serializable
{

   private static final long serialVersionUID = -7042878411608396483L;
   @Id @GeneratedValue
   private Long id;
   @NotNull @ManyToOne
   @OnDelete(action = OnDeleteAction.CASCADE)
   private Album album;
   @NotNull @NotEmpty
   @Length(min = 3, max = 200)
   @Column(length = 255, nullable = false)
   private String name;
   @Transient
   private boolean covering;
   @NotNull @NotEmpty
   @Length(min = 3)
   @Column(length = 1024, nullable = false)
   private String path;
   @Column(length = 255)
   private String cameraModel;
   private int height;
   private double size;
   private int width;
   @Temporal(TemporalType.TIMESTAMP)
   private Date uploaded;
   @NotNull @NotEmpty
   @Length(min = 3)
   @Column(length = 1024)
   private String description;
   @Temporal(TemporalType.TIMESTAMP)
   private Date created;
   @NotNull
   private boolean allowComments;
   private Boolean showMetaInfo = true;
   @Transient
   private boolean visited;

   /*
    * Comma separated tags value
    * */
   @Transient
   private String meta = "";

   /**
    * Getter for property preDefined
    *
    * @return is this shelf is predefined
    */
   // ********************** Accessor Methods ********************** //
   public Boolean getShowMetaInfo()
   {

      return showMetaInfo;

   }

   public void setShowMetaInfo(final Boolean showMetaInfo)
   {

      this.showMetaInfo = showMetaInfo;

   }

   public Long getId()
   {

      return id;

   }

   public String getName()
   {

      return name;

   }

   public void setName(String name)
   {

      this.name = name;

   }

   public String getDescription()
   {

      return description;

   }

   public void setDescription(String description)
   {

      this.description = description;

   }

   /**
    * Getter for property path.
    * Represent file-system structure, relative at uploadRoot dir(determined at startup, by default is system temp dir)
    * Usually is user.GetLogin() + SLASH + image.getAlbum().getId() + SLASH + fileName,
    * for example "amarkhel/15/coolPicture.jpg"
    *
    * @return relative path of image
    */
   public String getPath()
   {

      return path;

   }

   /**
    * Setter for property path
    *
    * @param path - relative path to image
    */
   public void setPath(String path)
   {

      this.path = path;

   }

   public Date getCreated()
   {

      return created;

   }

   public void setCreated(Date created)
   {

      this.created = created;

   }

   public Album getAlbum()
   {

      return album;

   }

   public void setAlbum(Album album)
   {

      this.album = album;

   }

   /**
    * Setter for property meta
    *
    * @param meta - string representation of metatags, associated to image. Used at jsf page.
    */
   public void setMeta(String meta)
   {

      this.meta = meta;

   }

   /**
    * Getter for property meta
    *
    * @return string representation of metatags, associated to image. Used at jsf page.
    */
   public String getMetaString()
   {

      return meta;

   }

   public String getCameraModel()
   {

      return cameraModel;

   }

   public void setCameraModel(String cameraModel)
   {

      this.cameraModel = cameraModel;

   }

   public int getHeight()
   {

      return height;

   }

   public void setHeight(int height)
   {

      this.height = height;

   }

   /**
    * Getter for property size
    *
    * @return size of image in KB
    */
   public double getSize()
   {

      return size;

   }

   /**
    * setter for property size
    *
    * @param size - size of image in KB
    */
   public void setSize(double size)
   {

      this.size = size;

   }

   public int getWidth()
   {

      return width;

   }

   public void setWidth(int width)
   {

      this.width = width;

   }

   /**
    * Getter for property uploaded
    *
    * @return date of upload to site of this image
    */
   public Date getUploaded()
   {

      return uploaded;

   }

   /**
    * setter for property uploaded
    *
    * @param uploaded - date of upload
    */
   public void setUploaded(Date uploaded)
   {

      this.uploaded = uploaded;

   }

   /**
    * Getter for property allowComments. If true, other user may comment this image.
    *
    * @return is other users may comment this image
    */
   public boolean isAllowComments()
   {

      return allowComments;

   }

   /**
    * @param allowComments the allowComments to set
    */
   public void setAllowComments(boolean allowComments)
   {

      this.allowComments = allowComments;

   }

   /**
    * @return if this image is covering for containing album
    */
   public boolean isCovering()
   {

      return covering;

   }

   /**
    * @param covering - determine if this image is covering for containing album
    */
   public void setCovering(boolean covering)
   {

      this.covering = covering;

   }

   /**
    * Getter for property visited
    *
    * @return boolean value, that indicated is user visit this image already
    */
   public boolean isVisited()
   {

      return visited;

   }

   /**
    * Setter for property visited
    *
    * @param visited - boolean value, that indicated is user visit this image already
    */
   public void setVisited(boolean visited)
   {

      this.visited = visited;

   }

   /**
    * Determine if this image should be marked as new(on jsf page, for example in tree)
    *
    * @return boolean value, that indicated is this image should be marked as new
    */
   public boolean isNew()
   {

      if (!visited)
      {

         final Calendar calendar = Calendar.getInstance();
         calendar.add(Calendar.DAY_OF_YEAR, -15);

         return this.getUploaded().after(calendar.getTime());

      }

      return false;

   }
   //---------------------------Business methods

   /**
    * Return relative path of this image in file-system(relative to uploadRoot parameter)
    */
   public String getFullPath()
   {

      if (getAlbum().getPath() == null)
      {

         return null;

      }

      return getAlbum().getPath() + this.path;

   }

   public AssocMember getOwner()
   {

      return getAlbum().getOwner();

   }

   public boolean isOwner(AssocMember user)
   {

      return (user != null) && user.equals(getOwner());

   }

   @Override
   public boolean equals(Object obj)
   {

      if (this == obj)
      {

         return true;

      }

      if (obj == null)
      {

         return false;

      }

      final Image image = (Image)obj;

      return ((id == null) ? (image.getId() == null)
                           : id.equals(image.getId()))
         && ((path == null) ? (image.getPath() == null)
                            : path.equals(image.getPath()));

   }

   @Override
   public int hashCode()
   {

      int result = (id != null) ? id.hashCode() : 0;
      result = (31 * result) + ((path != null) ? path.hashCode() : 0);

      return result;

   }

   @Override
   public String toString()
   {

      return "{id : " + getId() + ", name : " + getName() + "}";

   }

}