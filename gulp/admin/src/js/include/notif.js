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
