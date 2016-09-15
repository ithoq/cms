(function ($) {
  'use strict';

  // Cached variables
  var $modal;
  var $modalForm;
  var $blockTable;
  var aceEditor;

  function editBlockForm($form) {
  
    var isDynamic = $form.find('#changeType').data('dynamic') === true;
    var $content = $form.find('#content');
    $content.removeAttr("data-parsley-peeble");
    if (isDynamic) {
      $content.val(aceEditor.getSession().getValue().trim());
      $content.attr("data-parsley-peeble", "");
    }
  
    $.Cms.ajax({
      formJqueryElement: $form,
      successMessage: 'Block edited successfully!',
      onSuccess: function () {
        $blockTable.DataTable().ajax.reload();
        $modal.modal('hide');
      },
    });
  }
  
  function addBlockForm($form) {
    $.Cms.ajax({
      formJqueryElement: $form,
      successMessage: 'Block added successfully!',
      onSuccess: function () {
        $blockTable.DataTable().ajax.reload();
      },
    });
  }
  
  function deleteblock(id) {
    $.Cms.ajax({
      type: 'DELETE',
      url: '/admin/block/delete/' + id,
      successMessage: 'Block deleted successfully',
      onSuccess: function (data) {
        $blockTable.DataTable().ajax.reload();
      },
    });
  }
  
  function getBlock(id) {
    $.Cms.ajax({
      type: 'GET',
      url: '/admin/block/get/' + id,
      showSuccessMessage: false,
      onSuccess: function (data, status) {
        reloadEditModalBlock(data);
        $modal.modal('show');
      },
    });
  }
  
  function toggleDynamicBlock(id) {
    $.Cms.ajax({
      type: 'GET',
      url: '/admin/block/toggle/dynamic',
      data: { id: id },
      showSuccessMessage: false,
      onSuccess: function (data, status) {
        reloadEditModalBlock(data);
        $blockTable.DataTable().ajax.reload();
      },
    });
  }
  
  // jscs:disable maximumLineLength
  function reloadEditModalBlock(view) {
    var templateTpl = document.getElementById('modalEditBlockTpl').innerHTML;
    var output = Mustache.render(templateTpl, view);
    tinymce.remove('#content');
    $('#selectType').select2('destroy');
  
    $modalForm.empty();
    $modalForm.append(output);
    var $selectType = $('#selectType');
    $selectType.val(view.blockType);
    $selectType.select2({ minimumResultsForSearch: -1 });
    if (view.dynamic) {
      aceEditor = $.Cms.initAceEditor();
    } else {
      $.Cms.initTinyMce();
    }
  }
  
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
