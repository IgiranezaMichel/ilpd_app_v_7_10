function changeMovementType(id) {
    // alert("Hello");
    var self = $('#movementType' + id);
    var parentTd = self.parent(".form-group").parent("td");
    var movementLocation = parentTd.find(".movementLocation");
    if (self.val() === "Movement") {
        movementLocation.removeClass("div-hide");
    } else {
        movementLocation.addClass("div-hide");
    }
}


$(function () {
    $(document).on('submit', '#saveGroupForm', function (e) {
        e.preventDefault();
        // alert("Hello");
        var form = $(this);
        var btn = $('#btnCreateGroup');
        form.parsley().validate();
        if (form.parsley().isValid()) {
            // submit btn
            btn.button('loading');
            $.ajax({
                url: form.attr('action'),
                type: form.attr('method'),
                data: form.serialize(),
                success: function (data) {
                    console.log(data);
                    if (data === "1") {
                        $('.messages').html('<div class="alert alert-success">' +
                            '<button type="button" class="close" data-dismiss="alert">&times;</button>' +
                            '<strong><i class="glyphicon glyphicon-ok-sign"></i> </strong> Group successfully created. </div>');

                        $(".alert-success").delay(500).show(10, function () {
                            $(this).delay(3000).hide(10, function () {
                                $(this).remove();
                                $('#addGroupModal').modal('hide');
                                I(".hosted").click();
                            });
                        }); // /.alert
                    } else {
                        $('.messages').html('<div class="alert alert-danger">' +
                            '<button type="button" class="close" data-dismiss="alert">&times;</button>' +
                            '<strong><i class="fa fa-warning"></i> </strong>' + data + '</div>');
                    }
                    btn.button('reset');
                }, error: function (error) {
                    btn.button('reset');
                }
            });
        }
        return false;
    });
    $(document).on('submit', '#saveGroupForm2', function (e) {
        e.preventDefault();
        // alert("Hello");
        var form = $(this);
        var btn = $('#btnCreateGroup2');
        form.parsley().validate();
        if (form.parsley().isValid()) {
            // submit btn
            btn.button('loading');
            $.ajax({
                url: form.attr('action'),
                type: form.attr('method'),
                data: form.serialize(),
                success: function (data) {
                    console.log(data);
                    if (data === "1") {
                        $('.messages').html('<div class="alert alert-success">' +
                            '<button type="button" class="close" data-dismiss="alert">&times;</button>' +
                            '<strong><i class="glyphicon glyphicon-ok-sign"></i> </strong> Group successfully created. </div>');

                        $(".alert-success").delay(500).show(10, function () {
                            $(this).delay(3000).hide(10, function () {
                                $(this).remove();
                                $('#addGroupModal2').modal('hide');
                                I(".hosted").click();
                            });
                        }); // /.alert
                    } else {
                        $('.messages').html('<div class="alert alert-danger">' +
                            '<button type="button" class="close" data-dismiss="alert">&times;</button>' +
                            '<strong><i class="fa fa-warning"></i> </strong>' + data + '</div>');
                    }
                    btn.button('reset');
                }, error: function (error) {
                    btn.button('reset');
                }
            });
        }
        return false;
    });


    $(document).on('click', '.btnManagePeriodTranscript', function () {
        var btn = $(this);
        btn.button('loading');
        $.ajax({
            url: btn.attr('data-url'),
            method: 'POST',
            success: function () {
                btn.button('reset');
                I('.hosted').click();
            }, error: function () {
                btn.button('reset');
            }
        })
    });


    $(document).on('click', '.btn-graduate', function () {
        var btn = $(this);
        var url = btn.attr('data-href');
        btn.button('loading');

        $.ajax({
            url: url,
            method: 'GET'
        }).done(function (data) {
            $('#div-result').html('<div class="alert alert-success fade in">' +
                '  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>' +
                '  <strong>Success! </strong> You have successfully deliberated them.' +
                '</div>');
            btn.button('reset');
            $(this).attr("disabled", "disabled");
        }).fail(function (data) {
            $('#div-result').html('<div class="alert alert-danger fade in">' +
                '  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>' +
                '  <strong>Success! </strong> Internal server error  please try again.' +
                '</div>');
            btn.button('reset');
        });
    });


    $(document).on('click', '.btn-reseat', function () {
        var btn = $(this);
        var url = btn.attr('data-href');
        btn.button('loading');

        $.ajax({
            url: url,
            method: 'GET'
        }).done(function (data) {
            $('#div-result').html('<div class="alert alert-success fade in">' +
                '  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>' +
                '  <strong>Success! </strong> Period re-seat successfully made.' +
                '</div>');
            btn.button('reset');
        }).fail(function (data) {
            $('#div-result').html('<div class="alert alert-danger fade in">' +
                '  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>' +
                '  <strong>Success! </strong> Internal server error  please try again.' +
                '</div>');
            btn.button('reset');
        });
    });


    $(document).on('click', '.btn-cancel-reseat', function () {
        var btn = $(this);
        var url = btn.attr('data-href');
        btn.button('loading');

        $.ajax({
            url: url,
            method: 'GET'
        }).done(function (data) {
            $('#div-result').html('<div class="alert alert-success fade in">' +
                '  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>' +
                '  <strong>Success! </strong> Period re-seat successfully cancelled.' +
                '</div>');
            btn.button('reset');
        }).fail(function (data) {
            $('#div-result').html('<div class="alert alert-danger fade in">' +
                '  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>' +
                '  <strong>Success! </strong> Internal server error  please try again.' +
                '</div>');
            btn.button('reset');
        });
    });


    $(document).on('click', '.btnEvaluate', function () {
        var modal = $('#evolutionModal');
        var btn = $(this);
        modal.modal('show');
        var result = $("#ev-result");

        var form = $("#ev-form-result");

        form.attr("action",btn.attr("data-save"));

        $.ajax({
            url:btn.val(),
            method:"GET",
            success:function (res) {
                result.html(res);
            }
        })
    });


    //###############################################################

    $(document).on('submit', '#employeeConfirmationForm', function () {
        var form = $(this);
        var btn = $(document).find('.confirmBtn');
        btn.button('loading');
        btn.prop('disabled', true);
        $.ajax({
            url: form.attr('action'),
            method: form.attr('method'),
            data: form.serialize()
        }).done(function () {
            btn.prop('disabled', false);
            $(document).find('.modal').modal('hide');
            $('.hosted').click();
        }).fail(function () {
            btn.prop('disabled', false);
            // alert("Internal server error");
        });

        return false;
    });


    //Submit final request

    $(document).on('submit', '.submitFinalRequest', function (e) {
        var form = $(this);
        var btn = form.find('.btnConfirmIndividual');

        btn.button('loading');
        btn.prop("disabled", true);

        $.ajax({
            url: "/request/logistic/confirm/" + form.find("#hiddenRequestId").val() + "/1/ ",
            method: form.attr('method'),
            data: form.serialize()
        }).done(function (r) {
            $(document).find('.modal').modal('hide');
            btn.prop("disabled", false);
            $('.hosted').click();
        }).fail(function () {
            btn.button('reset');
            btn.prop("disabled", false);
            // alert("Error.. try again");
        });
        form.off('submit');
        btn.off('clik');
        // return false;
        $(document).find('.modal').modal('hide');
        $('.hosted').click();
    });


    $(document).on('submit', '.submitIndividualRequest', function (e) {
        e.preventDefault();
        var form = $(this);
        var btn = form.find('.btnConfirmIndividual');

        btn.button('loading');
        btn.attr("disabled", 'disabled');

        $.ajax({
            url: form.attr('action'),
            method: form.attr('method'),
            data: form.serialize()
        }).done(function (r) {
            $(document).find('.modal').modal('hide');
            $('.hosted').click();
        }).fail(function () {
            btn.button('reset');
            // alert("Error.. try again");
        });
    });

    $(document).on('click', '.btnRejectIndividual', function (e) {

        var form = $(".submitIndividualRequest");
        var btn = $(this);

        btn.button('loading');
        btn.attr("disabled", 'disabled');

        $.ajax({
            url: $("#rejReq").val(),
            method: 'post',
            data: form.serialize()
        }).done(function (r) {
            $(document).find('.modal').modal('hide');
            $('.hosted').click();
        }).fail(function () {
            btn.button('reset');
            // alert("Error.. try again");
        });
    });

});


