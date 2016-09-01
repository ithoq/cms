(function ($) {
  'use strict';

  // Cached variables
  var $table;

  $(function () {
    $.Cms.addCsrfAjaxHeaderToken();

    // init variables
    $table = $('#webContentTable');

    $.Cms.initDataTableWithSearch({
      tableJqueryElement: $table,
      searchElement: '#web-content-table',
      ajax: {
        url: '/admin/webContent/getJson',
        data: {
          contentType: window.contentType,
          locale: window.locale,
        },
      },
      columnDefs: [
        { // Active
          aTargets: [0],
          className: 'center',
          render: $.Cms.dataTableRenderBoolean,
        },
        { // Category
          aTargets: [2],
          className: 'center',
        },
        { // Edit
          aTargets: [3],
          className: 'center',
        },
        { // Operation
          aTargets: [4],
          className: 'center',
        },

      ],
      columns: [
        { data: 'active', },
        { data: 'title', },
        { data: 'category', },
        {
          data: null,
          defaultContent: '<button type="button" class="btn btn-default ' +
                          'btn-modal-edit"><i class="fa fa-pencil"></i></button>',
        },
        {
          data: null,
          defaultContent: $.Cms.tabSwitchSimpleTpl(),
        },
      ],
    });

    $table.on('click', '.btn-modal-edit', function () {
      var id = $(this).closest('tr').data('id');
      document.location.href = '/admin/webContent/edit/' + contentType + '/' + id + '?langCode=' + window.locale;
    });

    $.Cms.initTabSwitchYesNo({
      tableElement: '#webContentTable',
      onConfirmation: function ($tr) {
        deleteblock($tr.data('contentDataId'));
        //console.log('delete');
      },
    });

    function deleteblock(id) {
      console.log("Data id = " + id);
      $.Cms.ajax({
        type: 'DELETE',
        url: '/admin/webContent/delete/' + id,
        successMessage: 'User deleted successfully',
        onSuccess: function (data) {
          $table.DataTable().ajax.reload();
        },
      });
  }

  });
})(jQuery);
