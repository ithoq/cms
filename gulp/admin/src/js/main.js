//=include include/primitive_type_extension.js

(function ($) {
  'use strict';

  var languagesNativeName = {
    fr: 'french',
    en: 'english',
  };

  var Cms = function () {
    this.AUTHOR = 'Fabrice Cipolla';
    this.$body = $('body');
    this.locale = window.currentLocale;
    this.genericSuccessMessage = 'Operation performed successfully';
    this.genericErrorMessage = 'Oops! Something went wrong. Please try again later';
  };

  // remove default behaviour
  $(document).on('drop dragover', function (e) {
    e.preventDefault();
  });

  //=include include/utils.js
  //=include include/ajax.js
  //=include include/tabSwitchYesNo.js
  //=include include/initDynamicFields.js
  //=include include/notif.js
  //=include include/plugin_datatables.js
  //=include include/upload.js

  //=include include/editor.js

  //=include include/parsley_validators.js
  //=include include/keepSessionAlive.js

  $.Cms = new Cms();
})(jQuery);
