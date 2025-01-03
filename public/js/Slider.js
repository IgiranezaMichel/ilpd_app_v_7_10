var Slider = function( id , loop , nOfDiv ) {
    var self = this;
    this.container = 1;
    this.divs = 1;
    this.pager = undefined;
    this.nOfDiv = 1;
    this.deviation = 0;
    this.counter = 0;
    this.colorArray = new Array("success","danger","info","warning","primary","default");
    this.boxPrefix = "box-";
    this.badgePrefix = "label-";
    this.chatPrefix = "direct-chat-";
    this.buttonPrefix = "text-";

    this.showIco = function(){
        var elp = self.container.querySelectorAll(".selector");
        for (var i = 0; i < elp.length; i++) {
            elp[i].style.opacity = 1;
        };
    };
    this.hideIco = function(){
        var elp = self.container.querySelectorAll(".selector");
        for (var i = 0; i < elp.length; i++) {
            elp[i].style.opacity = 0.7;
        };
    };
    this.hide = function( det ){
        var cur = ((Math.ceil(self.divs.length / self.nOfDiv )) - 1);
        if( det ){
            if( self.counter == cur ) self.container.querySelector("#next").style.display = 'none';
            else self.container.querySelector("#next").style.display = 'block';
        }else{
            if( self.counter <= 0 ) self.container.querySelector("#prev").style.display = 'none';
            else self.container.querySelector("#prev").style.display = 'block';
        }
    };
    this.resetSlider = function () {
        for (var i = 0; i < self.divs.length; i++) {
            self.divs[i].style.width = ((self.containerW() - self.deviation) / self.nOfDiv ) + 2 +'px';
        }
    };
    this.getColor = function (ind,prefix) {
        var colorA = self.colorArray[ind];
        var prevColor = colorA;
        while ( colorA === undefined ) {
            ind = Math.ceil(ind / self.colorArray.length);
            colorA = self.colorArray[--ind];
        }
        var saveColor = ( self.colorArray[ind+1] ) ? self.colorArray[ind+1] : self.colorArray[0];
        hoss.defaultIndex = ( self.colorArray[ind+1] ) ? ind+1 : 0;
        hoss.defaultColor = this.boxPrefix+saveColor;
        return prefix+colorA;
    }
    this.divColors = function (div,indexing) {
      var box = div.querySelector(".box");
      if(!box) return false;
      hoss.Cladd(box,self.getColor(indexing,self.boxPrefix));
      hoss.Cladd(box,self.getColor(indexing,self.chatPrefix));
      var badge = div.querySelector(".badge");
      if(!badge) return false;
      hoss.Cladd(badge,self.getColor(indexing,self.badgePrefix));
        var buttonIcon = div.querySelector(".fa-send");
        if(!buttonIcon) return false;
        hoss.Cladd(buttonIcon,self.getColor(indexing,self.buttonPrefix));
    };
    this.showPagerSlide = function (counter) {
        var currentPagerLength = ((Math.ceil(self.divs.length / self.nOfDiv )) );
        //self.counter = (counter < 0 ? currentPagerLength : (counter > currentPagerLength ? 0 : counter));
        //above liine is no longer in use by DHevil WHispa
        self.hide( 0 );

        for (var i = 0; i < self.divs.length; i++) {
            if(self.loop && self.counter === 0) {
                self.divs[i].style.right = '0px';
            } else if(self.loop && self.counter === (self.divs.length - 1)) {
                self.divs[i].style.right = (currentPagerLength * (self.containerW())) + 'px';
            } else {
                self.divs[i].style.right = (self.counter) * ( ((self.containerW() ) / self.nOfDiv ) + ((counter+1.5)*nOfDiv)) + 'px';
            }
            self.divColors(self.divs[i],i);
        }
        self.hide( 1 );
    };
    this.showNext = function(){
        var selp = self.counter++;
        self.showPagerSlide( selp );
    };
    this.showPrev = function(){
        var selp = self.counter--;
        self.showPagerSlide( selp );
    };
    this.getDev = function(){
        var el = self.divs[0];
        x = self.getIt( el , "padding-left" );
        x += self.getIt( el , "padding-right" );
        x += self.getIt( el , "margin-left" );
        x += self.getIt( el , "margin-right" );
        self.deviation = x * this.nOfDiv;
    }
    this.getIt = function( elem , prop ){
        var xp = document.defaultView.getComputedStyle( elem , null ).getPropertyValue( prop );
        return parseInt( xp );
    };
    this.containerW = function(){
        var el = self.container;
        var x = self.getIt( el , "padding-left" );
        x += self.getIt( el , "padding-right" );
        x += self.getIt( el , "margin-left" );
        x += self.getIt( el , "margin-right" );
        return el.offsetWidth - x;
    };
    this.target = function( elem ){
        var a = self.counter = parseInt(elem.textContent)-1;
        self.showPagerSlide( a );
    };
    this.setPos = function(el){
        var x = self.getIt( el , "padding-left" );
        x += self.getIt( el , "padding-right" );
        x += self.getIt( el , "margin-left" );
        x += self.getIt( el , "margin-right" );
        return  el.offsetWidth;
    }

    this.create = function( id , loop , nOfDiv ){
        self.container = document.getElementById("meSlider");
        self.nOfDiv = nOfDiv;
        self.divs = self.container.querySelectorAll(".element");

        for (var i = 0; i < self.divs.length; i++) {
            self.divColors(self.divs[i],i);
            handleTexting(self.divs[i]);
            positions(self.divs[i],i,self.container);
            self.divs[i].querySelector(".box-title").onclick = function (e) {
                e.stopPropagation();
                var sel = hoss.dira(this,".innerEl","parentNode");
                var body = self.container;
                firstPlace(sel,body);
            }
        }
        if( !window.onresize )
            window.onresize = self.resetSlider;
    };
    this.create( id , loop , nOfDiv );
};




