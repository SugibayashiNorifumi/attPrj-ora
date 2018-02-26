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

$(function() {

    findOrg();

    $('#org-list > tr').click(function() {
        findUser($(this).data("org-cd"));
    });
});