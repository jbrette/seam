package org.jboss.seam.example.webassoc.admin;

import javax.ejb.Local;

@Local
public interface AdminOperations
{

   // Business methods
   public interface FormFieldNew
   {

      public int NouveauxMembre = 1;
      public int Nom = 2;
      public int Prenom = 3;
      public int PrenomConjoint = 4;
      public int Nationalite = 5;
      public int PrenonEnfant1 = 6;
      public int DateEnfant1 = 7;
      public int BabySittingEnfant1 = 8;
      public int BabySittingMEnfant1 = 9;
      public int PrenonEnfant2 = 10;
      public int DateEnfant2 = 11;
      public int BabySittingEnfant2 = 12;
      public int BabySittingMEnfant2 = 13;
      public int PrenonEnfant3 = 14;
      public int DateEnfant3 = 15;
      public int BabySittingEnfant3 = 16;
      public int BabySittingMEnfant3 = 17;
      public int PrenonEnfant4 = 18;
      public int DateEnfant4 = 19;
      public int BabySittingEnfant4 = 20;
      public int BabySittingMEnfant4 = 21;
      public int PrenonEnfant5 = 22;
      public int DateEnfant5 = 23;
      public int BabySittingEnfant5 = 24;
      public int BabySittingMEnfant5 = 25;
      public int Addresse = 26;
      public int Ville = 27;
      public int CodePostal = 28;
      public int Telephone = 29;
      public int EMail = 30;
      public int DureeInscription = 31;
      public int Clubs = 32;
      public int Suggestions1 = 33;
      public int Suggestions2 = 34;
      public int Suggestions3 = 35;
      public int CafeRecontre = 36;
      public int Potluck = 37;
      public int Activitee1 = 38;
      public int Activitee2 = 39;
      public int Guide = 40;
      public int Bulletin = 41;

   }

   public interface FormFieldOld
   {

      public int NouveauxMembre = 1;
      public int Nom = 2;
      public int Prenom = 3;
      public int PrenomConjoint = 4;
      public int Field5 = 5;
      public int Field6 = 6;
      public int Nationalite = 7;
      public int Addresse = 8;
      public int Ville = 9;
      public int Telephone = 10;
      public int CodePostal = 11;
      public int EMail = 12;
      public int DureeInscription = 13;
      public int Clubs = 14;
      public int Field15 = 15;
      public int Field16 = 16;
      public int Field17 = 17;
      public int Field18 = 18;
      public int Field19 = 19;
      public int CafeRecontre = 20;
      public int Potluck = 21;
      public int Activitee1 = 22;
      public int Activitee2 = 23;
      public int Guide = 24;
      public int Bulletin = 25;

   }

   public void invokedByMenuButton1();

   public void invokedByMenuButton2();

   public void invokedByMenuButton3();

   public void destroy();

}