function startChat( elem , event ) {
    var fixed = document.querySelector(".fixedChat");
    if( fixed ){
        jsfadeIn(fixed);
    }else{
        fixed = document.createElement("div");
        hoss.Cladd(fixed,"fixedChat");
        fixed.onclick = function (e) {
            hide(fixed);
        }

        var div = document.createElement("div");
        hoss.Cladd(div,"chat-data");
        hoss.Cladd(div,"transit");
        div.id = "meSlider";

        var moreButton = document.createElement("button");
        hoss.Cladd(moreButton,"left-button-chat");
        hoss.Cladd(moreButton,"no-default");

        moreButton.onclick = function (e) {
            e.stopPropagation();
            chatLeftNav(fixed);
        }


        moreButton.innerHTML = "<i class='fa fa-bars'></i>";

        fixed.appendChild(moreButton);


        fixed.appendChild(div);

        document.body.appendChild(fixed);

        chatLeftNav(fixed);
    }
    return fixed;
}

function chatData( truely ) {
    var fElem = startChat();
    var dplace = fElem.querySelector(".chat-data");
    hoss.Cladd(dplace, "chat-width");

    if( truely ){
        hoss.ajax({page:truely,o:dplace});
    }

    return dplace;
}

function popNotes( elem , evt ) {
    evt.preventDefault();
    evt.stopPropagation();
    var quered = I(elem.getAttribute("href"));

    if(quered) show( quered );
}

