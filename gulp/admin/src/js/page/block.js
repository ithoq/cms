(function ($) {
  'use strict';

  // Cached variables
  var $modal;
  var $modalForm;
  var $blockTable;
  var aceEditor;

  //=include include/block/crud_function.js
  //=include include/block/modal_function.js
  $(function () {
    $.Cms.addCsrfAjaxHeaderToken();

    // init variables
    $modal = $('#modalEditBlock');
    $modalForm = $('#modalEditBlockForm');
    $blockTable = $('#blockTable');

    $.Cms.initDataTableWithSearch({
      tableJqueryElement: $blockTable,
      searchElement: '#search-table-files',
      ajax: '/admin/block/getJson',
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

    $.Cms.initTabSwitchYesNo({
      tableElement: '#blockTable',
      onConfirmation: function ($tr) {
        deleteblock($tr.data('id'));
      },
    });

    $('#createBlockForm').on('submit', function (e) {
      e.preventDefault();
      addBlockForm($(this));
    });

    $modal.on('click', '#changeType', function () {

      var $this = $(this);
      var id = $this.data('id');
      toggleDynamicBlock(id);
    });

    $modalForm.on('submit', function (e) {
      e.preventDefault();
      editBlockForm($(this));
    });

    $blockTable.on('click', '.btn-modal-edit', function () {
      var id = $(this).closest('tr').data('id');
      getBlock(id);
    });

  });
})(jQuery);
