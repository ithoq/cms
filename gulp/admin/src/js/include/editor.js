// jscs:disable maximumLineLength

Cms.prototype.initTinyMce = function (params) {
  var defaults = {
    selector: '#content',
    theme: 'modern',
    relative_urls: false,
    entities: "",
    visualblocks_default_state: false,
    entity_encoding: "raw",
    height: 300,
    paste_as_text: true,
    content_css: ['/public/assets/css/editor.css' , '/public/assets/css/style.css'] ,
    plugins: [
      'advlist autolink link image lists charmap print preview hr anchor pagebreak spellchecker',
      'searchreplace wordcount visualblocks visualchars ace_beautify fullscreen insertdatetime media nonbreaking',
      'save table contextmenu directionality emoticons template paste textcolor jsplus_templates',
      'jsplus_bootstrap_include,jsplus_bootstrap_show_blocks,jsplus_bootstrap_block_conf,jsplus_bootstrap_templates,jsplus_bootstrap_button, jsplus_bootstrap_alert, jsplus_bootstrap_col_move_left, jsplus_bootstrap_col_move_right, jsplus_bootstrap_delete_col, jsplus_bootstrap_delete_row, jsplus_bootstrap_include, jsplus_bootstrap_row_add_down, jsplus_bootstrap_row_add_up, jsplus_bootstrap_row_move_down, jsplus_bootstrap_row_move_up',
    ],
    toolbar: 'insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image media fullpage | forecolor backcolor | jsplus_templates | fullscreen | jsplus_bootstrap_show_blocks,jsplus_bootstrap_block_conf,jsplus_bootstrap_templates,jsplus_bootstrap_button, , jsplus_bootstrap_alert, jsplus_bootstrap_col_move_left, jsplus_bootstrap_col_move_right, jsplus_bootstrap_delete_col, jsplus_bootstrap_delete_row, jsplus_bootstrap_include, jsplus_bootstrap_row_add_down, jsplus_bootstrap_row_add_up, jsplus_bootstrap_row_move_down, jsplus_bootstrap_row_move_up',
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
