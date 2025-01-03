/**
 * @return {number}
 */
function Obj(elem,isAll) {
    return ( typeof elem === "object" && elem !== undefined && elem !== null && !isAll ) ? elem : (typeof  elem !== "object" && document.querySelectorAll(elem).length > 1) ? document.querySelectorAll(elem) : (typeof  elem !== "object" && document.querySelector(elem) !== null ) ? document.querySelector(elem) : ( typeof elem !== "object" ) ? defObj() : elem;
}

function requestServer( page , obj ){
    var request = new XMLHttpRequest();
    var params = "";
    request.open('GET', page , true);
    request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
    request.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    request.onload = function(errors) {
        var headers = request.getAllResponseHeaders();
        if (request.status >= 200 && request.status < 400) {
            // Success!
            var resp = request.responseText;
            if ( obj.success !== undefined && typeof obj.success === "function" )  obj.success( resp , headers, request );
        } else {
            // We reached our target server, but it returned an error
            if ( obj.error !== undefined && typeof obj.error === "function" ) obj.error(errors);
        }
    };

    request.onerror = function(errors) {
        if ( obj.error !== undefined && typeof obj.error === "function" ) obj.error(errors);
    };
    data = params;
    request.send( data );
}

function defObj() {
    var documentation = document.createElement("aril");
    return of(documentation);
}

function of(elem) {
    return ofQuery(elem,false);
}
function ofAll(elem) {
    return ofQuery(elem,true);
}