// new js added
var $collapse = $(document).find('.collapse');
$collapse.on('hidden.bs.collapse', function () {
    $(this).parent("div").find(".panel-heading").find(".panel-title").find(".fa").removeClass("fa fa-minus-circle").addClass("fa fa-plus-circle");
});
$collapse.on('shown.bs.collapse', function () {
    $(this).parent("div").find(".panel-heading").find(".panel-title").find(".fa").removeClass("fa fa-plus-circle").addClass("fa fa-minus-circle");
});

$(function () {
    FastClick.attach(document.body);
    $('.datepicker').datepicker({
        autoclose: true
    });

});

$(document).on('submit', '#searchForm', function () {
    var form = $(this);
    var divResult = document.body.querySelector('.divResult');
    var loader = formLoader(divResult.parentNode);
    $.ajax({
        url: form.attr('action'),
        method: form.attr('method'),
        data: form.serialize()
    }).done(function (response) {

        divResult = $(divResult);
        divResult.html("");
        divResult.html(response);
        $(loader).hide();
    });
    return false;
});

function getPage(link) {
    var search = '?search=' + $(document).find('#searchText').val();
    var pageSize = '&pageSize=' + $(document).find('#pageSize').val();
    var training = '&training=' + $(document).find('#training').val();
    var divResult = document.body.querySelector('.divResult');
    var loader = formLoader(divResult.parentNode);
    $.ajax({
        url: link + search + pageSize + training,
        method: 'GET'
    }).done(function (response) {
        divResult = $(divResult);
        divResult.html("");
        divResult.html(response);
        $(loader).hide();
    });
    return false;
}


