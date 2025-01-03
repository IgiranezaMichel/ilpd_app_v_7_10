
// new js

var global = {
    elem:null,
    e:null,
    func:function(){},
    setFunc:function(func,elem,e){
        if( typeof func === "function" ) this.func = func;
        this.elem = elem;
        this.e = e;
    },
    reload:function () {
        if( this.elem ) this.func(this.elem,this.e);
    }
};


var hoss = {
    activeTab: "",
    groupId: null,
    selected: [],
    realMode: null,
    cArray: ["success", "danger", "info", "warning", "primary", "default"],
    defaultColor: "box-success",
    defaultChat: "direct-chat-success",
    defaultSend: "text-success",
    defaultBadge: "label-success",
    defaultIndex: 0,
    selectedItems:[],
    refreshColor: function () {
        this.defaultIndex = (this.cArray[this.defaultIndex + 1] !== undefined ) ? this.defaultIndex + 1 : 0;

        this.defaultColor = "box-" + this.cArray[this.defaultIndex];
        this.defaultChat = "direct-chat-" + this.cArray[this.defaultIndex];
        this.defaultSend = "text-" + this.cArray[this.defaultIndex];
        this.defaultBadge = "label-" + this.cArray[this.defaultIndex];
    },
    pushAt: function (check) {
        if (check.checked) this.selected.push(check);
        else this.selected.shift(check);
    },
    pushItems: function(check){
        if (check.checked) this.selectedItems.push(check);
        else this.selectedItems.shift(check);
    },
    getIndexOfNode: function (node) {
        var parent = node.parentNode;
        return Array.prototype.indexOf.call(parent.children, node);
    },
    updateUrl: function (url) {
        var vx = url;
        /*+ window.location.pathname*/
        hoss.activeTab = window.location.hash.toString().split("#").pop();
        if (history.pushState) {
            var newUrl = window.location.protocol + "//" + window.location.host + "/" + vx + "#" + hoss.activeTab;
            window.history.pushState({path: newUrl}, '', newUrl);
        }
    },
    addLoader: function (div) {
        var lo = document.createElement("div");
        lo.className = "loading-view";
        div.appendChild(lo);
        return 1 > 0;
    },
    finalize: function (form) {
        var but = form.querySelector(".aprox");
        if (but) {
            hoss.Cladd(but, "imgLoads");
        }
        sendFile(form.action, form, "", function (res) {
            hide(form);
            hoss.Clremove(but, "imgLoads");
            var span = document.createElement("span");
            span.className = "buttoner";
            span.style.textAlign = "center";
            span.innerHTML = res;
            form.parentNode.appendChild(span);
            //var me = I("#me-widget");
            //if( me ) hide( me );
            //span.innerHTML = "<i class='icon-check'></i> Accepted";
            var appUser = I("#tabs1");
            if (appUser) {
                hoss.htmlInside(res, appUser);
            }
        });
    },
    finish: function (frm) {
        var check = frm.querySelector("input");
        if (check && check.checked) {
            sendFile(frm.action, frm, "", function (res) {
                //alert(res);
            });
        } else if (check) {
            check.focus();
        }
    },
    loadAll: function (frm) {
        var api = I("#tabs1");
        var button = frm.querySelector(".sd-submit");
        if( button ) hoss.Cladd(button,"imgLoads");
        sendFile(frm.action, frm, "", function (res) {
            if (api) {
                if( button ) hoss.Clremove(button,"imgLoads");
                hoss.htmlInside(res, api);
            }
        });

    },
    loadAll2: function (frm) {
        sendFile(frm.action, frm, "", function (res) {
            $(frm.parentNode).modal("hide");
            $(".hosted").click();
        });

    },
    requiredBox: function (input, error) {
        var f = false;
        if (input.checked) {
            f = true;
        }
        if (!f) {
            this.updateError(input.parentNode, error);
            input.focus();
        }
        return f;
    },
    boxChecker: function (form) {
        var valid = true;
        var allCheck = form.querySelectorAll("input[type=checkbox]");
        Array.prototype.forEach.call(allCheck, function (v, k) {
            v.name = "specName";
            valid = valid && hoss.requiredBox(v, "Check at least 3");
        });
        var allChecked = form.querySelectorAll("input:checked");

        for (var i = 0; i < allChecked.length; i++) {
            var radName = ( i === 0 ) ? "choice1" : ( i === 1 ) ? "choice2" : ( i === 2 ) ? "choice3" : "specName";
            allChecked[i].name = radName;
        }


        return valid || allChecked.length >= 3;
    },
    stepping: function (parental, selfi) {
        var req = parental.querySelectorAll(".separated[required]");
        var matched = [];
        var dir = "previousElementSibling";
        var cur = selfi[dir];
        var inc = 0;
        while (cur && cur.nodeType !== 9) {
            if (cur.getAttribute("required")) matched.push(cur);
            cur = cur[dir];
            inc++;
        }
        return this.stepVal(matched);
    },
    stepVal: function (matched) {
        var cheet = true;
        var string = "Step ";
        for (var i = 0; i < matched.length; i++) {
            var val = matched[i].getAttribute("required");
            if (val != "done") {
                var title = matched[i].getAttribute("title");
                if (title) string += "<b>" + title + "</b>,";
                cheet = false;
            }
        }
        string += " Are required";
        if (!cheet) notFound(string);
        return cheet;
    },
    handleForms: function (parental, finish) {
        if (finish) {
            var allInput = parental.querySelectorAll("input,select,textarea,button");
            for (var im = 0; im < allInput.length; im++) {
                allInput[im].disabled = true;
            }
        }
        var allSeparated = parental.querySelectorAll(".separated");
        var iTab = I(".navigate");
        if (iTab) {
            iTab.innerHTML = "";
        }
        var active = null;
        var activeBody = null;
        for (var i = 0; i < allSeparated.length; i++) {

            var msi = allSeparated[i];

            var fg = document.createElement("li");
            var api = document.createElement("a");
            if (!i) {
                active = api;
                hoss.Cladd(active, "active-left");
                activeBody = msi;
            }
            api.textContent = allSeparated[i].getAttribute("title");
            api.href = i;
            api.onclick = function () {
                var ab = parseInt(this.getAttribute("href"));
                hide(allSeparated);
                hoss.Clremove(active, "active-left");
                active = this;
                hoss.Cladd(active, "active-left");
                hide(allSeparated[ab].querySelector(".body-widget"));
                show(allSeparated[ab]);
                return false;
            };
            fg.appendChild(api);
            if (iTab) iTab.appendChild(fg);

            var div = document.createElement("div");
            div.className = "body-widget";
            var prev = document.createElement("div");
            prev.className = "prevPlace";
            var ix = document.createElement("i");
            ix.className = "icon-angle-left";
            prev.appendChild(ix);

            if (i && !finish) hide(allSeparated[i]);
            var form = allSeparated[i].querySelector("form");
            if (form) form.onsubmit = function (e) {
                //e.preventDefault();
                var specialSteps = true;

                specialSteps = specialSteps && hoss.stepping(parental, this.parentNode);

                if (!specialSteps) return false;
                var valing = true;
                var self = this;
                validate(this, function (res) {
                    var parx = self.parentNode;

                    if (self.getAttribute("checkbox"))
                        valing = valing && hoss.boxChecker(self);
                    if (!valing) return false;
                    var zinzi = self.querySelector(".modal-area");
                    var c = active.parentNode.nextElementSibling;

                    if (zinzi) {
                        jsfadeIn(zinzi);
                        var buttx = zinzi.querySelector(".okButton");
                        var cancel = zinzi.querySelector(".cancelButton");
                        cancel.onclick = function (e) {
                            e.stopPropagation();
                            hide(zinzi)
                        };
                        if (buttx) {
                            buttx.addEventListener("click", function () {
                                show(parx.querySelector(".body-widget"));
                                hide(zinzi);
                                sendFile(self.action, self, "", function () {
                                    window.location.reload()
                                });
                            });
                        }
                        return false;
                    }
                    var activeSibling = parx.querySelector(".body-widget");
                    show(activeSibling);
                    sendFile(self.action, self, "", function (res) {
                        if( res !== "1" ){
                            hide(activeSibling);
                            formError(self,res);
                            return false;
                        }
                        parx.setAttribute("required", "done");
                        if (c) {
                            hoss.Clremove(active, "active-left");
                            active = c.firstChild;
                            hoss.Cladd(active, "active-left");
                        }
                        hide(parx);
                        var next = parx.nextElementSibling;
                        if (next) {
                            hide(next.querySelector(".body-widget"));
                            show(next);
                            var nextInput = next.querySelector("input");
                            if (nextInput) nextInput.focus();
                        }
                    }, '', true);
                });
                return !!self.getAttribute("leave");

            };
            if (!i || finish) hide(div);

            hoss.addLoader(div);

            allSeparated[i].appendChild(div);


            allSeparated[i].appendChild(prev);
            var elime = allSeparated[i];
            var button = elime.querySelector("#meBut");
            if (finish) button.remove();
            var buttonSpace = button.parentNode;

            button.style.right = "0px";
            button.style.top = "7px";
            var newPrev = document.createElement("button");
            newPrev.innerHTML = '<i class="fa fa-arrow-circle-left"></i> Prev';
            newPrev.className = "btn btn-info";
            newPrev.id = "meBut";
            newPrev.type = "button";
            if (!finish && i) buttonSpace.insertBefore(newPrev, buttonSpace.firstChild);
            newPrev.onclick = function () {
                var point = hoss.dira(this, ".separated", "parentNode");

                hoss.Clremove(active, "active-left");
                var c = active.parentNode.previousElementSibling;
                if (c) {
                    active = c.firstChild;
                    hoss.Cladd(active, "active-left");
                }
                var back = point.previousElementSibling;
                if (back) {
                    show(point.querySelector(".body-widget"));
                    hide(point);
                    hide(back.querySelector(".body-widget"));
                    show(back);
                }
            }
        }
    },
    dir: function (elem, dis, dir, until) {
        var matched = [],
            cur = elem[dir];
        var a = dis.split("#").pop();
        var inc = 0;
        while (cur && cur.nodeType !== 9 && (until === undefined || cur.nodeType !== 1 || !jQuery(cur).is(until))) {
            if (cur.id === a) {
                //matched.push( cur );
                matched = cur;
                inc++;
            }
            cur = cur[dir];
        }
        if (!inc) return 0;
        return matched;
    },
    dira: function (elem, dis, dir, until) {
        var matched = [],
            cur = elem[dir];
        var a = dis.split(".").pop();
        var inc = 0;
        while (cur && cur.nodeType !== 9 && (until === undefined || cur.nodeType !== 1 || !jQuery(cur).is(until))) {
            if (cur.className.split(" ").indexOf(a) > 0 || cur.className == a) {
                //matched.push( cur );
                matched = cur;
                inc++;
            }
            cur = cur[dir];
        }
        if (!inc) return 0;
        return matched;
    },
    updateError: function (elem, title) {
        var offTop = elem.offsetTop;
        var parent = getScrollParent(elem);
        $(parent).animate({
            scrollTop: $(elem).offset().top +"px"
        }, 'slow');
        var c = elem.querySelector(".error-tool");
        var text = elem.querySelectorAll("input,select,textarea");
        if (text[0]) hoss.Cladd(text[0], "error");
        if (c) {
            jsfadeIn(c);
            c.lastChild.textContent = title;
        } else {
            var cChwck = elem.className.split(" ").indexOf("pull-right");
            var c = document.createElement("div");

            c.className = "error-tool callout callout-danger";
            var xpi = document.createElement("i");
            xpi.className = "fa fa-warning";

            if (cChwck > -1 || elem.className === "pull-right") hoss.Cladd(c, "righted");
            else hoss.Cladd(c, "lefted");

            var labl = document.createElement("label");
            labl.style.paddingLeft = "4px";
            labl.textContent = title;
            c.appendChild(xpi);
            c.appendChild(labl);
            elem.appendChild(c);
            jsfadeIn(c);
        }
        setTimeout(function () {
            hide(c)
        }, 3000);
    },
    Cladd: function (el, className) {
        if (el.length === undefined) {
            if (el.classList) {
                el.classList.add(className);
            } else {
                el.className += ' ' + className;
            }
        } else {
            for (var i = 0; i < el.length; i++) {
                if (el[i].classList)
                    el[i].classList.add(className);
                else
                    el[i].className += ' ' + className;
            }
        }
    },
    login:function( form ){
        var but = form.querySelector("button");
        var loader = formLoader(form);
        sendFile(form.action,form,"",function(res){
            hide(loader);
            if( res !== "0" ) {
                $(".login-roling").html(res);
                runSignUp(form,2);
            }else{
                hide(form.querySelectorAll(".error-tool"));
                formError(form,"Login Error Check your login information !!");
            }
        });
    },
    Clremove: function (el, className) {
        if (el.length === undefined) {
            if (el.classList) {
                el.classList.remove(className);
            } else {
                el.className = el.className.replace(new RegExp('(^|\\b)' + className.split(' ').join('|') + '(\\b|S)', 'gi'), ' ');
            }
        } else {
            for (var i = 0; i < el.length; i++) {
                if (el[i].classList)
                    el[i].classList.remove(className);
                else
                    el[i].className = el[i].className.replace(new RegExp('(^|\\b)' + className.split(' ').join('|') + '(\\b|S)', 'gi'), ' ');
            }
        }
    },
    htmlEntities: function (str) {
        return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;');
    },
    checkLen: function (obj, min, max, title) {
        var vala = obj.value.trim();
        if (vala.length > max || vala.length < min) {
            this.updateError(obj.parentNode, title);
            obj.focus();
            return false;
        } else {
            return true;
        }
    },
    C_Valid: function (o, rex, title) {

        if (!( rex.test(o.value) )) {
            this.updateError(o.parentNode, title);
            o.focus();
            return false;
        } else {
            return true;
        }
    },
    C_Number: function (obj, title) {
        if (isNaN(obj.value)) {
            this.updateError(obj.parentNode, title);
            obj.focus();
            return false;
        } else {
            return true;
        }
    },
    reseting: function (form) {
        //ds();
        var text = form.querySelectorAll(".form-control");
        if (text.length > 1) {
            if (text[0].value === text[1].value) {
                form.submit();
            } else {
                hoss.updateError(text[1].parentNode, "Password have to be the same");
                text[1].focus();
            }
        }
    },
    requiredCheck: function (name, error, form) {
        var allByName = form.querySelectorAll("input[name=\"" + name + "\"]");
        var valid = false;
        for (var i = 0; i < allByName.length; i++) {
            if (allByName[i].checked) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            allByName[0].focus();
            this.updateError(allByName[0].parentNode, error);
        }
        return valid;
    },
    htmlInside: function (data, place) {
        place.innerHTML = data;
        let myScripts = place.getElementsByTagName("script");
        if (myScripts.length > 0) {
            for (let i = 0; i < myScripts.length; i++) {
                eval(myScripts[i].innerHTML);
            }
        }
    },
    ajax: function (req, first, second, fn, error, superFunc) {
        var request = new XMLHttpRequest();
        var params = "first=" + encodeURIComponent(first) + "&second=" + encodeURIComponent(second);
        request.open('GET', req.page, true);
        request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
        request.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
        request.onload = function (errors) {
            if (request.status >= 200 && request.status < 400) {
                // Success!
                var resp = request.responseText;
                if (typeof req.o === 'object') {
                    if (req.type === 'prepend')
                        req.o.insertAdjacentHTML('afterBegin', resp);
                    else if (req.type === 'append')
                        req.o.insertAdjacentHTML('afterEnd', resp);
                    else
                        req.o.innerHTML = resp;

                    var myScripts = req.o.getElementsByTagName("script");
                    if (myScripts.length > 0) {
                        for (var i = 0; i < myScripts.length; i++) {
                            eval(myScripts[i].innerHTML);
                        }
                        ;
                    }
                    blurer();
                    noProp();
                    modalSelect(".modal-select");
                }
                ( fn === undefined ) ? 1 : fn(request.responseText);
            } else {
                // We reached our target server, but it returned an error
                if (typeof superFunc == "function") superFunc(errors);
            }
        };

        request.onerror = function (errors) {
            errors.preventDefault();
            if (error && fn !== undefined) fn();
            if (typeof superFunc == "function") superFunc(errors);
        };
        data = params;
        request.send(data);
    },
    style: function (elem, prop) {
        return document.defaultView.getComputedStyle(elem, null).getPropertyValue(prop);
    }
};


