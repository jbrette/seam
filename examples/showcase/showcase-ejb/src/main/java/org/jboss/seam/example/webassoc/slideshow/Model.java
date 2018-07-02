/**
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
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
package org.jboss.seam.example.webassoc.slideshow;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.example.webassoc.mship.AssocMember;

import java.io.Serializable;

import java.util.List;

/**
 * This class represent 'M' in MVC pattern. It is storage to application flow related data such as selectedAlbum, image, mainArea to preview etc..
 *
 * @author Andrey Markhel
 */
@Name("model")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class Model
        implements Serializable
{

   private static final long serialVersionUID = -1767281809514660171L;
   private Image selectedImage;
   private Album selectedAlbum;
   private AssocMember selectedUser;
   private NavigationEnum mainArea;
   private List<Image> images;

   /**
    * This method invoked after the almost user actions, to prepare properly data to show in the UI.
    * @param mainArea - next Area to show(determined in controller)
    * @param selectedUser - user, that was selected(determined in controller)
    * @param selectedShelf - shelf, that was selected(determined in controller)
    * @param selectedAlbum - album, that was selected(determined in controller)
    * @param selectedImage - image, that was selected(determined in controller)
    * @param images - list of images, to show during slideshow process(determined in controller)
    */
   public void resetModel(NavigationEnum mainArea, AssocMember selectedUser,
      Album selectedAlbum, Image selectedImage, List<Image> images)
   {

      this.setSelectedAlbum(selectedAlbum);
      this.setSelectedImage(selectedImage);
      this.setSelectedUser(selectedUser);
      this.setMainArea(mainArea);
      this.images = images;

   }

   /**
    * This method observes <code> Constants.UPDATE_MAIN_AREA_EVENT </code>event and invoked after the user actions, that not change model, but change area to preview
    * @param mainArea - next Area to show
    *
    */
   @Observer(Constants.UPDATE_MAIN_AREA_EVENT)
   public void setMainArea(NavigationEnum mainArea)
   {

      if ((this.mainArea != null)
         && this.mainArea.equals(NavigationEnum.FILE_UPLOAD))
      {

         Events.instance().raiseEvent(Constants.CLEAR_FILE_UPLOAD_EVENT);

      }

      this.mainArea = mainArea;

   }

   public NavigationEnum getMainArea()
   {

      return mainArea;

   }

   public Image getSelectedImage()
   {

      return selectedImage;

   }

   private void setSelectedImage(Image selectedImage)
   {

      this.selectedImage = selectedImage;

   }

   public Album getSelectedAlbum()
   {

      return selectedAlbum;

   }

   public void setSelectedAlbum(Album selectedAlbum)
   {

      this.selectedAlbum = selectedAlbum;

   }

   public AssocMember getSelectedUser()
   {

      return selectedUser;

   }

   private void setSelectedUser(AssocMember selectedUser)
   {

      this.selectedUser = selectedUser;

   }

   public List<Image> getImages()
   {

      return images;

   }

   public void setImages(List<Image> images)
   {

      this.images = images;

   }

}