/**
 * Created by SISI on 9/8/2017.
 */


function tabs( unselectedEl , newTabRequest ){
    var elem = null;
    var active = null;
    var self = this;
    newTabs = function () {
        if(typeof unselectedEl == "object" ){
            var ptab = hoss.dira(unselectedEl,".nav-tabs-custom","parentNode");
            if( !ptab ) return false;

            var newLi = document.createElement("li");

            var newA = document.createElement("a");
            newA.setAttribute("data-toggle","tab");
            var newId = "NewIdea"+ parseInt(Math.random() * 100000000000);
            newA.href = "#"+newId;
            newA.textContent = unselectedEl.textContent;
            newLi.appendChild(newA);

            var nav = ptab.querySelector(".nav-tabs");
            nav.appendChild(newLi);

            var contPlace = ptab.querySelector(".tab-content");

            var newDivision = document.createElement("div");
            newDivision.id = newId;
            hoss.Cladd(newDivision,"heighted");
            hoss.Cladd(newDivision,"loading");

            contPlace.appendChild(newDivision);
            newA.onclick = function (e) {
                e.preventDefault();
                self.hideAndShow(nav.querySelectorAll("a"),this,ptab);
            }
            unselectedEl.setAttribute("targeted",newId);
        }
    }
    init = function( unSel ) {

        if( newTabRequest ){
            this.newTab();
            return false;
        }
        elem = I( unSel );
        // check tab existence
        if (!elem) return;
        this.basicLayout( elem );
    };
    basicLayout = function(){
        //hoss.Cladd(elem,"nav-tabs-custom");

        this.header( elem );
    };
    hideAndShow = function( all , activeEl , pTab ){
        //var nUse = ( pTab ) ? pTab : elem;
        for(var i = 0; i < all.length ; i++ ) {
            var bdy = elem.querySelector(all[i].getAttribute("href"));
            hide(bdy);
        }
        var actBdy = elem.querySelector(activeEl.getAttribute("href"));
        show( actBdy );
        //
        //hoss.Clremove(all.parentNode,"active");
        //hoss.Cladd(activeEl.parentNode,"active");

        var link = activeEl.getAttribute("data");
        var type = activeEl.getAttribute("type");
        var colx = activeEl.getAttribute("col");
        window.location.hash = actBdy.id;
        window.scrollTo(0,0);
        if( type != "local" ){
            var textOg = activeEl.textContent;
            var dataP = actBdy.querySelector(".server-deposit");
            if( dataP ) hoss.ajax({page:link,o:dataP},1,2,function(res){
                hoss.Clremove(dataP,"loading");
            });
        }
    };
    controlIcons = function( li ){
        var check = li.querySelector(".icons-box");
        if( !check ){
            check = document.createElement("div");
            check.className = "icons-box";
            var but = document.createElement("button");
            but.className = "icon-search";
            but.onclick = function(){
                var a = li.firstChild;
                a.click();
                var div = elem.querySelector(a.getAttribute("href"));
                var search = div.querySelector(".search-place");
                hoss.Clremove(search,"intop");
                hoss.Cladd(search,"ontop");
                search.firstChild.focus();
            }

            var but2 = document.createElement("button");
            but2.className = "icon-remove-circle";
            but2.title = "Multiple Delete";
            but2.onclick = function(){li.firstChild.click()}
            check.appendChild( but );
            check.appendChild( but2 );

            li.appendChild( check );
        }
    };
    dataPlace = function( bdy  , titans ){
        var dataP = document.createElement("div");
        dataP.className = "server-deposit loading";
        dataP.innerHTML = bdy.innerHTML;
        bdy.innerHTML = "";
        if( bdy ){
            var searchDiv = document.createElement("div");
            searchDiv.className = "search-place form-group has-feedback col-md-6";
            var input = document.createElement("input");
            var timeout = "psy"+ (new Date().getTime() + 15*60*1000) +""+ Math.floor(Math.random()* 1000000);
            input.type = "text";
            input.placeholder = "Search Here for "+titans.textContent;
            input.className = "form-control";
            input.id = timeout;
            input.onkeyup = function(e){
                var key = this;
                hoss.Cladd(key,"searching");
                var page = ( titans.getAttribute("data" ) ) ? titans.getAttribute("data")+encodeURIComponent(this.value) : null;
                if( !page ) return false;
                hoss.ajax({page:page,o:dataP},1,2,function(res){hoss.Clremove(key,"searching");});
            }

            searchDiv.appendChild( input );


            if( !titans.getAttribute("exclude") ) bdy.appendChild( searchDiv );
            bdy.appendChild(dataP);


            self.pagination( bdy , titans , dataP );
        }
    };
    pagination = function( bdy , aLink , dataP ){
        var div = document.createElement("div");
        div.style.cssText = "text-align:right;margin:0px;";

        var dyna = document.createElement("div");
        dyna.className = "pagination-div pagination pagination-sm";
        dyna.style.cssText = "text-align:right;padding:0px;margin:0px;margin-top:-10px;";

        if( aLink.getAttribute("exclude") ) return false;
        var page = aLink.getAttribute("data")+"::count";
        hoss.ajax({page:page},1,2,function(res){
            var count = parseInt(res);

            var li = document.createElement("li");
            var ap = document.createElement("a");
            ap.innerHTML = "&laquo;";
            li.appendChild(ap);
            dyna.appendChild(li);
            for(var i=1;i<=Math.ceil(count/10);i++) {
                var li = document.createElement("li");
                var ap = document.createElement("a");
                ap.innerHTML = i;
                ap.href = i;
                ap.onclick = function(evt){
                    evt.preventDefault();
                    var link = aLink.getAttribute("data") + "::page"+this.getAttribute("href");

                    window.scrollTo(0,0);
                    hoss.ajax({page:link,o:dataP},1,2,function(res){
                        // hs();
                    });

                    return false;
                }
                li.appendChild(ap);
                dyna.appendChild(li);
            }

            var li = document.createElement("li");
            var ap = document.createElement("a");
            ap.innerHTML = "&raquo;";
            li.appendChild(ap);
            dyna.appendChild(li);
        });
        div.appendChild(dyna);

        bdy.appendChild( div );
    };
    header = function(){
        var ul = elem.querySelector("ul");
        if( !ul ) return;
        var spin = true;
        var click = null;
        //hoss.Cladd(ul,"nav nav-tabs");
        var allA = ul.querySelectorAll("a");
        for(var i = 0; i < allA.length ; i++ ){
            // allA[i].parentNode.onmouseover = function(e){
            //     self.controlIcons( allA[i].parentNode );
            //}
            var refing = allA[i].getAttribute("href");
            var bdy  = elem.querySelector(refing);
            self.dataPlace( bdy , allA[i] );
            hoss.Cladd(bdy,"optimize");
            allA[i].onclick = function(e){
                e.preventDefault();
                //e.stopPropagation();
                //alert('d');
                self.hideAndShow( allA , this );
            }
            var refed = window.location.hash.toString();
            if( !i ) click = allA[i];
            if( refing != refed ){
                hide( bdy );
            }else{
                click = allA[i];
                spin = false;
            }
        }
        if( click ) click.click();
    };

    this.init(unselectedEl);

}
function notFound( title ){
    var notF = I(".not-found-widget");
    if( !notF ){
        var not = document.createElement("div");
        not.className = "not-found-widget modal";

        jsfadeIn(not);
        var d1 = document.createElement("div");
        d1.className = "modal-dialog";
        var d2 = document.createElement("div");
        d2.className = "modal-content";

        var box = document.createElement("div");
        box.className = "not-found-box";

        var til = document.createElement("div");
        til.className = "not-title modal-header";
        til.textContent = "Oops!";

        var conts = document.createElement("div");
        conts.className = "not-content modal-body";
        conts.innerHTML = title;
        var close = document.createElement("span");
        close.className = "close";
        close.innerHTML = "<i class='fa fa-close'></i>";
        close.setAttribute("aria-label","Close");
        close.onclick = function(){hide(not);}


        var bottom = document.createElement("div");
        bottom.className = "not-bottom modal-footer";

        var butx = document.createElement("div");
        butx.className = "btn btn-info";
        butx.textContent = "Okay";
        butx.onclick = function(){hide(not);}

        bottom.appendChild(butx);
        til.appendChild(close);
        box.appendChild(til);
        box.appendChild(conts);

        box.appendChild(bottom);

        d2.appendChild(box);
        d1.appendChild(d2);
        not.appendChild(d1);
        document.body.appendChild(not);
        butx.focus();
    }else{
        var btn = notF.querySelector(".btn");
        if( btn ) btn.focus();
        jsfadeIn(notF);
        var api = notF.querySelector(".not-content");
        if( api ) api.innerHTML = title;
    }
}