function input(type, name, value) {
    var i = document.createElement("input");
    i.type = type;
    i.name = name;
    i.value = value;

    return i;
}

function globalWidget( o ) {

    var doc = document.createElement("div");

    doc.className = "super-widget global-widget";

    var ing = document.createElement("div");
    ing.className = "global-center";

    doc.appendChild(ing);

    var bAppender = document.createElement("div");
    bAppender.className = "wiger";

    ing.appendChild(bAppender);

    bAppender.onclick = function (ev) {
        if( ev.target === this){
            $(doc).fadeOut();
            if( o && typeof o.onHide === "function" ) o.onHide(this,doc);
            modalClose();
        }
    };

    var box = document.createElement("div");
    box.className = "boxed-widget-io col-md-5 row col-centered transit";
    bAppender.appendChild(box);

    document.body.appendChild(doc);

    modalOpen();

    "use strict";

    return {
        parent:doc,
        remove:function () {
            doc.remove();
            modalClose();
        },
        hideModal:function () {
            hide(doc);
            modalClose();
        },
        showModal:function () {
            show(doc);
            modalOpen();
        },
        add:function (elem) {
            box.appendChild(elem);
            elem.focus();
        },
        extend:function () {
            $(box).removeClass("col-md-5").addClass("col-md-10");
        }
    }
}

function modalOpen() {
    $(document.body).addClass("modal-open");
}

function modalClose() {
    $(document.body).removeClass("modal-open");
}


function sendFile(page, form, output, messenger, append, reseter, errorFunction) {

    if (typeof output === 'object') output.textContent = "";//clear previous server response

    var url = page;
    var formData = new FormData(form);
    var xhr = new XMLHttpRequest();

    var method = output !== "GET" ? "POST" : output;

    if( method === "GET" ){
        formData = $(form).serialize();
    }

    xhr.open(method, url, true);


    xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    xhr.onload = function (errors) {
        if (xhr.status >= 200 && xhr.status < 400) {
            if (typeof output === 'object') {
                output.innerHTML = xhr.responseText;
                var myScripts = output.getElementsByTagName("script");
                if (myScripts.length > 0) {
                    eval(myScripts[0].innerHTML);
                }
            }
            if (messenger !== undefined) messenger(xhr.responseText,xhr);

            if (!reseter) form.reset();
        } else {
            if (errorFunction !== undefined) errorFunction(xhr.responseText,xhr);
        }
    };
    xhr.send(formData);
}


function show(el) {
    if (el.length === undefined) {
        el.style.display = "block";
    } else {
        for (var i = 0; i < el.length; i++) {
            el[i].style.display = "block";
        }
    }
}

function hide(el) {
    if (el.length === undefined) {
        el.style.display = "none";
    } else {
        for (var i = 0; i < el.length; i++) {
            el[i].style.display = "none";
        }
    }
}

function jsfadeIn(el,type) {
    el.style.display = ( type !== undefined ) ? type : "block";
    el.style.opacity = 0;

    var last = +new Date();
    var tick = function() {
        el.style.opacity = +el.style.opacity + (new Date() - last) / 600;
        last = +new Date();

        if (+el.style.opacity < 1) {
            (window.requestAnimationFrame && requestAnimationFrame(tick)) || setTimeout(tick, 16);
        }
    };

    tick();
}

function I(query) {
    return document.querySelector(query);
}
function T(query) {
    return document.querySelector(query);
}
function p(query) {
    return document.querySelector(query);
}


function globalTop(el, end, bot) {
    var i = el.scrollTop;
    if (bot === 'bottom') end = el.scrollHeight;
    var ice = 0;
    var cons = 20;
    var initial = i;
    if (initial >= end) cons = -20;
    var timer = setInterval(function () {
        ice += cons;
        i += ice * 2;
        if (i >= end && initial < end) clearInterval(timer);
        else if (i <= end && initial >= end) clearInterval(timer);
        el.scrollTop = i;
    }, 40);
}


function blurer() {
    var all = document.querySelectorAll(".form-input");
    for (var i = 0; i < all.length; i++) {
        all[i].onblur = function () {
            var labl = this.parentNode.querySelector(".animated-label");
            if (this.value) {
                if (labl)
                    hoss.Cladd(labl, "not-empty");
            } else if (labl)
                hoss.Clremove(labl, "not-empty");
        }
    }
}

function changeCamp(elem) {
    if (elem.value) {
        var page = "/student/getStudy/1/" + elem.value;
        hoss.ajax({page: page}, "", "", function (res) {
            var json = JSON.parse(res);
            var mood = document.getElementById("prgm");
            mood.innerHTML = "";
            var option = document.createElement("option");
            option.value = "";
            option.innerHTML = "-- choose program name --";
            mood.appendChild(option);
            for (var i = 0; i < json.length; i++) {
                var option = document.createElement("option");
                option.innerHTML = json[i].program.programName+" ("+json[i].program.programAcronym+")";
                option.value = json[i].id;
                mood.appendChild(option);
            }

        });
    }
}

function changeProgram(elem) {
    if (elem.value) {
        var page = "/student/getStudy/2/" + elem.value;
        hoss.ajax({page: page}, "", "", function (res) {
            var json = JSON.parse(res);
            var mood = document.getElementById("mood");
            mood.innerHTML = "";
            var option = document.createElement("option");
            option.value = "";
            option.innerHTML = "-- choose study mode --";
            mood.appendChild(option);
            for (var i = 0; i < json.length; i++) {
                var option = document.createElement("option");
                option.innerHTML = json[i].mode.modeName+" ("+json[i].mode.modeAcronym+")";
                option.value = json[i].id;
                mood.appendChild(option);
            }

        });
    }
}
function changeProgram2(elem) {
    if (elem.value) {
        var page = "/student/getStudy/6/" + elem.value;
        hoss.ajax({page: page}, "", "", function (res) {
            var json = JSON.parse(res);
            var mood = document.getElementById("trainingId");
            mood.innerHTML = "";
            var option = document.createElement("option");
            option.value = "";
            option.innerHTML = "-- choose training --";
            mood.appendChild(option);
            for (var i = 0; i < json.length; i++) {
                var option = document.createElement("option");
                option.innerHTML = json[i].iMode.intake.intakeName;
                option.value = json[i].id;
                mood.appendChild(option);
            }

        });
    }
}
function changeTrainings(elem) {
    if (elem.value) {
        var page = "/student/getStudy/7/" + elem.value;
        hoss.ajax({page: page}, "", "", function (res) {
            var json = JSON.parse(res);
            var mood = document.getElementById("groupIds");
            mood.innerHTML = "";
            var option = document.createElement("option");
            option.value = "";
            option.innerHTML = "-- choose group --";
            mood.appendChild(option);
            for (var i = 0; i < json.length; i++) {
                var option = document.createElement("option");
                option.innerHTML = json[i].groupName;
                option.value = json[i].id;
                mood.appendChild(option);
            }

        });
    }
}
function changeTrainingsStudent(elem) {
    if (elem.value) {
        var page = "/student/getStudy/7/" + elem.value;
        hoss.ajax({page: page}, "", "", function (res) {
            var json = JSON.parse(res);
            var mood = document.getElementById("groupIds");
            mood.innerHTML = "";
            var option = document.createElement("option");
            option.value = "";
            option.innerHTML = "-- choose group --";
            mood.appendChild(option);
            for (var i = 0; i < json.length; i++) {
                var option = document.createElement("option");
                option.innerHTML = json[i].groupName;
                option.value = json[i].id;
                mood.appendChild(option);
            }

        });
    }
}
function changeTrainingsComponent(elem) {
    if (elem.value) {
        var page = "/training/getComps/1/" + elem.value;
        hoss.ajax({page: page}, "", "", function (res) {
            var json = JSON.parse(res);
            var mood = document.getElementById("componentId");
            mood.innerHTML = "";
            var option = document.createElement("option");
            option.value = "";
            option.innerHTML = "** Select component **";
            mood.appendChild(option);
            for (var i = 0; i < json.length; i++) {
                var option = document.createElement("option");
                option.innerHTML = json[i].compName;
                option.value = json[i].id;
                mood.appendChild(option);
            }

        });
    }
}

function changeMode(elem) {
    if (elem.value) {
        var page = "/student/getStudy/3/" + elem.value;
        hoss.ajax({page: page}, "", "", function (res) {
            var json = JSON.parse(res);
            var mood = document.getElementById("sess");
            mood.innerHTML = "";
            var option = document.createElement("option");
            option.value = "";
            option.innerHTML = "-- choose session mode --";
            mood.appendChild(option);
            for (var i = 0; i < json.length; i++) {
                var option = document.createElement("option");
                option.innerHTML = json[i].session.sessionName;
                option.value = json[i].id;
                mood.appendChild(option);
            }

        });
    }
}

function changeSession(elem) {
    var campId = $("#prgm").val();
    if (elem.value && campId) {
        var page = "/student/getIntake/"+elem.value+"/" + campId;
        hoss.ajax({page: page}, "", "", function (res) {
            var json = JSON.parse(res);
            var mood = document.getElementById("intakeSelect");


            mood.innerHTML = "";
            var option = document.createElement("option");
            option.value = "";
            option.innerHTML = "-- choose intake --";
            mood.appendChild(option);
            for( var i =0;i<json.length;i++){
                option = document.createElement("option");
                option.innerHTML = json[i].intake.intakeName;
                option.value = json[i].intake.id;
                mood.appendChild(option);
            }

        });
    }
}
function changeIntake(elem) {
    if (elem.value) {

        var sess = $("#sess").val();
        var pro = $("#prgm").val();
        var page = "/Student/checkTrain/" + elem.value +"/"+sess+"/"+pro+"/" ;
        hoss.ajax({page: page}, "", "", function (res) {
            var json = JSON.parse(res);
            var mood = document.getElementById("trainingId");
            mood.innerHTML = "";
            var option1 = document.createElement("option");
            option1.value = "";
            option1.innerHTML = "-- choose training --";
            mood.appendChild(option1);
            for (var i = 0; i < json.length; i++) {
                var option = document.createElement("option");
                option.innerHTML = json[i].print;
                option.value = json[i].id;
                mood.appendChild(option);
            }

        });
    }
}

function formLoader(form) {

    var cCheck = form.querySelector(".formLoader");
    if (cCheck) jsfadeIn(cCheck);
    else {
        var loading = document.createElement("div");
        hoss.Cladd(loading, "formLoader");
        var loadin = document.createElement("div");
        hoss.Cladd(loadin, "formLoading");
        loading.appendChild(loadin);

        form.insertBefore(loading, form.firstChild);

        jsfadeIn(loading);

        cCheck = loading;
    }
    return cCheck;
}

function respData( form ,res , load ) {
    setTimeout(function (r) {
        hide(load);
    },12);
    if( res !== "1" ){
        formError(form,res);
        return false;
    }
    var mTab = hoss.dira(form,".nav-tabs-custom","parentNode");
    if( mTab !== 0 ) {
        var activeTab = mTab.querySelector(".active a");
        if (activeTab) activeTab.click();
        else{
            var activeLeft = I(".hosted");
            activeLeft.click();
        }
    }
    else{
        var activeLeft = I(".hosted");
        activeLeft.click();
    }
    hide(document.querySelectorAll(".modal-area"));

    form.reset();
}

function saveAndReload(form,e) {
    return allForms(form,e,undefined,function (res,form,load) {
        hide(load);
        if( res === "1" ) global.reload();
        else respData(form,res,load);
    })
}

function saveAndRf(form,e) {
    return allForms(form,e,undefined,function (res,form,load) {
        hide(load);
        if( res === "1" ) window.location.reload();
        else respData(form,res,load);
    })
}

function allForms(form, e ,iCheck , func ) {
    if( e !== undefined ) e.preventDefault();
    validate(form, function (res) {
        var load = formLoader(form);
        if( iCheck ){
            $.ajax({
                type:"POST",
                url:form.action,
                data:$(form).serialize(),
                success:function (res) {
                    if( typeof func === "function" ) {
                        func(res,form,load);
                    }else{
                        $(".modal").modal('hide');
                        respData(form, res, load);
                    }
                },
                error:function (res) {
                    hide(load);
                    formNetError(form,function (l,e) {
                        hide(l);
                        allForms(form);
                    });
                }
            });
            return false;
        }
        sendFile(form.action, form, "", function (res) {
            if( typeof func === "function" ) {
                func(res,form,load);
            }else{
                $(".modal").modal('hide');
                respData(form, res, load);
            }
        }, '', true, function (res) {
            hide(load);
            formNetError(form,function (l,e) {
                hide(l);
                allForms(form);
            });
        });
    });
    return false;
}

function validate(form, func) {
    var all = form.querySelectorAll("input,select,textarea");
    var valid = true;
    var max = 600;
    var min = 1;
    hoss.Clremove(all, "error");
    Array.prototype.forEach.call(all, function (v, k) {
        if (v.type !== "hidden" && (v.getAttribute("escape") !== "1" && v.getAttribute("data-escape") !== "1" )) {
            var max2 = parseInt(v.getAttribute("max"));
            if (!isNaN(max2))
                max = max2;

            var min2 = parseInt(v.getAttribute("min"));
            if (!isNaN(min2))
                min = min2;

            valid = valid && hoss.checkLen(v, min, max, "submit valid " + v.name);
            if (v.getAttribute("number"))
                valid = valid && hoss.C_Number(v, " Numbers Are required ");
            if (v.getAttribute("names"))
                valid = valid && hoss.C_Valid(v, /^[a-z$-@-!]([0-9a-z_ ])+$/i, " For Names Use A-Z, a-z, 0-10 and _ ");

            if (v.type == "radio")
                valid = valid && hoss.requiredCheck(v.name, "Check at least one", form);

            if (v.getAttribute("captcha"))
                valid = valid && ValidCaptcha(true);

        }
    });

    if (valid)
        func(valid, form);

    return valid && false;
}