function chatLeftNav( fixed ) {
    var leftNav = fixed.querySelector(".chat-left-nav");
    var cData = fixed.querySelector(".chat-data");
    if( leftNav ){
        var dataD = leftNav.querySelector(".people-nav");
        if( !dataD ) return false;

        show(leftNav);
        setTimeout(function () {
            hoss.Clremove(dataD, "people-no-width");
            hoss.Cladd(dataD, "people-width");
            hoss.Clremove(cData, "chat-width");
            hoss.Cladd(cData, "short-width");
        },500);
    }else{
        leftNav = document.createElement("div");
        hoss.Cladd(leftNav,"chat-left-nav");
        leftNav.onclick  = function (e) {
            e.stopPropagation();
            var sf = this;
            hoss.Clremove(getDataDiv,"people-width");
            hoss.Cladd(getDataDiv,"people-no-width");

            hoss.Cladd(cData, "chart-width");
            hoss.Clremove(cData, "short-width");
            setTimeout(function () {hide(sf);},500);
        }

        var getDataDiv = document.createElement("div");
        hoss.Cladd(getDataDiv,"people-nav");
        hoss.Cladd(getDataDiv,"transit");
        hoss.Cladd(getDataDiv,"loading");
        hoss.Cladd(getDataDiv,"people-no-width");
        getDataDiv.onclick  = function (e) {e.stopPropagation();}
        var gDiv = document.createElement("div");
        gDiv.className = "in-div";
        gDiv.id = "myScroll";
        var dataPlace = document.createElement("div");
        dataPlace.id = "datadata";
        var searchDiv = document.createElement("div");
        searchDiv.className = "form-group has-feedback";
        var inputSearch = document.createElement("input");
        inputSearch.name = "search";
        inputSearch.placeholder = "Search keyword here ...";
        inputSearch.onkeyup = function (e) {
            var self2 = this;
            if(!self2.value) return false;

            hoss.Cladd(self2,"imgLoads");
            hoss.ajax({page:"/admin/fetchChatPeoplepage/"+self2.value+"/",o:dataPlace},1,2,function () {
                hoss.Clremove(self2,"imgLoads");
            });
        }
        hoss.Cladd(inputSearch,"form-control");
        searchDiv.appendChild(inputSearch);
        gDiv.appendChild(searchDiv);

        hoss.ajax({page:"/admin/fetchChatPeoplepage/",o:dataPlace},1,2,function () {
            hoss.Clremove(getDataDiv,"loading");
        });

        gDiv.appendChild(dataPlace);
        getDataDiv.appendChild(gDiv);
        leftNav.appendChild(getDataDiv);

        fixed.appendChild(leftNav);

        setTimeout(function () {
            hoss.Clremove(getDataDiv,"people-no-width");
            hoss.Cladd(getDataDiv,"people-width");
            hoss.Clremove(cData, "chat-width");
            hoss.Cladd(cData, "short-width");
        },150);
    }
}

function getChat(elem,evt) {
    evt.preventDefault();
    var right = elem.querySelector(".fix-right");
    if(!right) return false;
    var lival = right.querySelector(".smallest-load");
    if( !lival ){
        lival = document.createElement("div");
        lival.className = "smallest-load";
        right.insertBefore(lival,right.firstChild);
    }else{
        jsfadeIn(lival);
        lival.style.display = "inline-block";
    }
    var body = I(".chat-data");

    hoss.ajax({page:elem.href},1,2,function (res) {
        hide(lival);
        var elex = document.createElement("div");
        elex.innerHTML = res;

        var din = elex.querySelector(".innerEl");
        if( !din ) return false;

        var element = body.querySelector("#"+din.id);
        if( element ){
            firstPlace(element,body);
        }else{
            body.insertBefore(din,body.firstChild);
            hoss.Cladd(din.querySelector(".box"),hoss.defaultColor);
            hoss.Cladd(din.querySelector(".direct-chat"),hoss.defaultChat);
            hoss.Cladd(din.querySelector(".fa-send"),hoss.defaultSend);
            hoss.Cladd(din.querySelector(".badge"),hoss.defaultBadge);
            hoss.refreshColor();
            noProp();
            fullDesign(body);
            din.querySelector(".box-title").onclick = function (e) {
                e.stopPropagation();
                var sel = hoss.dira(this,".innerEl","parentNode");
                firstPlace(sel,body);
            }
        }

    })
}

function firstPlace(elem,body) {
    elem.remove();
    body.insertBefore(elem,body.firstChild);
    fullDesign(body);
    var t = elem.querySelector(".text-chat");
    if( t ) t.focus();
}

function fullDesign(body) {
    var aLL = body.querySelectorAll(".innerEl");
    var xn = true;
    for(var i=0;i<aLL.length;i++){
        var lem = aLL[i];
        if(xn) positions(lem,i,body);
        handleTexting(lem);

        if(body.offsetWidth<(lem.offsetWidth*i)){
            reDesign(aLL,i-2);
            xn = false;
            break;
        }
    }
}

