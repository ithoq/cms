Cms.prototype.initDynamicFields = function ($form) {
  $form.find('[data-toggle="tooltip"]').tooltip();
  $form.find('[data-editor="tinymce"]').each(function (index, element) {
    $.Cms.initTinyMce({ selector: '#' + element.id });
  });

  $form.find('[data-plugin="datepicker"]').each(function (index, element) {
    var $picker = $(element);

    $picker.datepicker({
      format: 'yyyy-mm-dd',
      multidate: (typeof $picker.data('picker-multiple') !== 'undefined'),
    });
  });
};
