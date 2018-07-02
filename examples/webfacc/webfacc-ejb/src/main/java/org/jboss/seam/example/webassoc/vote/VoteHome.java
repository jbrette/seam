package org.jboss.seam.example.webassoc.vote;

import javax.ejb.Local;

@Local
public interface VoteHome
{

   // Methods from VoteHomeImpl
   public Vote initVote();

   public void ejbRemove();

   // Methods from EntityHome and Home
   public Object getId();

   public void setId(Object id);

   public String persist();

   public String update();

   public String remove();

   public boolean isManaged();

   public void create();

   public Object getInstance();

   // Images handling
   public void setUploadedData(byte[] uploadedData);

   public void setUploadedContentType(String contentType);

   public void setUploadedFileName(String fileName);

   public String uploadAttachment();

   public String deleteAttachment();

   //
   public String sendTestEmail();

   public String announceVote();

   public String buildBallotList();

   public String emptyBallotList();

   public String markAsSent();

   public String updateResults();

}