function reDesign(elem,i) {
    var t = true;
    for ( i=i;i < elem.length;i++ ){
        var lem = elem[i];

            lem.style.left = ( t ? lem.offsetWidth : lem.offsetWidth-50 ) + (((lem.offsetWidth + 50) - lem.offsetWidth) * i) + "px";

        t = false;
    }
}

function mTemplate(message) {
    return "   <div class=\"direct-chat-msg right\">\n" +
        "          <div class=\"direct-chat-info clearfix\">\n" +
        "            <span class=\"direct-chat-name pull-right\"></span>\n" +
        "            <span class=\"direct-chat-timestamp pull-left\">"+new Date()+"</span>\n" +
        "          </div>\n" +
        "            <!-- /.direct-chat-info -->\n" +
        "          <img class=\"direct-chat-img\" src=\"assets/images/boys.jpg\" alt=\"Message User Image\"><!-- /.direct-chat-img -->\n" +
        "          <div class=\"direct-chat-text\">\n" +
        "                    "+hoss.htmlEntities(message)+"\n" +
        "          </div>\n" +
        "            <!-- /.direct-chat-text -->\n" +
        "        </div>\n" +
        "     ";
}

function positions(division,i,body) {
    division.style.left =( division.offsetWidth * i) +"px";
}
function handleTexting(box) {
    var frm = box.querySelector(".chat-form");
    if( !frm ) return false;

    frm.onsubmit = function (e) {
        e.preventDefault();
        var textInput = this.querySelector("input");
        if( !textInput ) return false;
        textInput.focus();
        var nBox = hoss.dira(this,".innerEl","parentNode");
        var chatBody = nBox.querySelector(".direct-chat-messages");
        $(chatBody).animate({scrollTop:chatBody.scrollHeight},500);

        if( !textInput.value ) return false;
        var pForm = textInput.parentNode;
        chatBody.insertAdjacentHTML("beforeEnd",mTemplate(textInput.value));
        sendFile(pForm.action,pForm,"",function (res) {
            pForm.reset();
        })
    }
};


function rejectNote(elem,evt) {
    evt.preventDefault();
    evt.stopPropagation();
    hide(elem.parentNode);
    hoss.ajax({page:elem.href});
}

function fullNoteRead(elem,evt) {
    evt.stopPropagation();
    var load = elem.querySelector(".load-data");
    if( hasClass(elem,"full-view") ){
        hoss.Clremove(elem,"full-view");
        hoss.Clremove(elem.parentNode,"full-h");
        hoss.Cladd(load,"hide");
    }else{
        hoss.Cladd(elem,"full-view");
        hoss.Cladd(elem.parentNode,"full-h");
        hoss.Clremove(load,"hide");
    }
    hoss.ajax({page:elem.getAttribute("href"),o:load},1,2,function (res) {
        hoss.Clremove(load,"loading");
    });

}

function monthDays(y, m ) {
    var days = 31;
    if (m === 1 || m === 3 || m === 5 || m === 7 || m === 8 || m === 10 || m === 12) {
        days = 31;
    } else if (m === 4 || m === 6 || m === 9 || m === 11) {
        days = 30;
    } else {
        days = (y % 4 === 0) ? 29 : 28;
    }
    return days;
}


