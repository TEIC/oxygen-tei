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
package ro.sync.ecss.extensions.commons.sort;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.basic.util.NumberFormatException;
import ro.sync.basic.util.NumberParserUtil;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Util class for table sort operations
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public final class SortUtil {
  
  /**
   * Logger for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(SortUtil.class.getName());

  /**
   * Constructor.
   *
   * @throws UnsupportedOperationException when invoked.
   */
  private SortUtil() {
    // Private to avoid instantiations
    throw new UnsupportedOperationException("Instantiation of this utility class is not allowed!");
  }

  /**
   * Detect the value of the XML lang attr for this node.
   * 
   * @param parent The current node.
   * @return The xml lang value or null if none detected in current
   * or parents.
   */
  static String detectXMLLangFrom(AuthorNode parent) {
    String toRet = null;
    if (parent != null) {
      if(parent.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
        AttrValue attrVal = ((AuthorElement) parent).getAttribute("xml:lang"); 
        String langVal = attrVal != null ? attrVal.getValue() : null;
        if(langVal == null) {
          attrVal = ((AuthorElement) parent).getAttribute("lang");
          langVal = attrVal != null ? attrVal.getValue() : null;
        }
        if (langVal != null) {
          toRet = langVal;
        } else {
          toRet = detectXMLLangFrom(parent.getParent());
        }
      } else {
        toRet =  detectXMLLangFrom(parent.getParent());
      }
    }
    return toRet;
  }
  
  /**
   * Parse a string to a  XSD:DATE or XSD:DATETIME date.
   * 
   * @param error The errors array.
   * @param dateString  The string representing a date.
   * 
   * @return A date object or <code>null</code> if the given string 
   * is not a valid XSD:DATE or XSD:DATETIME format.
   */
  static Date parseXsdDatetime(final AuthorOperationException[] error, String dateString) {
    Date val1 = null;
    Calendar calendar = null;
    try {
      calendar = DatatypeConverter.parseDateTime(dateString);
    }  catch (IllegalArgumentException e1) { //NOSONAR java:S1166 It's handled
      try {
        calendar = DatatypeConverter.parseDate(dateString);
      } catch (IllegalArgumentException e2) { //NOSONAR java:S1166 It's handled
        try {
          calendar = DatatypeConverter.parseTime(dateString);
        } catch (IllegalArgumentException e3) {
          // Not a valid XSD:DATE/XSD:DATETIME/XS:TIME date
          error[0] = new AuthorOperationException("Unable to parse the following text value as a date:\n\"" + dateString + "\"", e3);
        }
      }
    }
    
    if (calendar != null) {
      long dateInMills = calendar.getTimeInMillis();
      val1 = new Date(dateInMills);
    }
    
    return val1;
  }

  /**
   * Parse a string to a date object.
   * 
   * @param error The errors array.
   * @param dateString  The string representing a date.
   * @param dateTimeFormatter The date time formatter.
   * @param dateFormatter The date formatter.
   * @param timeFormatter The time formatter.
   * 
   * @return A date object or <code>null</code> if the given string 
   * is not a valid date format.
   */
  static Date parseDate(final AuthorOperationException[] error, String dateString, DateFormat dateTimeFormatter, DateFormat dateFormatter,
      DateFormat timeFormatter) {
    Date val1 = null;
    try {
      val1 = dateTimeFormatter.parse(dateString);
    } catch (ParseException e) {
      try {
        val1 = dateFormatter.parse(dateString);
      } catch (ParseException e1) {
        try {
          val1 = timeFormatter.parse(dateString);
        } catch (ParseException e2) {
          // Not a date
          error[0] = new AuthorOperationException("Unable to parse the following text value as a date:\n\"" + dateString + "\"");
        }
      }
    }
    return val1;
  }

  /**
   * Parse a string to a Double object.
   * 
   * @param error The errors array.
   * @param number  The string representing a number.
   * 
   * @return A double object or <code>null</code> if the given string is not 
   * a valid number.
   */
  static Double parseNumber(final AuthorOperationException[] error, String number) {
    Double val1 = 0.0D;
    if (number.isEmpty()) {
      val1 = Double.MIN_VALUE;
    } else {
      try {
        val1 = NumberParserUtil.parseDouble(number);
      } catch (NumberFormatException e) {
        LOGGER.debug(e, e);
        error[0] = new AuthorOperationException("Unable to parse the following text value as a number:\n\"" + number + "\"", e);
      }
    }
    return val1;
  }        
}
