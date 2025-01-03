(function () {

    window.onload = function (e) {
        blurer();
        refreshChat();
        this.onclick = function () {
            var iNeedYou = I(".list-applied");
            if (iNeedYou) remover(iNeedYou);
            hide(all(".default-toggle,.left-form #form1"));
            hideDrawers();
        };
        modalSelect(".modal-select");
        var iStart = I(".positioned");
        var dataD = I(".data-deposit");

        var sdBar = I(".sidebar-toggle");
        if (sdBar) sdBar.click();

        var mayinya = I("#mayinya");
        if (mayinya) applied(mayinya);

        var aTop = all(".clicker");
        Array.prototype.forEach.call(aTop, function (el) {
            el.onclick = function (e) {
                e.preventDefault();
                var depo = I("#tabs1");
                var depos = I(".data-deposit");
                if (!depo) {
                    var depo = document.createElement("div");
                    depo.id = "tabs1";
                    depo.className = "tabcontents";
                    depos.appendChild(depo);
                }
                hide(all(".tabcontents"));
                show(depo);
                hoss.ajax({page: this.href, o: depo});
            }
        });

        noProp();

        var click = null;
        var tint = false;
        var allLeft = document.querySelectorAll(".sidebar-menu a");
        for (var i = 0; i < allLeft.length; i++) {
            var v = allLeft[i];
            var refs = v.getAttribute("href");
            if (v.getAttribute("no-event")) continue;
            allLeft[i].onclick = function (e) {
                e.preventDefault();
                var aLink = this;


                if (aLink.getAttribute("noded") === "true") return false;

                hoss.Clremove(allLeft, "hosted");

                hoss.Cladd(this, "hosted");

                var depos = I(".data-deposit");
                var tab = this.getAttribute("page");
                hide(all(".tabcontents"));
                var ref = this.getAttribute("href");
                hoss.updateUrl(encodeURIComponent(ref));
                var li = this.parentNode;
                var xx = I(".sidebar-menu");
                if (xx) {
                    var qp = xx.querySelector(".activity");
                    var ac = (qp != null) ? hoss.Clremove(qp, "activity") : 0;
                }
                hoss.Cladd(li, "activity");
                var chi = li.getAttribute("dataDiv");


                var elep = depos.querySelector(tab);
                if (chi) {
                    var cForm = I(".list-applied");
                    if (cForm) {
                        var nexing = cForm.className.split(" ").indexOf("po-lefted");
                        if (nexing > 0) {
                            hoss.Clremove(cForm, "po-lefted");
                            hoss.Cladd(cForm, "so-lefted");
                        }
                        var inps = cForm.querySelector("input");
                        if (inps) inps.focus();
                        if (elep) show(elep);
                    }
                }


                var addition = aLink.getAttribute("data-addition") != null;
                if (elep) {
                    if (addition) return additionTask(elep, aLink);
                    show(elep);
                    hoss.Cladd(elep, "loading");
                    hoss.ajax({page: ref, o: elep}, 1, 2, function (res) {
                        hoss.Clremove(elep, "loading");
                    });
                } else {
                    var newTab = document.createElement("div");
                    newTab.id = (tab != null) ? tab.split("#").pop() : "tabxer" + Math.ceil(Math.random() * 1000);
                    if (tab == null) aLink.setAttribute("page", "#" + newTab.id);
                    hoss.Cladd(newTab, "loading");
                    hoss.Cladd(newTab, "tabcontents");
                    depos.appendChild(newTab);
                    if (ref !== "" && ref !== "#" && ref !== "?") {


                        if (addition) return additionTask(newTab, aLink);

                        hoss.ajax({page: ref, o: newTab}, 1, 2, function (res) {
                            hoss.Clremove(newTab, "loading");
                        });
                    }
                }
                return false;
            };
            var urlForTab = window.location.pathname.toString().split("#")[0];
            if (hasClass(v, "request") && !v.getAttribute("noded") && v.getAttribute("href").length > 10) {
                click = v;
                v.click();
            }
            if (urlForTab && ("/" + refs) === decodeURIComponent(urlForTab)) {
                click = v;
                tint = true;
            }
        }
        if (click) click.click();
    }
})();


function goTop() {
    var top = document.body;
    globalTop(top, 0);
}


function additionTask(div, aLink) {
    var attr = aLink.getAttribute("data-addition");
    $(div).removeClass("loading");
    switch (attr) {
        case "reports": {
            handleReport(div, aLink);
            break;
        }
        case "extra-report": {
            handleExtraReport(div, aLink);
            break;
        }
    }

    return false;
}


function handleReport(div, aLink) {
    var section = bigAndSmall();

    var firstBox = section.firstBox();

    firstBox.setHeader("....");
    firstBox.isLarge();


    var loader = formLoader(firstBox.box);

    hoss.ajax({page: aLink.href}, 1, 2, function (res) {
        hide(loader);
        var json = jsonParse(res);
        firstBox.setHeader(json.title);
        var o = {
            json: json.nodeList,
            callback: function (div, obj) {
                var c = obj["content"];
                var form = {};
                form.form = c;
                var table = new DesignTable(form, div, true);
                table.callBack = function (e, b) {
                    handleInReport(e, section.secondBox());
                };
                var el = table.createForm();
                $(div).empty().addClass("five-pad");
                div.appendChild(el);
            }
        };
        var accordion = jsonAccordion(o);
        accordion.put(firstBox.body);
    });

    div.appendChild(section.parent);

    return false;
}

function handleInReport(e, secondBox) {
    validate(e, function () {
        secondBox.isLarge();
        $(secondBox.body).addClass("loading");

        var ld = formLoader(e);

        sendFile(e.action, e, undefined, function (res) {
            var json = jsonParse(res);

            getReport(secondBox,json,ld);

        })

    });


}


