(function ($) {
  'use strict';

  // Cached variables
  var $newsTable;



  $(function () {
    $.Cms.addCsrfAjaxHeaderToken();

    // init variables
    $newsTable = $('#webContentTable');

    console.log("Content type : " + window.contentType);
    /*
    $.Cms.initDataTableWithSearch({
      tableJqueryElement: $blockTable,
      searchElement: '#search-table-files',
      ajax: '/admin/webContent/getJson',
      columnDefs: [
        { // Type
          aTargets: [1],
          className: 'center',
        },
        { // Dynamic
          aTargets: [2],
          className: 'center',
          render: $.Cms.dataTableRenderBoolean,
        },
        { // Edit
          aTargets: [3],
          className: 'center',
        },
        { // Operation
          aTargets: [4],
          className: 'center',
          render: function (data) {
            return (data === true) ? $.Cms.tabSwitchSimpleTpl : '';
          },
        },

      ],
      columns: [
        { data: 'name', },
        { data: 'type', },
        { data: 'dynamic', },
        {
          data: null,
          defaultContent: '<button type="button" class="btn btn-default ' +
                          'btn-modal-edit"><i class="fa fa-pencil"></i></button>',
        },
        { data: 'deletable' },
      ],
    });

      */
  });
})(jQuery);
