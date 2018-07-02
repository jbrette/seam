package org.jboss.seam.example.webassoc.security;

import org.jboss.seam.annotations.security.TokenUsername;
import org.jboss.seam.annotations.security.TokenValue;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AccountAutoLoginToken")
public class AccountAutoLoginToken
        implements java.io.Serializable
{

   private static final long serialVersionUID = 2061709609986612060L;
   private Integer m_tokenId;
   private String m_username;
   private String m_value;

   @Id @GeneratedValue
   public Integer getTokenId()
   {

      return m_tokenId;

   }

   public void setTokenId(Integer tokenId)
   {

      this.m_tokenId = tokenId;

   }

   @TokenUsername
   public String getUsername()
   {

      return m_username;

   }

   public void setUsername(String username)
   {

      this.m_username = username;

   }

   @TokenValue
   public String getValue()
   {

      return m_value;

   }

   public void setValue(String value)
   {

      this.m_value = value;

   }

}