function createModal(elem, e, truer) {
    var par = elem.parentNode;
    if (truer) par = document.body;
    var check = par.querySelector(".modal");
    if (!check) {
        check = document.createElement("div");
        check.className = "modal";
        check.onclick = function () {
            hide(this);
        };
        check.style.display = "block";
        var close = document.createElement("button");
        close.className = "close-button btn btn-info btn-sm";
        close.innerHTML = '<i class="fa fa-times"></i>';
        close.onclick = function (evt) {
            hide(check);
        };
        var cont = document.createElement("div");
        cont.className = "modal-dialog widing";
        var dataPlace = document.createElement("div");
        dataPlace.className = "modal-contena";
        dataPlace.onclick = function (e) {
            e.stopPropagation();
        };
        var disp = document.createElement("div");
        disp.className = "disp-box";
        var inning = document.createElement("div");
        hoss.ajax({page: elem.getAttribute("href"), o: inning}, 1, 2);


        dataPlace.appendChild(disp);
        cont.appendChild(dataPlace);
        disp.appendChild(close);
        disp.appendChild(inning);
        check.appendChild(cont);
        par.appendChild(check);
    } else {
        jsfadeIn(check);
    }
    return false;
}

function grouper(elem) {
    if (elem.value === "178") {
        checker(elem, "#rwanda-civil");
        unchecker(elem, "#state-foreign");
    } else {
        unchecker(elem, "#rwanda-civil");
        checker(elem, "#state-foreign");
    }
    changeCountry(elem);
}

function changeCountry(elem) {
    var code = $("option:selected", elem).attr("code");
    $(".phone-label").html("+" + code)
}

function viewSubmit(elem, evt) {
    evt.stopPropagation();
    evt.preventDefault();
    var bdy = elem.parentNode.querySelector(".vModal");
    if (bdy) {
        jsfadeIn(bdy);
        var inps = bdy.querySelector("#okButton");
        if (inps) inps.focus();
    } else {
        bdy = document.createElement("div");
        bdy.className = "vModal modal";
        hoss.Cladd(bdy, "loading");
        bdy.onclick = function (e) {
            e.stopPropagation();
            hide(this);
        };
        elem.parentNode.appendChild(bdy);
        jsfadeIn(bdy);
        hoss.ajax({page: elem.href, o: bdy}, 1, 2, function (res) {
            hoss.Clremove(bdy, "loading");
        })
    }
}

function createDel(elem, logout) {
    var bdy = elem.parentNode.querySelector(".delete-modala");
    if (bdy) {
        jsfadeIn(bdy);
        var inps = bdy.querySelector("#okButton");
        if (inps) inps.focus();
    } else {
        var bdy = document.createElement("div");
        bdy.className = "delete-modala modal";
        bdy.onclick = function () {
            hide(this);
        };
        bdy.style.display = "block";
        var cont = document.createElement("div");
        cont.className = "modal-dialog widing";
        var dataPlace = document.createElement("div");
        dataPlace.className = "modal-content modal-contena";
        dataPlace.onclick = function (e) {
            e.stopPropagation();
        };
        var boxH = document.createElement("div");
        boxH.className = "delete-box";
        var header = document.createElement("div");
        header.className = "modal-header";
        var Hd = document.createElement("button");
        Hd.className = "close";
        Hd.innerHTML = "<i class='fa fa-close'></i>";
        Hd.setAttribute("aria-label", "Close");
        Hd.onclick = function (e) {
            e.stopPropagation();
            hide(bdy);
        };

        var title = document.createElement("span");
        title.href = "#";
        title.textContent = (!logout) ? "Delete Alert !!!!" : "Logout Confirmation ???";
        title.className = "modal-title";

        var dltBdy = document.createElement("div");
        dltBdy.className = "modal-body";
        if (logout) {
            dltBdy.innerHTML = "Are you sure to Logout?";
        } else {
            dltBdy.innerHTML = "Are you sure to Delete <b>" + hoss.htmlEntities(elem.parentNode.parentNode.querySelector("td").innerHTML) + "</b>?";
        }
        var buttonDiv = document.createElement("div");
        buttonDiv.className = "modal-footer";
        var ok = document.createElement("button");
        ok.id = "okButton";
        ok.value = elem.value;
        ok.innerHTML = "Ok <i class='fa fa-check-circle'></i>";
        ok.className = "btn btn-info";
        ok.onclick = function () {
            hide(bdy);
            hide(elem.parentNode.parentNode);
            if (!logout) {
                hoss.ajax({page: this.value});
            } else
                window.location = elem.href;
        };
        buttonDiv.appendChild(ok);

        var cancel = document.createElement("button");
        cancel.id = "okButton";
        cancel.innerHTML = "Cancel <i class='fa fa-eye-slash'></i>";
        cancel.className = "btn btn-default";
        cancel.onclick = function () {
            hide(bdy)
        };
        buttonDiv.appendChild(cancel);


        header.appendChild(title);
        header.appendChild(Hd);
        boxH.appendChild(header);
        dataPlace.appendChild(boxH);
        cont.appendChild(dataPlace);
        bdy.appendChild(cont);
        boxH.appendChild(dltBdy);
        boxH.appendChild(buttonDiv);
        elem.parentNode.appendChild(bdy);

        ok.focus();
    }
}

function createOg(elem, message, titleX) {
    var bdyg = hoss.dira(elem, ".end-block", "parentNode");
    var bdy = bdyg.querySelector(".super-modal");
    if (bdy) {
        jsfadeIn(bdy);
        var inps = bdy.querySelector("#okButton");
        if (inps) inps.focus();
    } else {
        bdy = document.createElement("div");
        bdy.className = "super-modal modal";
        bdy.onclick = function () {
            hide(this);
        };
        bdy.style.display = "block";
        var cont = document.createElement("div");
        cont.className = "modal-dialog widing";
        var dataPlace = document.createElement("div");
        dataPlace.className = "modal-content modal-contena";
        dataPlace.onclick = function (e) {
            e.stopPropagation();
        };
        var boxH = document.createElement("div");
        boxH.className = "delete-box";
        var header = document.createElement("div");
        header.className = "modal-header";
        var Hd = document.createElement("button");
        Hd.className = "close";
        Hd.innerHTML = "<i class='fa fa-close'></i>";
        Hd.setAttribute("aria-label", "Close");
        Hd.onclick = function (e) {
            e.stopPropagation();
            hide(bdy);
        };

        var title = document.createElement("span");
        title.href = "#";
        title.textContent = titleX;
        title.className = "modal-title";

        var dltBdy = document.createElement("div");
        dltBdy.className = "modal-body";
        dltBdy.innerHTML = message;
        var buttonDiv = document.createElement("div");
        buttonDiv.className = "modal-footer";
        var ok = document.createElement("button");
        ok.id = "okButton";
        ok.value = elem.value;
        ok.innerHTML = "Ok <i class='fa fa-check-circle'></i>";
        ok.className = "btn btn-info";
        ok.onclick = function () {
            hide(bdy);
            $(elem).parents(".end-block").hide();
            hoss.ajax({page: elem.getAttribute("href")});
        };
        buttonDiv.appendChild(ok);

        var cancel = document.createElement("button");
        cancel.id = "okButton";
        cancel.innerHTML = "Cancel <i class='fa fa-eye-slash'></i>";
        cancel.className = "btn btn-default";
        cancel.onclick = function () {
            hide(bdy)
        };
        buttonDiv.appendChild(cancel);


        header.appendChild(title);
        header.appendChild(Hd);
        boxH.appendChild(header);
        dataPlace.appendChild(boxH);
        cont.appendChild(dataPlace);
        bdy.appendChild(cont);
        boxH.appendChild(dltBdy);
        boxH.appendChild(buttonDiv);
        bdyg.appendChild(bdy);

        ok.focus();
    }
    return false;
}

function manageChange(elem) {
    var elx = elem.parentNode;
    var showing = elx.querySelector(".listing-req");
    var allSp = showing.querySelectorAll("span");
    for (var i = 0; i < allSp.length; i++) {
        allSp[i].addEventListener("click", function (e) {
            elem.click();
            elx.firstChild.value = this.getAttribute("value");

            elx.firstChild.focus();

            applied(elx.firstChild);
        });
    }
    if (showing && (showing.className.split(" ").indexOf("height") > -1 )) {
        hoss.Clremove(showing, "height");
    } else {
        hoss.Cladd(showing, "height");
    }
}

function modalSelect(elems) {
    var all = document.querySelectorAll(elems);
    for (var i = 0; i < all.length; i++) {
        oneByone(all[i]);
    }
}

function oneByone(elem) {
    hoss.Cladd(elem, "super-focus");
    hoss.Cladd(elem, "liquify");


    var newParent = document.createElement('nav');
    newParent.className = "listing-req height";
    var oldParent = elem;

    while (oldParent.childNodes.length > 0) {
        newParent.appendChild(oldParent.childNodes[0]);
    }

    var input = document.createElement("input");
    input.onkeydown = function (e) {
        e.preventDefault()
    };
    elem.insertBefore(input, elem.firstChild);
    var iC = document.createElement("i");
    hoss.Cladd(iC, "icon-arrow-down");
    elem.insertBefore(iC, input.nextSibling);
    elem.appendChild(newParent);

    iC.onclick = function () {
        manageChange(this);
    };
    input.onclick = function () {
        iC.click();
    }

}

function implementModal(likers) {
    for (var i = 0; i < likers.length; i++) {
        likers[i].onclick = function (e) {
            var self = this;
            var a = this.parentNode.parentNode.querySelector(".modal-area");
            if (a) {
                show(a);
            } else {
                a = document.createElement("div");
                a.className = "modal-area modal modal-default fade in";
                a.style.display = "block";
                var but = document.createElement("button");
                but.setAttribute("aria-label", "Close");
                but.className = "close-button close";
                but.innerHTML = "<i class='fa fa-close'></i>";

                but.onclick = function () {
                    hide(a)
                };
                var div1 = document.createElement("div");
                div1.className = "modal-dialog extender";
                var diver = document.createElement("div");
                diver.className = "modal-content";

                var ding = document.createElement("div");
                ding.className = "plus-modal";
                var dataOg = document.createElement("div");
                dataOg.className = "dataOging";
                hoss.ajax({page: self.getAttribute("href"), o: dataOg});

                ding.appendChild(dataOg);
                diver.appendChild(ding);
                div1.appendChild(diver);

                ding.appendChild(but);
                a.appendChild(div1);

                this.parentNode.parentNode.appendChild(a);
            }
            return false;
        }
    }
}


function checker(el, div) {
    if (el.checked || el.value) {
        show(I(div));
        var allInput = I(div).querySelectorAll("input,select,textarea");
        for (var i = 0; i < allInput.length; i++) {
            if (allInput[i].type === "file")
                continue;
            allInput[i].removeAttribute("escape");
        }
    }
}

function unchecker(el, div) {
    hide(I(div));
    var allInput = I(div).querySelectorAll("input,select,textarea");
    for (var i = 0; i < allInput.length; i++) {
        allInput[i].setAttribute("escape", 1);
    }
}

function mgt(el, div) {
    var elem = el.parentNode.parentNode.querySelector(".form-control");
    if (el.checked) {
        $(elem).show(300);
        elem.removeAttribute("escape");
    } else {
        $(elem).hide(300);
        elem.setAttribute("escape", 1);
    }
}

function quickClick(link) {
    for (var i = 0; i < link.length; i++) {
        link[i].onclick = function () {
            this.innerHTML = "verified";

            var inD = I("#tabs1");
            var iData = I(".app-user");
            if (iData) inD = iData;
            hoss.ajax({page: this.getAttribute("href"), o: inD}, 1, 2, function (res) {
                hide(all(".modal"));
            });
        }
    }
}

function formError(form,res) {
    var error = "<i class='fa fa-warning'></i> " + hoss.htmlEntities(res);
    var body = form.querySelector(".box-body");
    if( body ){
        var errorPlace = body.querySelector(".error-place");
        if( !errorPlace ) {
            errorPlace = document.createElement("div");
            errorPlace.className = "error-place";
            body.insertBefore(errorPlace,body.firstChild);
        }else{
            jsfadeIn(errorPlace);
        }
        setTimeout(function (res) {
            $(errorPlace).hide(550);
        },7200);

        var parent = getScrollParent(body);
        $(parent).animate({scrollTop:0},'fast');

        errorPlace.innerHTML = error;


    }else
        notFound(error);
}
function formSubmitted( form ) {
    var cCheck = form.querySelector(".submitted");
    if( cCheck ) jsfadeIn(cCheck);
    else {
        var loading = document.createElement("div");
        hoss.Cladd(loading, "formLoader");
        hoss.Cladd(loading, "submitted");

        var tDiv = document.createElement("div");
        var loadin = document.createElement("div");
        hoss.Cladd(loadin,"form-submitted");
        var inner = document.createElement("div");
        inner.innerHTML = "<div class='text-center no-block'><i class='fa fa-check big-intent'></i></div> <h3>Form submitted Successfully</h3>";
        loadin.appendChild(inner);
        of(inner).addClass("small-icon-info").press(function (elem,evt) {
            evt.stopPropagation();
        });
        tDiv.appendChild(loadin);
        loading.appendChild(tDiv);
        of(tDiv).addClass("table-div");
        of(loading).press(function (elem,evt) {
            fadeOut(elem,400);
        });

        form.insertBefore(loading, form.firstChild);

        jsfadeIn(loading);

        cCheck = loading;
    }
    return cCheck;
}
function formNetError( form ,func ) {
    var cCheck = form.querySelector(".formNET");
    if( cCheck ) jsfadeIn(cCheck);
    else {
        var loading = document.createElement("div");
        hoss.Cladd(loading, "formLoader");
        hoss.Cladd(loading, "formNET");
        hoss.Cladd(loading, "pointer");
        var pLoad = document.createElement("div");
        var loadin = document.createElement("div");
        hoss.Cladd(loadin,"formNetError");
        hoss.Cladd(loadin,"text-red");
        var tCell = document.createElement("div");
        tCell.innerHTML = "<div class='text-red'><div><i class='fa fa-warning'></i> Network Error or server is currently down </div><i class='fa fa-refresh biggest text-red'></i></div>";
        loadin.appendChild(tCell);
        pLoad.appendChild(loadin);

        loading.appendChild(pLoad);
        $(pLoad).addClass("table-div");

        form.insertBefore(loading, form.firstChild);
        $(loading).click(function (elem,evt) {
            fadeOut(this,300);
        });
        tCell.onclick = function (e) {
            e.stopPropagation();
            if( func !== undefined && typeof func === "function" ) func(loading,e);
        };
        jsfadeIn(loading);

        cCheck = loading;
    }
    return cCheck;
}