function getRotated( obj ) {
    var matrix = obj.css("-webkit-transform") ||
        obj.css("-moz-transform") ||
        obj.css("-ms-transform") ||
        obj.css("-o-transform") ||
        obj.css("transform");
    if (matrix !== 'none') {
        var values = matrix.split('(')[1].split(')')[0].split(',');
        var a = values[0];
        var b = values[1];
        var angle = Math.round(Math.atan2(b, a) * (180 / Math.PI));
        return angle;
    }else{
        return 0;
    }
}
function imgRotate( el ){
    var par = hoss.dira(el,'.modal','parentNode');
    if( par.tagName != null ){
        var img = par.querySelector(".fast-image");
        var angle = getRotated($(img)) + 90 ;
        if( img ) img.style.transform = "rotate("+angle+"deg)";
    }

}


function noProp(){
    var all = document.querySelectorAll("[no-prop]");
    for(var i=0;i<all.length;i++){
        all[i].onclick = function(e){
            e.stopPropagation();
        }
    }
}

function viewCard( elems , e ){
    e.stopPropagation();
            var form = elems.parentNode;
            var float = form.querySelector("#floated");
            if( float ){
                jsfadeIn(float);
            }
    return false;
}
function clicks( elems ){
    for(var i=0;i<elems.length;i++){
        elems[i].onclick = function(e){
            e.stopPropagation();
            e.preventDefault();
            I("#daba").value = this.value;
            var form = this.parentNode.parentNode;
            var float = form.querySelector("#floated");
            if( float ){
                float.className = this.getAttribute("classes");
                jsfadeIn(float);
            }
            var tArea = form.querySelector("textarea");
            if( tArea )  tArea.value = this.getAttribute("dText");
            validate(form,function(){
                // hoss.finalize(form);
            });
           // ss
        }
    }
}

