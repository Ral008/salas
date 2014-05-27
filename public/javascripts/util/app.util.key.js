/*
 * jQuery UI autoenter 1.8
 *
 * Copyright (c) 2010 TOHUMA (http://jqueryui.com/about)
 * Dual licensed under the MIT (MIT-LICENSE.txt)
 * and GPL (GPL-LICENSE.txt) licenses.
 *
 * http://docs.jquery.com/UI/autoenter
 *
 */
(function( $ ) {
  $.fn.autoenter = function(){
    form = $(this);
    inputs = form.find('input[tabindex]');
    inputs.each(function(){
      input = $(this);
      $(this).attr('tabindex');
      input.bind('keypress',function(e){
            index = $(this).attr('tabindex');
            if(e.keyCode==13){
              index++;
              form.find('[tabindex='+index+']').focus();
              $(this).unbind("keypress",e);
            }
      });
    });
    selects = form.find('select');
    selects.each(function(){
      select = $(this);
      select.bind('change',function(event){
        index = $(this).attr('tabindex');
        index++;
        form.find('[tabindex='+index+']').focus();
        $(this).unbind("change",event)
      }).bind("keypress",function(e){
        if(e.keyCode==13){
            index = $(this).attr('tabindex');
            index++;
            form.find('[tabindex='+index+']').focus();
            $(this).unbind("change",e)
        }
      });
    });
  };
}( jQuery ) );

/*

$.fn.focusNextInputField = function() {
	return this.each(function() {
		var fields = $(this).parents('form:eq(0),body').find('button,input,textarea,select');
		var index = fields.index( this );
		if ( index > -1 && ( index + 1 ) < fields.length ) {
		fields.eq( index + 1 ).focus();
		}
		return false;
	});
};	
*/
/*
  $.fn.autoenter = function(){
    form = $(this);
    form.find('input,select').each(function(){
      $(this).bind('keypress',function(e){
        if(e.keyCode==13){
          form.find('[tabindex='+(this.tabIndex + 1)+']').focus();
        }
      });
    });
  };


 */