$(function () {

  var $wrapper = $('#inputsAddContainer');
  var $addButton = $('#addBtnInputs');
  var $inputsEditContainer = $('#inputsEditContainer');
  var $modal = $('#modalCreateNewFieldset');
  var inputCounter = 1;
  var removeBtnHtml = '<span class="removeField" id="removeButton"><i class="fa fa-minus"></i></span>';

  /**
   * [addNewInput add a new input to the screen]
   * @param {[Object]} input Representation of an input of the fiedlset
   */
  function addNewInput(input) {
    $li = $('<li class="input-li" data-div="' +
      input.id + '">' +
      input.name +
      removeBtnHtml + '</li>');
    $li.animateCss('flipInX');
    $wrapper.append($li);

    var template = document.getElementById('inputsTemplate').innerHTML;
    var html = Mustache.render(template, input);
    $inputsEditContainer.append(html);
  }

/**
 * [getInput description]
 * @param  {[Number]} id [description]
 * @return {[Number]}    [description]
 */
  function getInput(id) {
    var input = {};
    $('#input-' + id).find('input').each(function () {
      input[this.name] = this.value;
    });

    return input;
  }

  $('#btnCreateFieldset').click(function () {
    $modal.modal('show');
    inputCounter = 1;
  });

  $modal.on('hide.bs.modal', function () {
    $wrapper.empty();
    $inputsEditContainer.empty();
    document.getElementById('fieldsetForm').reset();
  });

  $('#btnModalEditFieldset').click(function () {
    inputCounter = 1;
    var id = $(this).closest('tr').data('id');
    var ajax = {
      name: 'nom du fieldset',
      description: 'description du fieldset',
      inputs: [
        {
          name: 'nom de l\'input',
          validation: 'validation',
          array: true,
        }, {
          name: "nom de l'input2",
          validation: 'validation2',
          array: false,
        },
      ],
      blockName: 'nom du block',
      blockContent: 'nom du content',
    };

    $('#fieldsetName').val(ajax.name);
    $('#fieldsetDescription').val(ajax.description);
    $('#blockName').val(ajax.blockName);
    $('#blockContent').val(ajax.blockContent);

    var inputs = JSON.parse(JSON.stringify(ajax.inputs));

    for (var i = 0, size = inputs.length; i < size; i++) {
      var input = inputs[i];
      input.id = (i + 1);
      addNewInput(input);
    }

    inputCounter = i;
    $modal.modal('show');
  });

  $wrapper.sortable({
    revert: true,
  });

  $addButton.on('click', function () {
    inputCounter++;
    var name = 'input ' + inputCounter;
    var input = {
      id: inputCounter,
      name: name,
    };

    addNewInput(input);
  });

/**
 * [on description]
 * @return {[type]}                  [description]
 */
  $inputsEditContainer.on('input', '.js-input-name', function () {
    var $input = $(this);
    var id = $input.data('id');
    var $li = $wrapper.find('[data-div="' + id + '"]').first();
    $li.html(this.value).append(removeBtnHtml);

  });

  $wrapper.on('click', '.input-li', function () {
    var id = $(this).data('div');
    $wrapper.children().removeClass('activeInput');
    $(this).addClass('activeInput');
    $inputsEditContainer.children().hide();
    $inputsEditContainer.find('#input-' + id).first().show();
  });

  $wrapper.on('click', '.removeField', function () {
    var $li = $(this).parent('li');
    var divId = $li.data('div');
    $li.animate2Css('flipOutX', function () {
      $li.remove();
    });

    $inputsEditContainer.find('#input-' + divId).remove();
  });

  $('#submit').on('click', function () {
    var arrayInput = [];
    var test = $wrapper.children();
    $wrapper.children().each(function () {
      var id = $(this).data('div');
      arrayInput.push(getInput(id));
    });

    var jsonResult = JSON.stringify(arrayInput);
    console.log(jsonResult);
  });

});
