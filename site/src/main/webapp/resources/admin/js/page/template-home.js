$(function () {
  $.Cms.addCsrfAjaxHeaderToken();
  var $table = $('#data-table');
  $.Cms.initDataTableWithSearch({
    tableJqueryElement: $table,
    ajax: '/admin/contentTemplate/getJson',
    columnDefs: [
      { // Edit
        aTargets: [2],
        className: 'center',
      },
      { // Operation
        aTargets: [3],
        className: 'center',
        render: function (data) {
          return (data === true) ? $.Cms.tabSwitchSimpleTpl : '';
        },
      },

    ],
    columns: [
        { data: 'name', },
        { data: 'description', },
        {
          data: null,
          defaultContent: '<button type="button" class="btn btn-default ' +
          'btn-modal-edit"><i class="fa fa-pencil"></i></button>',
        },
        { data: 'deletable' },
    ],
  });

  $.Cms.initTabSwitchYesNo({
    tableElement: '#data-table',
    onConfirmation: function ($tr) {
      deleteFieldset($tr.data('id'));
    },
  });

  $table.on('click', '.btn-modal-edit', function () {
    var id = $(this).closest('tr').data('id');
    document.location.href = '/admin/contentTemplate/edit/' + id;
  });

  function deleteFieldset(id) {
    $.Cms.ajax({
      type: 'DELETE',
      url: '/admin/contentTemplate/delete/' + id,
      successMessage: 'Fieldset deleted successfully',
      onSuccess: function (data) {
        $table.DataTable().ajax.reload();
      },
    });
  }
});
