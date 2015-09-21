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
package ro.sync.ecss.extensions.commons;





/**
 * The collection of the extension messages.
 */

public interface ExtensionTags {
  /**
   * Name of a section in the Sort dialog.
   * 
   * en: Range
   */
  String RANGE = "Range";
  /**
   * Name of a section in the Sort dialog.
   * 
   * en: Criteria
   */
  String CRITERIA = "Criteria";
  /**
   * Name for ok buttons from an insertion dialog.
   * 
   * en: Insert
   */
  String INSERT = "Insert";
  /**
   * Label for 'Sort' dialog.
   * 
   * en: And then by
   */
  String AND_THEN_BY = "And_then_by";
  /**
   * String used for criterion name.
   * 
   * en: List item
   */
  String LIST_ITEM = "List_item";
  /**
   * String used for Sort dialog for a radio button.
   * 
   * en: Selected items
   */
  String SELECTED_ITEMS = "Selected_items";
  /**
   * String used for Sort dialog for a radio button.
   * 
   * en: Selected rows
   */
  String SELECTED_ROWS = "Selected_rows";
  /**
   * Tooltip for key combobox in the Sort table action dialog.
   * 
   * en: Select the column on which the sort operation will be applied.
   */
  String SELECT_KEY_COLUMN_TOOLTIP = "Select_key_column_tooltip";
  /**
   * Tooltip for type combobox in the Sort table action dialog.
   * 
   * en: Select the type of the information which will be sorted. It is one of: Text, Numeric, Date.
   */
  String SELECT_TYPE_COMBO_TOOLTIP = "Select_type_combo_tooltip";
  /**
   * Tooltip for order combobox in the Sort table action dialog.
   * 
   * en: Select the order in which the information will be sorted.
   */
  String SELECT_ORDER_COMBO_TOOLTIP = "Select_order_combo_tooltip";
  /**
   * Value used for sort order.
   * 
   * en: Ascending
   */
  String ASCENDING = "Ascending";
  /**
   * Value used for sort order.
   * 
   * en: Descending
   */
  String DESCENDING = "Descending";
  /**
   * Value used to sort elements.
   * 
   * en: Numeric
   */
  String NUMERIC = "Numeric";
  /**
   * Value used to sort elements.
   * 
   * en: Date
   */
  String DATE = "Date";
  /**
   * Value used to sort elements.
   * 
   * en: Text
   */
  String TEXT = "Text";
  /**
   * Head checkbox in the TEI table customizer dialog.
   * 
   * en: Head
   */
  String HEAD = "Head";
  /**
   * 'Frame' label text.
   * 
   * en: Frame
   */
  String FRAME = "Frame";
  /**
   * Column widths
   * 
   * en: Column widths
   */
  String COLUMN_WIDTHS = "Column_widths";
  /**
   * 'Generate table footer' checkbox text.
   * 
   * en: Generate table footer
   */
  String GENERATE_TABLE_FOOTER = "Generate_table_footer";
  /**
   * 'Generate table header' checkbox text.
   * 
   * en: Generate table header
   */
  String GENERATE_TABLE_HEADER = "Generate_table_header";
  /**
   * Columns
   * 
   * en: Columns
   */
  String COLUMNS = "Columns";
  /**
   * Column
   * 
   * en: Column
   */
  String COLUMN = "Column";
  /**
   * Cells
   * 
   * en: Cells
   */
  String CELLS = "Cells";
  /**
   * Cell
   * 
   * en: Cell
   */
  String CELL = "Cell";
  /**
   * Label used to specify how many columns are affected by modifications from
   * table properties dialog.
   * 
   * en: {0} column(s) will be affected.
   */
  String AFFECTED_COLUMNS = "Affected_columns";
  /**
   * Label used to specify how many cells are affected by modifications from
   * table properties dialog.
   * 
   * en: {0} cell(s) will be affected.
   */
  String AFFECTED_CELLS = "Affected_cells";
  /**
   * Label used to specify how many rows are affected by modifications from
   * table properties dialog.
   * 
   * en: {0} row(s) will be affected.
   */
  String AFFECTED_ROWS = "Affected_rows";
  /**
   * Horizontal alignment render string.
   * 
   * en: Horizontal alignment
   */
  String HORIZONTAL_ALIGNMENT = "Horizontal_alignment";
  /**
   * Vertical alignment render string.
   * 
   * en: Vertical alignment
   */
  String VERTICAL_ALIGNMENT = "Vertical_alignment";
  /**
   * Column separator render string.
   * 
   * en: Column separator
   */
  String COLUMN_SEPARATOR = "Column_separator";
  /**
   * Name for the alignment table attribute.
   * 
   * en: Alignment
   */
  String ALIGNMENT = "Alignment";
  /**
   * Row separator render string.
   * 
   * en: Row separator
   */
  String ROW_SEPARATOR = "Row_separator";
  /**
   * Separators group title.
   * 
   * en: Separators
   */
  String SEPARATORS = "Separators";
  /**
   * Rows
   * 
   * en: Rows
   */
  String ROWS = "Rows";
  /**
   * Row
   * 
   * en: Row
   */
  String ROW = "Row";
  /**
   * Table size.
   * 
   * en: Table Size
   */
  String TABLE_SIZE = "Table_size";
  /**
   * 'Simple' button text.
   * 
   * en: Simple
   */
  String SIMPLE = "Simple";
  /**
   * Name for a model chooser group.
   * 
   * en: Model
   */
  String MODEL = "Model";
  /**
   * Insert table dialog title.
   * 
   * en: Insert Table
   */
  String INSERT_TABLE = "Insert_table";
  /**
   * Insert choice table dialog title.
   * 
   * en: Insert Choice Table
   */
  String INSERT_CHOICE_TABLE = "Insert_choice_table";
  /**
   * Insert table dialog title.
   * 
   * en: Insert Table
   */
  String INSERT_COLUMN = "insert.table.column";
  /**
   * 
   * 'Remove ID's when copying content in the same document' button text.
   * 
   * en: Remove ID's when copying content in the same document
   */
  String REMOVE_IDS_ON_COPY_IN_SAME_DOC = "Remove_ids_on_copy_in_same_doc";
  /**
   * 
   * 
   * en: Remove
   */
  String REMOVE = "Remove";
  /**
   * 
   * 
   * en: Edit
   */
  String EDIT = "Edit";
  /**
   * 
   * 
   * en: Add
   */
  String ADD = "Add";
  /**
   * 'ID pattern' label text.
   * 
   * en: ID Pattern:
   */
  String ID_PATTERN = "ID_pattern";
  /**
   * 'Auto generate IDs for elements' button text.
   * 
   * en: Auto generate IDs for elements
   */
  String AUTOGENERATE_IDS_FOR_ELEMENTS = "Autogenerate_ids_for_elements";
  /**
   * Title for ID elements customizer dialog.
   * 
   * en: ID Options
   */
  String ID_OPTIONS = "ID_options";
  /**
   * Image file chooser file description.
   * 
   * en: Image files
   */
  String IMAGE_FILES = "Image_files";
  /**
   * Image file chooser title.
   * 
   * en: Choose image
   */
  String CHOOSE_IMAGE = "Choose_image";
  /**
   * Title checkbox in the DITA relationship table customizer dialog.
   * 
   * en: Title
   */
  String TITLE = "Title";
  /**
   * The title table tooltip for title checkbox in TEI table customizer dialog.
   * 
   * en: The title for the table
   */
  String TITLE_TABLE = "Title_table";
  /**
   * Title for DITA relationship table customizer dialog. 
   * 
   * en: Insert Relationship Table
   */
  String INSERT_RELATIONSHIP_TABLE = "Insert_relationship_table";
  /**
   * Tooltip for insert 'informalTable' in Docbook table customizer dialog. 
   * 
   * en: If not checked an 'informaltable' will be inserted.
   */
  String INSERT_INFORMAL_TABLE_TOOLTIP = "Insert_informal_table_tooltip";
  /**
   * 'Insert entry table' title for Docbook table customizer dialog. 
   * 
   * en: Insert Entry Table
   */
  String INSERT_ENTRY_TABLE = "Insert_entry_table";
  /**
   * Caption checkbox in the XHTML table customizer dialog.
   * 
   * en: Caption
   */
  String CAPTION = "Caption";
  /**
   * Message shown on paste column handling.
   */
  String HANDLE_PASTE_COLUMN_FAIL_MESSAGE = "handle.paste.column.warning";
  /**
   * Cancel message.
   */
  String CANCEL = "cancel";
  /**
   * Continue message
   */
  String CONTINUE = "continue";
  /**
   * Insert web link (link) dialog title.
   */
  String INSERT_WEB_LINK = "insert.web.link";
  /**
   * Insert web link (ulink) dialog title.
   */
  String INSERT_WEB_ULINK = "insert.web.ulink";
  /**
   * Name for elements sort dialog.
   * 
   * en: Sort
   */
  String SORT = "Sort";
  /**
   * Name for radio button used to sort all the elements.
   * 
   * en: All
   */
  String ALL = "All";
  /**
   * Label from the css outliner panel.
   * 
   * en: Sort by
   */
  String SORT_BY = "Sort_by";
  