function getReport( secondBox , json , ld ) {
    $(secondBox.body).removeClass("loading");
    secondBox.setHeader("Choose columns to display on report");
    var fields = json.fields;
    var hInfo = json.header;
    var data = json.data;

    var cols = of(fields).columns();

    var array = [];

    var list = addCols(cols, fields,
        {
            onTree: function (a, li, ul, element, el) {
                //console.log(a);
            },
            onMenu: function (a, li, element, el, ft) {
                var input = document.createElement("input");
                input.type = "checkbox";
                input.value = el;
                var obj = {
                    key: el,
                    father: ft,
                    path:ft+"."+el,
                    isN:true,
                    isNumber:function () {
                        return this.isN;
                    },
                    number:0,
                    notNumber:function(){
                        this.isN = false;
                    },
                    setNumber:function ( n ) {
                        this.number = typeof n === "number" ? this.number + n : this.number;
                    },
                    value: element
                };
                input.onchange = function () {
                    array.remove(obj);
                    if (this.checked) {
                        array.push(obj);
                    }
                };
                a.removeAttribute("href");
                var sp = document.createElement("div");
                sp.innerHTML = element;
                sp.className = "in-block";
                sp.ondblclick = function () {
                    this.setAttribute("contenteditable", "true");
                    var io = {
                        onHide: function () {
                            a.appendChild(sp);
                            $(sp).removeClass("io-input");
                            sp.removeAttribute("contenteditable");
                        }
                    };
                    $(sp).addClass("io-input");
                    var o = globalWidget(io);
                    o.add(sp);
                };
                sp.onkeyup = function () {
                    obj.value = this.textContent;
                    if (input.checked) {
                        array.remove(obj);
                        array.push(obj);
                    }
                };
                sp.style.paddingLeft = "10px";

                a.appendChild(input);
                a.appendChild(sp);

            }
        });

    secondBox.body.appendChild(list);

    var v = {
        value : 1,
        setValue:function (v) {
            this.value = v;
        }
    };

    function preViewReport() {


        var tGroup = document.createElement("div");
        tGroup.className = "form-group";
        var t = document.createElement("textarea");
        t.className = "form-control";
        t.placeholder = "Enter report title";
        tGroup.appendChild(t);
        var div = document.createElement("div");
        var i1 = input("radio","name");
        var i2 = input("radio","name");
        var i3 = input("radio","name");
        var formGroup = document.createElement("div");
        formGroup.className = "form-group";
        var formGroup2 = document.createElement("div");
        formGroup2.className = "form-group";
        var formGroup3 = document.createElement("div");
        formGroup3.className = "form-group";
        var inp = radioWrapper(i1,"View report");
        var inp2 = radioWrapper(i2,"Export report to excel");
        var inp3 = radioWrapper(i3,"View report && Export to excel");
        div.appendChild(tGroup);
        formGroup.appendChild(inp.parent);
        div.appendChild(formGroup);
        formGroup2.appendChild(inp2.parent);
        div.appendChild(formGroup2);
        formGroup3.appendChild(inp3.parent);
        div.appendChild(formGroup3);

        i1.checked = true;
        v.setValue(1);

        i1.onchange = function (ev) {
            v.setValue(1);
        };

        i2.onchange = function (ev) {
            v.setValue(2);
        };

        i3.onchange = function (ev) {
            v.setValue(3);
        };



        confirmBox({
            title: "Enter report title",
            body: div,
            accept: function (e, wig) {

                if(!t.value.trim()) {
                    t.focus();
                    return;
                }

                modalClose();

                viewReport(t.value);

                hide(wig);

            }
        });
    }

    function viewReport(title) {

        var tab,body;
        if( v.value !== 2 ) {
            tab = newTab();
            body = tab.document.body;
        }else{
            body = document.createElement("div");
        }

        var container = document.createElement("section");
        container.className = "invoice";
        body.appendChild(container);

        container.appendChild(reportHeader(hInfo, title));

        var csvArray = [];
        var csvHeader = [];
        var tb = tableStack({
            onFoot:true,
            onCreate: function (body, head,tHead,foot) {
                $(body).addClass("table-14");
                stackTr(array,
                    {
                        onCreate: function (td, tr, el, i) {
                            td.remove();
                            var th = document.createElement("th");
                            th.textContent = el.value;
                            head.appendChild(th);
                            csvHeader.push(el.value);
                        }
                    });

                stackRow(data,
                    {
                        onCreate: function (trH, elRow) {

                            var cSub = [];
                            stackTr(array,
                                {
                                    onCreate: function (td, tr, el, i) {
                                        var o = elRow[el.key];
                                        var def = o !== undefined && el.father === undefined;

                                        var fValue;

                                        if (def) {
                                            fValue = expressValue(o, td);
                                        } else fValue = deepValues(elRow, el, td);

                                        if( typeof fValue === "number" && el.isNumber() ) {
                                            el.setNumber(fValue);
                                        }else el.notNumber();

                                        trH.appendChild(td);

                                        fValue = typeof fValue === "number" ? fValue.toLocaleString() : fValue;

                                        cSub.push(fValue);
                                    }

                                });
                            csvArray.push(cSub);
                            body.appendChild(trH);
                        }
                    });


                stackTr(array,
                    {
                        onCreate: function (td, tr, el, i) {
                            td.remove();
                            var th = document.createElement("th");
                            th.textContent = el.isNumber() && el.number ? el.number.toLocaleString() : el.value;
                            foot.appendChild(th);
                        }
                    });

                var excel = new Excel();
                excel.headerArray = csvHeader;
                excel.dataArray = csvArray;
                excel.fileName = title;

                if( v.value === 2 || v.value === 3 ) excel.exportToCsv();

            }
        });



        container.appendChild(tb);
    }

    var reportButton = buttonDefault("View Report");
    reportButton.onclick = function () {
        preViewReport();
    };

    var n = reportButton.cloneNode(true);
    $(n).addClass("pull-right");
    n.onclick = function () {
        preViewReport();
    };

    secondBox.header.appendChild(n);

    $(reportButton).addClass("btn-block");

    secondBox.body.appendChild(reportButton);

    hide(ld);
}

