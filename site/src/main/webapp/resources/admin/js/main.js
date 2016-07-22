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

  // remove default behaviour
  $(document).on('drop dragover', function (e) {
    e.preventDefault();
  });

  $.fn.extend({
    animate2Css: function (animationName, callback) {
      var animationEnd = 'webkitAnimationEnd mozAnimationEnd MSAnimationEnd ' +
                         'oanimationend animationend';
      $(this).addClass('animated ' + animationName).one(animationEnd, function () {
        $(this).removeClass('animated ' + animationName);
        callback && callback();
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
  
  Cms.prototype.formatFileSize = function formatFileSize(bytes) {
    if (typeof bytes !== 'number') {
      return '';
    }
  
    if (bytes >= 1000000000) {
      return (bytes / 1000000000).toFixed(2) + ' GB';
    }
  
    if (bytes >= 1000000) {
      return (bytes / 1000000).toFixed(2) + ' MB';
    }
  
    return (bytes / 1000).toFixed(2) + ' KB';
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
  
  Cms.prototype.slugify = function slugify(text)
  {
    return text.toString().toLowerCase()
      .replace(/\s+/g, '-')           // Replace spaces with -
      .replace(/[^\w\-]+/g, '')       // Remove all non-word chars
      .replace(/\-\-+/g, '-')         // Replace multiple - with single -
      .replace(/^-+/, '')             // Trim - from start of text
      .replace(/-+$/, '');            // Trim - from end of text
  };
  
  Cms.prototype.copyToClipboard = function copyToClipboard(elem) {
    // create hidden text element, if it doesn't already exist
    var targetId = '_hiddenCopyText_';
    var isInput = elem.tagName === 'INPUT' || elem.tagName === 'TEXTAREA';
    var origSelectionStart;
    var origSelectionEnd;
    if (isInput) {
      // can just use the original source element for the selection and copy
      target = elem;
      origSelectionStart = elem.selectionStart;
      origSelectionEnd = elem.selectionEnd;
    } else {
      // must use a temporary form element for the selection and copy
      target = document.getElementById(targetId);
      if (!target) {
        var target = document.createElement('textarea');
        target.style.position = 'absolute';
        target.style.left = '-9999px';
        target.style.top = '0';
        target.id = targetId;
        document.body.appendChild(target);
      }
  
      target.textContent = elem.textContent;
    }
  
    // prevent scroll
    var x = window.scrollX;
    var y = window.scrollY;
  
    // select the content
    var currentFocus = document.activeElement;
  
    target.focus();
    target.setSelectionRange(0, target.value.length);
    window.scrollTo(x, y);
  
    // copy the selection
    var succeed;
    try { succeed = document.execCommand('copy'); } catch (e) { succeed = false; }
  
    // restore original focus
    if (currentFocus && typeof currentFocus.focus === 'function') {
      currentFocus.focus();
    }
  
    if (isInput) {
      // restore prior selection
      elem.setSelectionRange(origSelectionStart, origSelectionEnd);
    } else {
      // clear temporary content
      target.textContent = '';
    }
  
    return succeed;
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
      success: function (data, status, response) {
        if ($form && settings.formReset) {
          $form[0].reset();
        }
  
        if (settings.showSuccessMessage) {
          $.Cms.notif({
            message: settings.successMessage,
            type: 'success',
          });
        }
  
        settings.onSuccess && settings.onSuccess(data, status, response);
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
      iDisplayLength: 10,
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
  
  Cms.prototype.initFileUpload = function initFileUpload(params) {
  
    if (!params.$container) {
      alert('container element must be provided!');
      return;
    }
  
    var defaults = {
      // 50 MB
      maxSize: 50 * 1000  * 1000,
      formData: {},
      acceptFileTypes: false,
    };
  
    var options = $.extend({}, defaults, params);
    var $container = options.$container;
    var $ul = $container.find('ul').first();
    var $dropZone = $container.find('.dropzone').first();
    var $inputFile = $container.find('.fileupload').first();
    var $btnUpload = $dropZone.find('.btn-browse').first();
  
    $btnUpload.on('click', function () {
      $inputFile.click();
    });
  
    var jqMultiUploadConf = {
      dataType: 'json',
      formData: options.formData,
  
      // This function is called when a file is added to the queue;
      // either via the browse button, or via drag/drop:
      add: function (e, data) {
  
        // validation
        var file = data.originalFiles[0];
        var fileType = file.name.split('.').pop();
        if (options.maxSize) {
          if (file.size > options.maxSize) {
            $.Cms.notif({
              message: 'Max size is ' + $.Cms.formatFileSize(options.maxSize),
              type: 'error',
            });
            return;
          }
        }
  
        if (options.acceptFileTypes) {
          if (options.acceptFileTypes.test(fileType)) {
            $.Cms.notif({
              message: 'The file type is not allowed',
              type: 'error',
            });
            return;
          }
        }
  
        var tpl = $('<li class="working"><input type="text" value="0" data-width="18" data-height="18"'+
           ' data-fgColor="#0788a5" data-readOnly="1" data-bgColor="#3e4043" /><p></p><span></span></li>');
  
        // Append the file name and file size
        tpl.find('p').text(data.files[0].name)
                    .append('<i>' + $.Cms.formatFileSize(data.files[0].size) + '</i>');
  
        // Add the HTML to the UL element
        data.context = tpl.appendTo($ul);
  
        // Initialize the knob plugin
        tpl.find('input').knob();
  
        // Listen for clicks on the cancel icon
        tpl.find('span').click(function () {
          if (tpl.hasClass('working')) {
            jqXHR.abort();
          }
  
          tpl.fadeOut(function () {
            tpl.remove();
          });
        });
  
        // Automatically upload the file once it is added to the queue
        var jqXHR = data.submit();
      },
  
      progress: function (e, data) {
        // Calculate the completion percentage of the upload
        var progress = parseInt(data.loaded / data.total * 100, 10);
  
        // Update the hidden input field and trigger a change
        // so that the jQuery knob plugin knows to update the dial
        data.context.find('input').val(progress).change();
  
        if (progress == 100) {
          data.context.removeClass('working');
        }
      },
  
      fail: function (e, data) {
        // Something has gone wrong!
        data.context.addClass('error');
      },
  
      stop: function () {
        options.onStop && options.onStop();
      },
  
      /*progressall: function (e, data) {
        var progress = parseInt(data.loaded / data.total * 100, 10);
        if (progress == 100) {
          console.log('finish');
        }
      },*/
  
      // jscs:disable requireDollarBeforejQueryAssignment
      // This element will accept file drag/drop uploading
      dropZone: $dropZone,
  
      // jscs:enable requireDollarBeforejQueryAssignment
    };
  
    var dragEvent = function (e) {
      var $target = $(e.target);
      var $drop = $target.closest('.dropzone');
      var timeout = window.dropZoneTimeout;
      if (!timeout) {
        $drop.addClass('in');
      } else {
        clearTimeout(timeout);
      }
  
      var found = false;
      var node = e.target;
      do {
        if (node === $drop[0]) {
          found = true;
          break;
        }
  
        node = node.parentNode;
      } while (node !== null);
      if (found) {
        $drop.addClass('hover');
      } else {
        $drop.removeClass('hover');
      }
  
      window.dropZoneTimeout = setTimeout(function () {
        window.dropZoneTimeout = null;
        $drop.removeClass('in hover');
      }, 100);
    };
  
    $(document).bind('dragover', dragEvent);
    $($inputFile).fileupload(jqMultiUploadConf);
  };
  

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
  

  $.Cms = new Cms();
})(jQuery);