function quickDeny(link) {
    for (var i = 0; i < link.length; i++) {
        link[i].onclick = function () {
            var elms = this.querySelector(".commentDiv");
            if (elms) {
                show(elms);
            } else {
                elms = document.createElement("div");
                elms.className = "commentDiv";
                var text = document.createElement("textarea");
                var frm = document.createElement("form");
                frm.action = this.getAttribute("href");
                frm.method = "POST";
                frm.onsubmit = function (evt) {
                    evt.preventDefault();
                    var selt = this;
                    return validate(selt, function () {
                        var inD = I("#tabs1");
                        var iData = I(".app-user");
                        if (iData) inD = iData;
                        inD.innerHTML = "<div align='center'><img src='assets/images/loader.gif'></div>";
                        sendFile(selt.action, selt, "", function (res) {
                            hide(all(".modal"));
                            hoss.htmlInside(res, inD);
                        });
                    });
                };
                text.placeholder = "Enter reason to Deny This?";
                text.className = "form-control";
                text.setAttribute("max", "3000000");
                text.name = "userComment";

                var but = document.createElement("button");
                but.innerHTML = "<i class='fa fa-bitbucket'></i> To Deny This Document with comment please Click here";
                but.className = "btn btn-danger";

                frm.appendChild(text);
                frm.appendChild(but);
                elms.appendChild(frm);
                this.parentNode.appendChild(elms);
                text.focus();
            }
        }
    }
}

function allStudents(hrefEl) {
    var ab = I("#tabs1");
    if (!ab) return false;

    hoss.ajax({page: hrefEl.href, o: ab});

    return false;
}


function resetP(elem, e) {
    var sForm = elem.parentNode;
    var frm = sForm.querySelector(".emailForm");
    if (!frm) {
        var cre = document.createElement("form");
        cre.className = "emailForm default-toggle";
        cre.method = "POST";
        cre.action = elem.href;
        cre.onclick = function (e) {
            e.stopPropagation()
        };
        var input = document.createElement("input");
        input.className = "form-control";
        input.type = "text";
        input.name = "email";
        input.autocomplete = "off";
        input.setAttribute("email", "true");

        var label = document.createElement("label");
        hide(label);
        var label1 = document.createElement("a");
        label1.href = "#";
        var vld = true;
        label1.onclick = function (e) {
            vld = true;
            input.readOnly = null;
            hoss.Clremove(input, "imgLoad");
            input.focus();
            return false;
        };

        label1.textContent = "Resend Email??";
        label1.style.color = "red";
        var l2 = document.createElement("label");

        l2.style.paddingRight = "6px";

        label.appendChild(l2);
        label.appendChild(label1);


        input.placeholder = "Enter your registration email here";
        var but = document.createElement("button");

        but.type = "submit";
        but.innerHTML = "<i class='fa fa-send'></i>";

        cre.appendChild(input);
        cre.appendChild(but);
        cre.appendChild(label);
        show(cre);
        sForm.appendChild(cre);


        cre.onsubmit = function () {
            frm = this;
            if (!vld) return false;
            var v = validate(this, function () {
                vld = false;
                input.readOnly = true;
                hoss.Cladd(input, "imgLoad");
                sendFile(frm.action, frm, "", function (res) {
                    l2.textContent = res;
                    hoss.Clremove(input, "imgLoad");
                    show(label);
                });
            });
            return false;
        }

    } else {
        jsfadeIn(frm);
    }
    return false;
}

function all(query) {
    return document.querySelectorAll(query);
}

function hodClicks(cElem, wr) {

    var allInpr = hoss.dira(cElem, ".shadinga", "parentNode");

    if (wr == null) {
        var ab = allInpr.querySelector(".signup-data .heighted");
        ab.innerHTML = "";
        return false;
    }

    hoss.groupId = cElem.id;
    var evx = hoss.dira(cElem, ".nav-stacked", "parentNode");
    if (evx) {
        var allLi = evx.querySelectorAll("li");
        hoss.Clremove(allLi, "active");
        hoss.Cladd(cElem.parentNode, "active");
    }


    var lst = allInpr.querySelectorAll(".signup-data");


    var prnt = hoss.dira(cElem, ".signup-data", "parentNode");
    if (!prnt) {
        var br = allInpr.querySelector(".breadOne");
        if (br.firstChild) removeNext(br.firstChild);
        prnt = allInpr.querySelector(".signup-data");
        if (!prnt) return false;
        breadCrumb(prnt, lst, allInpr);
        var count = hoss.getIndexOfNode(prnt);

        anim(lst, prnt, count);
    } else if (prnt.nextElementSibling) {
        var count = hoss.getIndexOfNode(prnt) + 1;


        anim(lst, prnt, count);


        prnt = prnt.nextElementSibling;

        breadCrumb(prnt, lst, allInpr);
    }

    var page = cElem.getAttribute("href");


    var p = prnt.querySelector(".heighted");
    if (!p) return false;

    hoss.Cladd(p, "loading");

    p.innerHTML = "";

    hoss.ajax({page: page, o: p}, 1, 2, function () {
        hoss.Clremove(p, "loading");
    });


}

function anim(lst, prnt, count) {
    for (var i = 0; i < lst.length; i++) {
        lst[i].style.right = (count * parseFloat(hoss.style(prnt, "width")) ) + (4 * (count + 0.2)) + "px";
    }
}

function removeNext(li) {
    var arr = [];
    while (li.nextElementSibling) {
        li = li.nextElementSibling;
        arr.push(li);
    }
    arr.forEach(function (el) {
        el.remove();
    });
}

function breadCrumb(prnt, lst, pr) {
    var elex = pr.querySelector(".breadOne");
    if (!elex) return false;

    var listing = elex.querySelectorAll("li");

    var index = hoss.getIndexOfNode(prnt);

    if (listing[index]) return false;

    var title = "Students";
    var tnt = prnt.querySelector(".box-title");

    if (tnt) title = tnt.textContent;

    var li = document.createElement("li");
    li.onclick = function (e) {
        var cou = hoss.getIndexOfNode(this);
        //if( lst[cou].nextElementSibling ) ++cou;
        anim(lst, prnt, cou);
        removeNext(li);
    };
    hoss.Cladd(li, "active");
    var a = document.createElement("a");
    a.href = "#";
    a.innerHTML = ( ( index === 0 ) ? "<i class='fa fa-dashboard'></i>" : " " ) + hoss.htmlEntities(title);
    li.appendChild(a);

    elex.appendChild(li);

}


function studentSearch(input, e) {
    var page = input.getAttribute("href");
    if (!input.value || !hoss.groupId) return false;

    var ref = page + hoss.groupId + "/" + encodeURIComponent(input.value) + "/";

    var sign = hoss.dira(input, ".signup-data", "parentNode");
    var h = sign.querySelector(".heighted");

    hoss.ajax({page: ref, o: h});
}

function studentAll(val, el) {
    var page = "/lecture/search/student/";
    if (!hoss.groupId) return false;

    var ref = page + hoss.groupId + "/" + val + "/";

    var sign = hoss.dira(el, ".signup-data", "parentNode");
    var h = sign.querySelector(".heighted");

    hoss.ajax({page: ref, o: h});
}

function addSelected() {
    var checks = hoss.selected;
    if (checks.length <= 0) return false;

    var data = ($(checks).serialize());
    $.ajax({
        type: "POST",
        url: "/lecture/addMember/" + hoss.groupId + "/",
        data: data,
        success: function (res) {
            $(".buchi").find(".active a").click();
        }
    })
}


$(function () {
    $(document).on('click', ".fetch-students", function (e) {
        e.stopPropagation();
        e.preventDefault();
        global.setFunc(hodClicks,this,e);
        hodClicks(this, e);
    });
    $(document).on('click', ".fetch-data", function (e) {
        e.stopPropagation();
        e.preventDefault();
        var ref = this.href;
        var li = this.parentNode;

        var parent = hoss.dir(this,"#parent-data","parentNode");
        if( !parent ) return false;
        $ul = $(li).parents("ul");
        $ul.find("li").removeClass("li");
        $(li).addClass("active");

        var obj = parent.querySelector(".data-div");
        if(!obj) return false;

        hoss.ajax({page:ref,o:obj},1,2,function (res) {
            hoss.Clremove(obj,"loading");
        });
    })
});

function fileName(input) {
    return input.split('/').pop().split('\\').pop();
}

function findStudent() {
    var regno = $("#regNumberId").val();
    $.ajax({
        url: "/library/student/" + encodeURIComponent(regno),
        method: "GET",
        success: function (data) {
            $("#studentId").val(data.id);
            $("#studentNames").val(data.names);
            $("#program").val(data.program);
        },
        error: function () {

        }
    });
}

function findStudentfromLibrary() {
    var regno = $("#regno1").val();
    var bookCode = $("#bookCode1").val();
    $.ajax({
        url: "/library/" + regno + "/" + bookCode,
        method: "GET",
        success: function (data) {
            $("#libraryId").val(data.id);
            $("#bookTitle1").val(data.bookTitle);
            $("#studentNames1").val(data.names);
            $("#bookCost1").val(data.bookCost + "  Rwf");
        },
        error: function () {

        }
    });
}

// function to format Money
Number.prototype.formatMoney = function (c, d, t) {
    var n = this;
    c = isNaN(c = Math.abs(c)) ? 2 : c;
    d = d === undefined ? "." : d;
    t = t === undefined ? "," : t;
    var s = n < 0 ? "-" : " ";
    var i = String(parseInt(n = Math.abs(Number(n) || 0).toFixed(c)));
    var j = (j = i.length) > 3 ? j % 3 : 0;
    return s + (j ? i.substr(0, j) + t : " ") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
};

function findStudentReport() {
    var regno = $("#regno").val();
    var boolVal = 1;

    $.ajax({
        url: "/library/report/lost/" + encodeURIComponent(regno) + "/" + boolVal,
        method: "GET",
        success: function (data) {
            $('#tableLibrary').show(10);
            $(".div-result").html('<div class="alert alert-warning">' +
                '<button type="button" class="close" data-dismiss="alert">&times;</button>' +
                '<strong><i class="fa fa-warning"></i></strong> This student is not   cleared in library. Below are all the books already taken alongside their value charged for each and Total. </div>');

            $(".alert-warning").delay(500).show(10, function () {
                $(this).delay(10000).hide(10, function () {
                    $(this).remove();
                });
            }); // /.alert


            var tableBody = "";
            var total = 0;
            $.each(data, function (index, element) {
                tableBody += "<tr>";
                tableBody += "<td>" + element.bookTitle + "</td>";
                tableBody += "<td>" + element.bookCode + "</td>";
                tableBody += "<td>" + element.bookCost.formatMoney(2, '.', ',') + "</td>";
                tableBody += "</tr>";
                total += element.bookCost;
            });
            tableBody += "<tr class='text-red' style='font-weight: 800;background-color: #fff;'><td colspan=\"2\">Total</td> <td>" + total.formatMoney(2, '.', ',') + "</td> </tr>";
            $("#booksLost").html(tableBody);
            $("#btnPrint").show(10);
        },
        error: function (response) {
            console.log(response);
            $("#tableLibrary").hide(10);
            if (response.responseJSON.code === "404") {
                $(".div-result").html('<br><div class="alert alert-info">' +
                    '<button type="button" class="close" data-dismiss="alert">&times;</button>' +
                    '<strong><i class="glyphicon glyphicon-ok-sign"></i></strong> This student is already  cleared in library or The Entered Reg Number is Invalid </div>');
            }
        }
    });
    boolVal = 0;
    $.ajax({
        url: "/library/report/lost/" + encodeURIComponent(regno) + "/" + boolVal,
        method: "GET",
        success: function (data) {
            var tableBody = "";
            var total = 0;

            $.each(data, function (index, element) {
                tableBody += "<tr>";
                tableBody += "<td>" + element.bookTitle + "</td>";
                tableBody += "<td>" + element.bookCode + "</td>";
                tableBody += "<td>" + element.bookCost.formatMoney(2, '.', ',') + "</td>";
                tableBody += "</tr>";
                total += element.bookCost;
            });
            tableBody += "<tr class='text-red' style='font-weight: 800;background-color: #fff;'><td colspan=\"2\" >Total</td> <td>" + total.formatMoney(2, '.', ',') + "</td> </tr>";
            $("#lateBooks").html(tableBody);
            $('#tableLibraryLateReturn').show(10);
            $("#btnPrint").show(10);
        },
        error: function () {
            $("#tableLibraryLateReturn").hide(10);
        }
    });
}

function printReport() {
    var reg = $("#regno").val();
    if ( reg === undefined || reg.trim() !== "") {
        window.print();
    }
}

function printReportExcel( elem ) {
    var invoice = $(elem).closest(".invoice");
    var array = [],csvArray = [];

    var arr = invoice.find("table thead th");
    arr.each(function (i,el) {
       array.push(el.textContent);
    });


    var data = invoice.find("table tbody tr");

    data.each(function (i,el) {
       var sub = [];
       $(el).find("td").each(function (i,elem) {
          sub.push($(elem).text());
       });
       csvArray.push(sub);
    });

    var name = invoice.find("h4").text();

    var excel = new Excel();
    excel.headerArray = array;
    excel.dataArray = csvArray;
    excel.fileName = name === "" || name === undefined ? "f-name" : name ;

    excel.exportToCsv();
}

function exportTableToExcel(tableID, filename = ''){
    var downloadLink;
    var dataType = 'application/vnd.ms-excel';
    var tableSelect = document.getElementById(tableID);
    var tableHTML = tableSelect.outerHTML.replace(/ /g, '%20');

    // Specify file name
    filename = filename?filename+'.xls':'excel_data.xls';

    // Create download link element
    downloadLink = document.createElement("a");

    document.body.appendChild(downloadLink);

    if(navigator.msSaveOrOpenBlob){
        var blob = new Blob(['\ufeff', tableHTML], {
            type: dataType
        });
        navigator.msSaveOrOpenBlob( blob, filename);
    }else{
        // Create a link to the file
        downloadLink.href = 'data:' + dataType + ', ' + tableHTML;

        // Setting the file name
        downloadLink.download = filename;

        //triggering the function
        downloadLink.click();
    }
}

function hasClass(elem, className) {
    return elem.className.split(" ").indexOf(className) > -1 || elem.className === className;
}

function swapGroup(form) {
    validate(form, function (e) {
        sendFile(form.action, form, "", function (e) {
            $(".buchi").find(".active a").click();
        });
    });
    return false;
}

