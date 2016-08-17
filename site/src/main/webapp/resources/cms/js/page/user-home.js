$(function () {
  $.Cms.addCsrfAjaxHeaderToken();
  var $table = $('#data-table');
  $.Cms.initDataTableWithSearch({
    tableJqueryElement: $table,
    ajax: '/admin/user/getJson',
    columnDefs: [
      { // Edit
        aTargets: [3],
        className: 'center',
        render: $.Cms.dataTableRenderBoolean,
      },
      { // Edit
        aTargets: [4],
        className: 'center',
        render: $.Cms.dataTableRenderBoolean,
      },
      { // Edit
        aTargets: [6],
        className: 'center',
      },
      { // Operation
        aTargets: [7],
        className: 'center',
        render: function (data) {
          return $.Cms.tabSwitchSimpleTpl;
        },
      },

    ],
    columns: [
      { data: 'name', },
      { data: 'firstname', },
      { data: 'email', },
      { data: 'active', },
      { data: 'locked', },
      { data: 'role', },
      {
        data: null,
        defaultContent: '<button type="button" class="btn btn-default ' +
        'btn-modal-edit"><i class="fa fa-pencil"></i></button>',
      },
    ],
  });

  $.Cms.initTabSwitchYesNo({
    tableElement: '#data-table',
    onConfirmation: function ($tr) {
      deleteUser($tr.data('id'));
    },
  });

  $table.on('click', '.btn-modal-edit', function () {
    var id = $(this).closest('tr').data('id');
    document.location.href = '/admin/user/edit/' + id;
  });

  function deleteUser(id) {
    $.Cms.ajax({
      type: 'DELETE',
      url: '/admin/user/delete/' + id,
      successMessage: 'User deleted successfully',
      onSuccess: function (data) {
        $table.DataTable().ajax.reload();
      },
    });
  }
});
