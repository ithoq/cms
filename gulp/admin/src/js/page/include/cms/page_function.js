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

        // remove DataTable Plugin
        var $tables = $('table.dataTable');
        if ($tables.length > 0) {
          $tables.each(function (index, element) {
            $(element).DataTable().destroy();
          });
        }

        // remove Jquery Upload Plugin
        $('#fileupload').fileupload('destroy');

        $pageForm.html(data);

        $('.upload-container').each(function (index, element) {
          var $el = $(element);
          var options = {};
          var formData = {};
          formData.type = $el.data('type');
          formData.contentId = $el.data('id');

          if (formData.type === 'GALLERY') {
            options.maxSize = 5 * 1000 * 1000;
            options.acceptFileTypes = /(\.|\/)(gif|jpe?g|png)$/i;
          }

          options.formData = formData;
          options.$container = $el;
          options.onStop = function () {
            var $el = $(element);
            $('#' + $el.data('table')).DataTable().ajax.reload();
          };

          $.Cms.initFileUpload(options);
        });

        $pageForm.find('[data-toggle="tooltip"]').tooltip();
        $pageForm.find('[data-editor="tinymce"]').each(function (index, element) {
          $.Cms.initTinyMce({ selector: '#' + element.id });
        });

        $pageForm.find('[data-plugin="datepicker"]').each(function (index, element) {
          var $picker = $(element);

          $picker.datepicker({
            format: 'yyyy-mm-dd',
            multidate: (typeof $picker.data('picker-multiple') !== 'undefined'),
          });
        });
      },
    };

  if (_locale) {
    options.data = { locale: _locale };
  }

  $.Cms.ajax(options);
}
