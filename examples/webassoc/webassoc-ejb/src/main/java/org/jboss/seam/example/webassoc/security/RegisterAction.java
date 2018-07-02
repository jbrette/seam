package org.jboss.seam.example.webassoc.security;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.example.webassoc.mail.MailProcessor;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.mship.FamilyMember;
import org.jboss.seam.example.webassoc.mship.FamilyMember.FKind;
import org.jboss.seam.example.webassoc.mship.FamilyMember.Gender;
import org.jboss.seam.example.webassoc.orders.AccountOrder;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.example.webassoc.util.OccupationCode;
import org.jboss.seam.example.webassoc.util.UNCountryCode;
import org.jboss.seam.faces.FacesMessages;
import static org.jboss.seam.international.StatusMessage.Severity.ERROR;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.JpaIdentityStore;
import org.jboss.seam.util.Hex;

// import org.richfaces.component.html.HtmlModalPanel;

import java.security.MessageDigest;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.ejb.Remove;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Name("registerAction")
@Scope(ScopeType.EVENT)
public class RegisterAction
        implements java.io.Serializable
{

   private static final long serialVersionUID = 7860287229067330678L;
   @Logger
   private Log log;
   @In
   private EntityManager entityManager;
   @In
   private IdentityManager identityManager;
   @In
   private Credentials credentials;
   @In(create = true)
   private PasswordHashing passwordHashing;
   @In
   private FacesMessages facesMessages;
   @In(create = true)
   private MailProcessor mailProcessor;
   @Out(required = false, scope = ScopeType.SESSION)
   private AccountMember currentUser;

   // Private members
   private AccountMember m_newAccountMember;
   private AssocMember m_newAssocMember;

   // Members supporting the differents forms
   private String m_registrationUserName;
   private String m_registrationEmail;
   private String m_lostPasswordUserName;
   private String m_lostPasswordEmail;
   private String m_loggedInUserName;
   private String m_recoveredPasswordEmail;
   private String m_resetPassword;
   private boolean m_agreedToTermsOfUse = false;

   // Used to create an WEB Access Account for AssocMember
   // without WEB Access account
   @RequestParameter
   private Long assocMemberMissingWebAccessId;
   @RequestParameter
   private Long accountOrderMissingAssocMemberId;

   //=================================================================
   //
   //=================================================================
   public AssocMember initAssocMember()
   {

      AssocMember amember = new AssocMember();
      amember.setAssocName("theassocname");
      amember.setUseUSPS(Boolean.FALSE);
      amember.setAddressValid(Boolean.FALSE);
      amember.setAddress(Cts.Dflt.ADDRESS);
      amember.setAptOrSuite(Cts.Dflt.APTORNB);
      amember.setCity(Cts.Dflt.CITY);
      amember.setCountry(Cts.Dflt.COUNTRY);
      amember.setState(Cts.Dflt.STATE);
      amember.setZip(Cts.Dflt.ZIP);
      amember.setHomePhone(Cts.Dflt.PHONE);
      amember.setHomeFax(Cts.Dflt.PHONE);
      amember.setNation1(UNCountryCode.USA);
      amember.setNation2(UNCountryCode.___);
      amember.setUseEMail(Boolean.FALSE);
      amember.setEMailValid(Boolean.TRUE);
      amember.setEmail(Cts.Dflt.E_MAIL);
      amember.setHomePage("");
      amember.setMembershipValid(Boolean.TRUE);
      amember.setMemberSince(Calendar.getInstance().getTime());
      amember.setMemberUntil(Calendar.getInstance().getTime());
      amember.setMembershipDues(0);
      amember.setLastPaymentDate(Calendar.getInstance().getTime());
      amember.setMKind(AssocMember.MKind.guest);
      amember.setFreeMembership(Boolean.FALSE);
      amember.setPaymentInfo("");
      amember.setFormulaire("");

      return amember;

   }

   public FamilyMember initFamilyMember(AssocMember assocMember)
   {

      Calendar cdar = Calendar.getInstance();
      cdar.set(1900, 0, 0);

      FamilyMember fmember = new FamilyMember();
      fmember.setAssocMember(assocMember);
      assocMember.getFamilyMembers().add(fmember);
      fmember.setPosTitle("");
      fmember.setLastName(assocMember.getAssocName());
      fmember.setFirstName(Cts.Dflt.FIRST_NAME);
      fmember.setDob(cdar.getTime());
      fmember.setGender(Gender.male);
      fmember.setFKind(FKind.main);
      fmember.setCellPhone(Cts.Dflt.PHONE);
      fmember.setPhoneExt(Cts.Dflt.PHONE);
      fmember.setFaxExt(Cts.Dflt.PHONE);
      fmember.setEmailExt(Cts.Dflt.E_MAIL);
      fmember.setOccupation(OccupationCode.___);
      fmember.setMovedAway(false);

      return fmember;

   }

   //=================================================================
   // Normal security functions
   //=================================================================
   @Transactional
   public void doActivate(String activationKey)
   {

      Query q =
         entityManager.createQuery(
            "from AccountMember u where u.enabled=false AND u.activationKey=:activationKey");
      q.setParameter("activationKey", activationKey);

      AccountMember activatedUser = null;

      try
      {

         activatedUser = (AccountMember)q.getSingleResult();

      }
      catch (javax.persistence.NoResultException nre)
      {

         // safe to ignore ...
      }

      if (activatedUser != null)
      {

         activatedUser.setEnabled(true);
         activatedUser.setActivationKey(null);
         m_loggedInUserName = activatedUser.getUserName();
         credentials.setUsername(m_loggedInUserName);
         log.info("AccountMember {0} activated successfully.",
            m_loggedInUserName);

      }

   }

   @Observer(JpaIdentityStore.EVENT_USER_CREATED)
   public void accountCreated(AccountMember accountMember)
   {

      log.info("AccountMember created successfully. EVENT_USER_CREATED");

      // The user *may* have been created from the user manager screen. In
      // that case, create a dummy AsocMember record just for the purpose of
      // demonstrating the identity management API
      if (m_newAssocMember == null)
      {

         String tmpName = accountMember.getUserName();
         String[] parts = tmpName.split("\\.");
         m_newAssocMember = initAssocMember();
         m_newAssocMember.setAssocName((parts.length > 1) ? parts[1]
                                                          : parts[0]);
         m_newAssocMember.setEmail(accountMember.getEmail());
         m_newAssocMember.setHomePage("");
         m_newAssocMember.setMemberSince(accountMember.getCreatedOn());
         m_newAssocMember.setMemberUntil(accountMember.getCreatedOn());
         entityManager.persist(m_newAssocMember);

         FamilyMember fmember = initFamilyMember(m_newAssocMember);
         fmember.setFirstName((parts.length > 1) ? parts[0]
                                                 : Cts.Dflt.FIRST_NAME);
         entityManager.persist(fmember);

      }

      accountMember.setMember(m_newAssocMember);
      m_newAccountMember = accountMember;

   }

   @Observer(JpaIdentityStore.EVENT_USER_AUTHENTICATED)
   public void loginSuccessful(AccountMember account)
   {

      // This is the way seamspace is doing
      Contexts.getSessionContext().set("authenticatedMember",
         account.getMember());

      // This is the way the example was doing
      this.currentUser = account;
      log.info(
         "AccountMember {0} logged in successfully. EVENT_USER_AUTHENTICATED",
         this.currentUser.getUserName());

      // Let's update the last login fields.
      currentUser.incLoginCount();
      currentUser.updateLastLogin();

   }

   @Observer(Identity.EVENT_LOGIN_FAILED)
   public void onLoginFailed()
   {

      log.info("AccountMember login failed. EVENT_LOGIN_FAILED");
      this.currentUser = null;
      facesMessages.addToControlFromResourceBundle("loginBtn", ERROR,
         "login_error");

   }

   @Transactional
   public void doRecoverLostPassword()
           throws Exception
   {

      if ((m_lostPasswordUserName == null) || (m_lostPasswordEmail == null))
      {

         return;

      }

      m_lostPasswordEmail = m_lostPasswordEmail.trim();
      m_recoveredPasswordEmail = null;
      m_resetPassword = null;

      AccountMember foundUser = byUserName(m_lostPasswordUserName);

      if (foundUser == null)
      {

         facesMessages.addToControlFromResourceBundle("resetLostPassword",
            ERROR, "user_not_exist", m_lostPasswordUserName);

         return;

      }

      if (!foundUser.getEmail().equalsIgnoreCase(m_lostPasswordEmail))
      {

         facesMessages.addToControlFromResourceBundle("resetLostPassword",
            ERROR, "email_not_recognized");

         return;

      }

      try
      {

         // That password will be sent through the email
         m_resetPassword = PasswordHashing.generateTempPassword();

         final String newPassword = m_resetPassword;
         final String userName = foundUser.getUserName();
         final String userMail = foundUser.getEmail();
         new RunAsOperation()
            {

               public void execute()
               {

                  identityManager.changePassword(userName, newPassword);

               }

            }.addRole("admin").run();

         // JEB: Don't really know how transaction are working there.
         // There identityManagerr.changePassword has changed the same entity
         foundUser.setTemporaryPassword(true);
         entityManager.flush();

         if (credentials != null)
         {

            credentials.setUsername(foundUser.getUserName());

         }

         m_recoveredPasswordEmail = m_lostPasswordEmail;
         m_lostPasswordEmail = null;

         // @Observer("passwordReset")
         // RegistrationMailer.sendPasswordResetEmail
         // ==========================================
         // Events.instance().raiseTransactionSuccessEvent("passwordReset");
         mailProcessor.sendPasswordReset(userName, userMail, newPassword);

      }
      catch (Exception exc)
      {

         log.error("Password Reset Failed: ", exc);

         // note: don't send the exception message back to the client as it
         // may contain
         // too much information that a hacker could use to break into the
         // site
         facesMessages.addToControlFromResourceBundle("resetLostPassword",
            ERROR, "reset_failed_unknown");

      }

   }

   @Transactional
   public void doRegister()
   {

      if (!passwordHashing.isConfirmed())
      {

         facesMessages.addToControlFromResourceBundle("password", ERROR,
            "password_not_confirmed");

         return;

      }

      if (!m_agreedToTermsOfUse)
      {

         facesMessages.addToControlFromResourceBundle("registerButton", ERROR,
            "please_agree_to_terms");

         return;

      }

      try
      {

         String[] parts = m_registrationUserName.split("\\.");
         m_newAssocMember = initAssocMember();
         m_newAssocMember.setAssocName((parts.length > 1) ? parts[1]
                                                          : parts[0]);
         m_newAssocMember.setEmail(m_registrationEmail);
         m_newAssocMember.setHomePage("");
         m_newAssocMember.setMemberSince(Calendar.getInstance().getTime());
         m_newAssocMember.setMemberUntil(Calendar.getInstance().getTime());
         entityManager.persist(m_newAssocMember);

         FamilyMember fmember = initFamilyMember(m_newAssocMember);
         fmember.setFirstName((parts.length > 1) ? parts[0]
                                                 : Cts.Dflt.FIRST_NAME);
         entityManager.persist(fmember);
         new RunAsOperation()
            {

               public void execute()
               {

                  identityManager.createUser(m_registrationUserName,
                     passwordHashing.getPassword());
                  identityManager.grantRole(m_registrationUserName,
                     Cts.Roles.webuser);

               }

            }.addRole("admin").run();

         // Note that the accountCreated method
         m_newAccountMember.setMember(m_newAssocMember);
         m_newAccountMember = entityManager.merge(m_newAccountMember);
         m_newAccountMember.setEmail(m_registrationEmail);
         m_newAccountMember.setEnabled(false);
         m_newAccountMember.setActivationKey(getMD5Hash(
               m_newAccountMember.getUserName()
               + m_newAccountMember.getEmail()) + System.currentTimeMillis());
         m_newAccountMember.setLanguage("en");
         m_newAccountMember.setCreatedOn(Calendar.getInstance().getTime());
         m_newAccountMember.setLoginCount(0);
         m_newAccountMember.setLastLogin(null);

         // send the activation email ... as part of this transaction
         ExternalContext extCtxt =
            FacesContext.getCurrentInstance().getExternalContext();
         String activationLink =
            ((javax.servlet.http.HttpServletRequest)extCtxt.getRequest())
            .getRequestURL().toString();
         String newUserLink =
            activationLink
            + ((activationLink.indexOf("?") != -1) ? "&act=" : "?act=")
            + m_newAccountMember.getActivationKey();
         Contexts.getEventContext().set("inactiveNewUser",
            new InactiveNewUser(m_newAccountMember.getMember().getAssocName(),
               m_newAccountMember.getUserName(), m_newAccountMember.getEmail(),
               newUserLink));

         // @Observer("userRegistered")
         // Events.instance().raiseEvent("userRegistered");
         // ==========================================
         mailProcessor.sendActivation(m_newAccountMember.getUserName(), m_newAccountMember.getEmail(), "");

         //
         m_registrationUserName = null;
         m_newAccountMember = null;

      }
      catch (Exception exc)
      {

         log.error("Registration failed for {0}", exc,
            String.valueOf(m_registrationUserName));

         // NOTE: we don't return the Exception message back to the newUser
         // because it may reveal too much information to a hacker
         facesMessages.addToControlFromResourceBundle("registerButton", ERROR,
            "general_reg_error");

      }

   }

   @Transactional
   public String doChangePassword(Integer accountId)
   {

      AccountMember foundUser =
         entityManager.find(AccountMember.class, accountId);

      if (passwordHashing.checkConfirmed(facesMessages, "chngPswd"))
      {

         final String newPassword = passwordHashing.getPassword();
         final String userName = foundUser.getUserName();
         new RunAsOperation()
            {

               public void execute()
               {

                  identityManager.changePassword(userName, newPassword);

               }

            }.addRole("admin").run();
         foundUser.setTemporaryPassword(false);

         return "pswdChanged";

      }
      else
      {

         return "pswdUnchanged";

      }

   }

   //=================================================================
   // Error control functions
   //=================================================================
   public boolean getShowLogin()
   {

      // if there is a register error ... don't show login ...
      if (getRegisterError())
      {

         return false;

      }

      if (getForgotPasswordError())
      {

         return false;

      }

      boolean showLogin = false;

      if (m_loggedInUserName != null)
      {

         showLogin = true;

      }
      else
      {

         if (hasErrorMessage(FacesContext.getCurrentInstance(), "loginForm:"))
         {

            showLogin = true;

         }

      }

      return showLogin;

   }

   public boolean getForgotPasswordError()
   {

      return hasErrorMessage(FacesContext.getCurrentInstance(),
            "resetLostPassword");

   }

   public boolean getRegisterError()
   {

      return hasErrorMessage(FacesContext.getCurrentInstance(),
            "registerForm:");

   }

   public static final boolean hasErrorMessage(FacesContext jsf, String msgPfx)
   {

      boolean hasErrorMessage = false;
      FacesMessage.Severity maxSeverity = jsf.getMaximumSeverity();

      if ((maxSeverity != null)
         && (maxSeverity.getOrdinal()
            >= FacesMessage.SEVERITY_ERROR.getOrdinal()))
      {

         java.util.Iterator<String> clientIdIter =
            jsf.getClientIdsWithMessages();

         if (clientIdIter != null)
         {

            while (clientIdIter.hasNext())
            {

               String nextClientId = clientIdIter.next();

               if ((nextClientId != null)
                  && (nextClientId.startsWith(msgPfx)
                     || nextClientId.equals(msgPfx)))
               {

                  hasErrorMessage = true;

                  break;

               }

            }

         }

      }

      return hasErrorMessage;

   }

   public static final void showWhenRendered(FacesContext jsf,
      String modalPanelId, boolean showWhenRendered)
   {

      UIViewRoot viewRoot = jsf.getViewRoot();

      if (viewRoot != null)
      {

         Object modalPanel = viewRoot.findComponent(modalPanelId);

         if (modalPanel != null)
         {

            // ((HtmlModalPanel)modalPanel).setShowWhenRendered(showWhenRendered);

         }

      }

   }

   //=================================================================
   // Temporary member function
   //=================================================================
   public void setRegistrationUserName(String screenName)
   {

      if (hasUser(screenName))
      {

         facesMessages.addToControlFromResourceBundle("userName", ERROR,
            "user_id_is_taken", screenName);

      }
      else
      {

         m_registrationUserName = screenName;

      }

   }

   protected boolean hasUser(String userName)
   {

      boolean hasUser = false;

      if (userName != null)
      {

         Query q =
            entityManager.createQuery(
               "select u.userName from AccountMember u where u.userName = :userName");
         q.setParameter("userName", userName);
         hasUser = (q.getResultList().size() > 0);

      }

      return hasUser;

   }

   public String getRegistrationUserName()
   {

      return m_registrationUserName;

   }

   public void setRegistrationEmail(String email)
   {

      if (hasEmail(email))
      {

         facesMessages.addToControlFromResourceBundle("email", ERROR,
            "email_is_taken", email);

      }
      else
      {

         m_registrationEmail = email;

      }

   }

   protected boolean hasEmail(String email)
   {

      boolean hasUser = false;

      if (email != null)
      {

         Query q =
            entityManager.createQuery(
               "select u.email from AccountMember u where u.email = :email");
         q.setParameter("email", email);
         hasUser = (q.getResultList().size() > 0);

      }

      return hasUser;

   }

   public String getRegistrationEmail()
   {

      return m_registrationEmail;

   }

   public String getLostPasswordUserId()
   {

      return m_lostPasswordUserName;

   }

   public void setLostPasswordUserId(String value)
   {

      m_lostPasswordUserName = value;

   }

   public String getLostPasswordEmail()
   {

      return m_lostPasswordEmail;

   }

   public void setLostPasswordEmail(String value)
   {

      m_lostPasswordEmail = value;

   }

   public String getLoginUserId()
   {

      return m_loggedInUserName;

   }

   public void setLoginUserId(String loginUserId)
   {

      m_loggedInUserName = loginUserId;

   }

   public String getRecoveredPasswordEmail()
   {

      return m_recoveredPasswordEmail;

   }

   public void setRecoveredPasswordEmail(String value)
   {

      m_recoveredPasswordEmail = value;

   }

   public String getResetPassword()
   {

      return m_resetPassword;

   }

   public void setResetPassword(String value)
   {

      m_resetPassword = value;

   }

   public boolean getAgreedToTermsOfUse()
   {

      return m_agreedToTermsOfUse;

   }

   public void setAgreedToTermsOfUse(boolean agreedToTermsOfUse)
   {

      m_agreedToTermsOfUse = agreedToTermsOfUse;

   }

   //=================================================================
   // Search functions
   //=================================================================
   protected String getMD5Hash(final String msg)
   {

      try
      {

         MessageDigest md5 = MessageDigest.getInstance("MD5");
         md5.reset();

         return new String(Hex.encodeHex(md5.digest(msg.getBytes("UTF-8"))));

      }
      catch (Exception exc)
      {

         throw new RuntimeException(exc);

      }

   }

   protected AccountMemberRole getRole(String roleName)
   {

      try
      {

         Query q =
            entityManager.createQuery(
               "from AccountMemberRole r where r.name = :roleName");
         q.setParameter("roleName", roleName);

         return (AccountMemberRole)q.getSingleResult();

      }
      catch (javax.persistence.NoResultException nre)
      {

         return null;

      }

   }

   protected AccountMember byUserName(String userName)
   {

      try
      {

         Query q =
            entityManager.createQuery(
               "from AccountMember u where u.userName = :userName");
         q.setParameter("userName", userName);

         return (AccountMember)q.getSingleResult();

      }
      catch (javax.persistence.NoResultException nre)
      {

         return null;

      }

   }

   //=================================================================
   // Utility
   //=================================================================
   /**
    * Utility function used to populate the AccountMember table with the
    * content of the AssocMember
    */
   @SuppressWarnings("unchecked")
   @Transactional
   @Restrict("#{s:hasRole('admin')}")
   public void registerAllAssocMembers()
   {

      Query query1 =
         entityManager.createQuery("SELECT c FROM AssocMember c "
            + "WHERE (c.membershipValid = true) "
            + "AND (c.EMailValid = true) "
            + "AND ((c.MKind = :theKind1) or ((c.MKind = :theKind2a) or (c.MKind = :theKind2b) or (c.MKind = :theKind2c)) or (c.MKind = :theKind3)) "
            + "ORDER BY c.assocName");
      query1.setParameter("theKind1", AssocMember.MKind.normal);
      query1.setParameter("theKind2a", AssocMember.MKind.committee);
      query1.setParameter("theKind2b", AssocMember.MKind.corporate);
      query1.setParameter("theKind2c", AssocMember.MKind.youngpro);
      query1.setParameter("theKind3", AssocMember.MKind.guest);

      List<AssocMember> assocMembers = query1.getResultList();
      Query query2 =
         entityManager.createQuery(
            "SELECT c FROM AccountMember c where c.member = :assocMember");

      for (AssocMember member : assocMembers)
      {

         query2.setParameter("assocMember", member);

         List<AccountMember> accountMembers = query2.getResultList();

         if (accountMembers.size() == 0)
         {

            createAccountMemberFromAssocMember(member);

         }

      }

   }

   @SuppressWarnings("unchecked")
   @Transactional
   @Restrict("#{s:hasRole('membermgr')}")
   public void registerOneAssocMember()
   {

      log.info("registerOneAssocMember with id: "
         + assocMemberMissingWebAccessId);

      if (assocMemberMissingWebAccessId != null)
      {

         Query query1 =
            entityManager.createQuery(
               "SELECT c FROM AssocMember c where c.id = :assocMemberMissingWebAccessId");
         query1.setParameter("assocMemberMissingWebAccessId",
            assocMemberMissingWebAccessId);

         AssocMember assocMember = (AssocMember)query1.getSingleResult();
         log.info("registerOneAssocMember found AssocMember: "
            + assocMember.getAssocName());

         Query query2 =
            entityManager.createQuery(
               "SELECT c FROM AccountMember c where c.member = :assocMember");
         query2.setParameter("assocMember", assocMember);

         List<AccountMember> accountMembers = query2.getResultList();

         if (accountMembers.size() == 0)
         {

            log.info("registerOneAssocMember creating AccountMember for : "
               + assocMember.getAssocName());
            createAccountMemberFromAssocMember(assocMember);

         }
         else
         {

            log.info(
               "registerOneAssocMember found existing AccountMember for : "
               + assocMember.getAssocName());

         }

      }

   }

   private void createAccountMemberFromAssocMember(AssocMember member)
   {

      final boolean createwebuser =
         (member.getMKind() == AssocMember.MKind.guest);
      m_registrationUserName = member.getEmail().split("@")[0];
      m_registrationUserName =
         ((m_registrationUserName.length() <= 15)
            ? m_registrationUserName
            : m_registrationUserName.substring(0, 15));
      m_registrationEmail = member.getEmail();
      m_newAssocMember = member;
      log.info("STEP1 " + m_registrationUserName);
      new RunAsOperation()
         {

            public void execute()
            {

               int counter = 2;
               String newName = m_registrationUserName;

               while (identityManager.userExists(newName))
               {

                  newName = m_registrationUserName + counter;
                  counter++;

               }

               m_registrationUserName = newName;
               identityManager.createUser(m_registrationUserName, "webassoc");
               identityManager.grantRole(m_registrationUserName,
                  (createwebuser ? Cts.Roles.webuser : Cts.Roles.member));

            }

         }.addRole("admin").run();

      // Note that the accountCreated method
      m_newAccountMember.setMember(m_newAssocMember);
      m_newAccountMember = entityManager.merge(m_newAccountMember);
      m_newAccountMember.setEmail(m_registrationEmail);
      m_newAccountMember.setEnabled(true);
      m_newAccountMember.setActivationKey(null);
      m_newAccountMember.setLanguage("en");
      m_newAccountMember.setCreatedOn(Calendar.getInstance().getTime());
      m_newAccountMember.setLoginCount(0);
      m_newAccountMember.setLastLogin(null);
      entityManager.persist(m_newAccountMember);

   }

   @Transactional
   @Restrict("#{s:hasRole('membermgr')}")
   public void handleOrderAccount()
   {

      log.info("handleOrderAccount with id: "
         + accountOrderMissingAssocMemberId);

      if (accountOrderMissingAssocMemberId != null)
      {

         Query query1 =
            entityManager.createQuery(
               "SELECT c FROM AccountOrder c where c.id = :accountOrderMissingAssocMemberId");
         query1.setParameter("accountOrderMissingAssocMemberId",
            accountOrderMissingAssocMemberId);

         AccountOrder accountOrder = (AccountOrder)query1.getSingleResult();
         log.info("handleOrderAccount found AccountOrder: "
            + accountOrder.getBuyerName());

         String[] parts = accountOrder.getBuyerName().split(" ");
         m_newAssocMember = initAssocMember();

         if ((accountOrder.getFirstName() == null)
            || (accountOrder.getFirstName().trim().length() == 0))
         {

            // Order created before we added the firstNames
            m_newAssocMember.setAssocName((parts.length > 1) ? parts[1]
                                                             : parts[0]);

         }
         else
         {

            // Order created after we added the firstNames
            m_newAssocMember.setAssocName(accountOrder.getBuyerName());

         }

         m_newAssocMember.setAddress(accountOrder.getAddress());
         m_newAssocMember.setAptOrSuite(accountOrder.getAptOrSuite());
         m_newAssocMember.setCity(accountOrder.getCity());
         m_newAssocMember.setState(accountOrder.getState());
         m_newAssocMember.setZip(accountOrder.getZip());
         m_newAssocMember.setCountry(accountOrder.getCountry());
         m_newAssocMember.setHomePhone(accountOrder.getHomePhone());
         m_newAssocMember.setEmail(accountOrder.getEmail());
         entityManager.persist(m_newAssocMember);

         if ((accountOrder.getFirstName() == null)
            || (accountOrder.getFirstName().trim().length() == 0))
         {

            // Order created before we added the firstNames
            FamilyMember fmember = initFamilyMember(m_newAssocMember);
            fmember.setFirstName((parts.length > 1) ? parts[0]
                                                    : Cts.Dflt.FIRST_NAME);
            entityManager.persist(fmember);

         }

         if ((accountOrder.getFirstName() != null)
            && (accountOrder.getFirstName().trim().length() != 0))
         {

            // Order created after we added the firstNames
            FamilyMember fmember = initFamilyMember(m_newAssocMember);
            fmember.setFirstName(accountOrder.getFirstName().trim());
            entityManager.persist(fmember);

         }

         if ((accountOrder.getSpouseFirstName() != null)
            && (accountOrder.getSpouseFirstName().trim().length() != 0))
         {

            // Order created after we added the firstNames
            FamilyMember fmember = initFamilyMember(m_newAssocMember);
            fmember.setFirstName(accountOrder.getSpouseFirstName().trim());
            fmember.setFKind(FKind.spouse);
            entityManager.persist(fmember);

         }

         Calendar dob = Calendar.getInstance();
         dob.set(2011, Calendar.JANUARY, 1, 1, 1, 1);

         if ((accountOrder.getChildFirstName1() != null)
            && (accountOrder.getChildFirstName1().trim().length() != 0))
         {

            // Order created after we added the firstNames
            FamilyMember fmember = initFamilyMember(m_newAssocMember);
            fmember.setFirstName(accountOrder.getChildFirstName1().trim());
            fmember.setFKind(FKind.kid);
            fmember.setDob(dob.getTime());
            entityManager.persist(fmember);

         }

         if ((accountOrder.getChildFirstName2() != null)
            && (accountOrder.getChildFirstName2().trim().length() != 0))
         {

            // Order created after we added the firstNames
            FamilyMember fmember = initFamilyMember(m_newAssocMember);
            fmember.setFirstName(accountOrder.getChildFirstName2().trim());
            fmember.setFKind(FKind.kid);
            entityManager.persist(fmember);

         }

         if ((accountOrder.getChildFirstName3() != null)
            && (accountOrder.getChildFirstName3().trim().length() != 0))
         {

            // Order created after we added the firstNames
            FamilyMember fmember = initFamilyMember(m_newAssocMember);
            fmember.setFirstName(accountOrder.getChildFirstName3().trim());
            fmember.setFKind(FKind.kid);
            fmember.setDob(dob.getTime());
            entityManager.persist(fmember);

         }

         if ((accountOrder.getChildFirstName4() != null)
            && (accountOrder.getChildFirstName4().trim().length() != 0))
         {

            // Order created after we added the firstNames
            FamilyMember fmember = initFamilyMember(m_newAssocMember);
            fmember.setFirstName(accountOrder.getChildFirstName4().trim());
            fmember.setFKind(FKind.kid);
            fmember.setDob(dob.getTime());
            entityManager.persist(fmember);

         }

         if ((accountOrder.getChildFirstName5() != null)
            && (accountOrder.getChildFirstName5().trim().length() != 0))
         {

            // Order created after we added the firstNames
            FamilyMember fmember = initFamilyMember(m_newAssocMember);
            fmember.setFirstName(accountOrder.getChildFirstName5().trim());
            fmember.setFKind(FKind.kid);
            fmember.setDob(dob.getTime());
            entityManager.persist(fmember);

         }

      }

   }

   /**
    */
   @SuppressWarnings("unchecked")
   @Transactional
   public void checkAssocMembersWebAccess()
   {

      AssocMember.MKind[] todos =
         new AssocMember.MKind[]
         {
            AssocMember.MKind.pastmember, AssocMember.MKind.guest,
            AssocMember.MKind.sponsor, AssocMember.MKind.advertiser,
            AssocMember.MKind.partnerorg
         };
      Query query1 =
         entityManager.createQuery("SELECT c FROM AssocMember c "
            + "WHERE (c.MKind = :theKind1) " + "ORDER BY c.assocName");
      Query query2 =
         entityManager.createQuery(
            "SELECT c FROM AccountMember c where c.member = :assocMember");

      for (int i = 0; i < todos.length; i++)
      {

         query1.setParameter("theKind1", todos[i]);

         List<AssocMember> assocMembers = query1.getResultList();

         for (AssocMember member : assocMembers)
         {

            query2.setParameter("assocMember", member);

            List<AccountMember> accountMembers = query2.getResultList();

            if ((todos[i].equals(AssocMember.MKind.sponsor))
               && (accountMembers.size() != 0))
            {

               log.info("SPONSOR: [" + member.getAssocName()
                  + "] should not have a web access");

            }
            else if ((todos[i].equals(AssocMember.MKind.advertiser))
               && (accountMembers.size() != 0))
            {

               log.info("ADVERTISER: [" + member.getAssocName()
                  + "] should not have a web access");

            }
            else if ((todos[i].equals(AssocMember.MKind.partnerorg))
               && (accountMembers.size() != 0))
            {

               log.info("PARTNERORG: [" + member.getAssocName()
                  + "] should not have a web access");

            }
            else if (todos[i].equals(AssocMember.MKind.pastmember))
            {

               if (accountMembers.size() > 1)
               {

                  log.info("PASTMEMBER: [" + member.getAssocName()
                     + "] has MORE than one web access");

               }

               for (AccountMember accountMember : accountMembers)
               {

                  Set<AccountMemberRole> roles = accountMember.getRoles();

                  for (AccountMemberRole role : roles)
                  {

                     if (!role.getName().equals(Cts.Roles.webuser))
                     {

                        log.info("PASTMEMBER: [" + member.getAssocName()
                           + "] has a MORE than a simple web access");

                     }

                  }

               }

            }
            else if (todos[i].equals(AssocMember.MKind.guest))
            {

               if (accountMembers.size() > 1)
               {

                  log.info("GUEST: [" + member.getAssocName()
                     + "] has MORE than one web access");

               }

               for (AccountMember accountMember : accountMembers)
               {

                  Set<AccountMemberRole> roles = accountMember.getRoles();

                  for (AccountMemberRole role : roles)
                  {

                     if (!role.getName().equals(Cts.Roles.webuser))
                     {

                        log.info("GUEST: [" + member.getAssocName()
                           + "] has a MORE than a simple web access");

                     }

                  }

               }

            }

         }

      }

   }

   @SuppressWarnings("unchecked")
   @Transactional
   public void reinitAllPasswords()
   {

      Query alreadyInMailingList =
         entityManager.createQuery(
            "select c from AccountMember c where c.loginCount = :theValue");
      alreadyInMailingList.setParameter("theValue", 0);

      List<AccountMember> neverLoggedIn = alreadyInMailingList.getResultList();

      for (AccountMember foundUser : neverLoggedIn)
      {

         boolean dosomething = true;
         log.info(foundUser.getUserName());

         if (dosomething)
         {

            // That password will be sent through the email
            m_resetPassword = PasswordHashing.generateTempPassword();
            m_recoveredPasswordEmail = foundUser.getEmail();
            m_lostPasswordUserName = foundUser.getUserName();

            final String newPassword = m_resetPassword;
            final String userName = foundUser.getUserName();
            final String userEmail = foundUser.getEmail();
            new RunAsOperation()
               {

                  public void execute()
                  {

                     identityManager.changePassword(userName, newPassword);

                  }

               }.addRole("admin").run();

            // @Observer("passwordReset")
            // RegistrationMailer.sendPasswordResetEmail
            // ==========================================
            // Events.instance().raiseTransactionSuccessEvent("passwordReset");
            mailProcessor.sendAccountInit(userName, userEmail, newPassword);

            // JEB: Don't really know how transaction are working there.
            // There identityManagerr.changePassword has changed the same entity
            foundUser.setTemporaryPassword(true);
            entityManager.flush();

         }

      }

   }

   @SuppressWarnings("unchecked")
   @Transactional
   public void reinitAllUsernames()
   {

      Query alreadyInMailingList =
         entityManager.createQuery("select c from AccountMember c");
      List<AccountMember> neverLoggedIn = alreadyInMailingList.getResultList();

      for (AccountMember foundUser : neverLoggedIn)
      {

         String tmpName = foundUser.getEmail().split("@")[0];
         tmpName =
            ((tmpName.length() <= 15) ? tmpName : tmpName.substring(0, 15));
         foundUser.setUserName(tmpName);
         entityManager.flush();

      }

   }

   //=================================================================
   // EJB
   //=================================================================
   @Destroy @Remove
   public void destroy()
   {

   }

}