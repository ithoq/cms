$pageForm.on('click', '#tabFileBtn', function () {
  var tableFileId = '#tableFiles';
  $tableFiles = $(tableFileId);

  if (!$.fn.DataTable.isDataTable($tableGallery)) {
    var options = {};
    options.elementId = tableFileId;
    options.$element = $tableFiles;
    options.type = 'D';
    options.searchElementId = '#search-table-files';

    initDataTable(options);
    initSwitchYesNo(options);
  }
});

$pageForm.on('click', '#tabGalleryBtn', function () {
  var tableGalleryId = '#tableGallery';
  $tableGallery = $(tableGalleryId);
  if (!$.fn.DataTable.isDataTable($tableGallery)) {
    var options = {};
    options.elementId = tableGalleryId;
    options.$element = $tableGallery;
    options.type = 'G';
    options.searchElementId = '#search-table-gallery';

    initDataTable(options);
    initSwitchYesNo(options);
  }
});

function initDataTable(options) {
  var $contentId = $('#contentDataId');
  $.Cms.initDataTableWithSearch({
    tableJqueryElement: options.$element,
    searchElement: options.searchElementId,
    ajax: {
      url: '/admin/file/getJson/' + $contentId.val(),
      data: {
        type: options.type,
      },
    },
    appendOperationColumns: 'double',
    columnDefs: [
      { // name
        aTargets: [0],
        mRender: function (data, type, full) {
          return '<input required type="text" class="light-input fileName" value="' + data + '"/>';
        },
      },
      { // description
        aTargets: [1],
        mRender: function (data, type, full) {
          return '<textarea class="light-input">' + data + '</textarea>';
        },
      },
      { // group
        aTargets: [2],
        mRender: function (data, type, full) {
          return '<input required type="text" class="light-input fileGroup" value="' + data + '"/>';
        },
      },
      { // Active
        aTargets: [3],
        className: 'center',
      },
      { // Type
        aTargets: [4],
        className: 'center',
        mRender: function (data, type, full) {
          return '<img class="imgType" src="/resources/cms/img/files-icons/' +
                  data + '" />';
        },
      },
      { // Size
        aTargets: [5],
        className: 'center',
      },
      { // Operation
        aTargets: [6],
        className: 'center',
      },
    ],
    columns: [
      { data: 'name', },
      { data: 'description', },
      { data: 'group', },
      { data: 'active', },
      { data: 'type', },
      { data: 'size', },
    ],
  });
}

function initSwitchYesNo(options) {
  $.Cms.initTabSwitchYesNo({
    tableElement: options.elementId,
    onDelete: function ($tr) {
      var id = $tr.data('id');
      $.Cms.ajax({
        url: '/admin/file/delete/' + id,
        type: 'DELETE',
        successMessage: 'File deleted successfully!',
        onSuccess: function () {
          //$tr.hide(200, function () { $tr.remove(); });
          options.$element.DataTable().ajax.reload();
        },
      });
    },

    onSave: function ($tr) {
      var id = $tr.data('id');
      var $name = $tr.find('.fileName').first();
      var $group = $tr.find('.fileGroup').first();
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
        group: $group.val(),
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
