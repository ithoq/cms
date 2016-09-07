Cms.prototype.ajax = function (params) {

  var defaults = {
    successMessage: this.genericSuccessMessage,
    errorMessage: this.genericErrorMessage,
    showSuccessMessage: true,
    showErrorMessage: true,
    formReset: true,
    contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
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
    contentType: settings.contentType,
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
