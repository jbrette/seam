package org.jboss.seam.example.webassoc.util;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.log.Log;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.persistence.EntityManager;
import javax.persistence.Lob;
import javax.persistence.Transient;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Name("downloadAttachment")
public class DownloadAttachment
{

   @Logger
   private Log log;
   @In
   private EntityManager entityManager;
   @RequestParameter
   private Integer attachmentId = null;
   private Integer selectedAttachmentId = null;

   public String download()
   {

      Attachment attachment =
         entityManager.find(Attachment.class, attachmentId);
      FacesContext context = FacesContext.getCurrentInstance();
      ExternalContext extCtx = context.getExternalContext();
      HttpServletResponse response = (HttpServletResponse)extCtx.getResponse();

      // response.reset();
      // response.setHeader("Cache-Control", "no-cache");
      // response.addHeader("Content-Length", String.valueOf(attachment.getData().length));
      response.setContentType(attachment.getContentType());
      response.addHeader("Content-disposition",
         "attachment; filename=\"" + attachment.getFileName() + "\"");

      try
      {

         ServletOutputStream os = response.getOutputStream();
         os.write(attachment.getData());
         os.flush();
         os.close();
         context.responseComplete();

      }
      catch (Exception e)
      {

         log.error("Failure : ", e);

      }

      return "download";

   }

   @Transient
   public String getDownloadURL()
   {

      FacesContext context = FacesContext.getCurrentInstance();
      ExternalContext extCtx = context.getExternalContext();
      HttpServletRequest request = (HttpServletRequest)extCtx.getRequest();
      String downloadLink = request.getRequestURL().toString();
      downloadLink +=
         ((downloadLink.indexOf("?") != -1) ? "&attachmentId="
                                            : "?attachmentId=") + attachmentId;

      return downloadLink;

   }

   public Integer getSelectedId()
   {

      return selectedAttachmentId;

   }

   public void setSelectedId(Integer selectedId)
   {

      selectedAttachmentId = selectedId;

   }

   @Transient
   public byte[] getData()
   {

      if (selectedAttachmentId == null)
      {

         return null;

      }

      Attachment attachment =
         entityManager.find(Attachment.class, selectedAttachmentId);

      return ((attachment == null) ? null : attachment.getData());

   }

   public void setData(byte[] data)
   {

      // Not sure it is useful. Keep naming convention correct
   }

   @Transient
   public boolean isDataNotEmpty()
   {

      byte[] data = getData();

      return (data != null) && (data.length != 0);

   }

   @Transient
   public byte[] getThumbnail()
   {

      if (selectedAttachmentId == null)
      {

         return null;

      }

      Attachment attachment =
         entityManager.find(Attachment.class, selectedAttachmentId);

      return ((attachment == null) ? null : attachment.getThumbnail());

   }

   public void setThumbnail(byte[] data)
   {

      // Not sure it is useful. Keep naming convention correct
   }

   @Transient
   public boolean isThumbnailNotEmpty()
   {

      byte[] thumbnail = getThumbnail();

      return (thumbnail != null) && (thumbnail.length != 0);

   }

}