function searchByNumber(keyElem) {
    if (!keyElem.value) return false;
    var href = keyElem.getAttribute("href") + encodeURIComponent(keyElem.value);
    var obj = keyElem.parentNode.parentNode.parentNode.querySelector(".box-body");
    hoss.ajax({page: href, o: obj});

}



function hideDrawers() {
    var alls = all(".drawer");
    for (var i=0;i<alls.length;i++){
        hoss.Cladd(alls[i],"col-md-0");
    }
}

function changePro( elem , event ) {
    event.stopPropagation();
    event.preventDefault();
    var leftDrawer = I(".left-drawer");
    if( leftDrawer ){
        show(leftDrawer);
        hoss.Cladd(leftDrawer,"loading");
    }else{
        leftDrawer = document.createElement("div");
        leftDrawer.id = "myScroll";
        leftDrawer.onclick = function (e) {
            e.stopPropagation();
        };
        hoss.Cladd(leftDrawer,"left-drawer");
        hoss.Cladd(leftDrawer,"drawer");
        hoss.Cladd(leftDrawer,"bg-light");
        hoss.Cladd(leftDrawer,"percentHeight");
        hoss.Cladd(leftDrawer,"transit");
        hoss.Cladd(leftDrawer,"col-md-0");
        hoss.Cladd(leftDrawer,"loading");
        hoss.Cladd(leftDrawer,"no-padding");
        document.body.appendChild(leftDrawer);
    }

    hoss.ajax({page:elem.href,o:leftDrawer},1,2,function (res) {
        hoss.Clremove(leftDrawer,"loading");
    });
    setTimeout(function () {
        hoss.Clremove(leftDrawer,"col-md-0");
        hoss.Cladd(leftDrawer,"col-md-4");
    },500);
}



function dispButton(object, tools, wig) {
    var tool = tools.querySelector(".top-line");
    var body = tools.querySelector(".body");
    var header = tools.querySelector(".header");
    var hed = "<i class='fa fa-home text-green big-font right-pad'></i>";
    body.innerHTML = "";
    if (object.body !== undefined && typeof object.body === "object") body.appendChild(object.body);
    else if (object.message !== undefined) body.innerHTML = object.message;

    if (object.title !== undefined) header.innerHTML = hed + object.title;
    tool.innerHTML = "";
    var okButton = document.createElement("button");
    okButton.innerHTML = "Ok";
    okButton.className = "btn btn-success";
    okButton.onclick = function (e) {
        if (object.accept !== undefined && typeof  object.accept === "function") object.accept(e, wig, this, body, tools);
    };
    tool.appendChild(okButton);
    var cancelButton = document.createElement("button");
    cancelButton.innerHTML = "Cancel";
    cancelButton.className = "btn btn-danger cancel-button";
    cancelButton.onclick = function (e) {
        hide(wig);
        modalClose();
        if (object.cancel !== undefined && typeof  object.cancel === "function") object.cancel(e, wig, this, body);
    };
    tool.appendChild(cancelButton);
    okButton.focus();
    body.style.marginBottom = tool.offsetHeight + "px";

    if (object.create !== undefined && typeof  object.create === "function") object.create(wig, body);
}

function confirmBox(object) {
    var widget = I(".light-widget");
    var divBox = (widget !== null) ? widget.querySelector(".boxed-widget") : document.createElement("div");
    of(document.body).addClass("modal-open");
    of(divBox).addClass(object.extended?"col-md-10":"col-md-5");
    of(divBox).removeClass(object.extended?"col-md-5":"col-md-10");
    if (widget) {
        jsfadeIn(widget, "table");
    } else {
        widget = document.createElement("div");
        var boxAppender = document.createElement("div");
        boxAppender.appendChild(divBox);
        widget.appendChild(boxAppender);
        boxAppender.onclick = function (e) {
            if (e.target !== this) return;
            $(widget).fadeOut(300);
            var cancel = this.querySelector(".cancel-button");
            if (cancel) cancel.click();
        };
        var tools = document.createElement("div");
        var he = document.createElement("div");
        var bo = document.createElement("div");
        divBox.appendChild(he);
        divBox.appendChild(bo);
        divBox.appendChild(tools);
        document.body.appendChild(widget);
        of(divBox).addClass("boxed-widget col-centered super-shadow");
        of(boxAppender).addClass("wiger");
        of(he).addClass("header");
        of(bo).addClass("body").addClass("relative");
        of(tools).addClass("top-line").addClass("text-right");
        of(widget).addClass("fixedPosition").addClass("light-widget");
    }

    dispButton(object, divBox, widget);
}


function warnFirst( elem , evt ) {
    evt.preventDefault();
    var msg = $(elem).parents(".fetch-students").find(".roleValue").html();
    confirmBox({
        title:"Remove user role",
        message: "Are you sure to remove <span class='text-green'>"+msg+"</span> role ?",
        accept:function (e,widget,btn) {
            $(elem).parents(".fetch-students").hide(300);
            hoss.ajax({page:elem.href});
            $(widget).fadeOut(300);
        }
    })
}



function fadeOut( elem, time ) {
    var startOpacity = elem.style.opacity || 1;
    elem.style.opacity = startOpacity;

    (function go() {
        elem.style.opacity -= startOpacity / ( time / 100 );

        // for IE
        elem.style.filter = 'alpha(opacity=' + elem.style.opacity * 100 + ')';

        if( elem.style.opacity > 0 )
            setTimeout( go, 100 );
        else
            elem.style.display = 'none';
    })();
}



var urlGloabal = "";
function setUrl(URL) {
    urlGloabal = URL;
    console.log(urlGloabal);
}

$(function () {
    $(document).on("click",".fetch-data",function (e) {
        e.stopPropagation();
        e.preventDefault();
        var parnt = hoss.dira(this,".tabcontents","parentNode");
        if( !parnt ) return false;

        var allLi = parnt.querySelectorAll("li");
        hoss.Clremove(allLi,"active");
        hoss.Cladd(this.parentNode,"active");

        var load = parnt.querySelector(".load-data");
        if( !load ) return false;

        hoss.Cladd(load,"loading");
        hoss.ajax({page:this.href,o:load},1,2,function (res) {
            hoss.Clremove(load,"loading");
        });

    });
    $(document).on("click",".form-widget",function (e) {
        var elem = $(this);
        elem.find(".white-bundle,.white-right-bundle").animate({
                width:0
            },
            'slow',
            undefined,
            function () {
                elem.fadeOut(300);
            });
    });
   $(document).on("click",".linked-in a",function (e) {
       e.preventDefault();
       e.stopPropagation();
       var elem = $(this);
       $(".linked-in a").removeClass("imgLoads").removeClass("checked-elem");
       elem.addClass("imgLoads");
       hoss.ajax({page:this.href},1,2,function (res) {
           elem.removeClass("imgLoads").addClass("checked-elem");
       },true,function () {
           elem.removeClass("imgLoads");
       });
   });
});
function saveForm ( form , callback , isCheckForm ) {
    validate(form,function(){
        var loader = formLoader(form);
        if( isCheckForm ){
            $.ajax({
                type:"POST",
                url:form.action,
                data:$(form).serialize(),
                success:function (res) {
                    if( callback !== undefined && typeof callback === "function") callback(res,form,loader);
                    hide(loader);
                },
                error:function (res) {
                    hide(loader);
                    formNetError(form,function (l,e) {
                        hide(l);
                        saveForm(form,callback,isCheckForm);
                    });
                }
            });
            return false;
        }

        sendFile(form.action,form,"",function (res) {
            hide(loader);
            if( callback !== undefined && typeof callback === "function") callback(res,form,loader);
        },true,true,function (or) {
            hide(loader);
            formNetError(form,function (l,e) {
                hide(l);
                saveForm(form,callback,isCheckForm);
            });
        });
    });
    return false;
}
function startForm( elem , evt , forRight , reqFunc ) {
    var parent = $(elem).parents(".new-form");
    var widget = parent.find(".form-widget");
    widget.fadeIn(300);
    var findable = ( forRight ) ? ".white-right-bundle" : ".white-bundle";

    if( forRight  && !parent.find(findable).length ) {
        var elementNew = document.createElement("div");
        elementNew.onclick = function (evt) { evt.stopPropagation() };
        $(elementNew).addClass("white-right-bundle").addClass("col-md-0").addClass("percentHeight").addClass("loading");
        widget.append($(elementNew));
    }
    if( forRight === 200 && reqFunc !== undefined && typeof reqFunc === "function" ){
        $(findable).addClass("loading");
        reqFunc(widget.find(findable));
    }else if( forRight ){
        $(findable).addClass("loading");
        hoss.ajax({page:elem.value},1,2,function (res) {
            $(findable).removeClass("loading").html(res);
        })
    }
    widget.find(findable).animate({width:"40%"},'slow');

    return false;
}
function refreshPanel( elem ) {
    $(elem).parents(".tabcontents").find(".active").find("a").click();
}
function saveGroups( form ) {
    saveForm(form,function (res,frm) {
        if( res === "1" ) {
            $(".re-click").click();
            $("#addGroupModal").modal("hide");
        }else{
            formError(form,res);
        }
    },true);
    return false;
}
function refreshAndSave( form ) {
    saveForm(form,function (res,frm) {
        if( res === "1" ) {
            refreshPanel(form);
        }else{
            formError(form,res);
        }
    });
    return false;
}

function sendChecked( formObj , evt ) {
    var form = $(formObj).serialize();
    var len = formObj.querySelectorAll("input[type=checkbox]:checked").length;
    if( len < 1 ){
        alert("choose at least one");
        return false;
    }
    return startForm(formObj,evt,200,function (widget) {
        widget.html("");
        $.ajax({
            type:"POST",
            url:formObj.action,
            data:form,
            success:function (res) {
                widget.html(res).removeClass("loading");
            }
        });
    });
}

function preConf(elem,evt,msg,isNumber,isFinal){
    var title = "Approval alert";
    var div = document.createElement("div");
    div.className = "form-group has-feedback";
    var input = document.createElement("input");
    input.type = "text";
    input.placeholder = "Enter comment here";
    input.className = "form-control";
    var span = document.createElement("p");
    span.innerHTML = (isFinal) ? msg : "Are you sure to approve " + msg + " ?";
    div.appendChild(span);
    div.appendChild(input);

    confirmBox({
        title:title,
        message:div,
        accept:function (evt,wig,button,body) {
            if( !input.value.trim() ){
                input.focus();
                return false;
            }
            if( isNumber && !hoss.C_Number(input,"not number") ){
                input.focus();
                return false;
            }
            var loader = formLoader(body);
            var newLink = elem.value+encodeURIComponent(input.value.trim());
            hoss.Cladd(elem,"imgLoads");
            hoss.ajax({page:newLink},1,2,function (res) {
                hide(loader);
                hoss.Clremove(elem,"imgLoads");
                if( res === "1" ) {
                    hide(wig);
                    $(elem).parents("tr").hide().removeClass("imgLoads");
                }else{
                    span.innerHTML = res;
                    span.style.color = "red";
                }
            },true,function(es){
                hoss.Clremove(elem,"imgLoads");
                    hide(loader);
                });
        }
    })
}

function oConf(elem,evt,msg){
    evt.stopPropagation();
    evt.preventDefault();
    var div = "Are you to approve "+msg+" ?";
    confirmBox({
        title:"Accept alert",
        message:div,
        accept:function (evt,wig,button,body) {

            var loader = formLoader(body);
            var newLink = elem.href;
            hoss.Cladd(elem,"imgLoads");
            hoss.ajax({page:newLink},1,2,function (res) {
                hide(loader);
                hoss.Clremove(elem,"imgLoads");
                if( res === "1" ) {
                    hide(wig);
                    $(elem).parents("tr").hide().removeClass("imgLoads");
                }else{
                    body.innerHTML = res;
                }
            },true,function(es){
                hoss.Clremove(elem,"imgLoads");
                hide(loader);
            });
        }
    });
    return false;
}


var requestUrl="";
function setUrl(url) {
    requestUrl=url;
}

$(document).on('submit','#requestForm',function (e) {
   e.preventDefault();
   var form=$(this);
   var loader = formLoader(this);
   $.ajax({
       url:form.attr('action'),
       method:form.attr('method'),
       data:form.serialize(),
       headers: {
           'Content-type': 'application/x-www-form-urlencoded'
       }
   }).done(function (r) {
       var activeLeft = I(".hosted");
       activeLeft.click();
       hide(loader);
   }).fail(function () {
       hide(loader);
       alert("Internal server error.. try again");
   });
});


$(document).on('shown.bs.modal','#requestDetailsModal',function (e) {
    // alert("Modal shown:"+requestUrl);
    var modal=$(this);
    var modalLoading=modal.find('.modal-body').find('.modal-loading');
    var modalResult=modal.find('.modal-body').find('.bodyResult');
    modalLoading.removeClass('div-hide');
    modalResult.addClass('div-hide');
    $.ajax({
        url:requestUrl,
        method:"GET",
        dataType:'text'
    }).done(function (result) {
        modal.find('.modal-body').find('.bodyResult').html(result);
        modalLoading.addClass('div-hide');
        modalResult.removeClass('div-hide');
    }).fail(function () {
        alert("Internal server error.. try again");
    });
});




$(document).on('shown.bs.modal','#requestDetailsModalDaf',function (e) {
    // alert("Modal shown:"+requestUrl);
    var modal=$(this);
    var modalLoading=modal.find('.modal-body').find('.modal-loading');
    var modalResult=modal.find('.modal-body').find('.bodyResult');
    modalLoading.removeClass('div-hide');
    modalResult.addClass('div-hide');
    $.ajax({
        url:requestUrl,
        method:"GET",
        dataType:'text'
    }).done(function (result) {
        modal.find('.modal-body').find('.bodyResult').html(result);
        modalLoading.addClass('div-hide');
        modalResult.removeClass('div-hide');
    }).fail(function () {
        alert("Internal server error.. try again");
    });
});





$(document).on('shown.bs.modal','#requestDetailsModalIndividual',function (e) {
    // alert("Modal shown:"+requestUrl);
    var modal=$(this);
    var modalLoading=modal.find('.modal-body').find('.modal-loading');
    var modalResult=modal.find('.modal-body').find('.bodyResult');
    modalLoading.removeClass('div-hide');
    modalResult.addClass('div-hide');
    $.ajax({
        url:requestUrl,
        method:"GET",
        dataType:'text'
    }).done(function (result) {
        modal.find('.modal-body').find('.bodyResult').html(result);
        modalLoading.addClass('div-hide');
        modalResult.removeClass('div-hide');
    }).fail(function () {
        alert("Internal server error.. try again");
    });
});


