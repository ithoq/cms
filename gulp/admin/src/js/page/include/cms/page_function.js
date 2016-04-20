function updatePagePosition(pagesToUpdate, parentId) {
  // No need 'encodeURIComponent' because values are numeric
  var data = 'parentId=' + parentId;
  for (var i = 0; i < pagesToUpdate.length; i++) {
    data += '&id=' + pagesToUpdate[i].id;
    data += '&position=' + pagesToUpdate[i].position;
    data += '&level=' + pagesToUpdate[i].level;
  }

  $.Cms.ajax({
    url: '/admin/cms/page/updatePosition',
    type: 'POST',
    showSuccessMessage: false,
    errorMessage: 'The page could not be repositioned',
    data: data,
    onError: function () {
      treeCache.reload();
    },
  });
}

function reloadPage(id, _locale) {
  var options = {
      url: '/admin/cms/page/' + id,
      type: 'GET',
      formReset: false,
      showSuccessMessage: false,
      onSuccess: function (data) {
        // remove TinyMce Plugin
        $.Cms.removeAllTinyMce();
        var $tableFiles = $('#tableFiles');

        // remove DataTable Plugin and YesNo
        if ($tableFiles) {
          $tableFiles.DataTable().destroy();

          //$.Cms.destroyTabSwitchYesNo();
        }

        // remove Jquery Upload Plugin
        $('#fileupload').fileupload('destroy');

        $pageForm.html(data);
        initFileUpload();

        $pageForm.find('[data-toggle="tooltip"]').tooltip();
        $pageForm.find('[data-editor="tinymce"]').each(function (index, element) {
          $.Cms.initTinyMce({ selector: '#' + element.id });
        });
      },
    };

  if (_locale) {
    options.data = { locale: _locale };
  }

  $.Cms.ajax(options);
}
