package org.jboss.seam.example.webassoc.security;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.Pattern;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import static org.jboss.seam.international.StatusMessage.Severity.ERROR;
import org.jboss.seam.security.crypto.BinTools;
import org.jboss.seam.security.management.JpaIdentityStore;
import org.jboss.seam.security.management.PasswordHash;

@Scope(ScopeType.EVENT)
@Name("passwordHashing")
public class PasswordHashing
{

   @In
   private JpaIdentityStore identityStore;
   @NotEmpty
   @Length(min = 8, max = 12)
   @Pattern(
      regex =
         "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$",
      message = "{insecure_password}"
   )
   private String m_password;
   private String m_passwordConfirm;
   private String m_passwordHash;
   private String m_passwordSalt;

   public String getPassword()
   {

      return m_password;

   }

   public void setPassword(String password)
   {

      this.m_password =
         ((password != null) && (password.trim().length() > 0))
         ? password.trim() : null;

   }

   public String getPasswordConfirm()
   {

      return m_passwordConfirm;

   }

   public void setPasswordConfirm(String confirm)
   {

      this.m_passwordConfirm = confirm;

   }

   public boolean isConfirmed()
   {

      return ((m_passwordConfirm != null)
            && m_passwordConfirm.equals(m_password));

   }

   public boolean checkConfirmed(FacesMessages facesMessages, String controlId)
   {

      boolean isConfirmed = isConfirmed();

      if (!isConfirmed)
      {

         facesMessages.addToControlFromResourceBundle(controlId, ERROR,
            "password_not_confirmed");

      }

      return isConfirmed;

   }

   public String getPasswordHash()
   {

      return m_passwordHash;

   }

   public void setPasswordHash(String passwordHash)
   {

      this.m_passwordHash = passwordHash;

   }

   public String getPasswordSalt()
   {

      return m_passwordSalt;

   }

   public void setPasswordSalt(String passwordSalt)
   {

      this.m_passwordSalt = passwordSalt;

   }

   static public String generateTempPassword()
   {

      java.util.Random rand = new java.util.Random();
      int[] aNums = new int[8];

      for (int n = 0; n < aNums.length; n++)
      {

         aNums[n] = rand.nextInt(9) + 1;

      }

      char[] ach1 =
         new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
      char[] ach2 =
         new char[] {'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U'};
      char[] ach3 =
         new char[] {'v', 'w', 'x', 'y', 'z', 'V', 'W', 'X', 'Y', 'Z'};
      char[] ach4 =
         new char[] {'k', '$', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u'};
      char[] ach5 =
         new char[] {'$', '%', '!', '#', '$', '%', '!', '#', '$', '%'};

      return (ach4[aNums[7]] + String.valueOf(aNums[2]) + ach1[aNums[3]]
            + String.valueOf(aNums[0]) + ach3[aNums[5]] + ach2[aNums[4]]
            + ach4[aNums[6]] + ach5[aNums[1]]);

   }

   public void generate()
   {

      byte[] salt;

      if ((m_passwordSalt == null) || "".equals(m_passwordSalt.trim()))
      {

         salt = PasswordHash.instance().generateRandomSalt();
         m_passwordSalt = BinTools.bin2hex(salt);

      }
      else
      {

         salt = BinTools.hex2bin(m_passwordSalt);

      }

      m_passwordHash = identityStore.generatePasswordHash(m_password, salt);

   }

}