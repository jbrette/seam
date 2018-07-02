/**
 * License Agreement.
 *
 *  JBoss RichFaces - Ajax4jsf Component Library
 *
 * Copyright (C) 2007  Exadel, Inc.
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
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.example.webassoc.mship.AssocMember;

// import org.richfaces.component.UITree;

import java.io.Serializable;

/**
 * This class represent 'C' in MVC pattern. It is logic that determine what actions invoked and what next page need to be showed.
 * Typically on almost all user actions, this class populates the model and determine new view to show.
 * Also contain utility logic, such as checking is the given shelf belongs to the specified user etc..
 * @author Andrey Markhel
 */
@Name("controller")
@Scope(ScopeType.EVENT)
public class Controller
        implements Serializable
{

   private static final long serialVersionUID = 5656562187249324512L;
   @In @Out
   Model model;
   @In(scope = ScopeType.SESSION)
   AssocMember user;

   /**
    * This method invoked after the user want to see all her albums.
    */
   public void selectAlbums()
   {

      model.resetModel(NavigationEnum.ALL_ALBUMS, user, null, null, null);

   }

   /**
    * This method invoked after the user want to see all her images.
    */
   public void selectImages()
   {

      //      model.resetModel(NavigationEnum.ALL_IMAGES, user, null, null,
      //         user.getImages());
   }

   /**
    * This method invoked after the user want to see specified album independently is it her album or not.
    * @param album - album to show
    */
   public void showAlbum(Album album)
   {

      if (!canViewAlbum(album))
      {

         pushEvent(Constants.ADD_ERROR_EVENT, Constants.HAVENT_ACCESS);

         return;

      }

      FileManager fileManager =
         (FileManager)Contexts.getApplicationContext().get(
            Constants.FILE_MANAGER_COMPONENT);

      //Check, that album was not deleted recently.
      if (!fileManager.isDirectoryPresent(album.getPath()))
      {

         pushEvent(Constants.ADD_ERROR_EVENT,
            Constants.ALBUM_RECENTLY_DELETED_ERROR);
         model.resetModel(NavigationEnum.SHELF_PREVIEW, album.getOwner(), null,
            null, null);

         return;

      }

      model.resetModel(NavigationEnum.ALBUM_PREVIEW, album.getOwner(), album,
         null, album.getImages());

   }

   /**
    * This method invoked in cases, when it is need to clear fileUpload component
    *
    */
   public void resetFileUpload()
   {

      pushEvent(Constants.CLEAR_FILE_UPLOAD_EVENT);

   }

   /**
    * This method invoked after the user want to see specified image independently is it her image or not.
    * @param album - album to show
    */
   public void showImage(Image image)
   {

      //Clear not-saved comment in editor
      pushEvent(Constants.CLEAR_EDITOR_EVENT, "");

      if (!canViewImage(image))
      {

         pushEvent(Constants.ADD_ERROR_EVENT, Constants.HAVENT_ACCESS);

         return;

      }

      //Check, that image was not deleted recently
      final FileManager fileManager =
         (FileManager)Contexts.getApplicationContext().get(
            Constants.FILE_MANAGER_COMPONENT);

      if (!fileManager.isFilePresent(image.getFullPath()))
      {

         pushEvent(Constants.ADD_ERROR_EVENT,
            Constants.IMAGE_RECENTLY_DELETED_ERROR);
         model.resetModel(NavigationEnum.ALBUM_PREVIEW,
            image.getAlbum().getOwner(), image.getAlbum(), null,
            image.getAlbum().getImages());

         return;

      }

      model.resetModel(NavigationEnum.ALBUM_IMAGE_PREVIEW,
         image.getAlbum().getOwner(), image.getAlbum(), image,
         image.getAlbum().getImages());
      image.setVisited(true);

   }

   /**
    * This method invoked after the user want to edit specified image.
    * @param image - image to edit
    */
   @Restrict("#{s:hasRole('admin')}")
   public void startEditImage(Image image)
   {

      if (!canViewImage(image))
      {

         pushEvent(Constants.ADD_ERROR_EVENT, Constants.HAVENT_ACCESS);

         return;

      }

      model.resetModel(NavigationEnum.ALBUM_IMAGE_EDIT, image.getOwner(),
         image.getAlbum(), image, image.getAlbum().getImages());

   }

   /**
    * This method invoked after the user want to interrupt edit image process
    *
    */
   public void cancelEditImage()
   {

      model.resetModel(NavigationEnum.ALBUM_IMAGE_PREVIEW,
         model.getSelectedImage().getAlbum().getOwner(),
         model.getSelectedImage().getAlbum(), model.getSelectedImage(),
         model.getSelectedImage().getAlbum().getImages());

   }

   /**
    * This method invoked after the user want to edit specified album.
    * @param album - album to edit
    */
   @Restrict("#{s:hasRole('admin')}")
   public void startEditAlbum(Album album)
   {

      if (!album.isOwner(user))
      {

         pushEvent(Constants.ADD_ERROR_EVENT, Constants.HAVENT_ACCESS);

         return;

      }

      model.resetModel(NavigationEnum.ALBUM_EDIT, album.getOwner(), album,
         null, album.getImages());

   }

   /**
    * This method invoked after the user want to interrupt edit album process
    *
    */
   public void cancelEditAlbum()
   {

      model.resetModel(NavigationEnum.ALBUM_PREVIEW,
         model.getSelectedAlbum().getOwner(), model.getSelectedAlbum(), null,
         model.getSelectedAlbum().getImages());

   }

   /**
    * This method observes <code>Constants.ALBUM_ADDED_EVENT</code> and invoked after the user add new album
    * @param album - added album
    */
   @Observer(Constants.ALBUM_ADDED_EVENT)
   public void onAlbumAdded(Album album)
   {

      if (album.isShowAfterCreate())
      {

         model.resetModel(NavigationEnum.ALBUM_PREVIEW, album.getOwner(),
            album, null, album.getImages());

      }

   }

   /**
    * This method observes <code>Constants.ALBUM_EDITED_EVENT</code> and invoked after the user edit her album
    * @param album - edited album
    */
   @Observer(Constants.ALBUM_EDITED_EVENT)
   public void onAlbumEdited(Album album)
   {

      model.resetModel(NavigationEnum.ALBUM_PREVIEW, model.getSelectedUser(),
         album, null, album.getImages());

   }

   /**
    * This method observes <code>Constants.ALBUM_DELETED_EVENT</code> and invoked after the user delete her album
    * @param album - deleted album
    * @param path - relative path of the album directory
    */
   @Observer(Constants.ALBUM_DELETED_EVENT)
   public void onAlbumDeleted(Album album, String path)
   {

      model.resetModel(NavigationEnum.ALL_ALBUMS, model.getSelectedUser(),
         null, null, null);

   }

   /**
    * This method observes <code>Constants.IMAGE_DELETED_EVENT</code> and invoked after the user delete her image
    * @param image - deleted image
    * @param path - relative path of the image file
    */
   @Observer(Constants.IMAGE_DELETED_EVENT)
   public void onImageDeleted(Image image, String path)
   {

      model.resetModel(NavigationEnum.ALBUM_PREVIEW, model.getSelectedUser(),
         model.getSelectedAlbum(), null, model.getSelectedAlbum().getImages());

   }

   /**
    * This method observes <code>Constants.AUTHENTICATED_EVENT</code> and invoked after the user successfully authenticate to the system
    * @param u - authenticated user
    */
   @Observer(Constants.AUTHENTICATED_EVENT)
   public void onAuthenticate(AssocMember u)
   {

      model.resetModel(NavigationEnum.ALL_SHELFS, u, null, null, null);

   }

   /**
    * This method invoked after the user want to go to the file-upload page
    *
    */
   public void showFileUpload()
   {

      if (!(user.getAlbums().size() > 0))
      {

         //If user have no shelves, that can start fileupload process
         pushEvent(Constants.ADD_ERROR_EVENT,
            Constants.FILE_UPLOAD_SHOW_ERROR);

         return;

      }

      Album alb = null;

      //If selected album belongs to user
      alb = setDefaultAlbumToUpload(alb);
      model.resetModel(NavigationEnum.FILE_UPLOAD, user, alb, null,
         (alb != null) ? alb.getImages() : null);

   }

   /**
    * This method invoked after the user want to go to the file-upload page and download images to the specified album
    * @param album - selected album
    *
    */
   public void showFileUpload(Album album)
   {

      if (!isUserAlbum(album))
      {

         showError(Constants.YOU_CAN_T_ADD_IMAGES_TO_THAT_ALBUM_ERROR);

         return;

      }

      model.resetModel(NavigationEnum.FILE_UPLOAD, album.getOwner(), album,
         null, album.getImages());

   }

   /**
    * This method invoked after the user want to see profile of the specified user
    * @param user - user to see
    *
    */
   public void showUser(AssocMember user)
   {

      model.resetModel(NavigationEnum.USER_PREFS, user, null, null, null);
      Contexts.getConversationContext().set(Constants.AVATAR_DATA_COMPONENT,
         null);

   }

   /**
    * This method invoked after the user want to see all unvisited images, belongs to the of specified album
    * @param album - album to see
    *
    */
   public void showUnvisitedImages(Album album)
   {

      model.resetModel(NavigationEnum.ALBUM_UNVISITED, album.getOwner(), album,
         null, album.getUnvisitedImages());

   }

   /**
    * This utility method invoked in case if you want to show to the user specified error in popup
    * @param error - error to show
    *
    */
   public void showError(String error)
   {

      pushEvent(Constants.ADD_ERROR_EVENT, error);

   }

   /**
    * This utility method determine if the specified node should be marked as selected.
    * Used in internal rich:tree mechanism
    */
//   public Boolean adviseNodeSelected(UITree tree)
//   {
//
//      Object currentNode = tree.getRowData();
//
//      if (currentNode.equals(model.getSelectedAlbum()))
//      {
//
//         return true;
//
//      }
//
//      return false;
//
//   }

   /**
    * This utility method used by custom datascroller to determine images to show.
    * Used in internal rich:tree mechanism
    */
   public Integer getPage()
   {

      final Integer index =
         model.getSelectedAlbum().getIndex(model.getSelectedImage());

      return (index / 5) + 1;

   }

   /**
    * This utility method used to determine if the specified image belongs to the logged user
    * @param image - image to check
    */
   public boolean isUserImage(Image image)
   {

      if ((image == null) || (image.getOwner() == null))
      {

         return false;

      }

      return image.isOwner(user);

   }

   /**
    * This utility method used to determine if the logged user have any albums.
    *
    */
   public boolean isUserHaveAlbums()
   {

      return user.getAlbums().size() > 0;

   }

   /**
    * This utility method used to determine if the specified album belongs to the logged user
    * @param album - album to check
    */
   public boolean isUserAlbum(Album album)
   {

      return (album != null) && album.isOwner(user);

   }

   /**
    * This utility method used to determine if the specified user can be edited
    * @param user - user to check
    */
   public boolean isProfileEditable(AssocMember selectedUser)
   {

      return (selectedUser != null) && selectedUser.equals(user);

   }

   private boolean canViewAlbum(Album album)
   {

      return (album != null) && album.isOwner(user);

   }

   private boolean canViewImage(Image image)
   {

      return (image != null) && (image.getAlbum() != null)
         && image.isOwner(user);

   }

   private void pushEvent(String type, Object... parameters)
   {

      Events.instance().raiseEvent(type, parameters);

   }

   private Album setDefaultAlbumToUpload(Album alb)
   {

      if (isUserAlbum(model.getSelectedAlbum()))
      {

         alb = model.getSelectedAlbum();

      }

      if (alb == null)
      {

         if ((user != null) && (user.getAlbums().size() > 0))
         {

            alb = user.getAlbums().get(0);

         }

      }

      return alb;

   }

}