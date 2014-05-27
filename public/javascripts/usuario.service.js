var User = {
    init : function(){
        $(".btn-danger").on("click",User.delete_);
    },
    delete_:function(e){
        e.preventDefault();
        if(confirm("Desea Eliminar el usuario ? ")){
            location.href=this.href;
        }
    }
}
$(document).on(User.init());