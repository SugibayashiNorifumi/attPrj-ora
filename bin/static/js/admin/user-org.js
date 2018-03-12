function findOrg() {
    var formId = 'org-list';

    loading(formId);

    $.ajax( {
          url: '/admin/find-orgs',
          method: 'GET'
          }).done(function(res) {

              removeLoading(formId);

              $('#org-list').empty();

              if(res.results.length > 0) {
                  var tmpl = $.templates('#org-record-tmpl');

                  $.each(res.results, function(index, record){
                     $('#org-list').append(tmpl.render(record))
                  });

              } else {
                  $('#org-list').append("<tr><td><h4>データがありません。</h4></td></tr>");
              }

          }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
              removeLoading(formId);
          });
}


function findUser(targetOrgCd) {
    var formId = 'user-list';

    loading(formId);

    $.ajax( {
          url: '/admin/find-users',
          method: 'GET',
          data: {
              orgCd: targetOrgCd
          }
          }).done(function(res) {

              removeLoading(formId);

              $('#user-list').empty();

              if(res.results.length > 0) {
                  var tmpl = $.templates('#user-record-tmpl');

                  $.each(res.results, function(index, record){
                     $('#user-list').append(tmpl.render(record))
                  });

              } else {
                  $('#user-list').append("<tr><td><h4>データがありません。</h4></td></tr>");
              }

          }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
              removeLoading(formId);
          });
}

function handleRegisterOrg() {
   confirmBeforeSubmit("org-register-form", "登録しますか？", registerOrg);
}

function registerOrg() {
   var formId = "org-register-form";

   loading(formId);

   $form = $("#" + formId);

   $.ajax( {
          url: '/admin/orgs',
          method: 'POST',
          data: $form.serialize()
          }).done(function(res) {
              removeLoading(formId);
              alert("登録しました");
          }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
              removeLoading(formId);
              alert("登録に失敗しました");
          });
}

function handleRegisterUser() {
   confirmBeforeSubmit("user-register-form", "登録しますか？", registerUser);
}

function registerUser() {
   var formId = "user-register-form";

   loading(formId);

   $form = $("#" + formId);

   $.ajax( {
          url: '/admin/users',
          method: 'POST',
          data: $form.serialize()
          }).done(function(res) {
              removeLoading(formId);
              alert("登録しました");
          }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
              removeLoading(formId);
              alert("登録に失敗しました");
          });
}

$(function() {

    findOrg();

    $('#org-list > tr').click(function() {
        findUser($(this).data("org-cd"));
    });

    $('.modal').on('hidden.bs.modal', function(event) {
        $(event.target).find('form')[0].reset();
        $(event.target).find('select').val('').trigger('change');
    });

    $('.select-org').select2({
        ajax: {
            url: '/admin/orgs/select2',
            dataType: "json"
        }
    });

    $('.select-user').select2({
        ajax: {
            url: '/admin/users/select2',
            dataType: "json"
        }
    });

    $('.select-auth').select2({
        ajax: {
            url: '/admin/auths/select2',
            dataType: "json"
        }
    });

});