function addCols(cols, json, callback, father) {
    return stackList(cols,
        {
            isHidden: false,
            setHidden: function () {
                this.isHidden = !this.isHidden;
            },
            getHidden: function () {
                return this.isHidden;
            },
            onClick: function (a, arr, el, ev) {
                if (a === ev.target) ev.preventDefault();
            },
            onCreate: function (a, array, el, i) {
                var element = json[el];
                if (typeof element === "object") {
                    var cols = of(element).columns();
                    var ft = father !== undefined ? father + "." + el : el;
                    var u2 = addCols(cols, element, callback, ft);
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

                    a.parentNode.onclick = function (e) {
                        //e.preventDefault();
                        e.stopPropagation();

                        $(u2).slideToggle();

                        angle.className = eThis.getHidden() ? "fa fa-angle-down pull-right" : "fa fa-angle-right pull-right";

                        eThis.setHidden();
                    };

                    var o = {
                        link: a,
                        bolt: s,
                        name: elSp,
                        angle: angle
                    };

                    if (callback !== undefined && callback.onTree !== undefined && typeof callback.onTree === "function") {
                        callback.onTree(o, a.parentNode, u2, element, el,a);
                    }
                } else {
                    a.parentNode.onclick = function (e) {
                        e.stopPropagation();
                    };

                    if (callback !== undefined && callback.onMenu !== undefined && typeof callback.onMenu === "function") {
                        callback.onMenu(a, a.parentNode, element, el, father);
                    }
                }
            }
        });
}


function handleExtraReport(div, aLink) {

    $(div).empty();

    var section = bigAndSmall();

    var firstBox = section.firstBox();

    firstBox.setHeader("....");
    firstBox.isLarge();


    var loader = formLoader(firstBox.box);

    hoss.ajax({page: aLink.href}, 1, 2, function (res) {
        hide(loader);
        var json = jsonParse(res);

        var pad = document.createElement("div");
        pad.className = "ten-pad";
        var io = globalWidget();
        var newPanel = document.createElement("div");
        newPanel.className = "col-md-8 height-large text-left";
        var ul = stackList(json,
            {
                onCreate: function (a, array, el) {
                    var inp = input("radio", "report");
                    inp.onchange = function () {
                        var loaderL = formLoader(pad);
                        hoss.ajax({page: el.href}, 1, 2, function (res) {
                            hide(loaderL);
                            $(pad).addClass("col-md-4");
                            io.extend();
                            io.add(newPanel);
                            var big = bigAndSmall();
                            big.resize();
                            $(newPanel).empty();
                            var secondB = big.secondBox();
                            var vValue = el.value;
                            var tint = hoss.htmlEntities(vValue);
                            secondB.setHeader("Choose what use while filtering <b class='text-red'>" + tint + "</b>");
                            secondB.isLarge();
                            newPanel.appendChild(secondB.box);

                            var js = jsonParse(res);

                            var chosenArray = [];

                            function createList(jsCols, title) {

                                var h1 = document.createElement("h2");
                                h1.className = "text-bold";
                                h1.innerHTML = title;

                                var c = of(jsCols).columns();
                                var lst = addCols(c,jsCols,
                                    {
                                        onMenu:function(a,li,el){
                                            li.remove();
                                        },
                                        onTree:function (o,li,ul,el,x,a) {
                                            if( !el.access ) return;
                                            a.innerHTML = "";
                                            var ch = input("checkbox", "column");

                                            var i2 = input("hidden", el.key, el.store);
                                            var i3 = input("hidden", el.key, el.value);
                                            var i4 = input("hidden", el.key, el.extra);

                                            var isExtra = el.extra !== undefined && el.extra;

                                            ch.onchange = function (ev) {
                                                chosenArray.remove(i4);
                                                chosenArray.remove(i3);
                                                chosenArray.remove(i2);
                                                if (this.checked) {
                                                    chosenArray.push(i3);
                                                    chosenArray.push(i2);
                                                    if( isExtra ){
                                                        chosenArray.push(i4);
                                                    }
                                                }
                                            };
                                            var checkB = checkWrapper(ch);
                                            checkB.onclick = function(ex){
                                                ex.stopPropagation();
                                            };
                                            a.appendChild(checkB);
                                            var s = document.createElement("span");
                                            s.innerHTML = el.value;
                                            a.appendChild(s);
                                        }
                                    },undefined);


                                $(lst).addClass("margin-top-bottom");

                                var div = document.createElement("div");

                                div.appendChild(h1);
                                div.appendChild(lst);

                                return div;
                            }

                            var list2 = createList(js.columns, "Choose on list");

                            var ripple = rippleButton("View Final Step");
                            var loader = formLoader(secondB.body);
                            hide(loader);
                            ripple.onclick = function (ev) {
                                var form = document.createElement("form");
                                form.method = "POST";
                                form.action = js.route;
                                iterateJson(chosenArray, function (el) {
                                    form.appendChild(el);
                                });
                                show(loader);

                                sendFile(form.action, form, undefined, function (res) {
                                    hide(loader);
                                    io.hideModal();

                                    var cAgain = rippleButton("<i class='fa fa-refresh'></i> Choose again");
                                    cAgain.onclick = function (ev1) {
                                        io.showModal();
                                    };

                                    var c = jsonParse(res);

                                    var form = {};
                                    form.form = c.content;
                                    var table = new DesignTable(form, firstBox.body, true);
                                    table.callBack = function (form, obj) {
                                        handleInReport(form, section.secondBox());
                                    };
                                    var ex = table.createForm();
                                    $(ex).addClass("margin-top");
                                    firstBox.bodyEmpty();
                                    firstBox.body.appendChild(cAgain);
                                    firstBox.body.appendChild(ex);
                                    firstBox.setHeader(tint);
                                });
                            };
                            secondB.body.appendChild(ripple);

                            var otherList = document.createElement("div");

                            var btx = buttonDefault("<i class='fa fa-cart-arrow-down'></i> more options");
                            $(btx).addClass("btn-block");
                            btx.onclick = function () {
                                show(loader);
                                hoss.ajax({page: js.nextRoute}, 1, 2, function (res) {
                                    hide(loader);
                                    var json = jsonParse(res);
                                    var l2 = createList(json, vValue + " Belong in");
                                    $(otherList).empty();
                                    otherList.appendChild(l2);
                                });
                            };

                            secondB.body.appendChild(list2);
                            secondB.body.appendChild(otherList);
                            //secondB.body.appendChild(btx);

                        });
                    };
                    var i = radioWrapper(inp, el.value);
                    a.appendChild(i.parent);
                }
            });
        $(ul).addClass("text-left");
        pad.appendChild(ul);
        io.add(pad);
    });

    div.appendChild(section.parent);
}

