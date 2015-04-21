goog.provide('sync.tei.TeiExtension');


/**
 * Constructor for the tei Extension.
 *
 * @constructor
 */
sync.tei.TeiExtension = function(){
  sync.ext.Extension.call(this);
};
goog.inherits(sync.tei.TeiExtension, sync.ext.Extension);

/**
 * Editor created callback.
 *
 * @param {sync.Editor} editor The currently created editor.
 */
sync.tei.TeiExtension.prototype.editorCreated = function(editor) {
  goog.events.listen(editor, sync.api.Editor.EventTypes.ACTIONS_LOADED, function(e) {
    var actionsManager = editor.getActionsManager();
    var originalInsertImageAction = actionsManager.getActionById('insert image');
    if (originalInsertImageAction) {
      var insertImageAction = new sync.actions.InsertImage(
        originalInsertImageAction, 
        "ro.sync.ecss.extensions.tei.InsertImageOperationP5", 
        editor);
      actionsManager.registerAction('insert image', insertImageAction);
    }
    
    var originalInsertTableAction = actionsManager.getActionById('insert.table');
    if (originalInsertTableAction) {
      var insertTableAction = new sync.actions.InsertTable(
        originalInsertTableAction, 
        "ro.sync.ecss.extensions.tei.table.InsertTableOperation", 
        editor, 
        [sync.actions.InsertTable.TableTypes.CUSTOM],
        [sync.actions.InsertTable.ColumnWidthTypes.PROPORTIONAL, 
         sync.actions.InsertTable.ColumnWidthTypes.DYNAMIC, 
         sync.actions.InsertTable.ColumnWidthTypes.FIXED],
         "Head",
         "http://www.tei-c.org/ns/1.0");
      actionsManager.registerAction('insert.table', insertTableAction);
    }
  });
};

// Publish the extension.
sync.ext.Registry.extension = new sync.tei.TeiExtension();