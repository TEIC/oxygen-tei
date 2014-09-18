/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2012 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.ant;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.exml.workspace.api.node.customizer.BasicRenderingInformation;
import ro.sync.exml.workspace.api.node.customizer.NodeRendererCustomizerContext;
import ro.sync.exml.workspace.api.node.customizer.XMLNodeRendererCustomizer;

/**
 * Class used to customize the way a Ant node is rendered in the UI. 
 * A node represents an entry from Author outline, Author bread crumb,
 * Text page outline, content completion proposals window or Elements view.
 * 
 * @author alina_iordache
 * @author alin_balasa
 */
@API(type = APIType.INTERNAL, src = SourceType.PUBLIC)
public class AntNodeRendererCustomizer extends XMLNodeRendererCustomizer {
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(AntNodeRendererCustomizer.class.getName());
  /**
   * Mapping between name and icon path
   */
  private static final Map<String, String> nameToIconPath = new HashMap<String, String>();
  /**
   * Set with predefined tasks.
   */
  private static final Set<String> tasksSet = new HashSet<String>();
  /**
   * Set with predefined types.
   */
  private static final Set<String> typesSet = new HashSet<String>();
  
  /**
   * Path of the image used for tasks.
   */
  private static final String tasksImagePath =  getImageURL("/images/node-customizer/AntTask16.png");
  /**
   * Path of the image used for tasks.
   */
  private static final String typesImagePath =  getImageURL("/images/node-customizer/AntType16.png");
  
  // Initialize mapping
  static {
    //Default tasks
    String[] tasks = new String[] {
      "ant", "antcall", "antstructure", "antversion", "apply", "apt", "attributenamespacedef", "augment",
      "available", "basename", "bindtargets", "buildnumber", "bunzip2", "bzip2", "checksum", "chmod", "classloader",
      "commandlauncher", "componentdef", "concat", "condition", "copy", "cvs", "cvschangelog", "cvspass", "cvstagdiff",
      "cvsversion", "defaultexcludes", "delete", "dependset", "diagnostics", "dirname", "ear", "echo", "echoproperties",
      "echoxml", "exec", "fail", "filter", "fixcrlf", "genkey", "get", "gunzip", "gzip", "hostinfo", "import", "include",
      "input", "jar", "java", "javac", "javadoc", "length", "loadfile", "loadproperties", "loadresource", "local",
      "macrodef", "mail", "makeurl", "manifest", "manifestclasspath", "mkdir", "move", "nice", "parallel", "patch",
      "pathconvert", "presetdef", "projecthelper", "property", "propertyhelper", "record", "replace", "resourcecount",
      "retry", "rmic", "sequential", "signjar", "sleep", "sql", "subant", "sync", "tar", "taskdef", "tempfile", "touch",
      "tstamp", "truncate", "typedef", "unjar", "untar", "unwar", "unzip", "uptodate", "waitfor", "war", "whichresource",
      "xmlproperty", "xslt", "zip", "antlr", "attrib", "blgenclient", "cab", "cccheckin", "cccheckout", "cclock", "ccmcheckin",
      "ccmcheckintask", "ccmcheckout", "ccmcreatetask", "ccmkattr", "ccmkbl", "ccmkdir", "ccmkelem", "ccmklabel", "ccmklbtype",
      "ccmreconfigure", "ccrmtype", "ccuncheckout", "ccunlock", "ccupdate", "chgrp", "chown", "depend", "ejbjar", "ftp",
      "image", "iplanet-ejbc", "jarlib-available", "jarlib-display", "jarlib-manifest", "jarlib-resolve", "javacc", "javah",
      "jdepend", "jjdoc", "jjtree", "junit", "junitreport", "native2ascii", "netrexxc", "propertyfile", "pvcs", "replaceregexp",
      "rexec", "rpm", "schemavalidate", "scp", "script", "scriptdef", "serverdeploy", "setproxy", "soscheckin", "soscheckout",
      "sosget", "soslabel", "sound", "splash", "sshexec", "sshsession", "symlink", "telnet", "translate", "verifyjar", "vssadd",
      "vsscheckin", "vsscheckout", "vsscp", "vsscreate", "vssget", "vsshistory", "vsslabel", "wljspc", "xmlvalidate", "copydir",
      "copyfile", "copypath", "deltree", "execon", "javadoc2", "jlink", "jspc", "mimemail", "rename", "renameext", "style"
    };
    // Default types
    String[] types = new String[] {
      "description", "filterchain", "filterreader", "filterset", "mapper", "redirector", "patternset", "regexp", "substitution",
      "xmlcatalog", "extensionSet", "extension", "selector", "signedselector", "scriptfilter", "assertions", "concatfilter",
      "mavenrepository", "scriptselector", "scriptmapper", "identitymapper", "flattenmapper", "globmapper", "mergemapper",
      "regexpmapper", "packagemapper", "unpackagemapper", "compositemapper", "chainedmapper", "filtermapper", "firstmatchmapper",
      "cutdirsmapper", "isfileselected", "scriptcondition", "dirset", "filelist", "fileset", "path", "propertyset", "zipfileset",
      "classfileset", "libfileset", "files", "restrict", "union", "difference", "intersect", "sort", "resources", "first", "last",
      "tarfileset", "tokens", "mappedresources", "archives", "resourcelist", "resource", "file", "url", "string", "zipentry",
      "propertyresource", "tarentry", "gzipresource", "bzip2resource", "javaresource", "linetokenizer", "stringtokenizer", "filetokenizer"
    };
    // Add default tasks
    tasksSet.addAll(Arrays.asList(tasks));
    // Add default types
    typesSet.addAll(Arrays.asList(types));
    
    // typedef
    nameToIconPath.put("typedef", getImageURL("/images/node-customizer/AntDefinition16.png"));
    
    // taskdef
    nameToIconPath.put("taskdef", getImageURL("/images/node-customizer/AntDefinition16.png"));
    
    // extension-point
    nameToIconPath.put("extension-point", getImageURL("/images/node-customizer/AntExtensionPoint16.png"));
    
    // project
    nameToIconPath.put("project", getImageURL("/images/node-customizer/AntProject16.png"));
    
    // property
    nameToIconPath.put("property", getImageURL("/images/node-customizer/AntProperty16.png"));
    
    // target
    nameToIconPath.put("target", getImageURL("/images/node-customizer/AntTarget16.png"));
    
    // condition
    nameToIconPath.put("condition", getImageURL("/images/node-customizer/AntProperty16.png"));
    
    // available
    nameToIconPath.put("available", getImageURL("/images/node-customizer/AntProperty16.png"));
    
    // uptodate
    nameToIconPath.put("uptodate", getImageURL("/images/node-customizer/AntProperty16.png"));

    // import
    nameToIconPath.put("import", getImageURL("/images/node-customizer/Import16.png"));

    // include
    nameToIconPath.put("include", getImageURL("/images/node-customizer/Include16.png"));
  }