function manageMenu(elem) {
    var elx = I(".fixed-left");
    var dt = I(".data-place");
    var dtx = dt.querySelector(".data-deposit");
    var mq = window.matchMedia("(max-width:700px)");
    if (mq.matches) {
        if (parseInt(hoss.style(elx, "width")) !== 0) {
            hoss.Clremove(elx, "resp-width");
        } else if (elem)
            hoss.Cladd(elx, "resp-width");
        return false;
    }
    if (parseInt(hoss.style(elx, "width")) != 0) {
        hoss.Cladd(dt, "content-place");
        hoss.Clremove(dt, "other-place");

        hoss.Clremove(elx, "auto-width");
        hoss.Cladd(elx, "zero-width");
        hoss.Clremove(dt.parentNode, "left-data");


    } else if (elem) {
        hoss.Clremove(dt, "content-place");
        hoss.Cladd(dt, "other-place");
        hoss.Cladd(dt.parentNode, "left-data");
        hoss.Cladd(elx, "auto-width");
        hoss.Clremove(elx, "zero-width");
        hoss.Cladd(dt, "margined");

    }
}


Slider = function (slider) {
    var self = this;
    this.container = 1;
    this.prev = null;
    this.next = null;
    this.allDiv = null;
    this.counter = 0;
    this.navList = null;
    this.activity = true;
    this.init = function (slider) {
        var all = slider.querySelectorAll(".per-img");
        self.allDiv = all;
        self.navList = self.navLinks(slider, all.length);
        self.navButtons(slider);

        slider.onmouseover = function () {
            self.activity = false;
        }
        slider.onmouseleave = function () {
            self.activity = true;
        }
        //manage auto movement if nt hovered

        setInterval(function () {
            if (self.activity) self.next.click();
        }, 2000);
    }
    this.navButtons = function (sld) {
        var nex = document.createElement("span");
        nex.className = "next";
        var ix = document.createElement("i");
        ix.className = "icon-angle-right";
        nex.appendChild(ix);
        var prev = document.createElement("span");
        prev.className = "prev";
        var ip = document.createElement("i");
        ip.className = "icon-angle-left";
        prev.appendChild(ip);

        sld.appendChild(nex);
        sld.appendChild(prev);

        self.prev = prev;
        self.next = nex;

        self.prev.onclick = function () {
            self.prevPage(sld);
        }
        self.next.onclick = function () {
            self.nextPage(sld);
        }
    }
    this.active = function () {
        return self.navList.querySelector(".active-img");
    }
    this.navLinks = function (sld, len) {
        var doc = document.createElement("div");
        doc.className = "slider-numbers";
        var inner = document.createElement("div");
        inner.className = "inner-links";

        doc.appendChild(inner);
        sld.appendChild(doc);

        for (var i = 0; i < len; i++) {
            var a = document.createElement("a");
            if (!i) a.className = "active-img";
            a.href = i;
            a.textContent = i + 1;
            a.onclick = function (e) {
                e.preventDefault();
                self.slide(sld, parseInt(this.getAttribute("href")));
                self.rem();
                hoss.Cladd(this, "active-img");
                self.counter = i;
                return !true;
            }
            inner.appendChild(a);
        }

        return inner;
    }
    this.rem = function () {
        hoss.Clremove(self.navList.querySelectorAll("a"), "active-img");
    }
    this.containerW = function (sld) {
        return parseInt(hoss.style(sld, "width"));
    }
    this.nextPage = function (sld) {
        var slc = self.counter + 1;
        if (slc < self.allDiv.length) {
            var elex = self.active().nextElementSibling;
            self.rem();
            self.slide(sld, slc);
            self.counter++;
            hoss.Cladd(elex, "active-img");
        } else {
            self.slide(sld, 0);
            self.counter = 0;
            self.rem();
            hoss.Cladd(self.navList.firstChild, "active-img");
        }
    }
    this.prevPage = function (sld) {
        var slc = self.counter - 1;
        if (slc > -1) {
            var elex = self.active().previousElementSibling;
            self.rem();
            self.slide(sld, slc);
            self.counter--;
            hoss.Cladd(elex, "active-img");
        }
    }
    this.slide = function (sld, count) {
        for (var i = 0; i < self.allDiv.length; i++) {
            self.allDiv[i].style.right = (count * self.containerW(sld)) + (4 * count) + "px";
        }
    };
    self.init(slider);
};

function changeI(input) {
    var valed = input.parentNode.querySelector(".file-input");
    var label = input.parentNode.querySelector(".control-label");

    if (valed) valed.click();

    if (!valed.onchange)
        valed.onchange = function () {
            if (this.value) show(label);
            else hide(label);
            label.textContent = this.value.split('/').pop().split('\\').pop();
        }
}

function changeAndSubmit(changeEvent) {
    //changeIt(changeEvent);
    var form = changeEvent.parentNode;
    allForms(form);
}

function changeIt(input) {
    input.onkeydown = function (evt) {
        if (evt.ctrlKey || evt.altKey) { //CTRL+ALT+F4
            //alert("CTRL+ALT+F4");
        } else if (evt.metaKey) {
            alert("meta key pressed");
        } else
            evt.preventDefault();
    };
    var valed = input.parentNode.querySelector(".file-input");
    if (valed)
        valed.click();
    if (!valed.onchange)
        valed.onchange = function () {
            var namo = this.files[0].name;
            if (!namo.match(/.(jpg|jpeg|png|gif|pdf)$/i)) {
                this.value = "";
                notFound("<i class='fa fa-warning'></i> Only image or pdf are acceptable");
                return false;
            }
            input.value = this.value;
            if (namo.match(/.(pdf)$/i)) {
                var bing = input.parentNode.querySelector(".image-small-div");
                if (bing) bing.remove();
                var bingos = input.parentNode.querySelector(".bingos");
                if (!bingos) {
                    var div = document.createElement("div");

                    div.style.width = "100%";
                    div.style.textAlign = "center";
                    div.style.padding = "20px";
                    div.className = "bingos";
                    var diva = document.createElement("div");
                    hoss.Cladd(diva, "col-md-9");
                    hoss.Cladd(diva, "col-centered");

                    var obj = document.createElement("object");
                    obj.type = "application/pdf";
                    obj.title = "Simple pdf";
                    obj.width = 500;
                    obj.height = 220;
                    diva.appendChild(obj);
                    div.appendChild(diva);

                    readPdf(this, obj);

                    input.parentNode.appendChild(div);
                } else {
                    var obj = bingos.querySelector("object");
                    readPdf(this, obj);
                }
            } else {
                var bing = input.parentNode.querySelector(".bingos");
                if (bing) bing.remove();
                var shy = designPhoto(input.parentNode, this);
                shy.style.position = "relative";
                shy.style.margin = "20px";
            }
        }
    var xp = input.parentNode.querySelector(".image-small-div")
    if (xp) show(xp);
}


