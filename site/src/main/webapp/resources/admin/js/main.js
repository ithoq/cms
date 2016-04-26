String.prototype.capitalizeFirstLetter = function () {
  return this.charAt(0).toUpperCase() + this.slice(1);
};


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

  $.fn.extend({
    animateCss: function (animationName) {
      var animationEnd = 'webkitAnimationEnd mozAnimationEnd MSAnimationEnd ' +
                         'oanimationend animationend';
      $(this).addClass('animated ' + animationName).one(animationEnd, function () {
        $(this).removeClass('animated ' + animationName);
      });
    },
  });
  
  Cms.prototype.trimAllInputs = function ($elem) {
    $elem.find('input:text, textarea').each(function () {
      this.value = this.value.trim();
    });
  };
  
  Cms.prototype.removeAllTinyMce = function () {
    for (var i = tinymce.editors.length - 1; i > -1; i--) {
      var editorId = tinymce.editors[i].id;
      tinyMCE.execCommand('mceRemoveEditor', true, editorId);
    }
  };
  
  Cms.prototype.addCsrfAjaxHeaderToken = function (formId) {
    var token;
    if (formId) {
      token = $('#' + formId + ' input[name=_csrf]').val();
    } else {
      token = $('input[name=_csrf]').first().val();
    }
  
    var header = 'X-CSRF-TOKEN';
    $(document).ajaxSend(function (e, xhr, options) {
      xhr.setRequestHeader(header, token);
    });
  };
  
  Cms.prototype.transitionEndEventName = function () {
    var i;
    var el = document.createElement('div');
    var transitions = {
        transition: 'transitionend',
        OTransition: 'otransitionend', // oTransitionEnd in very old Opera
        MozTransition: 'transitionend',
        WebkitTransition: 'webkitTransitionEnd',
      };
  
    for (i in transitions) {
      if (transitions.hasOwnProperty(i) && typeof el.style[i] !== undefined) {
        return transitions[i];
      }
    }
  
    return null;
  };
  
  Cms.prototype.limitText = function (field, maxChar) {
    var $ref = $(field);
    var val = $ref.val();
    if (val.length >= maxChar) {
      $ref.val(function () {
        return val.substr(0, maxChar);
      });
    }
  };
  
  Cms.prototype.ajax = function (params) {
  
    var defaults = {
      successMessage: this.genericSuccessMessage,
      errorMessage: this.genericErrorMessage,
      showSuccessMessage: true,
      showErrorMessage: true,
      formReset: true,
      dataType: 'html',
      trimAllInputs: true,
      validate: true,
    };
  
    var settings = $.extend({}, defaults, params);
    var $form;
  
    if (settings.formJqueryElement) {
      $form = settings.formJqueryElement;
    } else if (settings.formElement) {
      $form = $(settings.formElement);
    }
  
    if ($form && tinyMCE) {
      tinyMCE.triggerSave();
    }
  
    if ($form && settings.trimAllInputs) {
      $.Cms.trimAllInputs($form);
    }
  
    if ($form && settings.validate) {
      var parsley = $form.parsley();
      if (parsley) {
        if (!parsley.validate()) {
          return false;
        }
  
        parsley.reset();
      }
    }
  
    settings.beforeValidate && settings.beforeValidate($form);
  
    var url = settings.url || settings.action || $form.attr('action');
    if (settings.appendToUrl) {
      url += settings.appendToUrl;
    }
  
    var method = settings.type || $form.attr('method');
    var methodLc = method.toLowerCase();
    var data = '';
    if (settings.data) {
      data = settings.data;
    } else if ($form && (methodLc === 'post' || methodLc === 'put')) {
      data = $form.serialize();
    }
  
    var ajaxParams = {
      url: url,
      type: method,
      data: data,
      success: function (data, status) {
        if ($form && settings.formReset) {
          $form[0].reset();
        }
  
        if (settings.showSuccessMessage) {
          $.Cms.notif({
            message: settings.successMessage,
            type: 'success',
          });
        }
  
        settings.onSuccess && settings.onSuccess(data, status);
      },
  
      error: function (result, status, error) {
        if (settings.showSuccessMessage) {
          $.Cms.notif({
            message: settings.errorMessage,
            type: 'error',
          });
        }
  
        settings.onError && settings.onError(result, status, error);
      },
    };
  
    $.ajax(ajaxParams);
  };
  
  // jscs:disable maximumLineLength
  
  /**
   * @description Plugin to display Yes/No Switch confirmation in a table.
   * @property {string}  tableElement      - Table ID.
   * @property {string}  containerElement  - Container class.
   * @property {function($tr)}  save  - Function performed during save confirmation.
   * @property {function($tr)}  delete  - Function performed during delete confirmation.
   * @property {function($tr)}  confirmation       - Default function executed upon confirmation.
   */
  Cms.prototype.initTabSwitchYesNo = function (params) {
    var defaults = {
      tableElement: '#data-table',
      containerElement: '.op-table-slide-container',
    };
    var settings = $.extend({}, defaults, params);
  
    function changeClass($container, type) {
      if (type) {
        $container.removeClass('trash save');
        $container.addClass(type);
      }
    }
  
    $(settings.tableElement + ' tbody').on('click', '.js-move', function () {
      var $this = $(this);
      var $container = $this.closest(settings.containerElement);
      $container.toggleClass('js-switched');
      changeClass($container, $this.data('type'));
    });
  
    $(settings.tableElement + ' tbody').on('click', '.js-op-ajax', function () {
      var $this = $(this);
      var $container = $this.closest(settings.containerElement);
      var $parent = $this.parent();
      var $tr = $parent.closest('tr');
      if ($parent.hasClass('trash')) {
        settings.onDelete && settings.onDelete($tr);
      } else if ($parent.hasClass('save')) {
        settings.onSave && settings.onSave($tr);
      } else {
        settings.onConfirmation && settings.onConfirmation($tr);
      }
  
      $container.toggleClass('js-switched');
      changeClass($container, $this.data('type'));
    });
  };
  
  /**
   * @description Unbing the plugin event
   * @property {string}  tableElement      - Table ID.
   * @property {string}  containerElement  - Container class.
   */
  Cms.prototype.destroyTabSwitchYesNo = function (params) {
    var defaults = {
      tableElement: '#data-table',
      containerElement: '.op-table-slide-container',
    };
  
    var settings = $.extend({}, defaults, params);
    $(settings.tableElement + ' tbody').off('click');
    $(settings.tableElement + ' tbody').off('click');
  };
  
  Cms.prototype.tabSwitchSimpleTpl = function () {
    return '<div class="op-table-slider-wrapper"><div class="op-table-slide-container"><div class="op-table-slide-item"><button id="js-show-controls" type="button" class="btn btn-default button js-move"><i class="fa fa-trash-o"> </i></button></div><div class="op-table-slide-item"><button type="button" class="btn btn-danger js-move operation"><i class="fa fa-times"></i></button><button type="button" class="btn btn-success js-op-ajax operation"> <i class="fa fa-check"></i></button></div></div></div>';
  };
  
  Cms.prototype.tabSwitchDoubleTpl = function () {
    return '<div class="op-table-slider-wrapper"><div class="op-table-slide-container"><div class="op-table-slide-item"><button type="button" data-type="trash" class="btn btn-default button js-move"><i class="fa fa-trash"></i></button><button type="button" data-type="save" class="btn btn-default button js-move"><i class="fa fa-save"> </i></button></div><div class="op-table-slide-item save"><button type="button" class="btn btn-danger js-move operation"><i class="fa fa-times"></i></button><button type="button" class="btn btn-success js-op-ajax operation"> <i class="fa fa-save"></i></button></div><div class="op-table-slide-item trash"><button type="button" class="btn btn-danger js-move operation"><i class="fa fa-times"></i></button><button type="button" class="btn btn-success js-op-ajax operation"> <i class="fa fa-trash"></i></button></div></div></div>';
  };
  
  Cms.prototype.notif = function (params) {
    var defaults = {
      style: 'bar',
      position: 'top',
    };
    var settings = $.extend({}, defaults, params);
  
    if (!settings.message) {
      if (settings.type === 'error') {
        settings.message = this.genericErrorMessage;
      } else if (settings.type === 'success') {
        settings.message = this.genericSuccessMessage;
      }
    }
  
    this.$body.pgNotification(settings).show();
  };
  
  /**
   * @description Function to help to init the datables Jquery Plugin.
   */
  
  Cms.prototype.dataTableRenderCheckboxDisabled = function (data, type, full) {
     var isChecked = data === true ? 'checked' : '';
     return '<input disabled type="checkbox" class="checkbox" ' + isChecked + ' />';
   };
  
  Cms.prototype.dataTableRenderCheckbox = function (data, type, full) {
    var isChecked = data === true ? 'checked' : '';
    return '<input type="checkbox" class="checkbox" ' + isChecked + ' />';
  };
  
  Cms.prototype.dataTableRenderBoolean = function (data, type, full) {
     // text-success
     var css;
     if (data === true) {
       css = ' text-success"';
     } else {
       css = '" style="opacity:0.15"';
     }
  
     return '<i class="fa fa-check-circle' + css + '></i>';
   };
  
  Cms.prototype.initDataTableWithSearch = function (params) {
    var defaults = {
      hasSearchInput: true,
      iDisplayLength: 5,
      tableElement: '#data-table',
      searchElement: '#search-table',
    };
  
    var options = $.extend({}, defaults, params);
  
    var columnDefsNoSort = {
      targets: 'no-sort',
      orderable: false,
    };
  
    var columnDefs = [columnDefsNoSort];
    if (options.columnDefs) {
      options.columnDefs.unshift(columnDefsNoSort);
      columnDefs = options.columnDefs;
    }
  
    var settings = {
      sDom: '<\'table-responsive\'t><\'row\'<p i>>',
      order: [],
      autoWidth: false,
      destroy: true,
      scrollCollapse: true,
      iDisplayLength: options.iDisplayLength, //rows to display on a single page (pagination)
      columnDefs: columnDefs,
    };
  
    var locale = options.locale || this.locale;
    if (locale) {
      settings.language = {
        url: '/resources/admin/libs/dataTables/i18n/' +
             languagesNativeName[locale].capitalizeFirstLetter() + '.json',
      };
    }
  
    if (options.ajax) {
      settings.ajax = options.ajax;
  
      if (options.columns) {
        if (options.appendOperationColumns === 'simple') {
          options.columns.push({
            data: null,
            defaultContent: this.tabSwitchSimpleTpl(),
          });
        } else if (options.appendOperationColumns === 'double') {
          options.columns.push({
            data: null,
            defaultContent: this.tabSwitchDoubleTpl(),
          });
        }
  
        settings.columns = options.columns;
      }
    }
  
    var $table;
    if (options.tableJqueryElement) {
      $table = options.tableJqueryElement;
    } else {
      $table = $(options.tableElement);
    }
  
    $table.dataTable(settings);
  
    if (options.hasSearchInput) {
      $(options.searchElement).keyup(function () {
        $table.fnFilter($(this).val());
      });
    }
  
    return $table;
  };
  

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
  

  $.Cms = new Cms();
})(jQuery);
