$pageForm.on('click', '#tabFileBtn', function () {
  var tableFileId = '#tableFiles';
  var id = $pageForm.find('#currentPageId').val();
  $tableFiles = $(tableFileId);
  if (!$.fn.DataTable.isDataTable($tableFiles)) {
    initDataTable(id, $tableFiles);
    initSwitchYesNo(tableFileId);
  }
});

function initDataTable(id, $tableFiles) {
  $.Cms.initDataTableWithSearch({
    tableJqueryElement: $tableFiles,
    searchElement: '#search-table-files',
    ajax: '/admin/file/getJson/' + id,
    appendOperationColumns: 'double',
    columnDefs: [
      { // name
        aTargets: [0],
        mRender: function (data, type, full) {
          return '<input required type="text" class="light-input" value="' + data + '"/>';
        },
      },
      { // description
        aTargets: [1],
        mRender: function (data, type, full) {
          return '<textarea class="light-input">' + data + '</textarea>';
        },
      },
      { // Active
        aTargets: [2],
        className: 'center',
      },
      { // Type
        aTargets: [3],
        className: 'center',
        mRender: function (data, type, full) {
          return '<img class="imgType" src="/resources/admin/img/files-icons/' +
                  data + '" />';
        },
      },
      { // Size
        aTargets: [4],
        className: 'center',
      },
      { // Operation
        aTargets: [5],
        className: 'center',
      },
    ],
    columns: [
      { data: 'name', },
      { data: 'description', },
      { data: 'active', },
      { data: 'type', },
      { data: 'size', },
    ],
  });
}

function initSwitchYesNo(tableFileId) {
  $.Cms.initTabSwitchYesNo({
    tableElement: tableFileId,
    onDelete: function ($tr) {
      var id = $tr.data('id');
      $.Cms.ajax({
        url: '/admin/file/delete/' + id,
        type: 'DELETE',
        successMessage: 'File deleted successfully!',
        onSuccess: function () {
          //$tr.hide(200, function () { $tr.remove(); });
          $tableFiles.DataTable().ajax.reload();
        },
      });
    },

    onSave: function ($tr) {
      var id = $tr.data('id');
      var $name = $tr.find('input').first();
      var $desc = $tr.find('textarea').first();
      $.Cms.trimAllInputs($tr);
      $name.prop('required', true);
      var validationName = $name.parsley();
      validationName.reset();
      validationName.validate();
      $name.prop('required', false);
      if ($name.val() === '') {
        return;
      }

      var data = {
        id: id,
        name: $name.val(),
        description: $desc.val(),
      };
      $.Cms.ajax({
        url: '/admin/file/edit',
        type: 'POST',
        data: data,
        successMessage: 'File edited successfully!',
      });
    },
  });
}