$(document).on('click', '.js-app-details', function (e) {
    e.preventDefault();
    var btn = $(this);
    var link = btn.attr('href');
    var container = document.body.querySelector('#detail');
    var loader = formLoader(container.parentNode);
    $.ajax({
        url: link,
        method: 'GET'
    }).done(function (response) {
        container = $(container);
        container.html("");
        container.html(response);
        $(loader).hide();
    });
    return false;
});


function removeGroupFunc(url) {
    $.ajax({
        url: url,
        method: 'GET'
    }).done(function (data) {
        // btn.button('reset');

        $('#div-result').html(' <div class="alert alert-success">' +
            '<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>' +
            '<strong>Success! </strong> ' + data +
            '</div>');

        I(".hosted").click();
    }).fail(function () {
        // btn.button('reset');
        $('#div-result').html(' <div class="alert alert-danger">' +
            '<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>' +
            '<strong>Error ! </strong> Internal server error!! please try again. ' +
            '</div>');
    });


    ///


}


function submitData(url) {
    $.ajax({
        url: url,
        method: 'POST',
        success: function () {
            $('#changeModal').modal('hide');

            var activeLeft = I(".hosted");
            activeLeft.click();
        }
    });
}

$(function () {
    $(document).on('click', '.js-info', function () {
        var modal = $('#detailsModal');
        var loading = $('.modal-loading');
        var result = $('.edit-result');
        var footer = $('.editFooter');
        var url = $(this).attr('data-url');
        modal.modal('show');

        // modal loading
        loading.removeClass('div-hide');
        // modal result
        result.addClass('div-hide');
        // modal footer
        footer.addClass('div-hide');

        $.ajax({
            url: url,
            method: 'GET'
        }).done(function (data) {
            loading.addClass('div-hide');
            result.removeClass('div-hide');
            footer.removeClass('div-hide');

            result.html("");
            result.html(data);
        });
    });

    $(document).on('submit','#updateApplicant',function () {
        var form = $(this);
        var btn = form.find('#btnUpdate');
        var modal = $(document).find('#detailsModal');
        var result = $(document).find('.result');

        btn.button('loading');
        $.ajax({
            url: form.attr('action'),
            method: 'PUT',
            data: form.serialize()
        }).done(function (response) {
            modal.modal('hide');
            btn.button('reset');

            result.html('<div class="alert alert-success">' +
                '<button type="button" class="close" data-dismiss="alert">&times;</button>' +
                '<strong><i class="glyphicon glyphicon-ok-sign"></i></strong> '
                + response + '</div>');
            $(".alert-success").delay(500).show(10, function () {
                $(this).delay(3000).hide(10, function () {
                    $(this).remove();
                });
            }); // /.alert

            var activeLeft = I(".hosted");
            activeLeft.click();

        }).fail(function () {
            modal.modal('hide');
            btn.button('reset');
            result.html('<div class="alert alert-danger">' +
                '<button type="button" class="close" data-dismiss="alert">&times;</button>' +
                '<strong><i class="glyphicon glyphicon-ok-sign"></i></strong> ' + "Unable to complete action please try again." + '</div>');

            $(".alert-danger").delay(500).show(10, function () {
                $(this).delay(3000).hide(10, function () {
                    $(this).remove();
                });
            }); // /.alert
        });
        return false;
    });
});

