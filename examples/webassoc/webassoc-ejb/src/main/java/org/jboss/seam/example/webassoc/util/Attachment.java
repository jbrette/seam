package org.jboss.seam.example.webassoc.util;

import org.jboss.seam.annotations.Name;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Transient;

@Entity
@Name("attachment")
@Inheritance(strategy = InheritanceType.JOINED)
public class Attachment
        implements Serializable
{

   private static final long serialVersionUID = 5920035490173158803L;
   private Integer m_imageId;
   private byte[] m_data;
   private byte[] m_thumbnail;
   private String m_contentType;
   private String m_caption;
   private String m_fileName;

   @Id @GeneratedValue
   public Integer getImageId()
   {

      return m_imageId;

   }

   public void setImageId(Integer imageId)
   {

      m_imageId = imageId;

   }

   public String getFileName()
   {

      return m_fileName;

   }

   public void setFileName(String fileName)
   {

      m_fileName = fileName;

   }

   public String getContentType()
   {

      return m_contentType;

   }

   public void setContentType(String contentType)
   {

      m_contentType = contentType;

   }

   public String getCaption()
   {

      return m_caption;

   }

   public void setCaption(String caption)
   {

      m_caption = caption;

   }

   @Lob
   public byte[] getData()
   {

      return m_data;

   }

   public void setData(byte[] data)
   {

      m_data = data;

   }

   @Transient
   public boolean isDataNotEmpty()
   {

      return (m_data != null) && (m_data.length != 0);

   }

   @Lob
   public byte[] getThumbnail()
   {

      return m_thumbnail;

   }

   public void setThumbnail(byte[] data)
   {

      m_thumbnail = data;

   }

   @Transient
   public boolean isThumbnailNotEmpty()
   {

      return (m_thumbnail != null) && (m_thumbnail.length != 0);

   }

   public void updateThumbnail()
   {

      Thumbnail th = new Thumbnail();
      byte[] res = th.createThumbnail1(getData(), this.getContentType(), 200);
      setThumbnail(res);

   }

}