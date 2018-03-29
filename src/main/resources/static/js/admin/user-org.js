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

function openRegisterOrg() {
    var $form = $('#modal-org-form');
    $form.modal('show');
    $('#org-register-button').show();
    $('#org-update-button').hide();
    $form.find('#org-cd').attr('readonly', false);
}

function openUpdateOrg(orgCd) {
    var $form = $('#modal-org-form');
    $form.find('#org-cd').attr('readonly', true);
    $.ajax({
          url: '/admin/find-org',
          method: 'GET',
          data: {
	          orgCd: orgCd
	      }
	      }).done(function(res) {
	          $form.find('#org-cd').val(res.results.orgCd);
	          $form.find('#org-name').val(res.results.orgName);
	          $form.find('#disp-seq').val(res.results.dispSeq);
	          $form.find('#org-regist-user-id').val(res.results.registUserId);
	          $form.find('#org-regist-date').val(res.results.registDate);
	          $form.find('#org-regist-func-cd').val(res.results.registFuncCd);
	          $form.modal('show');
	          $('#org-register-button').hide();
	          $('#org-update-button').show();
	      }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
	          removeLoading(formId);
	          alert("通信エラーが発生しました");
	          $form.modal('show');
          });
}

function handleUpdateOrg() {
    confirmBeforeSubmit("org-register-form", "更新しますか？", updateOrg);
}

function updateOrg() {
	   var formId = "org-register-form";

	   loading(formId);

	   $form = $("#" + formId);

	   $.ajax( {
	          url: '/admin/org-update',
	          method: 'POST',
	          data: $form.serialize()
	          }).done(function(res) {
	             removeLoading(formId);
	             alert("更新しました");
	             $form.modal('hide');
	             $('#org-register-button').show();
	             $('#org-update-button').hide();
	             findOrg();
	             $('#modal-org-form').modal('hide');
	          }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
	             removeLoading(formId);
	             alert("更新に失敗しました");
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
	          findOrg();
              $('#modal-org-form').modal('hide');
          }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
              removeLoading(formId);
              alert("登録に失敗しました");
          });
}

function deleteOrg(orgCd) {
    if (confirm("削除しますか？")) {
        $.ajax( {
            url: '/admin/org-delete',
            method: 'POST',
            data: {
                orgCd: orgCd
            }
            }).done(function(res) {
                alert("削除しました");
                findOrg();
            }).fail(function(XMLHttpRequest, textStatus, errorThrown) {
                alert("削除に失敗しました");
            });
    }
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
              $form.modal('hide');
	          findUser();
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