function designPhoto(fGroup, filer) {
    var elx = fGroup.querySelector(".image-small-div");
    if (elx) {
        var nix = elx.querySelector(".inner-small");
        show(elx);
    } else {
        elx = document.createElement("div");
        elx.ondblclick = function () {
            hide(this)
        }
        elx.className = "image-small-div";
        var nix = document.createElement("div");
        nix.className = "inner-small";
        elx.appendChild(nix);
        fGroup.appendChild(elx);
    }
    nix.innerHTML = "";
    var img = document.createElement("img");
    nix.appendChild(img);
    readF(filer, img);
    return elx;
}

function changePic(cli) {
    var whole = cli.parentNode.parentNode;
    var inpt = whole.querySelector("input");

    var out = whole.querySelector("img");

    inpt.onchange = function () {
        readF(this, out);
        inpt.parentNode.submit();
    }

    if (inpt) inpt.click();
}

function readPdf(input, output) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            output.data = e.target.result;
        }

        reader.readAsDataURL(input.files[0]);
    }
}

function readF(input, output) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            output.src = e.target.result;
        }

        reader.readAsDataURL(input.files[0]);
    }
}

function updateLang(elem) {
    elem.setAttribute("contenteditable", true);
    elem.focus();
    if (!elem.onblur) {
        elem.onblur = function () {
            this.removeAttribute("contenteditable");
            var pg = this.getAttribute("value");
            var cont = this.textContent;
            var idea = this.parentNode.getAttribute("value");
            idea = parseInt(idea);
            var hrf = "/change/languages/" + idea + "/str/stz?str=" + encodeURIComponent(pg) + "&stz=" + encodeURIComponent(cont);
            hoss.ajax({page: hrf}, 1, 2, function (res) {
                //alert(res);
            });
        }
    }
}

function addNew(elx, e) {
    e.stopPropagation();
    var par = elx.parentNode;
    var form = par.querySelector("#form1");
    if (form) show(form);
}

function studentC( form ){
    var but = form.querySelector("button");
    var loader = formLoader(form);
    sendFile(form.action,form,"",function(res){
        hide(loader);
        if( res !== "0" ) {
            window.location.reload();
        }else{
            hide(form.querySelectorAll(".error-tool"));
            formError(form,"Login Error Check your login information !!");
        }
    })
}

function textarea(simple) {

    var div = document.createElement("div");
    div.className = "form-group relative";
    var label = document.createElement("label");
    label.innerHTML = "...";
    var input = document.createElement(simple ? "input" : "textarea");
    input.className = "form-control";
    input.draggable = true;

    div.appendChild(label);
    div.appendChild(input);

    return {
        parent:div,
        input:input,
        label:label,
        setName:function ( name ) {
            this.input.name = name;
        },
        setValue:function ( name ) {
            this.input.value = name;
        },
        setType:function ( type ) {
            this.input.type = type;
        },
        setLabel:function ( label ) {
            this.label.innerHTML = label;
        },
        warnLabel:function(){
            var l = this.label;
            l.style.color = "red";
            setTimeout(function () {
              l.style.color = null;
            },4000);
        },
        setLabelAndHolder:function ( label ) {
            this.setLabel(label);
            this.setHolder(label);
        },
        setHolder:function ( label ) {
            this.input.placeholder = label;
        },
        picker:function () {
            $(this.input).datepicker({format:"yyyy-mm-dd"});
        }
    }
}

function saveStudent(form) {
    var img = form.querySelector(".regLoader");
    if (img) {
        jsfadeIn(img);
    }
    sendFile(form.action, form, "", function (res,xhr) {
        if (img) hide(img);

        if( xhr.getResponseHeader("next") === "1" ){
            $(".login-roling").html(res);
            runSignUp(form,2);
            return;
        }

        var stRoute = xhr.getResponseHeader("new-acc");
        if( stRoute ){
            var body = textarea(true);
            var frm = document.createElement("form");
            frm.method = "POST";
            frm.action = stRoute;
            frm.onsubmit = function (e) {
                e.preventDefault();
                return false;
            };
            body.setType("password");
            body.setName("password");
            body.setLabel("Create your new application [Or click cancel for previous program]");
            body.setHolder("password here ...");
            frm.appendChild(body.parent);
            confirmBox({
                title:"For new application, please fill this form",
                body:frm,
                accept:function (e,wig,but,b) {
                    validate(frm,function () {
                        var ld = formLoader(b.parentNode);
                        saveForm(frm,function (res) {
                            hide(ld);
                            if( res === "1" ) {
                                window.location.reload();
                            }else{
                                body.warnLabel();
                                body.input.focus();
                                body.setLabel(res);
                            }
                        })
                    })
                }
            });
            return;
        }

        if (res === "1")
            window.location.reload();
        else
            var name = form.querySelector("[name=email]");
        if (name) {
            name.focus();
            hoss.updateError(name.parentNode, res);
        }
    }, '', true);
}

function ds() {
    show(I(".showbox"));
}

function hs() {
    hide(I(".showbox"));
}

function reclick(elem) {
    var form = hoss.dir(elem, "#formPort", "parentNode");
    var file = form.querySelector("input[type=file]");
    if (file) file.click();
    var img = form.querySelector(".forImage img");
    if (img) {
        file.onchange = function () {
            if (!this.files[0].name.match(/.(jpg|jpeg|png|gif)$/i)) {
                this.value = "";
                notFound("<i class='fa fa-warning'></i> this is not image");
            }
            readF(this, img);
        }
    }
}

function levels(pro) {
    var dist = I("#district");
    var sector = I("#sector");
    var str = "/student/structure/" + pro.value;
    $(dist).addClass("imgLoads");
    if (!pro.value) {
        dist.innerHTML = "";
        sector.innerHTML = "";
        return false;
    }
    dist.innerHTML = "";
    hoss.ajax({page: str}, 1, 2, function (res) {
        var obj = JSON.parse(res);
        var defaultA = document.createElement("option");
        defaultA.value = "";
        $(dist).removeClass("imgLoads");
        dist.appendChild(defaultA);
        Array.prototype.forEach.call(obj, function (el) {
            var opt = document.createElement("option");
            opt.value = el.id;
            opt.textContent = el.districtName;
            dist.appendChild(opt);
        });
    });
}

