/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2015 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.commons.table.support.errorscanner;






/**
 * CALS table layout problem
 */

public enum CALSAndHTMLTableLayoutProblem implements TableLayoutProblem {
  /**
   * A specific attribute must have a numeric value
   * 
   * en: "The {0} attribute must have a numeric value. The current value is: {1}."
   * 
   * {0} is the attribute name
   * {1} is the attribute value 
   */
  ATTRIBUTE_VALUE_NOT_INTEGER(Severity.ERROR), 
  /**
   * A cell was already occupied
   * 
   * en:  "The cell[{0}][{1}] is already occupied."
   * 
   * {0} is the row number
   * {1} is the column number 
   */
  CELL_OVERLAPPING(Severity.ERROR), 
  /**
   * The column index determined from namest attribute value is less than the column index
   * determined from nameend.
   * 
   * en: "The column index determined from namest ({0}) attribute value is greater
   * than the column index determined from nameend ({1})."
   * 
   * {0} the namest attribute value
   * {1} the nameend attribute value
   */
  NAMEST_LESS_THAN_NAMEEND(Severity.ERROR), 
  /**
   * There is a difference between the number of colspecs and the number of actual columns 
   * 
   * en: "The number of table columns determined from the table structure ({0}) 
   * is different than the number of colspecs ({1})."
   * 
   * {0} is the table columns count
   * {1} is the colspecs number
   */
  COLSPECS_DIFFERENT_THAN_COLUMNS(Severity.WARN),
  /**
   * There is a difference between the cols value and the number of actual columns 
   * 
   * en: "The number of table columns determined from the table structure ({0}) 
   * is different than the value of the {1} attribute: ({2})."
   * 
   * {0} is the table columns count
   * {1} the name of the (cols) attribute 
   * {2} the value of the (cols) attribute
   */
  COLS_DIFFERENT_THAN_COLUMNS(Severity.ERROR),
  /**
   * The number of cells in the row overflows the number of table columns
   * 
   * en: "The number of cells in the row ({0}) is greater than the value ({1}) of the table {2} attribute."
   * 
   * {0} is the row cells count
   * {1} the value of the table attribute that counts the columns number (cols)
   * {2} the name of the table attribute that counts the columns number (cols)
   */
  ROW_CELL_COUNT_OVERFLOW(Severity.ERROR),
  /**
   * The number of cells in the row is less than the number of table columns
   * 
   * en: "The number of cells in the row ({0}) is less than the value ({1}) of the table {2} attribute."
   * 
   * {0} is the row cells count
   * {1} the value of the table attribute that counts the columns number (cols)
   * {2} the name of the table attribute that counts the columns number (cols)
   */
  ROW_CELL_COUNT_UNDERFLOW(Severity.ERROR), 
  /**
   * The column name from the namest or nameend attribute value is not found in column specifications
   * 
   * en: "The column name ({0}) from the {1} attribute value is not found in column specifications."
   * 
   * {0} the column name specified in the table 
   * {2} the name of the attribute that specifies the wrong column name
   */
  COLUMN_NAME_INCORRECT(Severity.ERROR); 

  /**
   * Message tag
   */
  private String message;
  /**
   * Severity
   */
  private Severity severity;

  /**
   * Constructor.
   * 
   * @param severity The problem severity, one of {@link Severity} constants.
   */
  private CALSAndHTMLTableLayoutProblem(Severity severity) {
    this(null, severity);
  }
  
  /**
   * Constructor.
   * 
   * @param tag The message tag.
   * @param severity The problem severity, one of {@link Severity} constants.
   */
  private CALSAndHTMLTableLayoutProblem(String message, Severity severity) {
    this.message = message;
    this.severity = severity;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.support.errorscanner.TableLayoutProblem#getMessage()
   */
  @Override
  public String getMessage() {
    return message;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.support.errorscanner.TableLayoutProblem#getSeverity()
   */
  @Override
  public Severity getSeverity() {
    return severity;
  }
}
