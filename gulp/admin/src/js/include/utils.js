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