function facultyChange(elex) {
    var frm = hoss.dir(elex, "#formPort", "parentNode");
    var eln = frm.querySelector("#fiesta");
    var href = "/getFaculty/" + elex.value;
    hoss.ajax({page: href}, 1, 2, function (res) {
        eln.value = res;
    });
}

function deepValues(row, el, td) {
    if( el.father === undefined ) return;
    var f = el.father.split(".");
    var elem = row;
    if (elem === null || elem === undefined) return;
    for (var i = 0; i < f.length; i++) {
        elem = elem[f[i]];
        if (elem === undefined || elem === null) return;
    }

    var value = elem[el.key];

    return expressValue(value, td);
}

function DrawCaptcha() {
    var a = Math.ceil(Math.random() * 10) + '';
    var b = Math.ceil(Math.random() * 10) + '';
    var c = Math.ceil(Math.random() * 10) + '';
    var d = Math.ceil(Math.random() * 10) + '';
    var e = Math.ceil(Math.random() * 10) + '';
    var f = Math.ceil(Math.random() * 10) + '';
    var g = Math.ceil(Math.random() * 10) + '';
    var code = a + ' ' + b + ' ' + ' ' + c + ' ' + d + ' ' + e + ' ' + f + ' ' + g;
    document.getElementById("txtCaptcha").value = code
}

// Validate the Entered input aganist the generated security code function
function ValidCaptcha() {
    var el = document.getElementById('txtCaptcha');
    var str1 = removeSpaces(el.value);
    var xx = document.getElementById('txtInput');
    var str2 = removeSpaces(xx.value);
    if (str1 == str2) return true;
    else
        hoss.updateError(xx.parentNode, "Captcha code are not equal");

    return false;

}

// Remove the spaces from the entered and generated code
function removeSpaces(string) {
    return string.split(' ').join('');
}


function activator(elem) {
    var href = elem.value;
    hoss.ajax({page: href}, 1, 2, function (res) {
        var mTab = hoss.dira(elem, ".nav-tabs-custom", "parentNode");
        mTab.querySelector(".active a").click();
    })
}


////////


function selectPeriod(self,status) {
    var tId = $(self).val();
    if (tId === "") {
        return;
    }

    var stat='';
    var url='/update/all/'+status+'/' + tId + '/';
    var boder = self.parentNode.parentNode;
    var cont = boder.querySelector(".liquify-content");
    var ul = document.createElement("ul");
    ul.className = "timeline";
    cont.style.backgroundImage = "url(assets/images/loader.gif)";

    $.post(url).done(function (response) {
        cont.style.backgroundImage = "none";
        cont.innerHTML = "";
        cont.appendChild(ul);
        show(cont);
        appliedData(ul, response, stat);
        // console.log(response);
    }).fail(function () {
        alert("Server error");
    });
}


////


function appliedIn(elem, det, id, tx) {
    var pro = I(".program-name");
    var href;
    if (!det) {
         href = "/register/Applied/1/var/status?var=" + ((elem && elem.value) ? encodeURIComponent(elem.value) : ":all") + "&status=" + det;
    } else {
         href = "register/" + tx + "/" + id;
    }
    var boder = elem.parentNode.parentNode;
    var cont = boder.querySelector(".liquify-content");

    var ul = document.createElement("ul");
    ul.className = "timeline";
    cont.style.backgroundImage = "url(assets/images/loader.gif)";
    hoss.ajax({page: href}, 1, 2, function (res) {
        cont.style.backgroundImage = "none";
        cont.innerHTML = "";
        cont.appendChild(ul);
        show(cont);
        var jsp = JSON.parse(res);
        appliedData(ul, jsp, true);
    });
}

function applied(elem, stat, det) {
    var pro = I(".program-name");
    if (det) {
        var href = "/register/Applied/1/var/status?var=" + ((elem && elem.value) ? encodeURIComponent(elem.value) : ":all") + "&status=" + det;
    } else {

        var href = "register/Applied/" + (1) + "/var?var=" + ((elem && elem.value) ? encodeURIComponent(elem.value) : ":all");
    }
    var boder = elem.parentNode.parentNode;
    var cont = boder.querySelector(".liquify-content");

    var ul = document.createElement("ul");
    ul.className = "timeline";
    cont.style.backgroundImage = "url(assets/images/loader.gif)";
    hoss.ajax({page: href}, 1, 2, function (res) {
        cont.style.backgroundImage = "none";
        cont.innerHTML = "";
        cont.appendChild(ul);
        show(cont);
        var jsp = JSON.parse(res);
        appliedData(ul, jsp, stat);
    });

     hoss.ajax({page:href2},1,2,function(res){
         var jsp = JSON.parse(res);
         appliedData(ul,jsp , stat );
     });
}

function appliedData(body, data, stat) {
    Array.prototype.forEach.call(data, function (v, k) {
        var elx = document.createElement("li");
        elx.onclick = function () {
            var li = hoss.dir(body, "#parentLi", "parentNode");
            var aHref = li.querySelector("a").getAttribute("page");
            var diver = I(aHref);
            if (diver) {
                hide(I(".data-deposit").querySelectorAll(".tabcontents"));
                show(diver);
                diver.innerHTML = "";
            }
        };
        var timeItem = document.createElement("div");
        timeItem.className = "timeline-item";
        var spImage = document.createElement("label");
        spImage.id = "mySmall";
        var img = document.createElement("img");
        img.ondragstart = false;
        var iC = document.createElement("span");
        iC.className = "fa hideOver bg-aqua";
        img.src = "assets/uploads/" + v.applicant.profile;
        iC.appendChild(img);
        var sp = document.createElement("h3");
        sp.className = "timeline-header no-border";
        sp.innerHTML = "<a href='#'>" + v.applicant.firstName + " " + v.applicant.familyName + "</a>";
        var buttonX = document.createElement("label");
        buttonX.className = "time";
        buttonX.innerHTML = (v.applicationStatus != "") ? "<i class='icon-check'></i>" + v.applicationStatus : "<i class='icon-check'></i>new";

        elx.appendChild(iC);
        timeItem.appendChild(buttonX);
        timeItem.appendChild(sp);
        elx.appendChild(timeItem);
        elx.setAttribute("page", "access/appPage/" + v.id + "/typ?typ=under");
        elx.onclick = function () {
            document.body.click();
            checkD(body, this);
        }
        body.appendChild(elx);
    });
}