$(document).on('click', '.btnDisableAccommodation', function () {
    var btn = $(this);
    $('#changeModal').modal('show');
    $(".my-title").html("Change accommodation");
    $("#bodyMessage").html("Are you sure you want disable accommodation?");
    $('#btnSubmitChange').unbind('click').bind('click', function () {
        submitData(btn.attr('data-url'));
    });
});

$(document).on('click', '.btnEnableAccommodation', function () {
    var btn = $(this);
    $('#changeModal').modal('show');
    $(".my-title").html("Change accommodation");
    $("#bodyMessage").html("Are you sure you want enable accommodation?");
    $('#btnSubmitChange').unbind('click').bind('click', function () {
        submitData(btn.attr('data-url'));
    });

});

$(document).on('click', '.btnDisableRestoration', function () {
    var btn = $(this);
    $('#changeModal').modal('show');
    $(".my-title").html("Change restoration");
    $("#bodyMessage").html("Are you sure you want disable restoration");
    $('#btnSubmitChange').unbind('click').bind('click', function () {
        submitData(btn.attr('data-url'));
    });
});

$(document).on('click', '.btnEnableRestoration', function () {
    var btn = $(this);
    $('#changeModal').modal('show');
    $(".my-title").html("Change restoration");
    $("#bodyMessage").html("Are you sure you want enable restoration?");
    $('#btnSubmitChange').unbind('click').bind('click', function () {
        submitData(btn.attr('data-url'));
    });
});

$(document).on('click', '.js-regNumber', function () {
    var btn = $(document).find('#btnMakeStudent');
    var url = $(this).attr('data-url');
    var modal = $('#regNumberModal');
    modal.modal('show');
    var form = $(document).find("#makeStudentForm");

    form.on('submit', function (e) {
        e.preventDefault();
        btn.button("loading");
        $.ajax({
            url: url,
            method: form.attr('method'),
            data: form.serialize()
        }).done(function () {
            modal.modal('hide');
            var activeLeft = I(".hosted");
            activeLeft.click();
        }).fail(function () {
            btn.button("reset");
        });
    });
});