function datePick(day1,year1) {

// <!--

// Project: Dynamic Date Selector (DtTvB) - 2006-03-16
// Script featured on JavaScript Kit- http://www.javascriptkit.com
// Code begin...
// Set the initial date.
    var ds_i_date = new Date();
    ds_c_month = ds_i_date.getMonth() + 1;
    ds_c_year = ds_i_date.getFullYear();

// Get Element By Id
    function ds_getel(id) {
        return document.getElementById(id);
    }

// Get the left and the top of the element.
    function ds_getleft(el) {
        var tmp = el.offsetLeft;
        el = el.offsetParent
        while (el) {
            tmp += el.offsetLeft;
            el = el.offsetParent;
        }
        return tmp;
    }

    function ds_gettop(el) {
        var tmp = el.offsetTop;
        el = el.offsetParent
        while (el) {
            tmp += el.offsetTop;
            el = el.offsetParent;
        }
        return tmp;
    }

// Output Element
    var ds_oe = ds_getel('ds_calclass');
// Container
    var ds_ce = ds_getel('ds_conclass');

// Output Buffering
    var ds_ob = '';

    function ds_ob_clean() {
        ds_ob = '';
    }

    function ds_ob_flush() {
        ds_oe.innerHTML = ds_ob;
        ds_ob_clean();
    }

    function ds_echo(t) {
        ds_ob += t;
    }

    var ds_element; // Text Element...

    var ds_monthnames = ['January', 'February', 'March', 'April', 'May',
        'June', 'July', 'August', 'September', 'October', 'November',
        'December']; // You can translate it for your language.

    var ds_daynames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']; // You can translate it for your language.

// Calendar template
    function ds_template_main_above(t) {
        return '<table cellpadding="3" cellspacing="1" class="ds_tbl">'
            + '<tr>'
            + '</tr>' + '<tr>' + '<td colspan="7" class="ds_head">' + t
            + '</td>' + '</tr>' + '<tr>';
    }

    function ds_template_day_row(t) {
        return '<td class="ds_subhead">' + t + '</td>';
        // Define width in CSS, XHTML 1.0 Strict doesn't have width property for it.
    }

    function ds_template_new_week() {
        return '</tr><tr>';
    }

    function ds_template_blank_cell(colspan) {
        return '<td colspan="' + colspan + '"></td>'
    }

    function ds_template_day(y, m, d) {
        return '<td class="ds_cell" data-click="ds_onclick(' + y + ',' + m
            + ',' + d + ')">' + d + '</td>';
        // Define width the day row.
    }

    function ds_template_main_below() {
        return '</tr>' + '</table>';
    }

// This one draws calendar...
    function ds_draw_calendar(m, y) {
        // First clean the output buffer.
        ds_ob_clean();
        // Here we go, do the header
        ds_echo(ds_template_main_above(ds_monthnames[m - 1] + ' ' + y));
        for (i = 0; i < 7; i++) {
            ds_echo(ds_template_day_row(ds_daynames[i]));
        }
        // Make a date object.
        var ds_dc_date = new Date();
        ds_dc_date.setMonth(m - 1);
        ds_dc_date.setFullYear(y);
        ds_dc_date.setDate(1);
        if (m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10
            || m == 12) {
            days = 31;
        } else if (m == 4 || m == 6 || m == 9 || m == 11) {
            days = 30;
        } else {
            days = (y % 4 == 0) ? 29 : 28;
        }
        var first_day = ds_dc_date.getDay();
        var first_loop = 1;
        // Start the first week
        ds_echo(ds_template_new_week());
        // If sunday is not the first day of the month, make a blank cell...
        if (first_day != 0) {
            ds_echo(ds_template_blank_cell(first_day));
        }
        var j = first_day;
        for (i = 0; i < days; i++) {
            // Today is sunday, make a new week.
            // If this sunday is the first day of the month,
            // we've made a new row for you already.
            if (j == 0 && !first_loop) {
                // New week!!
                ds_echo(ds_template_new_week());
            }
            // Make a row of that day!
            ds_echo(ds_template_day(y, m, i + 1));
            // This is not first loop anymore...
            first_loop = 0;
            // What is the next day?
            j++;
            j %= 7;
        }
        // Do the footer
        ds_echo(ds_template_main_below());
        // And let's display..
        ds_ob_flush();
        // Scroll it into view.
        //ds_ce.scrollIntoView();
    }

// A function to show the calendar.
// When user click on the date, it will set the content of t.
    function ds_sh(t) {
        // Set the element to set...
        ds_element = t;
        // Make a new date, and set the current month and year.
        var ds_sh_date = new Date();
        ds_c_month = ds_sh_date.getMonth() + 1;
        ds_c_year = ds_sh_date.getFullYear();
        // Draw the calendar
        ds_draw_calendar(ds_c_month, ds_c_year);
        // To change the position properly, we must show it first.
        ds_ce.style.display = '';
        // Move the calendar container!
        the_left = ds_getleft(t);
        the_top = ds_gettop(t) + t.offsetHeight;
        ds_ce.style.left = the_left + 'px';
        ds_ce.style.top = the_top + 'px';
        // Scroll it into view.
        //ds_ce.scrollIntoView();
    }

// Hide the calendar.
    function ds_hi() {
        ds_ce.style.display = 'none';
    }

// Moves to the next month...
    function ds_nm() {
        // Increase the current month.
        ds_c_month++;
        // We have passed December, let's go to the next year.
        // Increase the current year, and set the current month to January.
        if (ds_c_month > 12) {
            ds_c_month = 1;
            ds_c_year++;
        }
        // Redraw the calendar.
        ds_draw_calendar(ds_c_month, ds_c_year);
    }

// Moves to the previous month...
    function ds_pm() {
        ds_c_month = ds_c_month - 1; // Can't use dash-dash here, it will make the page invalid.
        // We have passed January, let's go back to the previous year.
        // Decrease the current year, and set the current month to December.
        if (ds_c_month < 1) {
            ds_c_month = 12;
            ds_c_year = ds_c_year - 1; // Can't use dash-dash here, it will make the page invalid.
        }
        // Redraw the calendar.
        ds_draw_calendar(ds_c_month, ds_c_year);
    }

// Moves to the next year...
    function ds_ny() {
        // Increase the current year.
        ds_c_year++;
        // Redraw the calendar.
        ds_draw_calendar(ds_c_month, ds_c_year);
    }

// Moves to the previous year...
    function ds_py() {
        // Decrease the current year.
        ds_c_year = ds_c_year - 1; // Can't use dash-dash here, it will make the page invalid.
        // Redraw the calendar.
        ds_draw_calendar(ds_c_month, ds_c_year);
    }

// Format the date to output.
    function ds_format_date(y, m, d) {
        // 2 digits month.
        m2 = '00' + m;
        m2 = m2.substr(m2.length - 2);
        // 2 digits day.
        d2 = '00' + d;
        d2 = d2.substr(d2.length - 2);
        // YYYY-MM-DD
        return y + '-' + m2 + '-' + d2;
    }

// When the user clicks the day.
    function ds_onclick(y, m, d) {
        // Hide the calendar.
        ds_hi();
        // Set the value of it, if we can.
        if (typeof (ds_element.value) != 'undefined') {
            ds_element.value = ds_format_date(y, m, d);
            // Maybe we want to set the HTML in it.
        } else if (typeof (ds_element.innerHTML) != 'undefined') {
            ds_element.innerHTML = ds_format_date(y, m, d);
            // I don't know how should we display it, just alert it to user.
        } else {
            alert(ds_format_date(y, m, d));
        }
    }

    ds_draw_calendar(day1,year1);
}


