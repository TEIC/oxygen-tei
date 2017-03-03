/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2016 Syncro Soft SRL, Romania.  All rights
 *  reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistribution of source or in binary form is allowed only with
 *  the prior written permission of Syncro Soft SRL.
 *
 *  2. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  3. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  4. The end-user documentation included with the redistribution,
 *  if any, must include the following acknowledgment:
 *  "This product includes software developed by the
 *  Syncro Soft SRL (http://www.sync.ro/)."
 *  Alternately, this acknowledgment may appear in the software itself,
 *  if and wherever such third-party acknowledgments normally appear.
 *
 *  5. The names "Oxygen" and "Syncro Soft SRL" must
 *  not be used to endorse or promote products derived from this
 *  software without prior written permission. For written
 *  permission, please contact support@oxygenxml.com.
 *
 *  6. Products derived from this software may not be called "Oxygen",
 *  nor may "Oxygen" appear in their name, without prior written
 *  permission of the Syncro Soft SRL.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL THE SYNCRO SOFT SRL OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 */
package ro.sync.ecss.extensions.commons;

import java.net.URL;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.dita.MediaInfo;
import ro.sync.util.URLUtil;

/**
 * Utility methods for media objects.
 * 
 * @author adrian_sorop
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class MediaObjectsUtil {

  /**
   * Constructor. Private to avoid using.
   */
  private MediaObjectsUtil(){
  }
  
  /**
   * Audio files extenstions.
   */
  public static final String[] MEDIA_AUDIO_EXTENSIONS = new String[] {
      "mp3", "wav", "pcm", "m4a", "aif", "aiff"
  };
  /**
   * Video files Extensions.
   */
  public static final String[] MEDIA_VIDEO_EXTENSIONS = new String[] {
      "mp4", "flv", "m4v", "avi", "wmv"
  };
  
  /**
   * All the allowed extensions for an media file.
   */
  public static final String[] ALLOWED_MEDIA_EXTENSIONS = new String[] {
      "mp3", "wav", "pcm", "m4a", "aif", "aiff", "mp4", "flv", "m4v", "avi", "wmv"};
  
  /**
   * Determine if an extension is found in an extension array.
   * @param extension Searched extension.
   * @param allowedExtensions Array with the allowed extensions.
   * @return  <code>true</code> if the extension is contained.
   */
  public static boolean containsExtension(String extension, String[] allowedExtensions) {
    boolean isContained = false;
    for (int i = 0; i < allowedExtensions.length; i++) {
      if (extension.equalsIgnoreCase(allowedExtensions[i])) {
        isContained = true;
        break;
      }
    }
    return isContained;
  }
  
  
  /**
   * Detects the output class of an media object by comparing object's href with the 
   * recognized extensions list. If an extension is not found, the selected type is iFrame.
   * @param href The file href.
   * @return Detected output class.
   */
  public static String detectOutputclass(String  href) {
    String extension = href != null ? URLUtil.getExtension(href) : "";
    String outputclass = MediaInfo.IFRAME_MEDIA_TYPE;
    // Audio files
    if (containsExtension(extension, MediaObjectsUtil.MEDIA_AUDIO_EXTENSIONS)) {
      outputclass = MediaInfo.AUDIO_MEDIA_TYPE;
    }
    // Video Files
    if (containsExtension(extension, MediaObjectsUtil.MEDIA_VIDEO_EXTENSIONS)) {
      outputclass = MediaInfo.VIDEO_MEDIA_TYPE;
    } 
    return outputclass;
  }
  
  /**
   * Checks if the URL is a media reference. 
   * 
   * @param url Resource's URL.
   * @return <code>true</code> if the resource is a media. YouTube links are
   * associated with media files.
   */
  public static boolean isMediaReference(URL url) {
    String u = url.toExternalForm();
    String extension = URLUtil.getExtension(u);
    return containsExtension(extension, ALLOWED_MEDIA_EXTENSIONS) ||
        url.getHost().toLowerCase().contains("youtube.com");
  }
  
}
