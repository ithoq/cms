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