  /**
   * Searches the class-path for the image with the given path 
   * and if found it returns the string representation of its URL, otherwise it returns <code>null</code>.  
   * 
   * @param path The image path to search for.
   * 
   * @return The string representation of the image URL or <code>null</code> if the image is not found.
   */
  private static String getImageURL(String path) {
    URL imageURL = AntNodeRendererCustomizer.class.getResource(path);
    if (imageURL != null) {
      return imageURL.toExternalForm();
    } else {
      logger.error(AntNodeRendererCustomizer.class.getName() + " - Image not found: " + path);
      return null;
    }
  }
  
  /**
   * @see ro.sync.exml.workspace.api.node.customizer.XMLNodeRendererCustomizer#getRenderingInformation(ro.sync.exml.workspace.api.node.customizer.NodeRendererCustomizerContext)
   */
  @Override
  public BasicRenderingInformation getRenderingInformation(NodeRendererCustomizerContext context) {
    BasicRenderingInformation renderingInfo = new BasicRenderingInformation();
    String nodeName = context.getNodeName();
    if (nodeName != null) {
      String iconPath = null;
      String imageKey = nodeName;
      int index = nodeName.indexOf(":");
      if (index != -1) {
        imageKey = nodeName.substring(index + 1);
      }
      // Get icon path from map.
      iconPath = nameToIconPath.get(imageKey);
      
      if (iconPath == null) {
        if (tasksSet.contains(imageKey)) {
          // Is a task, use the task image.
          iconPath = tasksImagePath;
        } else if (typesSet.contains(imageKey)) {
          // Is a type, use the type image.
          iconPath = typesImagePath;
        }
      }
      
      renderingInfo.setIconPath(iconPath);
    }
    return renderingInfo;
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Ant Node Renderer Customizer";
  }
}