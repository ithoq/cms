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
          locale: null,
        },
      },
      columnDefs: [
        { // Active
          aTargets: [0],
          className: 'center',
          render: $.Cms.dataTableRenderBoolean,
        },
        { // Lang
          aTargets: [2],
          className: 'center',
        },
        { // Lang
          aTargets: [3],
          className: 'center',
        },
        { // Lang
          aTargets: [4],
          className: 'center',
        },
        { // Edit
          aTargets: [5],
          className: 'center',
        },
        { // Operation
          aTargets: [6],
          className: 'center',
        },

      ],
      columns: [
        { data: 'active', },
        { data: 'title', },
        { data: 'lang', },
        { data: 'dateBegin', },
        { data: 'dateEnd', },
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
      var lang = $(this).closest('tr').data('lang');
      document.location.href = '/admin/webContent/edit/' + contentType + '/' + id + '?langCode=' + lang;
    });

    $.Cms.initTabSwitchYesNo({
      tableElement: '#webContentTable',
      onConfirmation: function ($tr) {
        deleteblock($tr.data('id'));
        //console.log('delete');
      },
    });

    function deleteblock(id) {
      $.Cms.ajax({
        type: 'DELETE',
        url: '/admin/cms/page/deleteContent/' + id,
        successMessage: 'User deleted successfully',
        onSuccess: function (data) {
          $table.DataTable().ajax.reload();
        },
      });
  }

  });
})(jQuery);
