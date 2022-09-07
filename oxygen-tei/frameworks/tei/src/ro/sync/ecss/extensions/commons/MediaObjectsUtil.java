/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2022 Syncro Soft SRL, Romania.  All rights
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
import ro.sync.basic.util.ArraysUtil;
import ro.sync.basic.util.URLUtil;
import ro.sync.ecss.dita.MediaInfo;

/**
 * Utility methods for media objects.
 * 
 * @author adrian_sorop
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public final class MediaObjectsUtil {

  /**
   * Host of vimeo embedded videos.
   */
  private static final String VIMEO_EMBEDDED_HOST = "player.vimeo.com";

  /**
   * The generic Vimeo host.
   */
  private static final String VIMEO_HOST = "vimeo.com";

  /**
   * YouTube host.
   */
  private static final String YOUTUBE_HOST = "www.youtube.com";

  /**
   * Attribute "data".
   */
  public static final String REFERENCE_ATTR_DATA = "data";
  
  /**
   * Attribute "data key reference".
   */
  public static final String REFERENCE_ATTR_DATAKEYREF = "datakeyref";

  /**
   * Constructor.
   *
   * @throws UnsupportedOperationException when invoked.
   */
  private MediaObjectsUtil() {
    // Private to avoid instantiations
    throw new UnsupportedOperationException("Instantiation of this utility class is not allowed!");
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
  public static final String[] ALLOWED_MEDIA_EXTENSIONS = (String[]) ArraysUtil.mergeArrays(
      MEDIA_AUDIO_EXTENSIONS, MEDIA_VIDEO_EXTENSIONS, ArraysUtil.getArrayLength(MEDIA_AUDIO_EXTENSIONS));
  
  /**
   * All accepted media hosts.
   */
  public static final String[] RECOGNIZED_MEDIA_HOSTS = new String[] {
     YOUTUBE_HOST, VIMEO_HOST, VIMEO_EMBEDDED_HOST
  };
  
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
    String outputclass = MediaInfo.IFRAME_MEDIA_TYPE;
    // Audio files
    if (isAudioReference(href)) {
      outputclass = MediaInfo.AUDIO_MEDIA_TYPE;
    } else if (isVideoReference(href)) {
      // Video Files
      outputclass = MediaInfo.VIDEO_MEDIA_TYPE;
    } 
    return outputclass;
  }
  
  /**
   * Checks if the URL is a media reference and an media object (not xref) should be inserted.
   * 
   * @param url Resource's URL.
   * @return <code>true</code> if the resource is a media. YouTube and Vimeo links are
   * associated with media files.
   */
  public static boolean isMediaReference(URL url) {
    boolean isMediaRef = false;
    if (url != null) {
      String u = url.toExternalForm();
      String extension = URLUtil.getExtension(u);
      String host = url.getHost().toLowerCase();
      isMediaRef = containsExtension(extension, ALLOWED_MEDIA_EXTENSIONS) ||
          isRecognizedAsMedia(host);
    }
    return isMediaRef;
  }
  
  /**
   * Determines if the referred resource is YouTube or Vimeo embedded content.
   * 
   * @param url the referred resource.
   * 
   * @return <code>true</code> if the referred resource is YouTube or Vimeo embedded video.
   */
  public static boolean isEmbeddedContent(String url){
    boolean toReturn = false;
    if (url != null) {
      String lowerCase = url.toLowerCase();
      boolean youTubeEmbedded = lowerCase.contains(".youtube.") && lowerCase.contains("embed");
      boolean vimeoEmbedded = lowerCase.contains(VIMEO_EMBEDDED_HOST);
      // YouTube and Vimeo are recognized as embedded content.
      toReturn = youTubeEmbedded || vimeoEmbedded;
    }
    return toReturn;
  }
  
  /**
   * Determine if the host of an inserted video should be treated as media object.
   * 
   * @param hostURL Video host (like YouTube or Vimeo)   
   * @return  <code>true</code> if the host is contained.
   */
  public static boolean isRecognizedAsMedia(String hostURL) {
    boolean accepted = false;
    for (int i = 0; i < RECOGNIZED_MEDIA_HOSTS.length; i++) {
      if (RECOGNIZED_MEDIA_HOSTS[i].equals(hostURL)) {
        accepted = true;
        break;
      }
    }
    return accepted;
  }
  
  /**
   * Corrects YouTube and Vimeo video references transforming the link into an embedded links.
   * <pre>
   * YouTube: From https://www.youtube.com/watch?v=video_id To https://www.youtube.com/embed/video_id
   * Vimeo  : From https://vimeo.com/video_id To https://player.vimeo.com/video/video_id
   * </pre>
   * 
   * @param url The inserted media reference.
   * @return    The corrected media reference.
   */
  public static String correctMediaEmbeddedReference(String url) {
    String corrected = url;
    URL convertToURL = null;
    try {
      convertToURL = URLUtil.convertToURL(url);
    } catch (SecurityException e) { //NOSONAR java:S1166 See the comment from below 
      // URLUtil.convertToURL tries to read the file system when given a Malformed URL -> security manager exception on WA. 
      // We don't care if the URL is malformed so ignore this exception.
    }
    String host = convertToURL != null ? convertToURL.getHost() : null;
    if (host != null && isRecognizedAsMedia(host)) {
      if (YOUTUBE_HOST.equals(host) && corrected.contains("/watch?v=")) {
        corrected = corrected.replace("/watch?v=", "/embed/");
      } else if(VIMEO_HOST.equals(host) && corrected.contains("/vimeo.com/")) {
        corrected = corrected.replace("/vimeo.com/", "/player.vimeo.com/video/");
      }
    }
    
    return corrected;
  }
  
  /**
   * Checks if the extension of file name is a reference to supported audio types.
   * 
   * @param fileName The name of the file to check. 
   * @return <code>true</code> if current file name points to an audio file.
   */
  public static boolean isAudioReference(String fileName) {
    String extension = URLUtil.getExtension(fileName);
    return hasAudioFormat(extension);
  }

  /**
   * Checks if the format is a supported audio format.
   * @param format resource format.
   * @return <code>true</code> if the format is audio.
   */
  public static boolean hasAudioFormat(String format) {
    return format != null && MediaObjectsUtil.containsExtension(format, MediaObjectsUtil.MEDIA_AUDIO_EXTENSIONS);
  }
  
  /**
   * Checks if the extension of file name is a reference to supported video types.
   * 
   * @param fileName The name of the file to check. 
   * @return <code>true</code> if current file name points to a video file.
   */
  public static boolean isVideoReference(String fileName) {
    String extension = URLUtil.getExtension(fileName);
    return hasVideoFormat(extension);
  }

  /**
   * Checks if the format is a supported video format.
   * @param format resource format.
   * @return <code>true</code> if the format is video.
   */
  public static boolean hasVideoFormat(String format) {
    return format != null && MediaObjectsUtil.containsExtension(format, MediaObjectsUtil.MEDIA_VIDEO_EXTENSIONS);
  }
  
}