function checkD(body, self) {

    var diver = I(".app-user");
    if (diver) {
        show(diver);
        diver.innerHTML = "<div align='center'><img src='assets/images/loader.gif'></div>";
        hoss.ajax({page: self.getAttribute("page"), o: diver}, 1, 2, function () {
            //hs();

            //window.scrollTo(0,270);
        });
    }
}


function remover(el) {
    hoss.Cladd(el, "po-lefted");
    hoss.Clremove(el, "so-lefted");
}


function printDiv( div , a ) {
    hide(a);
    html2canvas(div,{
        backgroundColor : null
    }).then(function(canvas) {
        canvas.toDataURL("image/png");
        var link = document.createElement('a');
        hide(link);
        show(a);
        link.setAttribute('href', canvas.toDataURL("image/png").replace("image/png", "image/octet-stream"));

        var din = document.createElement("div");
        var lb = document.createElement("span");
        lb.className = "text-red";
        din.appendChild(lb);
        var input = document.createElement("input");
        input.className = "form-control";
        input.type = "text";
        var text = div.querySelector(".give-data").textContent;
        var number = div.querySelector(".give-number").textContent;
        input.value = text.split(' ').join('+') + "_" + number.split('/').join('.');
        input.placeholder = "Enter file name";
        din.appendChild(input);

        confirmBox({
            title:"Image name(To save as)",
            body:din,
            accept:function (e,wig) {
                if( !input.value.trim() ){
                    input.focus();
                    return false;
                }
                if( !isFileName(input.value.trim()) ){
                    lb.textContent = "Enter a valid Attachment Name";
                    return false;
                }
                lb.textContent = "";
                hide(wig);
                link.setAttribute('download',input.value+".png");
                link.click();
            }
        });
        document.body.appendChild(link);
    });
}

