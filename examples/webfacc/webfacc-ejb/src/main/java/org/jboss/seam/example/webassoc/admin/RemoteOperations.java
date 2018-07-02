package org.jboss.seam.example.webassoc.admin;

import javax.ejb.Remote;

@Remote
public interface RemoteOperations
{

   public boolean login(String username, String password);

   public void logout();

   public void createAssocMember(String company, String firstName,
      String lastName, String address, String city, String state, String zip,
      String country, String email, String phone, String phoneext, String fax,
      String idstatus, String description, String industry, String title,
      String webSite);

   public void createMnyAccount(String userName, String password,
      String accountName);

   public void makeDeposit(String userName, String password,
      String accountName, Long amount, String memo);

   public void destroy();

}