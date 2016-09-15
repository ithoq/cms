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
