$(document).ready(function(){
    hookWindowLocation();

    //$('.search-query').bind('propertychange keyup input paste',function(){
    //    document.title = $(this).val();
    //});
});

function hookWindowLocation(){

    // Bind an event to window.onhashchange that, when the hash changes, gets the
    // hash and adds the class "selected" to any matching nav link.
    $(window).hashchange( function(){
        var hash = location.hash;

        // Set the page title based on the hash.
        //document.title = 'The hash is ' + ( hash.replace( /^#/, '' ) || 'blank' ) + '.';

        var hashsplit = ( hash.replace( /^#/, '' ) || 'blank' ).split("/");

        if(hashsplit[0] === 'series') {
            //1: serie id
            //2: season
            if( hashsplit[1] != undefined && hashsplit[2] == undefined) {
                getSeasonsOfSerie(hashsplit[1]);
            }
            else if(hashsplit[1] != undefined && hashsplit[2] != undefined) {
                getEpisodesOfSerieAndSeason(hashsplit[1],hashsplit[2]);
            }else{
                getSeries();
            }
        }
        if(hashsplit[0] === 'episode') {
            getEpisodeById(hashsplit[1]);
        }
        if(hashsplit[0] === 'movies') {

            if( hashsplit[1] != undefined ) {
                getMovieById(hashsplit[1]);
            }else{
                getMovies();
            }

        }
        if(hashsplit[0] === 'blank') {
            $("#content").empty();
            $("#title > h1").replaceWith('<h1>Welcome</h1>');
        }

        // Iterate over all nav links, setting the "selected" class as-appropriate.
        $('#nav li a').each(function(){
            var that = $(this);
            that[ that.attr( 'href' ) === hash ? 'addClass' : 'removeClass' ]( 'active' );
        });
    })

    // Since the event is only triggered when the hash changes, we need to trigger
    // the event now, to handle the hash the page may have loaded with.
    $(window).hashchange();

}

function getSeries(){

    $("#content").empty();
    $("#content").append('<div id="latestepisodes"><h2>Recently added episodes</h2></div>');
    $("#content").append('<div id="series"><h2>All series</h2></div>');
    $("#title > h1").replaceWith('<h1>Series</h1>');

    $.getJSON('api/series', function(data) {

        var content = '<ul class="thumbnails">';

        $.each(data, function(i, item) {

            content += '<li class="span4"> \
                <div class="thumbnail"> \
                    <h4>' + item.name + '</h4> \
                    <a href="#series/' + item.id + '"><img width="200" src="'+ getPoster(item) +'" alt="placeholder"></a> \
                </div> \
                </li>';

        });
        content += '</ul>';
        $("#series").append(content);

    });

    $.getJSON('api/episodes/latest?max=3', function(data) {

        var content = '<ul class="thumbnails">';

        $.each(data, function(i, item) {

            content += '<li class="span4"> \
                <div class="thumbnail"> \
                    <h4>' + item.episode + ': ' + item.name + '</h4> \
                    <a href="#episode/' + item.id + '"><img width="200" src="'+ getPoster(item) +'" alt="placeholder"></a> \
                </div> \
                </li>';

        });
        content += '</ul>';
        $("#latestepisodes").append(content);

    });
    
}

function getSeasonsOfSerie(serie){
    $("#content").empty();
    $("#title > h1").replaceWith('<h1>Seasons</h1>');

    $.getJSON('api/series/' + serie + '/seasons', function(data) {
        $.each(data, function(i, item) {
            $("#content").append('<a href="#series/' + serie + '/' + item + '" class="btn">Season ' + item + '</a> ');
        });
    });
}

function getEpisodesOfSerieAndSeason(serie, season){
    $("#content").empty();
    $("#title > h1").replaceWith('<h1>Season ' + season + '</h1>');

    $.getJSON('api/series/' + serie + '/seasons/' + season + '/episodes', function(data) {
        var content = '<ul class="thumbnails">';

        $.each(data, function(i, item) {

            content += '<li class="span4"> \
                <div class="thumbnail"> \
                    <h4>' + item.episode + ': ' + item.name + '</h4> \
                    <a href="#episode/' + item.id + '"><img width="200" src="'+ getPoster(item) +'" alt="placeholder"></a> \
                </div> \
                </li>';

        });
        content += '</ul>';
        $("#content").append(content);
    });


}


function getEpisodeById(episodeId) {

    $("#content").empty();
    $("#content").append('<div id="episodes"></div>');
    $("#content").append('<div id="devices"></div>');
    
    $.getJSON('api/episodes/' + episodeId, function(item) {

        $("#title > h1").replaceWith('<h1>'+ item.name +'</h1>');

        var content = '<img width="200" src="'+ getPoster(item) +'" alt="placeholder">';
        content += '<br><br>';
        content += item.description;
        $("#episodes").append(content);

    });

    $.getJSON('api/devices', function(data) {

        $("#devices").append('<hr><b>Play on:</b><br><br>');

        $.each(data, function(i, device) {
            var tmpButton = $('<button class="btn">'+device.name+'</button>').click(function () {
                playVideo(episodeId,device.id );
            });
            
            $("#devices").append(tmpButton);

        });
      
    });
}

function getMovies(){

    $("#content").empty();
    $("#title > h1").replaceWith('<h1>Movies</h1>');

    $.getJSON('api/movies', function(data) {

        var content = '<ul class="thumbnails">';

        $.each(data, function(i, item) {

            content += '<li class="span4"> \
                <div class="thumbnail"> \
                    <h4>' + item.name + '</h4> \
                    <a href="#movies/' + item.id + '"><img width="200" src="'+ getPoster(item) +'" alt="placeholder"></a> \
                </div> \
                </li>';

        });

        content += '</ul>';
        $("#content").append(content);

    });

}

function getMovieById(id){
    
    $("#content").empty();
    $("#content").append('<div id="movie"></div>');
    $("#content").append('<div id="devices"></div>');

    $.getJSON('api/movies/' + id, function(item) {

        $("#title > h1").replaceWith('<h1>'+ item.name +'</h1>');

        var content = '<img width="200" src="'+ getPoster(item) +'" alt="placeholder">';
        content += '<br><br>';
        content += item.description;
        $("#movie").append(content);

    });

    $.getJSON('api/devices', function(data) {

        $("#devices").append('<hr><b>Play on:</b><br><br>');

        $.each(data, function(i, device) {
            var tmpButton = $('<button class="btn">'+device.name+'</button>').click(function () {
                playVideo(id,device.id );
            });

            $("#devices").append(tmpButton);

        });

    });
}

function getPoster(item) {
    var poster;
    if( item.resources == undefined || item.resources.poster == undefined) {
        poster = 'http://placehold.it/200x300';
    }
    else {
        poster = '/resources' + item.resources.poster.path;
    }
    return poster;
}

function playVideo(id,device) {
    $.post('/api/devices/'+ device + '/play?video=' + id);
}

function startIndex() {
    $.post('/api/indexer/index');
}