  /**
   * Options label.
   * 
   * en: Type
   */
  String TYPE = "Type";
  /**
   * Options label. Order as in: node order, attributes order.
   * 
   * en: Order
   */
  String ORDER = "Order";
  /**
   * Option group name in find/replace dialog.
   * 
   * en: Scope
   */
  String SCOPE = "Scope";
  /**
   * en: All rows
   */
  String ALL_ROWS = "All_rows";
  /**
   * en: All items
   */
  String ALL_ITEMS = "All_items";
  /**
   * "Table properties" preview group title.
   * 
   * en: Preview
   */
  String PREVIEW = "Preview";
  /**
   * "Table properties" dialog title.
   * 
   * en: Table properties
   */
  String TABLE_PROPERTIES = "Table_properties";
  /**
   * Table
   * 
   * en: Table
   */
  String TABLE = "Table";
  /**
   * Message that explain why the table properties operation cannot be performed.
   * 
   * en: The operation cannot be performed on multiple tables.
   */
  String CANNOT_PERFORM_TABLE_PROPERTIES_OPERATION = "Cannot_perform_table_properties_operation";
  
  /**
   * Message shown when the "Table properties" operation cannot be performed because there
   * are no elements detected to modified properties for.
   * 
   * en: Current action cannot be performed because there is no element whose properties can be modified. 
   */
  String CANNOT_PERFORM_OPERATION_NO_ELEMENT_TO_EDIT_PROPERTIES_FOR = "Cannot_perform_operation_no_element_to_edit_properties_for";
  /**
   * Row type property render string. It refers to the type a row. A row can be header row,
   * body row or footer row.
   * 
   * en: Row type
   */
  String ROW_TYPE = "Row_type";
  /**
   * "Insert Rows" action and frame/shell title. The action label has 3 dots added, the frame/shell title doesn't.
   */
  String INSERT_ROWS = "insert.table.rows.custom";
  /**
   * en: Number of rows
   */
  String NUMBER_OF_ROWS = "Number_of_rows";
  /**
   * "Insert Columns" action and frame/shell title. The action label has 3 dots added, the frame/shell title doesn't.
   */
  String INSERT_COLUMNS = "insert.table.columns.custom";
  /**
   * en: Number of columns
   */
  String NUMBER_OF_COLUMNS = "Number_of_columns";
  /**
   * en: Position
   */
  String POSITION = "Position";
  /**
   * en: Above
   */
  String ABOVE = "Above";
  /**
   * en: Below
   */
  String BELOW = "Below";
  /**
   * en: Before
   */
  String BEFORE = "Before";
  /**
   * en: After
   */
  String AFTER = "After";
}