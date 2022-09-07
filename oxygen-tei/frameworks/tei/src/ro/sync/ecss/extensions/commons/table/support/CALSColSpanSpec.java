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
package ro.sync.ecss.extensions.commons.table.support;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;

/**
 * Contains information about column span for the CALS table model 
 * (e.g. DocBook or DITA tables). 
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class CALSColSpanSpec {
  
  /**
   * The name of the span specification.
   */
  private String spanName;
  
  /**
   * The name of the start column.
   */
  private String namest;
  
  /**
   * The name of the end column.
   */
  private String nameend;
  
  /**
   * Constructor.
   * 
   * @param spanName The name of the span element. 
   * @param namest The start column name.
   * @param nameend The end column name.
   */
  public CALSColSpanSpec(String spanName, String namest, String nameend) {
    this.spanName = spanName;
    this.namest = namest;
    this.nameend = nameend;
  }

  /**
   * @return The name of the span specification. Can be <code>null</code>.
   */
  public String getSpanName() {
    return spanName;
  }

  /**
   * Return the name of the start column.
   * 
   * @return The name of the start column. It can be <code>null</code> if the
   * value for the start column name given on the constructor was <code>null</code>.
   */
  public String getStartColumnName() {
    return namest;
  }

  /**
   * Return the name of the end column.
   * 
   * @return The name of the end column. It can be <code>null</code> if the
   * value for the end column name given on the constructor was <code>null</code>.
   */
  public String getEndColumnName() {
    return nameend;
  }
}