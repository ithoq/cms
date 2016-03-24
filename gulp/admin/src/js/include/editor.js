// jscs:disable maximumLineLength

Cms.prototype.initTinyMce = function (params) {
  var defaults = {
    selector: '#content',
    theme: 'modern',
    height: 200,
    plugins: [
      'advlist autolink link image lists charmap print preview hr anchor pagebreak spellchecker',
      'searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking',
      'save table contextmenu directionality emoticons template paste textcolor',
    ],
    toolbar: 'insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image | print preview media fullpage | forecolor backcolor emoticons',
  };
  var settings = $.extend({}, defaults, params);
  return tinymce.init(settings);
};

Cms.prototype.initAceEditor = function (params) {

  var defaults = {
    selector: '#ace-editor',
    theme: 'ace/theme/monokai',
    mode: 'ace/mode/twig',
  };

  var settings = $.extend({}, defaults, params);
  var editor = ace.edit(settings.selector.substring(1));
  editor.setTheme(settings.theme);
  editor.getSession().setMode(settings.mode);

  return editor;
};