$(document).on('shown.bs.modal','#requestDetailsModalLogistic',function (e) {
    // alert("Modal shown:"+requestUrl);
    var modal=$(this);
    var modalLoading=modal.find('.modal-body').find('.modal-loading');
    var modalResult=modal.find('.modal-body').find('.bodyResult');
    modalLoading.removeClass('div-hide');
    modalResult.addClass('div-hide');
    $.ajax({
        url:requestUrl,
        method:"GET",
        dataType:'text'
    }).done(function (result) {
        modal.find('.modal-body').find('.bodyResult').html(result);
        modalLoading.addClass('div-hide');
        modalResult.removeClass('div-hide');
    }).fail(function () {
        alert("Internal server error.. try again");
    });
});


function getScrollParent(element, includeHidden) {
    var style = getComputedStyle(element);
    var excludeStaticParent = style.position === "absolute";
    var overflowRegex = includeHidden ? /(auto|scroll|hidden)/ : /(auto|scroll)/;

    if (style.position === "fixed") return document.body;
    for (var parent = element; (parent = parent.parentElement);) {
        style = getComputedStyle(parent);
        if (excludeStaticParent && style.position === "static") {
            continue;
        }
        if (overflowRegex.test(style.overflow + style.overflowY + style.overflowX)) return parent;
    }

    return document.body;
}
















function jsonParse( json) {
    try{
        return JSON.parse(json);
    }catch (e){
        return [];
    }
}


function handleReport2(div,evt) {
    evt.preventDefault();
    var section = bigAndSmall();

        var col = I(".col-report");

        if( !col ) return false;

        $(col).empty();

        section.resize();


        validate(div,function () {
            var secondBox = section.secondBox();
            secondBox.isLarge();
            $(secondBox.body).addClass("loading");

            var ld = formLoader(div);

            sendFile(div.action,div,undefined,function (res) {
                $(secondBox.body).removeClass("loading");
                secondBox.setHeader("Choose columns to display on report");
                var json = jsonParse(res);
                var fields = json.fields;
                var hInfo = json.header;
                var data = json.data;

                var cols = of(fields).columns();

                var array = [];

                var list = addCols(cols,fields,
                    {
                        onTree:function (a,li,ul,element,el) {
                            //console.log(a);
                        },
                        onMenu:function (a,li,element,el) {
                            var input = document.createElement("input");
                            input.type = "checkbox";
                            input.value = el;
                            var obj = {
                                key:el,
                                value:element
                            };
                            input.onchange = function (ev) {
                                if( this.checked ){
                                    array.push(obj);
                                }else array.remove(obj);
                            };
                            var sp = document.createElement("span");
                            sp.innerHTML = element;
                            sp.style.paddingLeft = "10px";

                            a.appendChild(input);
                            a.appendChild(sp);
                        }
                    });

                secondBox.body.appendChild(list);

                function preViewReport() {


                    var t = document.createElement("textarea");
                    t.className = "form-control";

                    confirmBox({
                        title:"Enter report title",
                        body:t,
                        accept:function (e,wig) {

                            viewReport(this.body.value);

                            hide(wig);
                        }
                    });
                }

                function viewReport( title ) {
                    var tab = newTab();
                    var body = tab.document.body;

                    var container = document.createElement("section");
                    container.className = "invoice";
                    body.appendChild(container);

                    container.appendChild(reportHeader(hInfo,title));

                    var tb = tableStack({
                        onCreate:function (body,head) {
                            stackTr(array,
                                {
                                    onCreate:function (td,tr,el,i) {
                                        td.remove();
                                        var th = document.createElement("th");
                                        th.innerHTML = el.value;
                                        head.appendChild(th);
                                    }
                                });

                            stackRow(data,
                                {
                                    onCreate:function (trH,elRow) {

                                        stackTr(array,
                                            {
                                                onCreate:function (td,tr,el,i) {
                                                    var o = elRow[el.key];
                                                    var def = o !== undefined;

                                                    if( def ){
                                                        expressValue(o,td);
                                                    }else deepCols(elRow,el,td);

                                                    trH.appendChild(td);
                                                }
                                            });
                                        body.appendChild(trH);
                                    }
                                });
                        }
                    });

                    container.appendChild(tb);
                }

                var reportButton = buttonDefault("View Report");
                reportButton.onclick = function(){
                    preViewReport();
                };

                var n = reportButton.cloneNode(true);
                $(n).addClass("pull-right");
                n.onclick = function(){
                    preViewReport();
                };

                secondBox.header.appendChild(n);

                $(reportButton).addClass("btn-block");

                secondBox.body.appendChild(reportButton);

                hide(ld);
            })

        });

    col.appendChild(section.parent);

    return false;
}

function stackRow(json, callback) {
    var create = callback !== undefined && callback.onCreate !== undefined && typeof callback.onCreate === "function";
    Array.prototype.forEach.call(json, function (el, i) {
        var td = document.createElement("tr");
        if (create) callback.onCreate(td , el, i);
    });
}


function stackTr(json, callback) {
    var ul = document.createElement("tr");

    var array = [];
    var create = callback !== undefined && callback.onCreate !== undefined && typeof callback.onCreate === "function";
    Array.prototype.forEach.call(json, function (el, i) {
        var td = document.createElement("td");
        ul.appendChild(td);
        if (create) callback.onCreate(td, ul, el, i);
    });
    return ul;
}


function tableStack( callback ) {

    var ul = document.createElement("div");
    ul.className = "table-responsive";
    var table = document.createElement("table");
    table.className = "table table-striped";
    var tHead = document.createElement("thead");
    var trH = document.createElement("tr");
    tHead.appendChild(trH);
    var body = document.createElement("tbody");
    table.appendChild(tHead);
    table.appendChild(body);
    ul.appendChild(table);

    var foot = document.createElement("tfoot");
    var ftHr = document.createElement("tr");
    foot.appendChild(ftHr);

    if( callback.onFoot ) table.appendChild(foot);


    var create = callback !== undefined && callback.onCreate !== undefined && typeof callback.onCreate === "function";

    if (create) callback.onCreate(body, trH,tHead,ftHr);

    return ul;
}


function buttonDefault(title) {
    var b = document.createElement("button");
    b.className = "btn btn-default";
    b.type = "button";
    b.innerHTML = "<i class='fa fa-gavel'></i> " + title;
    return b;
}


function expressValue(obj, td) {
    if (typeof obj === "object" && obj && obj.length) {
        var array = [];
        var ul = stackList(obj,
            {
                onCreate: function (a, array1, el, i) {
                    var xp  = defValue(el);
                    a.innerHTML = xp;
                    array.push(xp);
                }
            });
        $(ul).addClass("small-li");
        td.appendChild(ul);
        return array;
    } else{
        td.innerHTML = typeof obj === "number" ? obj.toLocaleString() : obj;
        return obj;
    }
}


function reportHeader( info , title ) {
    var repHeader = document.createElement("div");
    repHeader.innerHTML = "<div class=\"row no-print\">\n" +
        "           <div class=\"col-xs-12\">\n" +
        "               <a href=\"javascript:window.print();\" class=\"btn btn-primary pull-right\">\n" +
        "                   <i class=\"fa fa-print\"></i> Print report\n" +
        "               </a>\n" +
        "           </div>\n" +
        "       </div>";
    var doc = document.createElement("div");
    doc.className = "row";
    doc.innerHTML = '\n' +
        '<div>\n' +
        '    <table class="table" style="border: none;">\n' +
        '        <thead>\n' +
        '            <tr>\n' +
        '                <td style="border-right: 2px solid #E200E2;vertical-align: middle">\n' +
        '                    <span>\n' +
        '                        <i class="fa fa-circle"></i>\n' +
        '                        <i class="fa fa-circle" style="color: #E200E2;"></i>\n' +
        '                        <i class="fa fa-circle" style="color: #E200E2;"></i>\n' +
        '                    </span>\n' +
        '                </td>\n' +
        '                <td style="vertical-align: top">\n' +
        '                    <span>Institute of Legal Practice <br>\n' +
        '                        and Development\n' +
        '                    </span>\n' +
        '                </td>\n' +
        '                <td style="vertical-align: bottom">\n' +
        '                    <small>\n' +
        '                        <em class="text-left">Excellence in Legal Practice</em>\n' +
        '                    </small>\n' +
        '                </td>\n' +
        '                <td>\n' +
        '                    <img src="/assets/images/smLogo.png"  alt="" class="img-responsive pull-right" style="width: 50px">\n' +
        '                </td>\n' +
        '            </tr>\n' +
        '        </thead>\n' +
        '    </table>\n' +
        '</div>';

    var eHeader = document.createElement("div");
    eHeader.className = "row";
    eHeader.innerHTML = "\n" +
        "            <div class=\"col-xs-12\">\n" +
        "                <h3>\n" +
        "                    <span class=\"pull-left\">\n" +
        "                        <strong>"+title+"</strong>\n" +
        "                    </span>\n" +
        "                    <span class=\"pull-right\">\n" +
        "                        Date: <small>"+info.date+"</small>\n" +
        "                    </span>\n" +
        "                </h3>\n" +
        "                <div class=\"clearfix\"></div>\n" +
        "                <hr>\n" +
        "\n" +
        "                \n" +
        "            </div>";

    var akka = document.createElement("div");
    akka.appendChild(repHeader);
    akka.appendChild(doc);
    akka.appendChild(eHeader);
    return akka;
}

function deepCols(row , el , td ) {
    var cols = of(row).columns();
    iterateJson(cols,function (elem) {
        if( row[el.key] !== undefined){
            expressValue(row[el.key],td);
        }else if( typeof row[elem] === "object" ){
            deepCols(row[elem],el,td);
        }
    });
}


function iterateJson(json, callBack) {
    Array.prototype.forEach.call(json, function (el, i) {
        if (callBack !== undefined && typeof callBack === "function") {
            callBack(el, i);
        }
    });
}

function newTab() {
    var newWindow = window.open();
    newWindow.document.head.innerHTML = document.head.innerHTML;
    return newWindow;
}

function stackList(json, callback) {
    var ul = document.createElement("ul");
    ul.className = "nav nav-stacked";
    var array = [];
    var click = callback !== undefined && callback.onClick !== undefined && typeof callback.onClick === "function";
    var create = callback !== undefined && callback.onCreate !== undefined && typeof callback.onCreate === "function";
    Array.prototype.forEach.call(json, function (el, i) {
        var li = document.createElement("li");
        var a = document.createElement("a");
        a.href = "#";
        a.onclick = function (ev) {
            $(array).removeClass("active");
            of(this.parentNode).addClass("active");
            if (click) callback.onClick(this, array, el, ev);
        };


        li.appendChild(a);
        ul.appendChild(li);
        array.push(li);

        if (create) callback.onCreate(a, array, el, i);
    });
    return ul;
}

function addCols(cols,json,callback){
    return stackList(cols,
        {
            isHidden:false,
            setHidden:function(){
                this.isHidden = !this.isHidden;
            },
            getHidden:function(){
                return this.isHidden;
            },
            onClick:function(a,arr,el,ev){
                if( a === ev.target ) ev.preventDefault();
            },
            onCreate:function (a,array,el,i) {
                var element = json[el];
                if( typeof element === "object" ){
                    var cols = of(element).columns();
                    var u2 = addCols(cols,element,callback);
                    a.parentNode.appendChild(u2);
                    u2.style.marginLeft = "30px";

                    var s = document.createElement("i");
                    s.className = "fa fa-bolt";
                    var elSp = document.createElement("span");
                    elSp.innerHTML = el;
                    elSp.style.marginLeft = "6px";
                    var angle = document.createElement("span");
                    angle.className = "fa fa-angle-down pull-right";

                    a.appendChild(s);
                    a.appendChild(elSp);
                    a.appendChild(angle);

                    var eThis = this;

                    a.parentNode.onclick = function(e){
                        e.preventDefault();
                        e.stopPropagation();

                        $(u2).slideToggle();

                        angle.className = eThis.getHidden() ? "fa fa-angle-down pull-right" : "fa fa-angle-right pull-right";

                        eThis.setHidden();
                    };

                    var o = {
                        link:a,
                        bolt:s,
                        name:elSp,
                        angle:angle
                    };

                    if( callback !== undefined && callback.onTree !== undefined && typeof callback.onTree === "function" ){
                        callback.onTree(o,a.parentNode,u2,element,el);
                    }
                }else{
                    a.parentNode.onclick = function(e){
                        e.stopPropagation();
                    };

                    if( callback !== undefined && callback.onMenu !== undefined && typeof callback.onMenu === "function" ){
                        callback.onMenu(a,a.parentNode,element,el);
                    }
                }
            }
        });
}


function bigAndSmall() {
    var doc = document.createElement("section");
    doc.className = "row";
    var div1 = document.createElement("div");
    div1.className = "col-md-4 transit";

    var div2 = document.createElement("div");
    div2.className = "col-md-8 transit";

    doc.appendChild(div1);
    doc.appendChild(div2);

    return {
        parent: doc,
        firstDiv: div1,
        secondDiv: div2,
        firstBox: function () {
            $(this.firstDiv).empty();
            var box = document.createElement("div");
            box.className = "box box-solid";

            var headerBox = document.createElement("div");
            headerBox.className = "box-header with-border";
            var h4 = document.createElement("h4");
            h4.className = "box-title";
            headerBox.appendChild(h4);

            var body = document.createElement("div");
            body.className = "box-body";

            var footer = document.createElement("div");
            footer.className = "clearfix box-footer";


            box.appendChild(headerBox);
            box.appendChild(body);

            this.firstDiv.appendChild(box);

            return {
                box: box,
                footer: footer,
                header: headerBox,
                h4: h4,
                body: body,
                bodyEmpty: function () {
                    this.body.innerHTML = "";
                },
                setHeader: function (title) {
                    this.h4.innerHTML = title;
                    return this;
                },
                addFooter: function () {
                    this.box.appendChild(this.footer);
                },
                isLarge: function (title) {
                    $(this.body).addClass("height-largest");
                }
            };
        },
        secondBox: function () {
            $(this.secondDiv).empty();
            var box = document.createElement("div");
            box.className = "box box-info";

            var headerBox = document.createElement("div");
            headerBox.className = "box-header with-border";
            var h4 = document.createElement("h4");
            h4.className = "box-title";
            headerBox.appendChild(h4);

            var body = document.createElement("div");
            body.className = "box-body";


            box.appendChild(headerBox);
            box.appendChild(body);

            this.secondDiv.appendChild(box);

            return {
                box: box,
                header: headerBox,
                h4: h4,
                body: body,
                setHeader: function (title) {
                    this.h4.innerHTML = title;
                    return this;
                },
                isLarge: function () {
                    $(this.body).addClass("height-largest");
                }
            };
        },
        resize: function () {
            if ($(this.firstDiv).hasClass('col-md-4')) {
                $(this.firstDiv).removeClass("col-md-4").addClass("col-md-1");
                $(this.secondDiv).removeClass("col-md-8").addClass("col-md-11");
            } else {
                $(this.firstDiv).addClass("col-md-4").removeClass("col-md-1");
                $(this.secondDiv).addClass("col-md-8").removeClass("col-md-11");
            }
        }
    }
}