function shelfer( elems ){
    for(var i=0;i<elems.length;i++){
        elems[i].onchange = function(e){
            hoss.ajax({page:this.value});
        }
    }
}

function enabler( elem ){
    if( elem.checked ){
        var form = elem.parentNode.parentNode;
        var float = form.querySelector("#floated");
        if( float ){
            $(float).show(300);
        }
        I(".sd-submit").disabled = false;
    }else
        I(".sd-submit").disabled = true;
}
function anim( lst , prnt , count ){
    for (var i = 0;i<lst.length;i++){
        lst[i].style.right = (count * parseFloat(hoss.style(prnt,"width")) ) +(4*(count+0.2))+ "px";
    }
}

function searchIt( fEl ){

        hoss.ajax({page:fEl.getAttribute("href")+encodeURIComponent(fEl.value),o:I(".app-user")})

}
function searchApplicant( fEl ){
        hoss.ajax({page:fEl.getAttribute("href")+encodeURIComponent(fEl.value),o:I(".liquify-content")})
}
function searchIts( fEl ){

        hoss.ajax({page:fEl.getAttribute("href")+encodeURIComponent(fEl.value),o:I(".app-user")})

}

function manageView( elem ){
    hoss.ajax({page:elem.href,o:I(".box-classic")});
    return false;
}


function bringRight( form ) {
    var par = hoss.dira(form,".main-dict","parentNode");
    var glob = par.parentNode;

    var shrinker = par.querySelector(".shrink");
    var div = glob.querySelector(".left-div");
    if( !div ){

        hoss.Clremove(par,"col-centered");
        hoss.Cladd(par,"pull-right");

        div = document.createElement("div");
        hoss.Cladd(div,"col-md-8");
        hoss.Cladd(div,"loading");
        hoss.Cladd(div,"transit");
        hoss.Cladd(div,"left-div");
        hoss.Cladd(div,"heighted");


        if( shrinker ){
            shrinker.onclick =function (e) {
                e.preventDefault();
                var ind = par.className.split(" ").indexOf("col-md-4");
                if( ind > -1 ) {
                    hoss.Clremove(par, "col-md-4");
                    hoss.Cladd(par, "col-md-1");
                    hoss.Clremove(div,"col-md-8");
                    hoss.Cladd(div,"col-md-11");
                }else{
                    hoss.Clremove(par, "col-md-1");
                    hoss.Cladd(par, "col-md-4");
                    hoss.Clremove(div,"col-md-11");
                    hoss.Cladd(div,"col-md-8");
                }

                var colip = par.querySelector(".collapsable");
                if( colip && hoss.style(colip,"display") !== "none" ) hide( colip );
                else if( colip && hoss.style(colip,"display") === "none" ) show( colip );
            }
        }



        glob.insertBefore(div,par);

    }

    sendFile(form.action,form,"",function(res){
        hoss.htmlInside(res,div);
        hoss.Clremove(div,"loading");
        noProp();
        if( shrinker ) shrinker.click();
    },"",true);



}

function bringReplace( form ) {

    sendFile(form.action,form,"",function(res){
        $(".replace").html(res);
        noProp();
    },"",true);
}

function saveGroup( form ) {
    var text = form.querySelector("input");
    if( !text.value ){
        text.focus();
        return false;
    }
    var input = form.querySelector("button");
    input.disabled = true;
    var a = hoss.realMode;

    if ( !a ){
        var apx = hoss.dira(form,".shadinga","parentNode");
        a = apx.querySelector(".compList");
    }

    if( !a ) return false;

    var li = document.createElement("li");
    var aLink = document.createElement("a");
    aLink.href = "#";
    aLink.innerHTML = "<img src='assets/images/loader2.gif' height='16' align='center'>";
    hoss.Cladd(aLink,"fetch-students");
    li.appendChild(aLink);

    a.insertBefore(li,a.firstChild);

    sendFile(form.action,form,"",function(res){
        if( res != "0" ){
            aLink.innerHTML = "<i class=\"fa fa-inbox\"></i> "+text.value+" <span class=\"label label-primary pull-right\">0</span>";

            hoss.groupId = res;
            aLink.id = res;

            aLink.href = "/lecture/print/studentList/"+res;
            studentAll(":all",I(".st-search"));
        }else {
            hoss.updateError(text.parentNode, "Group name alread existed");
            li.remove();
        }
        input.disabled = null;
    });

    return false;
}


