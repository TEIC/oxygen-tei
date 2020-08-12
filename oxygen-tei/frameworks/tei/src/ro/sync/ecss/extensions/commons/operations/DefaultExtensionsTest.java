/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2011 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.commons.operations;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.junit.JFCTestCase;

/**
 * Tests that if a new operation is created in the commons package its also 
 * added in {@link DefaultExtensions}
 * @author alex_jitianu
 */
public class DefaultExtensionsTest extends JFCTestCase {

  /**
   * <p><b>Description:</b> Tests that if a new operation is created in the commons package its
   *                        also added in {@link DefaultExtensions}</p>
   * <p><b>Bug ID:</b> EXM-20861</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testDefaultExtensions() throws Exception {
    String commonsPackage = "ro.sync.ecss.extensions.commons.operations";
    List<Class> unregisteredClasses = getUndeclaredOperations(
        commonsPackage,
        AuthorOperation.class,
        DefaultExtensions.DEFAULT_OPERATIONS);
    
    // All operations from commons should be declared in DEFAULT_OPERATIONS
    assertTrue("Operations not presented in DefaultExtensions.DEFAULT_OPERATIONS" +
        unregisteredClasses.toString(), unregisteredClasses.isEmpty());
  }

  /**
   * Check if all classes form a package, that implement a given interface, are 
   * present in the given defaults list.
   * 
   * @param packageToSearch Package to search. Not recursive.
   * @param declaredDefaults List where the all classes should be present.
   * 
   * @return Exceptions. Classes that implement the interface and are not found 
   * in the list.
   * 
   * @throws ClassNotFoundException Class not found.
   */
  private List<Class> getUndeclaredOperations(
      String packageToSearch, 
      Class<?> toImplement,
      Class[] declaredDefaults)
      throws ClassNotFoundException {
    // Collect all classes from the commons package.
    File commonsOperations = new File("classes/" + packageToSearch.replace('.', '/'));
    File[] operations = commonsOperations.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(".class");
      }
    });

    // Load AuthorOperations. 
    List<Class> operationsClasses = new ArrayList<Class>();
    for (int i = 0; i < operations.length; i++) {
      String name = operations[i].getName();
      String className = name.substring(0, name.lastIndexOf('.'));
      Class<?> loadClass = getClass().getClassLoader().loadClass(
          packageToSearch + "." + className);
      
      if (toImplement.isAssignableFrom(loadClass) && !Modifier.isAbstract(loadClass.getModifiers())) {
        operationsClasses.add(loadClass);
      }
    }
    
    // Remove all operations declared in DEFAULT_OPERATIONS.
    List<Class> defaultOperations = Arrays.asList(declaredDefaults);
    for (Iterator iterator = operationsClasses.iterator(); iterator.hasNext();) {
      Class class1 = (Class) iterator.next();
      if (defaultOperations.contains(class1)) {
        iterator.remove();
      }
    }
    return operationsClasses;
  }
}