function ofQuery(elem,isAll) {
    this.elem = new Obj(elem,isAll);
    this.self = this;
    this.hasClass = function (cn) {
        return (' ' + this.elem.className + ' ').indexOf(' ' + cn + ' ') !== -1;
    };

    this.finalAdd = function(el,cn){
        if (!hasClass(cn)) {
            el.className = (el.className === '') ? cn : el.className + ' ' + cn;
        }
    };
    this.addClass = function (cn) {
        if (this.elem.length !== undefined) {
            for(var i=0;i<this.elem.length;i++)
                this.finalAdd(this.elem[i],cn);
        } else {
            this.finalAdd(this.elem,cn);
        }
        return self;
    };
    this.finalFind = function (elem , q ) {
        return ( q !== null ) ? elem.querySelectorAll(q) : [];
    };
    this.finalFindOne = function (elem , q ) {
        return ( q !== null ) ? elem.querySelector(q) : [];
    };
    this.putData = function ( htmlData ) {
        if (this.elem.length !== undefined) {
            for(var i=0;i<this.elem.length;i++)
                this.finalPut(this.elem[i],htmlData);
        } else {
            this.finalPut(this.elem,htmlData);
        }
        return self;
    };
    this.finalPut = function (elem,data) {
        htmlInside(data,elem);
    };
    this.find = function ( findQuery ) {
        var list = [];
        if (this.elem.length !== undefined) {
            for(var i=0;i<this.elem.length;i++)
                list = this.finalFind(this.elem[i],findQuery);
            //console.log(list);
        } else {
            list = this.finalFind(this.elem,findQuery);
            //console.log(list);
        }
        return of(list);
    };
    this.findOne = function ( findQuery ) {
        var list = [];
        if (this.elem.length !== undefined) {
            for(var i=0;i<this.elem.length;i++)
                list = this.finalFindOne(this.elem[i],findQuery);
            //console.log(list);
        } else {
            list = this.finalFindOne(this.elem,findQuery);
            //console.log(list);
        }
        return of(list);
    };
    this.getHTML = function () {
        var nude = "";
        if (this.elem.length !== undefined) {
            for(var i=0;i<this.elem.length;i++) {
                nude += this.elem[i].innerHTML;
            }
            return nude;
        } else {
            return this.elem.innerHTML;
        }
    };
    this.finalLoad = function (elem,url,cBack) {
        requestServer(url,{
            success:function (donne) {
                self.removeClass("loading").putData(donne);
            },
            error:function (e) {
                self.removeClass("loading");
            }
        });
    };
    this.load = function ( url , cBack ) {
        if (this.elem.length !== undefined) {
            for(var i=0;i<this.elem.length;i++)
                this.finalLoad(this.elem[i],url,cBack);
            //console.log(list);
        } else {
            this.finalLoad(this.elem,url,cBack);
            //console.log(list);
        }
        return self;
    };
    this.findHref = function () {
        var findQuery = "xm";
        var list = [];
        if (this.elem.length !== undefined) {
            for(var i=0;i<this.elem.length;i++) {
                findQuery = this.elem[i].getAttribute("href");
                console.log(findQuery);
                list = document.querySelectorAll(findQuery);
            }
        } else {
            findQuery = this.elem.getAttribute("href");
            list = document.querySelectorAll(findQuery);
        }
        return of(list);
    };
    this.finalEvent = function ( elem , func , evt ) {
        elem.addEventListener(evt,function (eventClick) {
            if( func !== undefined && typeof func === "function" ) func(elem,eventClick);
        })
    };
    this.press = function ( callBack ) {
        if (this.elem.length !== undefined) {
            for(var i=0;i<this.elem.length;i++)
                this.finalEvent(this.elem[i],callBack,"click");
        } else {
            this.finalEvent(this.elem,callBack,"click");
        }
        return self;
    };
    this.clicked = function () {
        if (this.elem.length !== undefined) {
            for(var i=0;i<this.elem.length;i++)
                this.elem[i].click();
        } else {
            this.elem.click();
        }
        return self;
    };
    this.finalRemove = function (el,className) {
        if (el.classList) {
            el.classList.remove(className);
        } else {
            el.className = el.className.replace(new RegExp('(^|\\b)' + className.split(' ').join('|') + '(\\b|S)', 'gi'), ' ');
        }
    };
    this.removeClass = function (className) {
        var el = this.elem;
        if (el.length === undefined) {
            this.finalRemove(el,className);
        } else {
            for (var i = 0; i < el.length; i++) {
                this.finalRemove(el[i],className);
            }
        }
        return self;
    };

    this.columns = function(){
        var obj = this.elem;
        var cols = [];
        var o = obj[0] !== undefined ? obj[0] : obj;
        for (var key in o ) {
            cols.push(key);
        }
        return cols;
    };

    this.columnsDouble = function(){
        var obj = this.elem;
        var len = this.columns().length / 2;
        var cols = [];
        var cols2 = [];
        var o = obj[0] !== undefined ? obj[0] : obj;
        var i = 0;
        for (var key in o ) {
            if( i < len ) cols.push(key);
            else cols2.push(key);
            i++;
        }
        return {
            column:cols,
            column2:cols2
        };
    };

    this.isHidden = function () {
        var el = this.elem;
        if (el.length === undefined) {
           return hoss.style(el,"display") === "none";
        }
    };
    this.server = function (hreference) {
        var el = this.elem;
        requestServer(hreference,{
            error:function () {

            },
            success:function (result,statuses,xhr) {
                if (el.length === undefined) {
                    htmlInside(result,el);
                } else {
                    for (var i = 0; i < el.length; i++) {
                        htmlInside(result,el[i]);
                    }
                }
            }
        });
    };
    this.finalSetStyle = function (el,list) {
        for( var single in list ){
            el.style[single] = list[single];
        }
    };
    this.setStyle = function (list) {
        const el = this.elem;
        if (el.length === undefined) {
            this.finalSetStyle(el,list);
        } else {
            for (var i = 0; i < el.length; i++) {
                this.finalSetStyle(el[i],list);
            }
        }
        return self;
    };
    this.handeData = function (elementOne,ul) {
        requestServer(elementOne.value,{
            success:function (res) {
                var key = elementOne.getAttribute("key");
                key = ( key === null ) ? "jsonInfo" : key;
                var json = JSON.parse(res);
                ul.innerHTML = "";
                Array.prototype.forEach.call(json,function (elem) {
                    var li = document.createElement("li");
                    var a = document.createElement("a");
                    a.href = elem["route"];
                    a.innerHTML = elem[key];
                    li.appendChild(a);
                    ul.appendChild(li);

                    of(a).addClass("fetch-students");
                    a.onclick = function (e) {
                        e.preventDefault();
                        e.stopPropagation();
                        hodClicks(this);
                    }
                })
            }
        });

    };
    this.handleChange = function (el,l) {


        var prnt = of(el).parents(".flex-box");
        if( !prnt.hasClass("ofQuery-loaded") ){
            prnt.addClass("ofQuery-loaded");

            var ul = document.createElement("ul");

            prnt.addElem(ul);


            of(ul).addClass("nav").addClass("nav-pills").addClass("nav-stacked");

            el.onchange = function (e) {
                self.handeData(this,ul);
            };
            if(el.value) self.handeData(el,ul);

        }else{
            //
        }

    };
    this.addElem = function (element) {
        var el = this.elem;
        el.appendChild(element);
        return self;
    };
    this.parents = function( parentStr ) {
        var el = this.elem;
        var matched = [],
            cur = el.parentNode;
        var a = parentStr.split("#").pop();
        var className = parentStr.split(".").pop();
        var inc = 0;
        while ( cur ) {
            if ( cur.id === a || of(cur).hasClass(className) ) {
                //matched.push( cur );
                matched = cur;
                inc++;
            }
            cur = cur.parentNode;
        }
        if( !inc ) return defObj();
        return of(matched);
    };
    this.parent = function( parentStr ) {
        var el = this.elem;
        var matched = [],
            cur = el.parentNode;
        var a = parentStr.split("#").pop();
        var className = parentStr.split(".").pop();
        var inc = 0;
        while ( cur ) {
            if ( cur.id === a || of(cur).hasClass(className) || (cur.tagName !== undefined && cur.tagName.toLowerCase() === parentStr.toLowerCase()) ) {
                matched = cur;
                inc++;
                return matched;
            }
            cur = cur.parentNode;
        }
        if( !inc ) return false;
        return matched;
    };

    this.refresh = function () {
        var el = this.elem;
        if (el.length === undefined) {
            this.handleChange(el,"");
        } else {
            for (var i = 0; i < el.length; i++) {
                this.handleChange(el[i],"");
            }
        }
        return self;
    };

    return this;
}
function getOffset( el ) {
    var _x = 0;
    var _y = 0;
    while( el && !isNaN( el.offsetLeft ) && !isNaN( el.offsetTop ) ) {
        _x += el.offsetLeft - el.scrollLeft;
        _y += el.offsetTop - el.scrollTop;
        el = el.offsetParent;
    }
    return { top: _y, left: _x };
}
function catchPosition( elem ) {
    var bound = elem.getBoundingClientRect();
    alert(bound.top);
    return {left:elem.offsetLeft,top:bound.top+"px",width:"500px"};
}


function htmlInside(data,place){
    $(place).html(data);
}