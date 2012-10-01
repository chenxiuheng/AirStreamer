$(document).ready(function(){
    getSeries();
});

function getSeries(){

    $.getJSON('api/?command=series', function(data) {


        $.each(data, function(i, item) {

            var poster;
            if( item.resources == undefined) {
                poster = 'http://placehold.it/300x200';
            }
            else {
                poster = '/resources' + item.resources.poster.path;
            }


            $(".thumbnails").append('<li class="span4"> \
                <div class="thumbnail"> \
                    <h4>' + item.name + '</h4> \
                    <img width="200" src="'+ poster +'" alt="placeholder"> \
                </div> \
                </li>');

        });

    });
    
}