function isFileName( val ) {
    return /^(?=[\S])[^\\ \/ : * ? " < > | ]+$/.test(val);
}

function freeEdit(elem) {
    var rect = elem.parentNode.getBoundingClientRect();
    var potoEdit = document.body.querySelector(".userEditor");
    if (!potoEdit) {
        var div = document.createElement("div");
        div.className = "userEditor";

        var data = document.createElement("div");
        data.className = "free-place";
        data.id = "myScroll";

        hoss.ajax({page: elem.value, o: data});

        div.appendChild(data);
        div.style.top = rect.top + "px";
        div.style.left = rect.left + rect.width + 10 + "px";

        document.body.appendChild(div);
    } else {

        potoEdit.style.top = rect.top + "px";
        potoEdit.style.left = rect.left + rect.width + 10 + "px";
    }
}

function targeting(elem, tSelect, accessOne, accessTwo, accessThree, ifSecond) {
    var iding = I(tSelect);
    if (elem.value && iding) {
        $(elem).addClass("imgLoads");
        var href = elem.getAttribute((ifSecond == 1) ? "href2" : "href") + elem.value + "/";
        hoss.ajax({page: href}, 1, 2, function (res) {
            iding.innerHTML = "";
            var js = JSON.parse(res);
            var optionOne = document.createElement("option");
            optionOne.value = "";
            optionOne.innerHTML = "-- Choose " + accessOne + " --";
            iding.appendChild(optionOne);
            Array.prototype.forEach.call(js, function (el) {
                var option = document.createElement("option");
                option.value = el.id;
                option.innerHTML = (accessThree) ? el[accessThree][accessTwo][accessOne] : (accessTwo) ? el[accessTwo][accessOne] : el[accessOne];
                iding.appendChild(option);
            });
            $(elem).removeClass("imgLoads");
        })
    }
}


function manageEditing(table) {

    if (!table) return false;

    var allEditable = table.querySelectorAll(".editable");

    Array.prototype.forEach.call(allEditable, function (el) {
        el.onclick = function (e) {
            e.stopPropagation();
            var self = this;
            var max = parseInt(self.getAttribute("max"));

            if (isNaN(max)) return false;

            self.setAttribute("contenteditable", "true");
            self.focus();

            if (!self.onkeypress) {
                self.onkeypress = function () {
                    return (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46 && this.textContent.split(".").length < 2);
                }
            }
            if (!self.onblur) {
                self.onblur = function () {
                    var thisa = this;
                    if (!this.textContent) thisa.textContent = 0;

                    var marks = parseInt(thisa.textContent);

                    if (marks > max ) {
                        hoss.Cladd(thisa, "error-text");
                        hoss.updateError(thisa.parentNode, "maximum is " + max);
                        hoss.Clremove(thisa, "imgLoads");
                        this.focus();
                    }  else {
                        hoss.Cladd(thisa, "imgLoads");
                        thisa.removeAttribute("contenteditable");
                        hoss.Clremove(thisa, "error-text");

                        var compId = thisa.getAttribute("data-comp");
                        var stuId = thisa.getAttribute("data-stu");
                        var markType = thisa.getAttribute("data-type");

                        var ab = thisa.querySelector(".error-tool");

                        if (ab) ab.remove();

                        var unparsed = thisa.textContent;
                        var href = "/Lecture/saveStudentMarks/" + stuId + "/" + compId + "/" + unparsed + "/" + markType + "/";

                        hoss.ajax({page: href}, 1, 2, function (res) {
                            hoss.Clremove(thisa, "imgLoads");
                        });


                    }

                }
            }
        }
    })

}


function expander(bool) {
    if (!$(".relatively").hasClass("absolutely")) {
        $(".relatively").removeClass("col-md-9").addClass("absolutely");
        $(".in-expand").html("<i class='fa fa-compress'></i>");
    } else {
        $(".relatively").removeClass("absolutely").addClass("col-md-9");
        $(".in-expand").html("<i class='fa fa-expand'></i>");
    }
}

function didChange(select) {
    var par = $(select).parents(".toggable");
    if (select.value && select.value === "true") {
        par.find(".didHide").show().find(".form-control").removeAttr("escape");
    } else {
        par.find(".didHide").hide().find(".form-control").attr("escape", "1");
    }
}

function getAttendList(elem, evt) {
    evt.stopPropagation();
    evt.preventDefault();
    var tdCont = elem.parentNode.parentNode;

    hide(all(".attend-div"));
    var lefting = "righted-attend";

    var dataDiv = tdCont.querySelector(".attend-div");
    if (!dataDiv) {
        dataDiv = document.createElement("div");
        dataDiv.onclick = function (e) {
            e.stopPropagation()
        }
        hoss.Cladd(dataDiv, "attend-div");
        hoss.Cladd(dataDiv, "default-toggle");

        var mouseX = evt.pageX - dataDiv.offsetLeft;
        var mouseY = evt.pageY - dataDiv.offsetTop;

        if (mouseX > 970) lefting = "lefted-attend";

        hoss.Cladd(dataDiv, lefting);


        var subData = document.createElement("div");
        subData.id = "myScroll";

        hoss.Cladd(subData, "more-heighted");
        hoss.Cladd(subData, "loading");

        dataDiv.appendChild(subData);

        tdCont.insertBefore(dataDiv, tdCont.firstChild);

        hoss.ajax({page: elem.href, o: subData}, 1, 2, function (res) {
            hoss.Clremove(subData, "loading");
        });

        jsfadeIn(dataDiv);
    } else {
        jsfadeIn(dataDiv);
    }

}


function simpleSave(form) {
    validate(form, function (re) {
        var load = formLoader(form);
        var errorPlace = form.querySelector(".error-place");
        var data = $(form).serialize();

        $.ajax({
            method: "POST",
            url: form.action,
            data: data,
            success: function (res) {
                if (res === "1") {
                    //do tiing
                    $("#myModal").modal("hide");
                    global.reload();
                } else {
                    $(errorPlace).html(res);
                }
                hide(load);
            },
            error: function () {
                hide(load);
            }
        })
    });

    return false;
}


$(document).on('submit', '#changePasswordForm', function () {
    var email = $("#inputEmail").val();
    var phoneNumber = $("#phoneNumber").val();
    var oldPassword = $("#oldPassword").val();
    var newPassword = $("#newPassword").val();
    var form = $(this);
    $("#btnSumit").button("loading");
    $.ajax({
        url: form.attr('action'),
        type: form.attr('method'),
        data: form.serialize()
    }).done(function (response) {
        $("#btnSumit").button("reset");

        $("#div-result").html("<div class=\"alert alert-success alert-remove\">" +
            "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>" +
            "  <strong>Success!</strong> Changes applied successfully." +
            "</div>");
        setTimeout(function () {
            $(".alert-remove").remove();
        }, 5000);
    }).fail(function (response) {
        $("#btnSumit").button("reset");
        $("#div-result").html("<div class=\"alert alert-danger alert-remove\">" +
            "<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>" +
            "  <strong>Fail! </strong> Changes not applied. May be you miss-typed your old password try again" +
            "</div>");
        setTimeout(function () {
            $(".alert-remove").remove();
        }, 5000);
    });
    return false;
});

function handleEdit(div, aLink) {
    $(div).empty();
    var d = bigAndSmall();
    var x = d.firstBox();
    x.setHeader("Search student reg number here ...");

    $(x.body).addClass("height-large");
    div.appendChild(d.parent);

    var second = d.secondBox();
    second.isLarge();


    var form = document.createElement("form");
    form.className = "form-group has-feedback";
    var field = document.createElement("input");
    field.className = "form-control";
    field.placeholder = "Search here ...";
    var btn = document.createElement("span");
    btn.className = "fa fa-search eventing pointer form-control-feedback";
    form.appendChild(field);
    form.appendChild(btn);
    x.body.appendChild(form);
    var data = document.createElement("div");
    x.body.appendChild(data);

    form.onsubmit = function (ev) {

        if (!field.value) return false;

        var href = aLink.href;
        href = String(href).replace("@~data", encodeURIComponent(field.value));

        var loader = formLoader(x.box);

        hoss.ajax({page: href}, 1, 2, function (res) {
            hide(loader);
            var json = jsonParse(res);
            $(data).empty();
            var ul = stackList(json,
                {
                    onCreate: function (a, array, el, i) {
                        a.innerHTML = el.names + "(" + el.registration_no + ")";
                    },
                    onClick: function (a, arr, el) {
                        $(second.body).empty().goTo();
                        var ob = {
                            "Old reg Number": {
                                reg: el.registration_no,
                                name: "oldNumber",
                                disabled: true
                            },
                            "New reg Number": {
                                reg: el.registration_no,
                                name: "newNumber",
                                disabled: false
                            }
                        };

                        var od = of(ob).columns();

                        var f = stackForm(od,
                            {
                                onCreate: function (grp, input, el) {
                                    var din = ob[el];

                                    input.value = din.reg;
                                    input.readOnly = din.disabled;
                                    input.name = din.name;

                                    grp.appendChild(input);
                                }
                            });

                        var hidden = document.createElement("input");
                        hidden.type = "hidden";
                        hidden.name = "isUnder";
                        hidden.value = el.trimester != null;
                        f.appendChild(hidden);

                        var frx = createUpdateForm({
                            title: "Update reg number",
                            content: f,
                            route: $(aLink).attr("data-save"),
                            buttonText: "Save changes"
                        });

                        second.body.appendChild(frx);
                    }
                });

            data.appendChild(ul);
        });

        return false;
    };
}
function doDenyForm(elem, src) {
    var div = document.createElement("div");
    div.className = "form-group has-feedback";
    var label = document.createElement("label");
    label.textContent = src;
    div.appendChild(label);
    var input = document.createElement("textarea");
    input.className = "form-control";
    input.placeholder = "Enter comment here ...";
    div.appendChild(input);
    confirmBox({
        title: "Message alert",
        body: div,
        accept: function (e, wig, but, body, box) {
            if (!input.value.trim()) {
                input.focus();
                return false;
            }
            var loader = formLoader(box);
            var page = String(elem.href).replace("@~data", encodeURIComponent(input.value));
            hoss.ajax({page: page}, 1, 2, function (res) {
                hide(wig);
                hide(loader);
                refreshPanel(elem);
            }, true, function () {
                hide(loader);
            });
        }
    });
    return false;
}
