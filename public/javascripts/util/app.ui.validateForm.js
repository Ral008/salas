/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
(function( $ ) {
    var err=0;
    var form = null;
    var id = '';
    var inputs = null;
    var input = null;
    var label = null;
    $.fn.cleanForm  = function(){
    	this.each(function(){
    		form = $(this);
        	inputs = form.find('.required-form');
        	inputs.parent('.control-group').removeClass('error').find('.help-inline').addClass('hide');
    	});
    }
    $.fn.validateForm=function(){
    	err = 0;
    	this.each(function(){
    		form = $(this);
        	inputs = form.find('.required-form');
        	inputs.parent('.control-group').removeClass('error').find('.help-inline').addClass('hide');
        	$.each(inputs,function(key,obj){
        		label = $(obj);
        		id=label.attr('for');
        		input = $("#"+id);
        		if(input.is('input')){
        			if(input.attr('type')=='text'){
        				if($.trim(input.val())=='' || input.val() == 0){
        					label.parent(".control-group").addClass("error").find(".help-inline").removeClass("hide")
        					err++;
        				}
        			}else if(input.attr('type')=='checkbox' && input.attr('checked') == false){
        				label.parent(".control-group").addClass("error").find(".help-inline").removeClass("hide")
    					err++;
        			}
        		}else if(input.is('textarea')){
        			if($.trim(input.val())=='' || input.val() == 0){
    					label.parent(".control-group").addClass("error").find(".help-inline").removeClass("hide")
    					err++;
    				}
        		}else{
        			if((input.attr('type')=='select-one') && ($(input).val()=='00')){
        				label.parent(".control-group").addClass("error").find(".help-inline").removeClass("hide")
    					err++;
                    }else{
                        if($('#'+label.attr('for')).length == 0){
                            label = $(obj);
                            if($('input[name='+label.attr('for')+']:checked').length == 0){
                            	label.parent(".control-group").addClass("error").find(".help-inline").removeClass("hide")
            					err++;
                            }
                        }
                    }
        		}
        		
        	})
    	});
    	return err;
    };
}( jQuery ) );