package org.jboss.seam.example.webassoc.util;

public enum USStateCode
{

   ___(0), // Not Specified
   AK(1), //       Alaska  Juneau  -
   AL(2), //       Alabama Montgomery      Ala.
   AR(3), //       Arkansas        Little Rock     Ark.
   AS(4), //       American Samoa  Pago Pago       -
   AZ(5), //       Arizona Phoenix Ariz.
   CA(6), //       California      Sacramento      Calif.
   CO(7), //       Colorado        Denver  Colo.
   CT(8), //       Connecticut     Hartford        Conn.
   DC(9), //       District of Columbia    Washington
   DE(10), //      Delaware        Dover   Del.
   FL(11), //      Florida Tallahassee     Fla.
   FM(12), //      Federated States of Micronesia  Palikir
   GA(13), //      Georgia Atlanta Ga.
   GU(14), //      Guam    Hagatna (Agana)
   HI(15), //      Hawaii  Honolulu        -
   IA(16), //      Iowa    Des Moines      -
   ID(17), //      Idaho   Boise   -
   IL(18), //      Illinois        Springfield     Ill.
   IN(19), //      Indiana Indianapolis    Ind.
   KS(20), //      Kansas  Topeka  Kan.
   KY(21), //      Kentucky        Frankfort       Ky.
   LA(22), //      Louisiana       Baton Rouge     La.
   MA(23), //      Massachusetts   Boston  Mass.
   ME(24), //      Maine   Augusta -
   MD(25), //      Maryland        Annapolis       Md.
   MH(26), //      Marshall Islands        Majuro
   MI(27), //      Michigan        Lansing Mich.
   MN(28), //      Minnesota       St. Paul        Minn.
   MO(29), //      Missouri        Jefferson City  Mo.
   MP(30), //      Northern Mariana Islands        Saipan
   MS(31), //      Mississippi     Jackson Miss.
   MT(32), //      Montana Helena  Mont.
   NC(33), //      North Carolina  Raleigh N.C.
   ND(34), //      North Dakota    Bismarck        N.D.
   NE(35), //      Nebraska        Lincoln Neb.
   NH(36), //      New Hampshire   Concord N.H.
   NJ(37), //      New Jersey      Trenton N.J.
   NM(38), //      New Mexico      Santa Fe        N.M.
   NV(39), //      Nevada  Carson City     Nev.
   NY(40), //      New York        Albany  N.Y
   OH(41), //      Ohio    Columbus        -
   OK(42), //      Oklahoma        Oklahoma City   Okla
   OR(43), //      Oregon  Salem   Ore.
   PA(44), //      Pennsylvania    Harrisburg      Pa.
   PR(45), //      Puerto Rico     San Juan
   PW(46), //      Palau   Koror
   RI(47), //      Rhode Island    Providence      R.I.
   SC(48), //      South Carolina  Columbia        S.C.
   SD(49), //      South Dakota    Pierre  S.D.
   TN(50), //      Tennessee       Nashville       Tenn.
   TX(51), //      Texas   Austin  -
   UT(52), //      Utah    Salt Lake City  -
   VI(53), //      Virgin Islands  St. Thomas
   VT(54), //      Vermont Montpelier      Vt.
   VA(55), //      Virginia        Richmond        Va.
   WA(56), //      Washington      Olympia Wash.
   WI(57), //      Wisconsin       Madison Wis.
   WV(58), //      West Virginia   Charleston      W.Va.
   WY(59); //      Wyoming Cheyenne        Wyo.

   private int id;

   private USStateCode(int id)
   {

      this.id = id;

   }

   public int getId()
   {

      return id;

   }

}