function refreshChat() {
    setTimeout(function (r) {
        hoss.ajax({page:"/AllUsers/refreshChat/"},1,2,function (res) {
            var obj = JSON.parse(res);
            $(".unread").text(obj.length);
            var depos = I(".new-message");
            if(!depos) return false;
            depos.innerHTML = "";

            Array.prototype.forEach.call(obj,function (el,i) {
                var pop = document.createElement("div");
                hoss.Cladd(pop,"ann-data");
                var dataDesc = document.createElement("div");
                hoss.Cladd(dataDesc,"data-desc");
                dataDesc.innerHTML = "<i class=\"fa fa-warning text-yellow\"></i> New message from <b>"+el.sendFrom.email+"</b>";
                var popDiv = document.createElement("div");
                var close = document.createElement("a");
                close.className = "pull-right btn-box-tool";
                close.innerHTML= "<i class=\"fa fa-times\"></i>";
                close.onclick = function (e) {
                    e.stopPropagation();
                    hide(this.parentNode);
                }
                pop.appendChild(close);
                popDiv.className = "has-feedback c-title";
                popDiv.innerHTML = hoss.htmlEntities(el.content);
                pop.onclick = function (e) {
                    e.stopPropagation();
                    chatData("/admin/academicalPages/chat/q?q=");
                    hide(this);
                }

                pop.appendChild(dataDesc);
                pop.appendChild(popDiv);
                depos.appendChild(pop);
            });

            refreshChat();
        },function (ex) {
            refreshChat();
        },function (res) {
            refreshChat();
        });
    },30000);
}