function checkWrapper(elem) {
    var element = document.createElement("label");
    element.className = "check-container";
    element.appendChild(elem);
    var span = document.createElement("span");
    span.className = "checkmark";
    element.appendChild(span);
    return element;
}

function radioWrapper(elem,title) {
    var element = document.createElement("span");
    element.className = "t-radio";
    element.appendChild(elem);
    var uId = Math.random() * 100000000000000000;
    elem.id = uId;
    var span = document.createElement("label");
    span.textContent = title;
    span.setAttribute("for",uId.toString());
    element.appendChild(span);
    return {
        parent:element,
        label:span
    };
}


function rippleButton(title) {
    var btn = document.createElement("button");
    btn.className = "ripple pen-button bg-purple full-width";
    btn.innerHTML = title;
    return btn;
}


function jsonAccordion( json ) {
    var accordElem = json.json;
    var array = [];
    Array.prototype.forEach.call(accordElem,function (el,i) {

        var box = document.createElement("div");
        box.className = "panel box";
        var id = "collapse"+i;
        var header = document.createElement("div");
        header.innerHTML = "<h4 class=\"box-title\">\n" +
            "                <a data-toggle=\"collapse\" data-href=\"#\" data-parent=\"#accordion\" href=\"#"+id+"\" aria-expanded=\"false\" class=\"collapsed\">\n" +
            "               "+el["title"]+"\n" +
            "                </a>\n" +
            "              </h4>";
        header.className = "box-header with-border";
        box.appendChild(header);

        array.push(box);

        var body = document.createElement("div");
        body.className = "panel-collapse collapse";
        var place = document.createElement("div");
        place.className = "height-large";
        body.appendChild(place);
        box.appendChild(body);
        body.id = id;

        if( json.callback !== undefined && typeof json.callback === "function" ) json.callback(body,el);

        var classArray = ["box-default", "box-primary", "box-danger", "box-info", "box-warning"];


        var cl = "box-default", pIndex = 0;
        if (classArray[i] === undefined) {
            for (var sp = 0; sp < classArray.length; sp++) {
                if (classArray[pIndex] !== undefined) {
                    cl = classArray[pIndex];
                    pIndex++;
                    break;
                }
                if (sp === classArray.length - 2) pIndex = 0;
            }
        } else
            cl = classArray[i];

        $(box).addClass(cl);

    });

    return {
        boxArray:array,
        put:function (elem) {
            var parent = document.createElement("div");
            parent.id = "accordion";
            parent.className = "box-group";
            iterateJson(this.boxArray,function (el) {
                parent.appendChild(el);
            });
            elem.appendChild(parent);
        }
    }
}


Array.prototype.remove = function () {
    var what, a = arguments, L = a.length, ax;
    while (L && this.length) {
        what = a[--L];
        while ((ax = this.indexOf(what)) !== -1) {
            this.splice(ax, 1);
        }
    }
    return this;
};


function DesignTable(json, place , noAuto) {
    var self = this;
    this.place = place;
    this.newPlace = null;
    this.jsonForm = null;
    this.callBack = undefined;
    this.createForm = function () {
        if (json.form === undefined || typeof json.form !== "object") return;

        var jForm = this.jsonForm != null ? this.jsonForm : json.form;

        this.jsonForm = jForm;

        var head = jForm.formHead;
        var data = jForm.formData;

        if (data === undefined || typeof data !== "object"){
            console.log("error");
            return;
        }


        var columns = of(data).columns();

        var checkCondition = false;

        var form = stackForm(columns, {
            onCreate: function (grp, input, el,ib,lb) {
                if( !checkCondition ) checkCondition = isCheck(data[el]);
                handleText(grp, input, el, data,lb);
            }
        });



        var headObject = {
            content: form,
            title: head["formName"],
            route: head["saveRoute"],
            buttonText: head["buttonName"],
            isCheck: checkCondition,
            callback:this.callBack
        };

        var formElement = this.newForm(headObject);

        var colon = document.createElement("div");
        colon.className = "col-md-9 col-centered";
        colon.appendChild(formElement);

        this.place.innerHTML = "";
        this.place.appendChild(colon);

        return formElement;
    };

    this.slim = function () {
        var div = document.createElement("div");
        div.id = "issue";
        div.className = "text-center";
        div.style.marginTop = "5px";
        var div2 = document.createElement("div");
        div2.className = "text-center in-block";
        var span = document.createElement("span");
        span.textContent = "!";
        var label1 = document.createElement("label");
        label1.textContent = "No Content Found...";
        var p = document.createElement("p");
        p.textContent = "Click add new to add more contents of this page";
        div2.appendChild(span);
        div.appendChild(div2);
        div.appendChild(label1);
        div.appendChild(p);
        return div;
    };

    this.newForm = function (obj) {
        var valid = obj !== undefined;
        var title = valid && obj.title !== undefined ? obj.title : "Update form";
        var content = valid && obj.content !== undefined && typeof obj.content === "object" ? obj.content : document.createElement("div");
        var solid = document.createElement("div");
        solid.className = "box box-primary";
        var header = document.createElement("div");
        header.className = "box-header with-border";
        header.innerHTML = "<h3 class=\"box-title\"><i class=\"fa fa-edit\"></i> " + title + "</h3>";
        solid.appendChild(header);

        var form = document.createElement("form");
        form.action = obj.route;
        form.method = "POST";
        var isCheck = obj.isCheck !== undefined && obj.isCheck;
        form.onsubmit = function () {
            if( obj.callback !== undefined && typeof obj.callback === "function" ){
                obj.callback(this,obj);
                return false;
            } else return allForms(this, isCheck);
        };
        var body = document.createElement("div");

        form.appendChild(body);
        body.className = "box-body";

        body.appendChild(content);
        solid.appendChild(form);

        var footer = document.createElement("div");
        footer.className = "box-footer clearfix";
        footer.innerHTML = "<button type=\"submit\" class=\"pull-right btn btn-default\">" + obj.buttonText + " <i class=\"fa fa-arrow-circle-right\"></i></button>";
        form.appendChild(footer);

        return solid;
    };

    this.setDefaultJson = function () {
        this.jsonForm = json.form;
    };

    this.init = function () {
        this.place.innerHTML = "";
        var nForm = document.createElement("div");


        nForm.className = "new-form height-large no-shadow";
        var widget = document.createElement("div");
        widget.className = "form-widget didHide";
        nForm.appendChild(widget);
        var handle = document.createElement("div");
        handle.className = "table-handle";


        this.newPlace = handle;

        var deleteLink = document.createElement("a");
        deleteLink.className = "btn btn-sm margin-left bg-light-blue-gradient";
        deleteLink.name = "big-error";
        deleteLink.innerHTML = "<i class=\"fa fa-refresh bg-light-blue-gradient\"></i>";

        var button = document.createElement("button");
        button.className = "btn btn-default btn-sm only-func";
        button.innerHTML = "<i class='fa fa-plus'></i> Create new";
        button.onclick = function (ev) {
            startForm(this, ev, undefined, false, function (res, node) {
                self.place = node;
                self.setDefaultJson();
                self.createForm();
            });
        };

        if( json["newDisabled"] ){
            button.disabled = true;
        }


        handle.appendChild(button);
        //handle.appendChild(deleteLink);
        nForm.appendChild(handle);

        place.appendChild(nForm);

        this.createListTable();
    };


    this.createListTable = function () {
        var isTable = json.page !== undefined && typeof json.page === "object";

        if (!isTable) {
            console.log(json);
            return;
        }

        var table = json.page;


        var cols = of(table).columns();

        //cols.sort();

        var rTable = stackTable(table, {
            onCreate: function (body, tHead, el, inc) {
                var valueTr = stackTr(cols, {
                    onCreate: function (td, tr, eli, increment) {
                        var o = el[eli];
                        if (typeof  o !== "object") return;
                        var isHtml = o["isHtml"] !== undefined;
                        var value = o["value"];
                        var row = document.createElement("div");

                        var c1 = document.createElement("div");
                        c1.className = "col-md-6";
                        var c2 = document.createElement("div");
                        c2.className = "col-md-6";

                        td.appendChild(row);
                        if( typeof value === "object" && o.isForm){
                            var btn = buttonDefault("Approve");
                            btn.onclick = function(e){
                                var cols = of(value).columns();
                                var form = stackForm(cols, {
                                    onCreate: function (grp, input, el,ib,lb) {
                                        handleText(grp, input, el, value,lb);
                                    }
                                });
                                var frm = document.createElement("form");
                                frm.className = "relative";
                                frm.onsubmit = function(){return false};
                                frm.method = "POST";
                                frm.action = value["saveRoute"];
                                frm.appendChild(form);
                                confirmBox({
                                    title:"Confirm",
                                    body:frm,
                                    accept:function (e,wig) {
                                        var ld = formLoader(frm);
                                        saveForm(frm,function (res) {
                                            hide(ld);
                                            if(res === "1"){
                                                hide(wig);
                                                refreshPanel(td);
                                            }else{
                                                formError(wig,res);
                                            }
                                        })
                                    }
                                })
                            };
                            td.appendChild(btn);
                        }else if( typeof value === "object" && value.page !== undefined ){
                            var page = value.page;
                            var ul = stackDiv(page,
                                {
                                    onCreate:function (a,array,lst ,ix ) {
                                        if( typeof lst !== "object" ) return;

                                        var din = document.createElement("div");
                                        din.className = "relative table-list";

                                        var abs = document.createElement("div");
                                        abs.className = "absolute bg-light five-pad transit li-button five-radius shadow-deep";
                                        din.appendChild(abs);

                                        var columns = of(lst).columns();
                                        var ulLi = stackList(columns,
                                            {
                                                onCreate:function (a,arr,elx,i) {
                                                    var val = lst[elx];

                                                    if( typeof val !== "object" ) return;

                                                    var v = document.createElement("span");
                                                    v.innerHTML = elx + " : ";
                                                    v.className = "text-bold";
                                                    var vp = document.createElement("span");
                                                    vp.innerHTML = val.value;
                                                    if( val.button ){
                                                        vp.className = "classic";
                                                        abs.appendChild(vp);
                                                        a.parentNode.remove();
                                                    }else{
                                                        vp.className = "pull-right";
                                                        a.appendChild(v);
                                                        a.appendChild(vp);
                                                    }
                                                }
                                            });
                                        $(ulLi).addClass("bg-light")
                                            .addClass("five-radius")
                                            .addClass("bottom-bottom")
                                            .addClass("border-light-gray");


                                        din.appendChild(ulLi);

                                        row.appendChild(din);

                                    }
                                });
                        }else {
                            var x = isHtml ? td.innerHTML = value : td.textContent = value;
                        }

                        $(td).find(".d-form").click(function (e) {
                            startForm(this, e, true, false, function (res, node,ld) {
                                self.place = node;
                                $(self.place).empty().append(self.padSlim());
                                hide(ld);
                                self.jsonForm = jsonParse(res);
                                self.createForm();
                            });
                        });

                        $(td).find(".delete-btn").click(function (e) {
                            var dlt = document.createElement("div");
                            dlt.className = "table-responsive";
                            var span = document.createElement("div");
                            span.className ="big-font text-bold bottom-bottom";
                            span.innerHTML = "Delete this";
                            var table = document.createElement("table");
                            table.className = "table table-striped";
                            var bdy = document.createElement("tbody");

                            if( !tr ) return false;

                            var trNew = tr.cloneNode(true);

                            bdy.appendChild(trNew);
                            table.appendChild(bdy);
                            dlt.appendChild(span);
                            dlt.appendChild(table);

                            alertBody(this,dlt);
                        });

                        if (!inc) {
                            var th = document.createElement("th");
                            th.textContent = eli;
                            tHead.appendChild(th);
                        }

                    }
                });
                body.appendChild(valueTr);
            }
        });

        if (!table.length) {
            rTable = this.slim();
        }

        this.newPlace.appendChild(rTable);


    };

    this.padSlim = function () {
        var pad = document.createElement("div");
        pad.className = "ten-pad";
        pad.appendChild(this.slim());
        return pad;
    };

    if( noAuto === undefined ) this.init();
}


function stackForm(json, callback) {
    var form = document.createElement("div");
    form.className = "form-group";
    var array = [];
    var click = callback !== undefined && callback.onClick !== undefined && typeof callback.onClick === "function";
    var create = callback !== undefined && callback.onCreate !== undefined && typeof callback.onCreate === "function";
    Array.prototype.forEach.call(json, function (el, i) {
        var group = document.createElement("div");
        group.className = "form-group relative";
        var label = document.createElement("label");
        label.className = "label-class";
        label.innerHTML = el;
        var input = document.createElement("input");
        input.autocomplete = "off";
        input.className = "form-control";
        input.onclick = function (ev) {
            $(array).removeClass("active");
            of(this.parentNode).addClass("active");
            if (click) callback.onClick(this, array, el, ev);
        };


        group.appendChild(label);
        array.push(group);


        form.appendChild(group);

        if (create) callback.onCreate(group, input, el, i, label);
    });
    return form;
}

function isCheck( elem ) {
    var valid = elem !== undefined;
    if( !valid ) return false;
    var check = elem["isCheck"];
    var group = elem["grouped"];
    return valid && check !== undefined && check || valid && group !== undefined && group;
}



function handleText(grp, input, el, data , labelText ) {
    var obj = data[el];
    if (obj === undefined || typeof  obj !== "object"){
        grp.remove();
        return {input:input};
    }
    var value = obj.value;
    var dValue = obj.defaultValue;


    var selectCondition = typeof value === "object" && value !== null;
    var checkListCondition = obj.isCheck !== undefined && obj.isCheck && !dValue;
    var groupCondition = obj.grouped !== undefined && obj.grouped;
    var variable = input;

    if (selectCondition) {
        if (checkListCondition) {
            var checkCondition = checkListCondition;
            variable = stackCheckBox(value, undefined, obj.name, dValue,labelText);
            grp.appendChild(variable);
        } else if ( groupCondition ) {
            variable = stackGroupedBox(value, undefined, obj.name, dValue);
            grp.appendChild(variable);
        } else {
            variable = stackSelect(value, undefined, obj.name, dValue, el,obj.search);
            grp.appendChild(variable);
        }

    } else if (obj.isDownload !== undefined && obj.isDownload) {

        variable = downLoadLink("Download", value);
        grp.appendChild(variable);

    } else if (obj.type && obj.type.toLowerCase() === "textarea") {
        var tArea = document.createElement("textarea");
        $(tArea).addClass("form-control").addClass(obj.className);
        tArea.name = obj.name;
        tArea.placeholder = el;
        tArea.value = obj.value === undefined ? null : obj.value;
        variable = tArea;
        grp.appendChild(tArea);
    } else {
        input.name = obj.name;
        input.type = obj.type;
        input.placeholder = el;
        input.value = value === undefined ? null : value;
        if (obj.id !== "" && obj.id !== undefined) input.id = obj.id;
        $(input).addClass(obj.className).attr("data-name", el);

        if(input.type === "checkbox" ){
            var input2  = document.createElement("input");
            input2.type = obj.type;
            input2.checked = obj.checked !== undefined && obj.checked;
            input2.name = obj.name;
            input2.value = input.value;
            variable = input = input2;
            var wrapper = document.createElement("div");

            var wr = checkWrapper(input2);
            if( obj.checkValue !== undefined && obj.checkValue ){
                var sV = document.createElement("span");
                sV.innerHTML = obj.checkValue;
                wr.appendChild(sV);
            }

            wrapper.appendChild(wr);
            grp.appendChild(wrapper);
        }else grp.appendChild(input);

        if (obj.isNumber !== undefined && obj.isNumber) {
            input.setAttribute("number", "1");
        }

        if (obj.isCalendar !== undefined && obj.isCalendar) {
            $(input).datepicker({
                autoclose: false,
                format: "yyyy-mm-dd"
            });
        }

        if (obj.timePicker !== undefined && obj.timePicker) {
            input.onkeypress = function(){
                return false;
            };
            $(input).clockpicker({
                autoclose: true
            });
        }
    }
    if (obj.disabled !== undefined && obj.disabled) {
        variable.disabled = obj.disabled;
    }

    if (obj.readOnly !== undefined && obj.readOnly) {
        variable.readOnly = obj.readOnly;
    }

    if(obj.escape !== undefined && obj.escape ){
        variable.setAttribute("data-escape","1");
    }

    return {
        input:input
    }
}

