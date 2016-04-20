$(function () {

  var $wrapper = $('#inputsAddContainer');
  var $addButton = $('#addBtnInputs');
  var $inputsEditContainer = $('#inputsEditContainer');
  var $modal = $('#modalCreateNewTemplate');
  var inputCounter = 1;
  var removeBtnHtml = '<span class="removeField" id="removeButton"><i class="fa fa-minus"></i></span>';
  var data = [];
  var $select2 = $('.fieldsetSelectInTemplate');
  for (var i = 0; i < window.FIELDSETS.length; i++) {
    data.push({ id: window.FIELDSETS[i].name, text: window.FIELDSETS[i].name });
  }

  var $tabs = $('#inputsTab');
  /**
   * [addNewInput add a new input to the screen]
   * @param {[Object]} input Representation of an input of the fiedlset
   */
  function addNewInput(fieldsetName) {
    var fieldset;
    for (var i = 0; i < window.FIELDSETS.length; i++) {
      if (window.FIELDSETS[i].name === fieldsetName) {
        fieldset = window.FIELDSETS[i];
      }
    }

    fieldset.id = inputCounter;
    inputCounter++;

    for (i = 0; i < fieldset.inputs.length; i++) {
      fieldset.inputs[i].id = fieldset.id + (i + 1);
    }

    $li = $('<li class="input-li" data-div="' +
      fieldset.id + '">' +
      fieldset.name +
      removeBtnHtml + '</li>');
    $li.animateCss('flipInX');
    $wrapper.append($li);

    fieldset.renderedLi = inputTab(fieldset);
    fieldset.renderedInputs = contentTab(fieldset);

    var template = document.getElementById('templateView').innerHTML;
    var html = Mustache.render(template, fieldset);
    $inputsEditContainer.append(html);
  }

  function inputTab(fieldset) {

    var renderInput = '<li class="liTab{{cssClass}}" data-div="{{id}}"><a data-toggle="tab" href="#{{id}}"><span>{{name}}</span></a></li>';
    var result = '';
    for (var i = 0, s = fieldset.inputs.length; i < s; i++) {
      var cssClass = '';
      if (i === 0) {
        fieldset.inputs[i].cssClass = ' active';
      }
      result += Mustache.render(renderInput, fieldset.inputs[i]);
    }

    return result;
  }

  function contentTab(fieldset) {
    var template = document.getElementById('inputsTemplate').innerHTML;
    var result = '';
    for (var i = 0, s = fieldset.inputs.length; i < s; i++) {
      var cssClass = '';
      if (i === 0) {
        fieldset.inputs[i].cssClass = ' active';
      }
      console.log(fieldset.inputs[i]);
      result += Mustache.render(template, fieldset.inputs[i]);
    }
    return result;
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

  $('#btnCreateTemplate').click(function () {
    $modal.modal('show');
    inputCounter = 1;
  });

  $modal.on('hide.bs.modal', function () {
    $wrapper.empty();
    $inputsEditContainer.empty();
    document.getElementById('templateForm').reset();
  });

  $('#btnModalEditTemplate').click(function () {
    inputCounter = 1;
    var id = $(this).closest('tr').data('id');
    var ajax = {
      name: 'nom1',
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
    };

    $('#fieldsetName').val(ajax.name);

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
  });

  $select2.select2({
    data: data,
    placeholder: 'select a fieldset',
  });

  $('.container-template').on('click', '#addBtnInputs', function () {
    var data = $select2.val();
    if (data.trim() !== '') {
      addNewInput(data);
    }
    else{
      console.log($select2.val());
    }

    //addNewInput($select2.val());
  });

});
