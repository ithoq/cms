function editBlockForm($form) {
  if ($form.find('#changeType').data('dynamic') === true) {
    $form.find('#content').val(aceEditor.getSession().getValue().trim());
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
