// Delete a page
$('#btnDeletePage').click(function () {
  var id = $('#contentDataId').val();
  var contentId = $('#contentId').val();
  var params = {
    url: '/admin/cms/page/delete/' + id,
    type: 'DELETE',
    successMessage: 'The page was deleted successfully!',
    onSuccess: function (data) {
      $.Cms.removeAllTinyMce(); // must do before dom is deleted (bug Firefox)
      if(data=== "empty"){
        $pageForm.empty();
      } else{
        reloadPage(contentId);
      }
      treeCache.reload();
    },
  };
  $.Cms.ajax(params);
});

// expand
$pageForm.on('click', '#btn-expand', function () {
  $('#main-content').toggleClass('expanded');
});

// Save a page
$pageForm.on('click', '#savePageBtn', function () {
  var id = $('#contentId').val();
  var dataId = $('#contentDataId').val();
  $.Cms.ajax({
    formJqueryElement: $pageForm,
    successMessage: 'The page was saved successfully!',
    appendToUrl: id,
    formReset: false,
    onSuccess: function () {
      treeCache.reload();
      reloadPage(id, dataId);
    },
  });
});

$pageForm.on('click', '.creatlang', function () {
  reloadPage($('#contentId').val(), $(this).data('id'));
});

$pageForm.on('click', '#preview', function () {
  var $form = $(this).closest('form').clone();
  console.log($form);
  var $csrf = $('[name=_csrf]').first().clone();
  console.log($csrf);
  $csrf.attr('id', 'csrf');
  $form.append($csrf);
  $form.attr('target', '_blank');
  $form.attr('action', '/admin/cms/preview');
  console.log($form);
  $form.submit();
});

// change language
$pageForm.on('change', '#selectLanguage', function () {
  reloadPage($('#contentId').val(), this.value);
});

// Create Page
$('#btnFormCeatePage').click(function () {
  $.Cms.ajax({
    formElement: '#createPageForm',
    successMessage: 'The page was created successfully!',
    onSuccess: function (data, status, response) {
      if (response.getResponseHeader('Validation-Failed')) {
        window.console.log(data);
        $.Cms.notif({
          message: 'Validation error',
          type: 'error',
        });
        return;
      }
      $modalCreateNewPage.modal('hide');
      treeCache.reload();
    },
  });
});

// Open Modal Form
$('#btnCreatePage').click(function () {
  var node = treeCache.getActiveNode();
  var $containerParent = $('#radio-parent-container');
  var $containerRoot = $('#radio-root-container');
  $containerParent.show();
  $containerRoot.show();
  if (node) {
    var title = node.title;
    var parentId = node.key;
    $('#radio-parent').val(parentId).prop('checked', true);
    $containerParent.find('label').html('Child of : ' + title);
  } else {
    $containerParent.hide();
    $('#radio-root').prop('checked', true);
  }

  $selectType.val('Page').trigger('change');
  $modalCreateNewPage.modal('show');
});

$selectType.on('change', function () {
  var $selector = $modalCreateNewPage.find('selectHide');
  var anim = 200;
  if (this.value === 'Page') {
    $selector.find('.js-page').show(anim);
    $selector.find('.js-module').hide(anim);
  } else if (this.value === 'Module') {
    $selector.find('.js-page').hide(anim);
    $selector.find('.js-module').show(anim);
  } else {
    $selector.hide(anim);
  }
});
