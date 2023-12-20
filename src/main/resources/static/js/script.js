console.log("hello js !")

const toggleSidebar= () => {
    if($('.sidebar').is(":visible")){
        //we have show SIDE-BAR
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");


    }else{
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
    }
}