function stackCheckBox(json, callback, name, dValue, labelText) {
    var box = document.createElement("div");
    var searchValue = document.createElement("div");
    var iSearch = document.createElement("input");
    iSearch.placeholder = "Filter rows";
    iSearch.className = "form-control input-sm";
    searchValue.appendChild(iSearch);

    var defaultValue = document.createElement("option");
    defaultValue.value = "";
    defaultValue.innerHTML = "-- Select from list here --";

    var selectedData = document.createElement("div");
    selectedData.className = "form-group";

    var label = document.createElement("span");
    label.className = "text-center text-bold pull-right pointer";

    var sm = document.createElement("span");
    sm.innerText = "Selected list";
    var badge = document.createElement("span");
    badge.className = "badge bg-green margin-left";
    badge.innerText = 0;

    label.appendChild(sm);
    label.appendChild(badge);

    var selectedDiv = document.createElement("div");
    hide(selectedDiv);

    labelText.appendChild(label);
    selectedData.appendChild(selectedDiv);

    label.onclick = function (ev) {
        $(selectedDiv).slideToggle();
    };


    box.appendChild(selectedData);

    box.appendChild(searchValue);

    var unSelected = document.createElement("div");
    box.appendChild(unSelected);

    var searchObject = {
        json: json,
        gang: 0,
        keyUp: function (js) {
            checkValues(js, unSelected, selectedDiv, callback, name, dValue, this);
        },
        removeKey: function (key) {
            ++this.gang;
            badge.innerText = this.gang.toString();
            this.json.remove(key);
        },
        addKey: function (key) {
            --this.gang;
            badge.innerText = this.gang.toString();
            this.json.push(key);
        }
    };

    iSearch.setAttribute("data-escape", "1");

    iSearch.onkeyup = function (ev) {
        var js = searchJson(searchObject.json, "print", this.value);
        unSelected.innerHTML = "";
        $(selectedDiv).hide(300);
        searchObject.keyUp(js);
    };


    checkValues(json, unSelected, selectedDiv, callback, name, dValue, searchObject);

    return box;
}


function checkValues(json, unSelected, selectedDiv, callback, name, dValue, otherJson) {
    var array = [];
    var click = callback !== undefined && callback.onClick !== undefined && typeof callback.onClick === "function";
    var create = callback !== undefined && callback.onCreate !== undefined && typeof callback.onCreate === "function";
    Array.prototype.forEach.call(json, function (el, i) {
        var option = document.createElement("div");
        $(option).addClass("form-group");
        var id = el["id"] !== undefined ? el["id"] : 0;

        var input = document.createElement("input");
        input.type = "checkbox";
        input.name = name;
        input.value = id;
        var wrapper = checkWrapper(input);

        itemMove(input, option, unSelected, selectedDiv, el, otherJson);


        var span = document.createElement("span");
        span.innerHTML = defValue(el);

        option.selected = id === dValue;

        option.appendChild(wrapper);
        option.appendChild(span);

        unSelected.appendChild(option);
        array.push(option);

        if (create) callback.onCreate(option, array, el, i);
    });
}



function stackGroupedBox(json, callback, name, dValue) {
    var box = document.createElement("div");
    $(box).addClass("group-stack");
    Array.prototype.forEach.call(json, function (el, i) {
        if (typeof el === "object") {
            var og = el["combo"];
            var doc = document.createElement("div");

            var parentObject = [];
            var frm = stackForm(og,
                {
                    onCreate: function (grp, inputX, elem, i, label) {
                        label.remove();
                        if (typeof elem !== "object") return;
                        var columns = of(elem).columns();
                        var fx = stackForm(columns,
                            {
                                onCreate: function (group, inputT, elex, i, label) {
                                    var text = handleText(group, inputT, elex, elem, label);
                                    parentObject.push(text.input);
                                }
                            });
                        doc.className = "vertical-margin five-radius stack";
                        grp.appendChild(fx);
                        doc.appendChild(grp);
                        box.appendChild(doc);
                    }
                });

            //jsonTextCreate(parentObject,input,name);
        } else {
            console.log(el);
        }
    });
    return box;
}

function jsonTextCreate(array, ex, parent) {
    Array.prototype.forEach.call(array, function (el) {
        el.oninput = function () {
            var object = {};
            var self = this;
            Array.prototype.forEach.call(array, function (elem) {
                object[elem.name] = elem.value.trim();
                if (elem.type === "checkbox" && self.type !== "checkbox") elem.checked = true;
            });
            ex.value = JSON.stringify(object);
            if (!this.checked && this.type === "checkbox") ex.value = "";
        }
    });
}

function defValue(el) {
    el = typeof el === "object" && el[0] !== undefined ? el[0] : el;
    return typeof el !== "object" ? el : el["name"] !== undefined ? el["name"] : el["print"] !== undefined ? el["print"] : el;
}

function stackSelect(json, callback, name, dValue, xName , search ) {
    var select = document.createElement("select");
    select.className = "form-control";
    select.name = name;
    var defaultValue = document.createElement("option");
    defaultValue.value = "";
    defaultValue.innerHTML = "-- Select from list here --";
    select.appendChild(defaultValue);
    if (xName !== undefined) $(select).attr("data-name", xName);
    var array = [];
    var click = callback !== undefined && callback.onClick !== undefined && typeof callback.onClick === "function";
    var create = callback !== undefined && callback.onCreate !== undefined && typeof callback.onCreate === "function";
    Array.prototype.forEach.call(json, function (el, i) {
        var valueHtml = defValue(el);
        var option = document.createElement("option");
        var id = el["id"] !== undefined ? el["id"] : 0;
        option.value = id.toString();
        option.innerHTML = valueHtml;

        option.selected = id === dValue;

        select.appendChild(option);
        array.push(option);

        if (create) callback.onCreate(option, array, el, i);
    });
    return modalSelection({
        title: xName,
        data: json,
        name: name,
        dValue:dValue,
        search:search
    });
}


function listComponent( elem ) {
    var option = $('option:selected', elem).attr('data-value');
    $.ajax({
        url:option,
        success:function (res) {
            var el = $("#component-data");
            el.empty();
            el.html( "<option value=\"\">--select component--</option>\n" +
                "                                                    <option value=\"0\">All</option>");
            res.forEach(function (obj) {
                var option = document.createElement("option");
                option.id = obj.id;
                option.value = obj.id;
                option.innerText = obj.compName;
                el.append(option);
            })
        }
    });
}

function listComponents( elem ) {
    if (elem.value) {
        var page = "/module/getComponent/1/" + elem.value;
        hoss.ajax({page: page}, "", "", function (res) {
            var json = JSON.parse(res);
            var mood = document.getElementById("componentId");
            mood.innerHTML = "";
            var option = document.createElement("option");
            option.value = "";
            option.innerHTML = "-- Select component --";
            mood.appendChild(option);
            for (var i = 0; i < json.length; i++) {
                var option = document.createElement("option");
                option.innerHTML = json[i].compName;
                option.value = json[i].id;
                mood.appendChild(option);
            }

        });
    }
}


function getStudent( elem ) {
    var option = elem.getAttribute("href").replace(0,$(".trainVal").val());

    $.ajax({
        url:option,
        success:function (res) {
            $("#addGroupModal").modal("show");
            $(".data-stu-list").html(res);
        }
    });
    return false;
}
function getStudent2( elem ) {
    var option = elem.getAttribute("href");

    $.ajax({
        url:option,
        success:function (res) {
            $("#addGroupModal2").modal("show");
            $(".data-stu-list-s").html(res);
        }
    });
}

function modalSelection(opt) {
    var div = document.createElement("div");
    div.className = "modal-select pointer relative";
    var span = document.createElement("span");
    span.textContent = opt.title;
    span.name = "uri";
    div.tabIndex = 0;
    span.className = "selected-span";
    div.appendChild(span);
    var caret = document.createElement("span");
    caret.className = "caret";

    var list = document.createElement("div");
    list.className = "select-list hidden full-width";

    var valueInput = document.createElement("input");
    valueInput.className = "hidden";
    $(valueInput).attr("data-name", opt.title);
    valueInput.name = opt.name;
    valueInput.type = "text";
    valueInput.oninput = function (ev) {
        console.log(this.value);
    };

    div.appendChild(valueInput);


    var divInput = document.createElement("div");
    divInput.className = "five-pad";

    var input = document.createElement("input");
    input.className = "form-control";
    input.placeholder = "Search ...";
    input.setAttribute("data-escape", "1");
    divInput.appendChild(input);

    var ul = document.createElement("ul");
    ul.className = "select-ul";

    list.appendChild(divInput);

    var innerList = document.createElement("div");
    innerList.className = "in-list";

    list.appendChild(innerList);
    innerList.appendChild(ul);

    div.onclick = function () {
        if ($(list).hasClass("hidden")) {
            $(list).removeClass("hidden")
        } else {
            $(list).addClass("hidden")
        }
    };

    document.addEventListener("click", function (e) {
        if (e.target !== div && e.target !== span) $(list).addClass("hidden");
    });

    list.onclick = function (ev) {
        ev.stopPropagation();
    };

    var array = [];
    var index = 0;
    var dValue = opt.dValue;

    function makeChange( indx , value , text , li ) {
        span.innerHTML = text;
        valueInput.value = value;
        $(array).removeClass("chosen");
        $(li).addClass("chosen");
        dValue = value;
        index = indx;
    }

    function getData(data,dValue){
        array = [];
        ul.innerHTML = "";
        Array.prototype.forEach.call(data, function (el, k) {
            var li = document.createElement("li");
            var value = defValue(el);
            li.innerHTML = value;
            li.tabIndex = 0;
            li.onclick = function () {
                makeChange(k,el.id,value,this);
                $(list).addClass("hidden");
            };
            li.onfocus = function () {
                makeChange(k,el.id,value,this);
            };

            if( el.id === dValue ) makeChange(k,el.id,value,li);

            array.push(li);
            ul.appendChild(li);
        });

    }

    getData(opt.data,dValue);

    var request = null;
    var backUp = opt.data;

    input.onkeyup = function () {
        if( opt.search && this.value ){
            var o = this;
            $(o).addClass("imgLoads");
            var search = opt.search.replace("~q",encodeURIComponent(o.value));
            if( request !== null ){
                request.abort();
            }
            request = hoss.ajax({page:search},1,2,function (res) {
                var data = jsonParse(res);
                getData(data,dValue);
                $(o).removeClass("imgLoads");
            });
        }else if( opt.search ){
            getData(backUp,dValue);
        }else{
            var data = searchJsonArrayCol(opt.data,["name","print"],this.value);
            getData(data,dValue);
        }
    };



    var obj;
    div.onkeydown = function (ev) {
        ev.stopPropagation();

        if (ev.code === "ArrowDown" || ev.code === "ArrowUp" ) ev.preventDefault();
    };
    div.onkeyup = function (ev) {
        ev.stopPropagation();
        if (ev.code === "ArrowDown") {
            index = array.length > index - 2 ? ++index : index;
            obj = array[index];

            if( !obj ) {
                --index;
                return;
            }

            if ($(list).hasClass("hidden")) {
                obj.click();
            }else{
                obj.focus();
            }
        }else if( ev.code === "ArrowUp" ){
            index = array.length > index - 1 ? --index : index;

            obj = array[index];

            if( !obj ) {
                ++index;
                return;
            }

            if ($(list).hasClass("hidden")) {
                obj.click();
            }else{
                obj.focus();
            }
        }else if( ev.code === "Enter" ){
            obj = array[index];

            if( !obj ) return;

            obj.click();
        }
    };


    div.appendChild(list);


    div.appendChild(caret);

    setTimeout(function () {

        valueInput.form.addEventListener("reset", function () {
            span.innerHTML = opt.title;
        });
    },2000);

    return div;
}

function Excel() {
    this.dataArray = [];
    this.headerArray = [];
    this.CsvString = "";
    this.fileName = new Date().getTime().toString();

    this.header = function () {
        var csvString = this.CsvString;
        this.headerArray.forEach(function(RowItem, RowIndex) {
            csvString += RowItem + ',';
        });

        csvString += "\r\n";

        this.CsvString = csvString;
    };

    this.data = function () {
        var csvString = this.CsvString;
        var header = this.headerArray;

        this.dataArray.forEach(function(RowItem) {
            RowItem.forEach(function(el) {
                csvString += '"'+ el + '",';
            });
            csvString += "\r\n";
        });

        this.CsvString = csvString;
    };

    this.exportToCsv = function() {

        this.header();

        this.data();

        this.CsvString = "data:application/csv," + encodeURIComponent(this.CsvString);
        var x = document.createElement("A");
        x.setAttribute("href", this.CsvString );
        hide(x);
        x.setAttribute("download",this.fileName+".csv");
        document.body.appendChild(x);
        x.click();
    };

}




$(document).on('submit','#submitFormComp',function (e) {
    e.preventDefault();
    var form=$(this);
    var btn=$(document).find('#btnMax');
    btn.button("loading");

    $.ajax({
        url:form.attr("action"),
        method:form.attr("method"),
        data:form.serialize()
    }).done(function (data) {
        if(data==='1'){
            $('.modal').modal('hide');
            $('.hosted').click();
        }else{
            alert(data);
        }
        btn.button("reset");
    }).fail(function () {
        btn.button("reset");
        alert("Errors.. Try again");
    });

});
