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
