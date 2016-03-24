// Tree event
$('#searchTree').keyup(function (e) {
  var n;
  var opts = {
    autoExpand: $('#autoExpand').is(':checked'),
    leavesOnly: $('#leavesOnly').is(':checked'),
  };
  var match = $(this).val();

  if (e && e.which === $.ui.keyCode.ESCAPE || $.trim(match) === '') {
    $('#btnResetSearch').click();
    return;
  }

  // Pass a string to perform case insensitive matching
  n = treeCache.filterNodes(match, opts);

  $('#btnResetSearch').attr('disabled', false);
  var result = ' resultat';
  if (n > 1) {
    result += 's';
  }

  $('#matches').text(n + result);
}).focus();

$('#btnResetSearch').click(function (e) {
  $('input[name=search]').val('');
  $('#matches').text('');
  treeCache.clearFilter();
}).attr('disabled', true);
