(function ($) {
  'use strict';

  // Cached variables
  var $newsTable;

  $(function () {
    $.Cms.addCsrfAjaxHeaderToken();

    // init variables
    $newsTable = $('#webContentTable');

    $.Cms.initDataTableWithSearch({
      tableJqueryElement: $newsTable,
      searchElement: '#web-content-table',
      ajax: {
        url: '/admin/webContent/getJson',
        data: {
          contentType: window.contentType,
          locale: locale,
        },
      },
      columnDefs: [
        { // Active
          aTargets: [0],
          className: 'center',
          render: $.Cms.dataTableRenderBoolean
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

  });
})(jQuery);
