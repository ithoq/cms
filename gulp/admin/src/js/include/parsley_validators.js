window.Parsley.addValidator('peeble', {

    validateString: function(value) {
    	var success = false;
    	if(value.trim() === '') return true;
        $.ajax({
            url: '/admin/validation/peeble',
            data: {
                peebleData: value
            },
            type: 'post',
            async: false,
            dataType: 'json',
            success: function(response) {
                if(!response.error){
                	success = true;
                }  else{
                	$('#peebleConsole').html(response.error).parent().show(500);
                }
            }
        });
        return success;
    },
    messages: {
        en: 'Template parsing error!',
        fr: "Erreur de parsing du template"
    }
});

Parsley.options.excluded =  'input[type=button], input[type=submit], input[type=reset]';