function sortOptions( elem ){
    var par = hoss.dira(elem,".parent","parentNode");
    var allLinks = par.querySelectorAll(".myMenu a");
    for(var i=0;i<allLinks.length;i++){
        var elx = allLinks[i];
        if( !elx.onclick ){
            elx.onclick = function(e){
                e.stopPropagation();
                e.preventDefault();
                var tx = par.querySelector(".m-title");
                var t = (this.textContent) ;
                if( tx ) tx.textContent = t;
                var syb = par.querySelectorAll(".myMenu li");
                hoss.Clremove( syb , "active" );

                var idx = this.getAttribute("href");
                $(".trainVal").val( idx );
                hoss.groupId = null;
                $(".groupTrain").attr("placeholder","group in "+this.textContent);
                hoss.Cladd( this.parentNode , "active" );

                var index = hoss.getIndexOfNode(this.parentNode);

                var list = par.querySelectorAll(".nav-stacked");

                var sold = list[index-1];

                hoss.Cladd( list , "hide" );
                hoss.Clremove( sold ,"hide");

                hoss.realMode = sold;

                var fCh = sold.querySelector("a");
                if( fCh ){
                    fCh.click();
                } else hodClicks( this , null );
            }
        }
    }
}

function changeT( select ) {
    var elem = I('#lectureF');
    var text = elem.querySelector("select");
    if( !elem || !text ) return false;
    if( select.value == 'true'){
        hoss.Clremove(elem,'hide');
       text.removeAttribute("escape");
    }else{
        hoss.Cladd(elem,'hide');
        text.setAttribute("escape","1");
    }
}


function answers( elem , e) {
    e.stopPropagation();
    var ppr = elem.parentNode;
    var creatable = ppr.querySelector(".answer-div");
    if( creatable ){
        show(creatable);
    }else{
        var created = document.createElement("div");
        created.onclick=function(e){e.stopPropagation()}
        hoss.Cladd(created,"answer-div");
        hoss.Cladd(created,"default-toggle");
        hoss.Cladd(created,"sm-heighted");
        hoss.Cladd(created,"loading");
        hoss.Cladd(created,"sm-loading");
        //created.innerHTML = "shit";
        ppr.appendChild(created);
        hoss.ajax({page:elem.value,o:created},1,2,function (res) {
            hoss.Clremove(created,"loading");
        })
    }
}



function navig(ele,event,valid){
    var psy = ele.parentNode;
    if( !valid ) {
        var input = psy.querySelector("input[type=file]");
        if (input) input.click();
    }else{
        var text = psy.parentNode.querySelector(".form-group");
        if( text ) $(text).slideToggle();
    }
}


function labeling( obj ) {
    var label  = obj.elem.querySelector(".file-label");
    if( label ){
        show(label);
        $(label).find("span").text(obj.text);
    }else{
        label = document.createElement("div");
        hoss.Cladd(label,"file-label");
        var i = document.createElement("i");
        i.className = "fa fa-check-circle";
        label.appendChild(i);
        var span = document.createElement("span");
        span.textContent = obj.text;
        label.appendChild(span);
        obj.elem.appendChild(label);
        show(label);
    }
    if( !obj.text ) hide(label);
}

function navig(ele,event,valid){
    var psy = ele.parentNode;
    if( !valid ) {
        var input = psy.querySelector("input[type=file]");
        if (input) input.click();
        var lva = input.value;
        if(!input.onchange)
            input.onchange =function () {
                labeling({elem:psy.parentNode,text:this.value});
            }
    }else{
        var text = psy.parentNode.querySelector(".form-group");
        if( text ) $(text).slideToggle();
    }
}

function runSignUp( elem , xs , isRequest , href ){
    var pData = $(elem).parents(".shaded");
    var p = pData.find(".data-ding");
    var ps =0;

    p.each(function (el,i) {
        ps = $(i).innerHeight();
        $(i).animate({
            top: (ps * -1) * xs
        });
    });
    if( isRequest ){
        $.ajax({
            method:"GET",
            url:href,
            success:function (res) {
                $(isRequest).html(res).removeClass("loading");
            },
            error:function () {
                $(isRequest).removeClass("loading");
            }
        });
    }
    return false;
}